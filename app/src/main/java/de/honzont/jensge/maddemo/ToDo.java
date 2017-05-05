package de.honzont.jensge.maddemo;

import java.io.Serializable;

/**
 * Created by Jens on 03.05.2017.
 */

public class ToDo implements Serializable{

    private String name;
    private long dueDate;

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
}
