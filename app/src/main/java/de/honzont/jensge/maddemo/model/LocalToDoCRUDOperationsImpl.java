package de.honzont.jensge.maddemo.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Gäbeler on 25.05.2017.
 */

public class LocalToDoCRUDOperationsImpl implements IToDoCRUDOperations {

    protected static String logger = LocalToDoCRUDOperationsImpl.class.getSimpleName();

    private SQLiteDatabase db;

    public LocalToDoCRUDOperationsImpl(Context context) {

        db = context.openOrCreateDatabase("mydb.sqlite", Context.MODE_PRIVATE, null);
//        db.setVersion(0);
//        Log.i(logger, "DB Version: " + db.getVersion());
//        Log.i(logger, "DELETE TODOS");
//        db.execSQL("DELETE FROM TODOS");
//        Log.i(logger, "DROP TODOS");
//        db.execSQL("DROP TABLE TODOS");

        if (db.getVersion() == 0) {
            db.setVersion(1);
            db.execSQL("CREATE TABLE TODOS (ID INTEGER PRIMARY KEY, NAME TEXT, DUEDATE INTEGER, DESCRIPTION TEXT, DONE INTEGER, FAVOURITE INTEGER, CONTACTS TEXT)");
        }

    }

    @Override
    public ToDo createToDo(ToDo item) {
        Log.i(logger, "Got item: " + item);
        ContentValues values = new ContentValues();
        values.put("NAME", item.getName());
        values.put("DUEDATE", item.getDueDate());
        values.put("DESCRIPTION", item.getDescription());
        values.put("DONE", item.isDone());
        values.put("FAVOURITE", item.isFavourite());
        String contacts = null;
        if (item.getContacts() != null && !item.getContacts().isEmpty()) {
            for (int index = 0; index < item.getContacts().size(); index++) {
                contacts = item.getContacts().get(index) + ", ";
            }
            Log.i(logger, "contactstring: " + contacts);
            if (contacts.length() > 3) {
                contacts = contacts.substring(0, contacts.length() - 2);
            }
        }
        values.put("CONTACTS", contacts);
        long id = db.insert("TODOS", null, values);
        item.setId(id);
        Log.i(logger, "created ToDo: " + item);
        return item;
    }

    @Override
    public List<ToDo> readAllToDos() {
        List<ToDo> items = new ArrayList<ToDo>();

        Cursor cursor = db.query("TODOS", new String[]{"ID", "NAME", "DUEDATE", "DESCRIPTION", "DONE", "FAVOURITE", "CONTACTS"}, null, null, null, null, "NAME");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            boolean next = false;
            do {
                ToDo item = new ToDo();
                items.add(item);

                item.setId(cursor.getLong(cursor.getColumnIndex("ID")));
                item.setName(cursor.getString(cursor.getColumnIndex("NAME")));
                item.setDueDate(cursor.getLong(cursor.getColumnIndex("DUEDATE")));
                item.setDescription(cursor.getString(cursor.getColumnIndex("DESCRIPTION")));
                item.setDone(cursor.getInt(cursor.getColumnIndex("DONE")) == 1);
                item.setFavourite(cursor.getInt(cursor.getColumnIndex("FAVOURITE")) == 1);
                String contactstring = cursor.getString(cursor.getColumnIndex("CONTACTS"));
                if (contactstring != null) {
                    ArrayList<String> contactArray = new ArrayList<String>(Arrays.asList(contactstring.split(", ")));
                    item.setContacts(contactArray);
                }
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
        ContentValues values = new ContentValues();
        values.put("NAME", item.getName());
        values.put("DUEDATE", item.getDueDate());
        values.put("DESCRIPTION", item.getDescription());
        values.put("DONE", item.isDone());
        values.put("FAVOURITE", item.isFavourite());
        String contacts = null;
        if (item.getContacts() != null && !item.getContacts().isEmpty()) {
            for (int index = 0; index < item.getContacts().size(); index++) {
                contacts = item.getContacts().get(index) + ", ";
            }
            Log.i(logger, "contactstring: " + contacts);
            if (contacts.length() > 3) {
                contacts = contacts.substring(0, contacts.length() - 2);
            }

        }
        values.put("CONTACTS", contacts);
        db.update("TODOS", values, "ID=?", new String[]{String.valueOf(id)});
        return item;
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
