package vesnell.pl.lsportfolio.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import vesnell.pl.lsportfolio.R;
import vesnell.pl.lsportfolio.model.Image;
import vesnell.pl.lsportfolio.model.Project;
import vesnell.pl.lsportfolio.model.ProjectDetails;
import vesnell.pl.lsportfolio.model.Store;
import vesnell.pl.lsportfolio.service.DownloadService;
import vesnell.pl.lsportfolio.service.DownloadResultReceiver;

public class DetailsActivity extends AppCompatActivity implements DownloadResultReceiver.Receiver {

    private TextView tvName;
    private TextView tvDescription;
    private LinearLayout llGallery;
    private ImageView ivStore1;
    private ImageView ivStore2;
    private ImageView ivStore3;

    private DownloadResultReceiver mReceiver;
    private ProgressDialog progressDialog;
    private List<Store> stores;

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
        ivStore1 = (ImageView) findViewById(R.id.store1);
        ivStore2 = (ImageView) findViewById(R.id.store2);
        ivStore3 = (ImageView) findViewById(R.id.store3);

        Picasso.with(this).load(project.getIcon())
                .resizeDimen(R.dimen.list_item_width, R.dimen.list_item_height).centerCrop().into(ivIcon);

        progressDialog = new ProgressDialog(this);
        mReceiver = new DownloadResultReceiver(new Handler());
        mReceiver.setReceiver(this);

        String projectDetailsUrl = String.format(getString(R.string.project_details_url), project.getId());
        startDownloadService(projectDetailsUrl, project);
    }

    private void startDownloadService(String url, Project project) {
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, DownloadService.class);
        intent.putExtra(DownloadService.URL, url);
        intent.putExtra(Project.NAME, project);
        intent.putExtra(DownloadService.RECEIVER, mReceiver);
        intent.putExtra(DownloadService.DOWNLOAD_TYPE, DownloadService.DownloadType.DETAILS);
        startService(intent);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case DownloadService.STATUS_RUNNING:
                setEnabledDownloadAction(true);
                break;
            case DownloadService.STATUS_ERROR:
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            case DownloadService.STATUS_FINISHED:
                ProjectDetails projectDetails = (ProjectDetails) resultData.getSerializable(DownloadService.RESULT);
                if (projectDetails != null) {
                    setProjectDetailsContent(projectDetails);
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

    private void setProjectDetailsContent(ProjectDetails projectDetails) {
        tvName.setText(projectDetails.getName());
        tvDescription.setText(projectDetails.getDescription());
        stores = projectDetails.getStores();
        setStores();
        setGallery(projectDetails.getImages());
    }

    private void setStores() {
        switch (stores.size()) {
            case 3:
                Store store3 = stores.get(2);
                ivStore3.setVisibility(View.VISIBLE);
                Picasso.with(this).load(store3.getImage())
                        .resizeDimen(R.dimen.store_item_width, R.dimen.store_item_height).centerInside().into(ivStore3);
            case 2:
                Store store2 = stores.get(1);
                ivStore2.setVisibility(View.VISIBLE);
                Picasso.with(this).load(store2.getImage())
                        .resizeDimen(R.dimen.store_item_width, R.dimen.store_item_height).centerInside().into(ivStore2);
            case 1:
                Store store1 = stores.get(0);
                ivStore1.setVisibility(View.VISIBLE);
                Picasso.with(this).load(store1.getImage())
                        .resizeDimen(R.dimen.store_item_width, R.dimen.store_item_height).centerInside().into(ivStore1);
        }
    }

    public void onStoreClick(View v) {
        switch(v.getId()) {
            case R.id.store1:
                openStore(stores.get(0).getUrl());
                break;
            case R.id.store2:
                openStore(stores.get(1).getUrl());
                break;
            case R.id.store3:
                openStore(stores.get(2).getUrl());
                break;
        }
    }

    private void openStore(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    public void setGallery(List<Image> gallery) {
        for (Image image : gallery) {
            final ImageView imageView = getImageViewGalleryContent();
            Picasso.with(this)
                    .load(image.getUrl())
                    .into(imageView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            llGallery.addView(imageView);
                        }

                        @Override
                        public void onError() {

                        }
                    });
        }
    }

    private ImageView getImageViewGalleryContent(){
        ImageView imageView = new ImageView(getApplicationContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(getResources().getDimensionPixelSize(R.dimen.gallery_margin_item), 0,
                getResources().getDimensionPixelSize(R.dimen.gallery_margin_item), 0);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        return imageView;
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
