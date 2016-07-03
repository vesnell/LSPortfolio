package vesnell.pl.lsportfolio.model;

import java.io.Serializable;

public class Image implements Serializable {

    private static final String TAG = "Image";

    public static final String NAME = "image";

    private ProjectDetails projectDetails;
    private String url;

    public Image(String url, ProjectDetails projectDetails) {
        this.url = url;
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

}
