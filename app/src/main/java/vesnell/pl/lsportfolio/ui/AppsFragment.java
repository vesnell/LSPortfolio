package vesnell.pl.lsportfolio.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import vesnell.pl.lsportfolio.R;
import vesnell.pl.lsportfolio.database.controller.ProjectController;
import vesnell.pl.lsportfolio.database.model.Project;
import vesnell.pl.lsportfolio.service.DownloadAppsService;
import vesnell.pl.lsportfolio.service.DownloadResultReceiver;

public class AppsFragment extends Fragment implements DownloadResultReceiver.Receiver,
        ProjectController.ProjectsListSaveCallback {

    public static final String TAG = "AppsFragment";

    private ListView listView;
    private ListViewAdapter adapter;
    private DownloadResultReceiver mReceiver;
    private RunServiceType runServiceType;
    private ProgressDialog progressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Project> projects;
    private ProjectController projectController;
    private Context context;

    public static AppsFragment newInstance() {
        AppsFragment f = new AppsFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.apps_fragment, container, false);
        context = container.getContext();

        projectController = new ProjectController(getContext());
        projectController.setProjectsListSaveCallback(this);
        final String projectsUrl = getString(R.string.projects_url);

        progressDialog = new ProgressDialog(getContext());
        listView = (ListView) v.findViewById(R.id.listView);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        TextView emptyView = (TextView) v.findViewById(R.id.tvEmpty);
        listView.setEmptyView(emptyView);

        adapter = new ListViewAdapter(getContext());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (projects != null) {
                    Project project = projects.get(position);
                    Intent i = new Intent(getActivity(), DetailsActivity.class);
                    i.putExtra(Project.NAME, project);
                    startActivity(i);
                }
            }
        });

        mReceiver = new DownloadResultReceiver(new Handler());
        mReceiver.setReceiver(this);

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        runServiceType = RunServiceType.REFRESH;
                        swipeRefreshLayout.setRefreshing(true);
                        startDownloadService(projectsUrl);
                    }
                }
        );

        //start service to download projects
        startDownloadService(projectsUrl);

        return v;
    }

    private void startDownloadService(String url) {
        //send extras to download service
        Intent intent = new Intent(Intent.ACTION_SYNC, null, getContext(), DownloadAppsService.class);
        intent.putExtra(DownloadAppsService.URL, url);
        intent.putExtra(DownloadAppsService.RECEIVER, mReceiver);
        intent.putExtra(DownloadAppsService.DOWNLOAD_TYPE, DownloadAppsService.DownloadType.APPS);
        getActivity().startService(intent);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case DownloadAppsService.STATUS_RUNNING:
                setEnabledDownloadAction(true);
                break;
            case DownloadAppsService.STATUS_ERROR:
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Toast.makeText(context, error, Toast.LENGTH_LONG).show();
            case DownloadAppsService.STATUS_FINISHED:
                List<Project> projects = (List<Project>) resultData.getSerializable(DownloadAppsService.RESULT);
                if (projects != null && projects.size() > 0) {
                    projectController.saveProjectsList(projects);
                } else {
                    showProjectList();
                }
                setEnabledDownloadAction(false);
                break;
        }
    }

    private void showProjectList() {
        projectController.setProjectsListLoadCallback(new ProjectController.ProjectsListLoadCallback() {
            @Override
            public void onProjectsListLoaded(List<Project> projects) {
                AppsFragment.this.projects = projects;
                adapter.setProjects(projects);
                listView.invalidateViews();

                if (runServiceType == RunServiceType.REFRESH) {
                    listView.setSelection(0);
                }
            }
        });
        projectController.requestList();
    }

    private void setEnabledDownloadAction(boolean isEnabled) {
        if (isEnabled) {
            if (!swipeRefreshLayout.isRefreshing() && progressDialog != null) {
                progressDialog.show();
            }
        } else {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            } else {
                if (progressDialog != null) {
                    progressDialog.cancel();
                }
            }
        }
    }

    @Override
    public void onProjectsListSaved(boolean result) {
        if (result) {
            showProjectList();
        } else {
            Log.w(TAG, "error: write to db");
            Toast.makeText(getContext(), R.string.error_write_to_db, Toast.LENGTH_LONG).show();
        }
    }

    public enum RunServiceType {
        FIRST_RUN,
        REFRESH,
        EXPAND,
        BACK_FROM_DETAILS;
    }

    @Override
    public void onPause() {
        super.onPause();

        if ((progressDialog != null) && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = null;
    }
}
