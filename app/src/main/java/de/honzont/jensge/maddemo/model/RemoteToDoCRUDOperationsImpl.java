package de.honzont.jensge.maddemo.model;

import android.util.Log;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by Gäbeler on 08.06.2017.
 */

public class RemoteToDoCRUDOperationsImpl implements IToDoCRUDItemOperations {

    public interface IToDoCRUDWebAPI {

        @POST("/api/todos")
        public Call<ToDo> createToDo(@Body ToDo item);

        @GET("/api/todos")
        public Call<List<ToDo>> readAllToDos();

        @GET("/api/todos/{id}")
        public Call<ToDo> readToDo(@Path("id") long id);

        @PUT("/api/todos/{id}")
        public Call<ToDo> updateToDo(@Path("id") long id, @Body ToDo item);

        @DELETE("/api/todos/{id}")
        public Call<Boolean> deleteToDo(@Path("id") long id);
    }

    private IToDoCRUDWebAPI webAPI;

    public RemoteToDoCRUDOperationsImpl() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.webAPI = retrofit.create(IToDoCRUDWebAPI.class);
    }

    @Override
    public ToDo createToDo(ToDo item) {

        try {
            Log.i("RemoteToDoCRUDOpImpl","Got item: " + item);
            ToDo created;
            created = this.webAPI.createToDo(item).execute().body();
            Log.i("RemoteToDoCRUDOpImpl","created ToDo: " + created);
            return created;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ToDo> readAllToDos() {
        try {
            return this.webAPI.readAllToDos().execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
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

        try {
            return this.webAPI.deleteToDo(id).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
