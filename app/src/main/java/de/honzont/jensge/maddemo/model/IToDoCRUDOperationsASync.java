package de.honzont.jensge.maddemo.model;

import java.util.List;

/**
 * Created by Jens on 14.06.2017.
 */

public interface IToDoCRUDOperationsASync {

    public static interface CallbackFunction<T> {

        public void process(T result);

    }

    // Create
    public void createToDo(ToDo item, CallbackFunction<ToDo> callback);

    // Read (All)
    public void readAllToDos(CallbackFunction<List<ToDo>> callback);

    // Read (Single)
    public void readToDo(long id, CallbackFunction<ToDo> callback);

    // Update
    public void updateToDo(long id, ToDo item, CallbackFunction<ToDo> callback);

    // Delete
    public void deleteToDo(long id, CallbackFunction<Boolean> callback);

}
