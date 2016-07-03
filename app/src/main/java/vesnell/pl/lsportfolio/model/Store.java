package vesnell.pl.lsportfolio.model;

import org.json.JSONObject;

import java.io.Serializable;

import vesnell.pl.lsportfolio.json.JsonTags;

public class Store implements Serializable {

    private static final String TAG = "Store";

    public static final String NAME = "store";

    private ProjectDetails projectDetails;
    private String url;
    private String image;

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

}
