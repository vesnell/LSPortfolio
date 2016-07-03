package vesnell.pl.lsportfolio.database.model;

import android.util.Log;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import org.json.JSONObject;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import vesnell.pl.lsportfolio.json.JsonTags;

@DatabaseTable(tableName="ProjectDetails")
public class ProjectDetails implements Serializable {

    private static final String TAG = "ProjectDetails";

    public static final String NAME = "projectDetails";

    @DatabaseField(generatedId = true)
    private String id;
    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true, canBeNull = false)
    private Project project;
    @DatabaseField
    private String name;
    @DatabaseField
    private String description;
    @ForeignCollectionField(eager = true, foreignFieldName = "Image")
    private ForeignCollection<Image> images;
    @ForeignCollectionField(eager = true, foreignFieldName = "Store")
    private ForeignCollection<Store> stores;

    private List<Image> tempImages;
    private List<Store> tempStores;

    public ProjectDetails() {}

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

    public List<Store> getTempStores() {
        return tempStores;
    }

    public void setTempStores(List<Store> tempStores) {
        this.tempStores = tempStores;
    }

    public List<Image> getTempImages() {
        return tempImages;
    }

    public void setTempImages(List<Image> tempImages) {
        this.tempImages = tempImages;
    }

    public List<Image> getImages() {
        try {
            if (images != null) {
                images.refreshCollection();
                return getSortedImageList();
            }
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());
        } catch (NullPointerException e) {
            return getSortedImageList();
        }
        return null;
    }

    private List<Image> getSortedImageList() {
        List<Image> list = new ArrayList<Image>(images);
        Collections.sort(list);
        return list;
    }

    public List<Store> getStores() {
        try {
            if (stores != null) {
                stores.refreshCollection();
                return getSortedStoreList();
            }
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());
        } catch (NullPointerException e) {
            return getSortedStoreList();
        }
        return null;
    }

    private List<Store> getSortedStoreList() {
        List<Store> list = new ArrayList<Store>(stores);
        Collections.sort(list);
        return list;
    }

}
