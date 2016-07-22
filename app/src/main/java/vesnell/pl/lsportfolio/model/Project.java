package vesnell.pl.lsportfolio.model;

import java.io.Serializable;

public class Project implements Serializable, Comparable<Project> {

    public int id;
    public String name;
    public String icon;
    public String description;

    @Override
    public int compareTo(Project project) {
        if (id < project.id) {
            return -1;
        } else {
            return 1;
        }
    }
}

