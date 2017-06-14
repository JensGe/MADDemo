package de.honzont.jensge.maddemo.model;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Jens on 17.05.2017.
 */

public class SimpleToDoCRUDOperationsImpl implements IToDoCRUDOperations {


    @Override
    public ToDo createToDo(ToDo item) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return item;
    }

    @Override
    public List<ToDo> readAllToDos() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(new ToDo[]{new ToDo("lorem"), new ToDo("ipsum"), new ToDo("dolor"), new ToDo("sit"), new ToDo("amet"), new ToDo("adispicsing"), new ToDo("elit")/*, new ToDo("lirem"), new ToDo("sdfiff"), new ToDo("sdfwerfw"), new ToDo("ame2t"), new ToDo("adispicsing2"), new ToDo("elit2"), new ToDo("ám3"), new ToDo("adispicsing3"), new ToDo("elit3")*/});
    }

    @Override
    public ToDo readToDo(long id) {
        return null;
    }

    @Override
    public ToDo updateToDo(long id, ToDo item) {
        return null;
    }

    @Override
    public boolean deleteToDo(long id) {
        return false;
    }
}
