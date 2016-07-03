package vesnell.pl.lsportfolio.model;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

import vesnell.pl.lsportfolio.json.JsonTags;

public class ProjectDetails implements Serializable {

    private static final String TAG = "ProjectDetails";

    public static final String NAME = "projectDetails";

    private Project project;
    private String name;
    private String description;
    private List<Image> images;
    private List<Store> stores;

    public ProjectDetails(JSONObject jsonObject, Project project) {
        String name = jsonObject.optString(JsonTags.NAME);
        String description = jsonObject.optString(JsonTags.DESCRIPTION);
        this.name = name;
        this.description = description;
        this.project = project;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Store> getStores() {
        return stores;
    }

    public void setStores(List<Store> stores) {
        this.stores = stores;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

}
