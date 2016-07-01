package vesnell.pl.lsportfolio.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import vesnell.pl.lsportfolio.R;
import vesnell.pl.lsportfolio.database.model.Project;
import vesnell.pl.lsportfolio.service.DownloadAppsService;
import vesnell.pl.lsportfolio.service.DownloadResultReceiver;

public class AppsFragment extends Fragment implements DownloadResultReceiver.Receiver {

    public static final String TAG = "AppsFragment";
    private static final int REQ_DETAILS = 1;

    private ListView listView;
    private ListViewAdapter adapter;
    private DownloadResultReceiver mReceiver;
    private RunServiceType runServiceType;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Project> projects;

    public static AppsFragment newInstance() {
        AppsFragment f = new AppsFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.apps_fragment, container, false);

        final String projectsUrl = getString(R.string.projects_url);

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
                    //Intent i = new Intent(getActivity(), Details.class);

                    //i.putExtra(Project.NAME, project);
                    //startActivityForResult(i, REQ_DETAILS);
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
                //setEnabledDownloadAction(true);
                break;
            case DownloadAppsService.STATUS_ERROR:
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            case DownloadAppsService.STATUS_FINISHED:
                List<Project> projects = (List<Project>) resultData.getSerializable(DownloadAppsService.RESULT);
                if (projects != null && projects.size() > 0) {

                } else {

                }
                //setEnabledDownloadAction(false);
                break;
        }
    }

    public enum RunServiceType {
        FIRST_RUN,
        REFRESH,
        EXPAND,
        BACK_FROM_DETAILS;
    }
}
