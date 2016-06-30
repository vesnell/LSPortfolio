package vesnell.pl.lsportfolio.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import vesnell.pl.lsportfolio.R;

public class ContactFragment extends Fragment {

    public static final String TAG = "ContactFragment";

    public static ContactFragment newInstance() {
        ContactFragment f = new ContactFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.contact_fragment, container, false);

        WebView webView = (WebView) v.findViewById(R.id.wvContact);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(getString(R.string.contact_url));

        return v;
    }

}
