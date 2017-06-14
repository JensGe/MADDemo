package de.honzont.jensge.maddemo.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gäbeler on 25.05.2017.
 */

public class LocalToDoCRUDOperationsImpl implements IToDoCRUDOperations {

    protected static String logger = LocalToDoCRUDOperationsImpl.class.getSimpleName();

    private SQLiteDatabase db;

    public LocalToDoCRUDOperationsImpl(Context context) {

        db = context.openOrCreateDatabase("mydb.sqlite", Context.MODE_PRIVATE, null);
        if (db.getVersion() == 0) {
            db.setVersion(1);
            db.execSQL("CREATE TABLE TODOS (ID INTEGER PRIMARY KEY, NAME TEXT, DUEDATE INTEGER)");
        }

    }

    @Override
    public ToDo createToDo(ToDo item) {
        ContentValues values = new ContentValues();
        values.put("NAME", item.getName());
        values.put("DUEDATE", item.getDueDate());

        long id = db.insert("TODOS", null, values);
        item.setId(id);

        return item;
    }

    @Override
    public List<ToDo> readAllToDos() {
        List<ToDo> items = new ArrayList<ToDo>();

        Cursor cursor = db.query("TODOS", new String[]{"ID","NAME","DUEDATE"},null,null,null, null, "NAME");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            boolean next = false;
            do {
                ToDo item = new ToDo();
                items.add(item);

                item.setId(cursor.getLong(cursor.getColumnIndex("ID")));
                item.setName(cursor.getString(cursor.getColumnIndex("NAME")));
                item.setDueDate(cursor.getLong(cursor.getColumnIndex("DUEDATE")));

                next = cursor.moveToNext();
            } while (next);
        }
        return items;
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

        int numOfRows = db.delete("TODOS","ID=?", new String[]{String.valueOf(id)});

        if (numOfRows>0) {
            return true;
        }

        return false;
    }
}
