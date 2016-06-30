package vesnell.pl.lsportfolio.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import vesnell.pl.lsportfolio.R;

public class AppsFragment extends Fragment {

    public static final String TAG = "AppsFragment";

    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;

    public static AppsFragment newInstance() {
        AppsFragment f = new AppsFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.apps_fragment, container, false);

        listView = (ListView) v.findViewById(R.id.listView);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        TextView emptyView = (TextView) v.findViewById(R.id.tvEmpty);
        listView.setEmptyView(emptyView);

        return v;
    }

}
