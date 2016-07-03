package vesnell.pl.lsportfolio.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import vesnell.pl.lsportfolio.R;
import vesnell.pl.lsportfolio.database.model.Project;
import vesnell.pl.lsportfolio.database.model.ProjectDetails;
import vesnell.pl.lsportfolio.service.DownloadAppsService;
import vesnell.pl.lsportfolio.service.DownloadResultReceiver;


public class DetailsActivity extends AppCompatActivity implements DownloadResultReceiver.Receiver {

    private TextView tvName;
    private TextView tvDescription;
    private LinearLayout llGallery;
    private ImageButton ibStoreIos;
    private ImageButton ibStoreAndroid;
    private ImageButton ibStoreWindows;

    private DownloadResultReceiver mReceiver;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Bundle b = getIntent().getExtras();
        Project project = (Project) b.getSerializable(Project.NAME);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_HOME_AS_UP);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar_text);
        TextView actionBarTitle = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);
        actionBarTitle.setText(project.getName());

        ImageView ivIcon = (ImageView) findViewById(R.id.icon);
        tvName = (TextView) findViewById(R.id.name);
        tvDescription = (TextView) findViewById(R.id.description);
        llGallery = (LinearLayout) findViewById(R.id.gallery);
        ibStoreIos = (ImageButton) findViewById(R.id.store_ios);
        ibStoreAndroid = (ImageButton) findViewById(R.id.store_android);
        ibStoreWindows = (ImageButton) findViewById(R.id.store_windows);

        Picasso.with(this).load(project.getIcon())
                .resizeDimen(R.dimen.list_item_width, R.dimen.list_item_height).centerCrop().into(ivIcon);

        progressDialog = new ProgressDialog(this);
        mReceiver = new DownloadResultReceiver(new Handler());
        mReceiver.setReceiver(this);

        String projectDetailsUrl = String.format(getString(R.string.project_details_url), project.getId());
        startDownloadService(projectDetailsUrl, project);
    }

    private void startDownloadService(String url, Project project) {
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, DownloadAppsService.class);
        intent.putExtra(DownloadAppsService.URL, url);
        intent.putExtra(Project.NAME, project);
        intent.putExtra(DownloadAppsService.RECEIVER, mReceiver);
        intent.putExtra(DownloadAppsService.DOWNLOAD_TYPE, DownloadAppsService.DownloadType.DETAILS);
        startService(intent);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case DownloadAppsService.STATUS_RUNNING:
                setEnabledDownloadAction(true);
                break;
            case DownloadAppsService.STATUS_ERROR:
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            case DownloadAppsService.STATUS_FINISHED:
                ProjectDetails projectDetails = (ProjectDetails) resultData.getSerializable(DownloadAppsService.RESULT);
                if (projectDetails != null) {
                    tvName.setText(projectDetails.getName());
                    tvDescription.setText(projectDetails.getDescription());
                } else {
                    String projectDetailsNull = getString(R.string.project_details_null);
                    Toast.makeText(this, projectDetailsNull, Toast.LENGTH_LONG).show();
                }
                setEnabledDownloadAction(false);
                break;
        }
    }

    private void setEnabledDownloadAction(boolean isEnabled) {
        if (isEnabled) {
            progressDialog.show();
        } else {
            if (progressDialog != null) {
                progressDialog.cancel();
            }
        }
    }

}
