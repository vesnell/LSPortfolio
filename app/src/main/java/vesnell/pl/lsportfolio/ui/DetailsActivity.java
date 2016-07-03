package vesnell.pl.lsportfolio.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import vesnell.pl.lsportfolio.R;
import vesnell.pl.lsportfolio.database.controller.ImageController;
import vesnell.pl.lsportfolio.database.controller.ProjectDetailsController;
import vesnell.pl.lsportfolio.database.controller.StoreController;
import vesnell.pl.lsportfolio.database.model.Project;
import vesnell.pl.lsportfolio.database.model.ProjectDetails;
import vesnell.pl.lsportfolio.database.model.Store;
import vesnell.pl.lsportfolio.service.DownloadService;
import vesnell.pl.lsportfolio.service.DownloadResultReceiver;


public class DetailsActivity extends AppCompatActivity implements DownloadResultReceiver.Receiver,
    ProjectDetailsController.ProjectDetailsSaveCallback {

    private TextView tvName;
    private TextView tvDescription;
    private LinearLayout llGallery;
    private ImageView ivStore1;
    private ImageView ivStore2;
    private ImageView ivStore3;

    private DownloadResultReceiver mReceiver;
    private ProgressDialog progressDialog;
    private ProjectDetailsController projectDetailsController;
    private StoreController storeController;
    private ImageController imageController;
    private List<Store> stores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        projectDetailsController = new ProjectDetailsController(this);
        projectDetailsController.setProjectDetailsSaveCallback(this);
        storeController = new StoreController(this);
        imageController = new ImageController(this);

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
                    projectDetailsController.save(projectDetails);
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

    @Override
    public void onProjectDetailsSaved(boolean result, final ProjectDetails projectDetails) {
        if (result) {
            storeController.setStoresListSaveCallback(new StoreController.StoresListSaveCallback() {
                @Override
                public void onStoresListSaved(boolean result) {
                    if (result) {
                        imageController.setImagesListSaveCallback(new ImageController.ImagesListSaveCallback() {
                            @Override
                            public void onImagesListSaved(boolean result) {
                                if (result) {
                                    setProjectDetailsContent(projectDetails);
                                } else {
                                    Toast.makeText(DetailsActivity.this, "Error: could not save images list", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        imageController.saveImagesList(projectDetails.getTempImages());
                    } else {
                        Toast.makeText(DetailsActivity.this, "Error: could not save stores list", Toast.LENGTH_LONG).show();
                    }
                }
            });
            storeController.saveStoresList(projectDetails.getTempStores());
        } else {
            Toast.makeText(this, "Error: could not save projectDetails", Toast.LENGTH_LONG).show();
        }
    }

    private void setProjectDetailsContent(ProjectDetails projectDetails) {
        tvName.setText(projectDetails.getName());
        tvDescription.setText(projectDetails.getDescription());
        stores = projectDetails.getStores();
        setStores(stores);
    }

    private void setStores(List<Store> stores) {
        Store store1 = stores.get(0);
        Store store2 = stores.get(1);
        Store store3 = stores.get(2);
        Picasso.with(this).load(store1.getImage())
                .resizeDimen(R.dimen.store_item_width, R.dimen.store_item_height).centerCrop().into(ivStore1);
        Picasso.with(this).load(store2.getImage())
                .resizeDimen(R.dimen.store_item_width, R.dimen.store_item_height).centerCrop().into(ivStore2);
        Picasso.with(this).load(store3.getImage())
                .resizeDimen(R.dimen.store_item_width, R.dimen.store_item_height).centerCrop().into(ivStore3);
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
}
