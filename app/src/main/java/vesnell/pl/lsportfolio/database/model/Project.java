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

@DatabaseTable(tableName="Project")
public class Project implements Serializable, Comparable<Project> {

    private static final String TAG = "Project";

    public static final String NAME = "project";

    @DatabaseField(id = true)
    private String id;
    @DatabaseField
    private String name;
    @DatabaseField
    private String icon;
    @DatabaseField
    private String description;
    @ForeignCollectionField(eager = true)
    private ForeignCollection<Image> images;
    @ForeignCollectionField(eager = true)
    private ForeignCollection<Store> stores;

    //for OrmLite
    public Project() {
    }

    public Project(JSONObject item) {
        String name = item.optString(JsonTags.NAME);
        String id = item.optString(JsonTags.ID);
        String icon = item.optString(JsonTags.ICON);
        String description = item.optString(JsonTags.DESCRIPTION);
        this.name = name;
        this.id = id;
        this.icon = icon;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    @Override
    public int compareTo(Project project) {
        return id.compareTo(project.id);
    }
}

