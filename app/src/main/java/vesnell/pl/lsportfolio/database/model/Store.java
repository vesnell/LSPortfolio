package vesnell.pl.lsportfolio.database.model;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.json.JSONObject;

import java.io.Serializable;

import vesnell.pl.lsportfolio.json.JsonTags;

@DatabaseTable(tableName="Store")
public class Store implements Serializable, Comparable<Store> {

    private static final String TAG = "Store";

    public static final String NAME = "store";

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true, canBeNull = false)
    private ProjectDetails projectDetails;
    @DatabaseField
    private String url;
    @DatabaseField
    private String image;

    public Store() {}

    public Store(JSONObject jsonObject, ProjectDetails projectDetails) {
        String url = jsonObject.optString(JsonTags.URL);
        String image = jsonObject.optString(JsonTags.IMAGE);
        this.url = url;
        this.image = image;
        this.projectDetails = projectDetails;
    }

    public ProjectDetails getProjectDetails() {
        return projectDetails;
    }

    public void setProjectDetails(ProjectDetails projectDetails) {
        this.projectDetails = projectDetails;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int compareTo(Store store) {
        return id < store.id ? -1 : 1;
    }

}
