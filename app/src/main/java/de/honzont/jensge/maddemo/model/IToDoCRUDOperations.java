package de.honzont.jensge.maddemo.model;

import java.util.List;

/**
 * Created by Jens on 17.05.2017.
 */

public interface IToDoCRUDOperations {

    // Create
    public ToDo createToDo(ToDo item);

    // Read (All)
    public List<ToDo> readAllToDos();

    // Read (Single)
    public ToDo readToDo(long id);

    // Update
    public ToDo updateToDo(long id, ToDo item);

    // Delete
    public boolean deleteToDo(long id);




}
