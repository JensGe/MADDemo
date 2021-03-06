package de.honzont.jensge.maddemo.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Jens on 03.05.2017.
 */

public class ToDo implements Serializable{

    private long id;
    private String name;
    private String description;

    private ArrayList<String> contacts;

    @SerializedName("expiry")
    private long dueDate;

    private boolean done;
    private boolean favourite;

    public ToDo() {
    }
    public ToDo(String name) {
        this.name = name;
    }
    public ToDo(String name, String description, long dueDate, boolean favourite, boolean done, ArrayList<String> contacts) {
        this.name = name;
        this.description = description;
        this.dueDate = dueDate;
        this.favourite = favourite;
        this.done = done;
        this.contacts = contacts;
    }
    public ToDo(long id, String name, String description, long dueDate, boolean favourite, boolean done, ArrayList<String> contacts) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.dueDate = dueDate;
        this.favourite = favourite;
        this.done = done;
        this.contacts = contacts;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public ArrayList<String> getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<String> contacts) {
        this.contacts = contacts;
    }

    @Override
    public String toString() {
        return "ToDo{" +
                "name='" + name + '\'' +
                ", dueDate=" + dueDate +
                ", id=" + id +
                ", description=" + description +
                ", favourite=" + favourite +
                ", done=" + done +
                ", contacts=" + contacts +
                '}';
    }


}
