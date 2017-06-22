package de.honzont.jensge.maddemo;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.honzont.jensge.maddemo.model.ToDo;

/**
 * Created by Jens on 26.04.2017.
 */


public class DetailviewActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TODO_ITEM = "toDoItem";
    public static final int RESULT_DELETE_ITEM = 10;
    public static final int RESULT_UPDATE_ITEM = 25;
    public static final int REQUEST_PICK_CONTACT = 1;
    protected static String logger = DetailviewActivity.class.getSimpleName();

    private TextView itemNameText, itemDescriptionText, contactList, itemDueDateDate, itemDueDateTime;
    private Button datePickButton, timePickButton, addContactButton;

    private String dateString, timeString, contactsString;
    private ToggleButton favouriteToggle, doneToggle;

    private Button saveItemButton;

    private boolean done, favourite;
    private int dateYear, dateMonth, dateDay, timeHour, timeMinute;
    private ToDo item;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // select layout
        setContentView(R.layout.activity_detailview);

        // read out ui elements
        itemNameText = (TextView) findViewById(R.id.itemName);
        itemDescriptionText = (TextView) findViewById(R.id.itemDescription);
        contactList = (TextView) findViewById(R.id.contactList);

        itemDueDateDate = (TextView) findViewById(R.id.itemDueDate);
        itemDueDateTime = (TextView) findViewById(R.id.itemDueTime);

        datePickButton = (Button) findViewById(R.id.date_pick_button);
        timePickButton = (Button) findViewById(R.id.time_pick_button);
        addContactButton = (Button) findViewById(R.id.addContactButton);

        favouriteToggle = (ToggleButton) findViewById(R.id.toggle_favButton);
        doneToggle = (ToggleButton) findViewById(R.id.toggle_doneButton);

        saveItemButton = (Button) findViewById(R.id.saveItem);

        // set content on ui elements
        setTitle(R.string.title_detailview);
        item = (ToDo) getIntent().getSerializableExtra(TODO_ITEM);
        if (item != null) {
            itemNameText.setText(item.getName());
            itemDescriptionText.setText(item.getDescription());
            dateString = new SimpleDateFormat("dd. MM. yyyy", Locale.GERMANY).format(new Date(item.getDueDate()));
            timeString = new SimpleDateFormat("HH:mm", Locale.GERMANY).format(new Date(item.getDueDate()));
            itemDueDateDate.setText(dateString);
            itemDueDateTime.setText(timeString);
            favouriteToggle.setChecked(item.isFavourite());
            doneToggle.setChecked(item.isDone());
//            contactsString = item.getContacts();
            contactList.setText(item.getContacts());
        }

        // prepare for user interaction
        saveItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveItem();
            }
        });

        datePickButton.setOnClickListener(this);
        itemDueDateDate.setOnClickListener(this);
        timePickButton.setOnClickListener(this);
        itemDueDateTime.setOnClickListener(this);

        addContactButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == datePickButton || v == itemDueDateDate) {
            if (itemDueDateDate.getText().length() == 12) {
                dateYear = Integer.parseInt((String) itemDueDateDate.getText().subSequence(8, 12));
                dateMonth = Integer.parseInt((String) itemDueDateDate.getText().subSequence(4, 6)) - 1;
                dateDay = Integer.parseInt((String) itemDueDateDate.getText().subSequence(0, 2));
            } else if (itemDueDateDate.getText().length() == 0) {
                final Calendar c = Calendar.getInstance();
                dateYear = c.get(Calendar.YEAR);
                dateMonth = c.get(Calendar.MONTH);
                dateDay = c.get(Calendar.DAY_OF_MONTH);
            }


            DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    String m = "";
                    String d = "";
                    if (monthOfYear + 1 < 10) m = "0";
                    if (dayOfMonth < 10) d = "0";
                    itemDueDateDate.setText(d + dayOfMonth + ". " + m + (monthOfYear + 1) + ". " + year);
                    if (itemDueDateTime.getText().length() == 0) {
                        itemDueDateTime.setText("00:00");
                    }
                }
            }, dateYear, dateMonth, dateDay);
            dpd.show();
        }

        else if (v == timePickButton || v == itemDueDateTime) {
            final Calendar c = Calendar.getInstance();
            timeHour = c.get(Calendar.HOUR_OF_DAY);
            timeMinute = c.get(Calendar.MINUTE);

            dateYear = c.get(Calendar.YEAR);
            dateMonth = c.get(Calendar.MONTH) + 1;
            dateDay = c.get(Calendar.DAY_OF_MONTH);

            TimePickerDialog tpd = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    String m = "";
                    String h = "";
                    if (minute < 10) m = "0";
                    if (hourOfDay < 10) h = "0";
                    itemDueDateTime.setText(h + hourOfDay + ":" + m + minute);
                    if (itemDueDateDate.getText().length() == 0) {
                        itemDueDateDate.setText(dateDay + ". " + dateMonth + ". " + dateYear);
                    }
                }
            }, timeHour, timeMinute, true);
            tpd.show();

        }

        else if (v == addContactButton) {
            addContact();
        }
    }

    private void saveItem() {
        Intent returnIntent = new Intent();
        String itemName = itemNameText.getText().toString();
        String itemDescription = itemDescriptionText.getText().toString();
        SimpleDateFormat f = new SimpleDateFormat("dd. MM. yyyy HH:mm", Locale.GERMANY);
        long itemDueDate = 0;
        try {
            Date d = f.parse(itemDueDateDate.getText().toString() + " " + itemDueDateTime.getText().toString());
            itemDueDate = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        favourite = favouriteToggle.isChecked();
        done = doneToggle.isChecked();
        ToDo item = new ToDo(itemName, itemDescription, itemDueDate, favourite, done);
        returnIntent.putExtra(TODO_ITEM, item);
        Log.i(logger, "Creating item " + item);
        setResult(Activity.RESULT_OK, returnIntent);
/*        Log.i(logger,"Updating item " + item);
        setResult(RESULT_UPDATE_ITEM, returnIntent);*/
        Log.i(logger, "returnintent: " + returnIntent);
        finish();
    }

    private void deleteItem() {
        // ToDo Abfrage ob wirklich gelöscht werden soll

        Intent returnIntent = new Intent();
        returnIntent.putExtra(TODO_ITEM, item);
        //Hier reicht die Übergabe der ID
        setResult(RESULT_DELETE_ITEM, returnIntent);
        Log.i("DetailviewActivity", "finishing");
        finish();

    }

    private void updateItem() {
        long itemId = item.getId();
        Intent returnIntent = new Intent();
        String itemName = itemNameText.getText().toString();
        String itemDescription = itemDescriptionText.getText().toString();
        SimpleDateFormat f = new SimpleDateFormat("dd. MM. yyyy HH:mm", Locale.GERMANY);
        long itemDueDate = 0;
        try {
            Date d = f.parse(itemDueDateDate.getText().toString() + " " + itemDueDateTime.getText().toString());
            itemDueDate = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        boolean favourite = favouriteToggle.isChecked();
        done = doneToggle.isChecked();
        ToDo item = new ToDo(itemId, itemName, itemDescription, itemDueDate, favourite, done);
        returnIntent.putExtra(TODO_ITEM, item);
        Log.i(logger, "Updating item " + item);
        setResult(RESULT_UPDATE_ITEM, returnIntent);
        Log.i(logger, "returnintent: " + returnIntent);
        finish();

    }

    private void addContact() {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(pickContactIntent, REQUEST_PICK_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PICK_CONTACT && resultCode == RESULT_OK) {
            Log.i(logger, "got data from contact picker: " + data);
            processSelectedContact(data.getData());
        }
    }

    private void processSelectedContact(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToNext();
        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        Log.i(logger, "Contact Name: " + name);

        long contactId = cursor.getLong(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
        Log.i(logger, "Contact ID: " + contactId);
        contactList.setText(contactList.getText() + String.valueOf(uri)+ ", ");

//        Cursor phoneCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{String.valueOf(contactId)}, null);
//        if (phoneCursor.getCount() > 0) {
//            phoneCursor.moveToFirst();
//            do {
//                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                int phoneNumberType = phoneCursor.getInt(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA2));
//
//                Log.i(logger, "Got Phone Number: " + phoneNumber + " of Type: " + phoneNumberType);
//
//                if (phoneNumberType == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
//                    Log.i(logger, "Mobile Number found: " + phoneNumber);
//                    break;
//                }
//
//            } while (phoneCursor.moveToNext());
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (item == null) {
            getMenuInflater().inflate(R.menu.options_detailview_new, menu);
            return super.onCreateOptionsMenu(menu);
        } else {
            getMenuInflater().inflate(R.menu.options_detailview_update, menu);
            return super.onCreateOptionsMenu(menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saveItem:
                saveItem();
                return true;
            case R.id.deleteItem:
                deleteItem();
                return true;
            case R.id.updateItem:
                updateItem();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
