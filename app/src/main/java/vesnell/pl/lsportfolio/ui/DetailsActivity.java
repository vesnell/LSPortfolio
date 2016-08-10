package vesnell.pl.lsportfolio.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import vesnell.pl.lsportfolio.R;
import vesnell.pl.lsportfolio.api.LooksoftApi;
import vesnell.pl.lsportfolio.api.LooksoftDetailsApi;
import vesnell.pl.lsportfolio.model.details.Data;
import vesnell.pl.lsportfolio.model.main.Project;
import vesnell.pl.lsportfolio.model.details.ProjectDetails;
import vesnell.pl.lsportfolio.model.details.Store;

public class DetailsActivity extends AppCompatActivity implements Callback<Data> {

    private TextView tvName;
    private TextView tvDescription;
    private LinearLayout llGallery;
    private ImageView ivStore1;
    private ImageView ivStore2;
    private ImageView ivStore3;

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

        progressDialog = new ProgressDialog(this);

        ImageView ivIcon = (ImageView) findViewById(R.id.icon);
        tvName = (TextView) findViewById(R.id.name);
        tvDescription = (TextView) findViewById(R.id.description);
        llGallery = (LinearLayout) findViewById(R.id.gallery);
        ivStore1 = (ImageView) findViewById(R.id.store1);
        ivStore2 = (ImageView) findViewById(R.id.store2);
        ivStore3 = (ImageView) findViewById(R.id.store3);

        actionBarTitle.setText(project.name);
        Picasso.with(this).load(project.icon)
                .resizeDimen(R.dimen.list_item_width, R.dimen.list_item_height).centerCrop().into(ivIcon);

        startDownloadDetailsProject(project);
    }

    private void startDownloadDetailsProject(Project project) {
        setEnabledDownloadAction(true);

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(LooksoftApi.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        String projectId = Integer.toString(project.id);
        LooksoftDetailsApi looksoftDetailsApi = retrofit.create(LooksoftDetailsApi.class);
        Call<Data> call = looksoftDetailsApi.getDetails(projectId);
        call.enqueue(this);
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
        tvName.setText(projectDetails.name);
        tvDescription.setText(projectDetails.description);
        stores = projectDetails.link;
        setStores();
        setGallery(projectDetails.gallery);
    }

    private void setStores() {
        switch (stores.size()) {
            case 3:
                Store store3 = stores.get(2);
                ivStore3.setVisibility(View.VISIBLE);
                Picasso.with(this).load(store3.image)
                        .resizeDimen(R.dimen.store_item_width, R.dimen.store_item_height).centerInside().into(ivStore3);
            case 2:
                Store store2 = stores.get(1);
                ivStore2.setVisibility(View.VISIBLE);
                Picasso.with(this).load(store2.image)
                        .resizeDimen(R.dimen.store_item_width, R.dimen.store_item_height).centerInside().into(ivStore2);
            case 1:
                Store store1 = stores.get(0);
                ivStore1.setVisibility(View.VISIBLE);
                Picasso.with(this).load(store1.image)
                        .resizeDimen(R.dimen.store_item_width, R.dimen.store_item_height).centerInside().into(ivStore1);
        }
    }

    public void onStoreClick(View v) {
        switch(v.getId()) {
            case R.id.store1:
                openStore(stores.get(0).url);
                break;
            case R.id.store2:
                openStore(stores.get(1).url);
                break;
            case R.id.store3:
                openStore(stores.get(2).url);
                break;
        }
    }

    private void openStore(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    public void setGallery(List<String> gallery) {
        for (String image: gallery) {
            final ImageView imageView = getImageViewGalleryContent();
            Picasso.with(this)
                    .load(image)
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

    @Override
    public void onResponse(Call<Data> call, Response<Data> response) {
        setEnabledDownloadAction(false);
        int code = response.code();
        if (code == 200) {
            setProjectDetailsContent(response.body().data);
        } else {
            Toast.makeText(this, String.valueOf(code), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailure(Call<Data> call, Throwable t) {
        setEnabledDownloadAction(false);
        Toast.makeText(this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }
}
