package vesnell.pl.lsportfolio.database.model;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName="Image")
public class Image implements Serializable, Comparable<Image> {

    private static final String TAG = "Image";

    public static final String NAME = "image";

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true, canBeNull = false)
    private ProjectDetails projectDetails;
    @DatabaseField
    private String url;

    public Image() {}

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

    @Override
    public int compareTo(Image image) {
        return id < image.id ? -1 : 1;
    }

}
