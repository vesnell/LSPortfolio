package vesnell.pl.lsportfolio.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import vesnell.pl.lsportfolio.R;
import vesnell.pl.lsportfolio.api.LooksoftApi;
import vesnell.pl.lsportfolio.api.LooksoftMainApi;
import vesnell.pl.lsportfolio.model.main.Data;
import vesnell.pl.lsportfolio.model.main.Project;

public class AppsFragment extends Fragment implements Callback<Data> {

    public static final String TAG = "AppsFragment";

    private RecyclerView listView;
    private MyAdapter adapter;
    private ProgressDialog progressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.apps_fragment, container, false);

        progressDialog = new ProgressDialog(getContext());
        listView = (RecyclerView) v.findViewById(R.id.listView);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        listView.setLayoutManager(llm);

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        swipeRefreshLayout.setRefreshing(true);
                        startDownloadProjects();
                    }
                }
        );

        adapter = new MyAdapter(getContext());
        listView.setAdapter(adapter);
        adapter.setOnItemClickListenr(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Project project = adapter.getProjects().get(position);
                Intent i = new Intent(getActivity(), DetailsActivity.class);
                i.putExtra(Project.NAME, project);
                startActivity(i);
            }
        });

        startDownloadProjects();
        return v;
    }

    private void startDownloadProjects() {
        setEnabledDownloadAction(true);

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(LooksoftApi.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        LooksoftMainApi looksoftMainApi = retrofit.create(LooksoftMainApi.class);
        Call<Data> call = looksoftMainApi.loadData();
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<Data> call, Response<Data> response) {
        setEnabledDownloadAction(false);
        int code = response.code();
        if (code == 200) {
            showProjectList(response.body().data.portfolio);
        } else {
            Toast.makeText(getContext(), String.valueOf(code), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailure(Call<Data> call, Throwable t) {
        setEnabledDownloadAction(false);
        Toast.makeText(getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }

    private void showProjectList(List<Project> projects) {
        adapter.swap(projects);
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
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.cancel();
                }
            }
        }
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
