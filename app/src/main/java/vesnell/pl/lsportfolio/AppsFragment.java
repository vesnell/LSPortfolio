package vesnell.pl.lsportfolio;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AppsFragment extends Fragment {

    public static final String TAG = "AppsFragment";

    public static AppsFragment newInstance() {
        AppsFragment f = new AppsFragment();
        /*Bundle b = new Bundle();
        b.putString("text", text);
        f.setArguments(b);*/
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //String text = getArguments().getString("text");
        View v =  inflater.inflate(R.layout.apps_fragment, container, false);
        return v;
    }

}