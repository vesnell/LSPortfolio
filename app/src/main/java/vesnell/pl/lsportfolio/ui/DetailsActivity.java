package vesnell.pl.lsportfolio.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import vesnell.pl.lsportfolio.R;
import vesnell.pl.lsportfolio.database.model.Project;


public class DetailsActivity extends AppCompatActivity {

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
        TextView tvName = (TextView) findViewById(R.id.name);
        TextView tvDescription = (TextView) findViewById(R.id.description);
        LinearLayout llGallery = (LinearLayout) findViewById(R.id.gallery);
        ImageButton ibStoreIos = (ImageButton) findViewById(R.id.store_ios);
        ImageButton ibStoreAndroid = (ImageButton) findViewById(R.id.store_android);
        ImageButton ibStoreWindows = (ImageButton) findViewById(R.id.store_windows);

        Picasso.with(this).load(project.getIcon())
                .resizeDimen(R.dimen.list_item_width, R.dimen.list_item_height).centerCrop().into(ivIcon);
        tvName.setText(project.getName());
        tvDescription.setText(project.getDescription());
    }

}
