package de.honzont.jensge.maddemo;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import de.honzont.jensge.maddemo.model.IToDoCRUDOperations;
import de.honzont.jensge.maddemo.model.IToDoCRUDOperationsASync;
import de.honzont.jensge.maddemo.model.RemoteToDoCRUDOperationsImpl;
import de.honzont.jensge.maddemo.model.ToDo;

/**
 * Created by Jens on 14.06.2017.
 */

public class ToDoApplication extends Application implements IToDoCRUDOperationsASync {

    private static String logger = ToDoApplication.class.getSimpleName();

    private IToDoCRUDOperations syncCrudOperations = new RemoteToDoCRUDOperationsImpl();

    @Override
    public void onCreate() {
        Log.i(logger,"onCreate()");

    }

    public IToDoCRUDOperationsASync getCRUDOperationsImpl() {
        return this;
    }


    @Override
    public void createToDo(ToDo item, final CallbackFunction<ToDo> callback) {
        new AsyncTask<ToDo,Void,ToDo>() {

            @Override
            protected ToDo doInBackground(ToDo... params) {
                return syncCrudOperations.createToDo(params[0]);
            }

            @Override
            protected void onPostExecute(ToDo toDo) {
                callback.process(toDo);

            }
        }.execute(item);

    }

    @Override
    public void readAllToDos(final CallbackFunction<List<ToDo>> callback) {

        new AsyncTask<Void,Void,List<ToDo>>() {

            @Override
            protected List<ToDo> doInBackground(Void... params) {
                return syncCrudOperations.readAllToDos();
            }

            @Override
            protected void onPostExecute(List<ToDo> toDos) {
                callback.process(toDos);
            }
        }.execute();

    }

    @Override
    public void readToDo(long id, CallbackFunction<ToDo> callback) {

    }

    @Override
    public void updateToDo(final long id, final ToDo item, final CallbackFunction<ToDo> callback) {
        new AsyncTask<Long,Void,ToDo>() {

            @Override
            protected ToDo doInBackground(Long... params) {
                return syncCrudOperations.updateToDo(id, item);
            }
            @Override
            protected void onPostExecute(ToDo toDo) {
                callback.process(toDo);
            }

        }.execute(id);
    }

    @Override
    public void deleteToDo(long id, final CallbackFunction<Boolean> callback) {

        new AsyncTask<Long,Void,Boolean>() {

            @Override
            protected Boolean doInBackground(Long... params) {
                return syncCrudOperations.deleteToDo(params[0]);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                callback.process(aBoolean);
            }
        }.execute(id);

    }
}
