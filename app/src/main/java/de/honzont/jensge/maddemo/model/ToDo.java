package de.honzont.jensge.maddemo.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Jens on 03.05.2017.
 */

public class ToDo implements Serializable{

    private long id;
    private String name;
    private String description;

    @SerializedName("expiry")
    private long dueDate;

    private boolean done;
    private boolean favourite;

    public ToDo() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ToDo(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDueDate() {
        return dueDate;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    @Override
    public String toString() {
        return "ToDo{" +
                "name='" + name + '\'' +
                ", dueDate=" + dueDate +
                ", id=" + id +
                '}';
    }
}
