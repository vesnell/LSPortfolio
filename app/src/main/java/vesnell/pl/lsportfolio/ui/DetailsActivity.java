package vesnell.pl.lsportfolio.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import vesnell.pl.lsportfolio.R;
import vesnell.pl.lsportfolio.database.model.Project;


public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_HOME_AS_UP);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar_text);

        TextView actionBarTitle = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);

        Bundle b = getIntent().getExtras();
        Project project = (Project) b.getSerializable(Project.NAME);
        actionBarTitle.setText(project.getName());
    }

}
