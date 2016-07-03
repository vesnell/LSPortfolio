package vesnell.pl.lsportfolio.database.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.json.JSONObject;

import java.io.Serializable;

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

    @Override
    public int compareTo(Project project) {
        return id.compareTo(project.id);
    }
}

