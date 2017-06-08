package de.honzont.jensge.maddemo.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Jens on 03.05.2017.
 */

public class ToDo implements Serializable{

    private String name;

    @SerializedName("expiry")
    private long dueDate;

    private long id;

    public ToDo() {
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
