package de.honzont.jensge.maddemo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import java.util.Calendar;

import de.honzont.jensge.maddemo.model.ToDo;

/**
 * Created by Jens on 26.04.2017.
 */


public class DetailviewActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TODO_ITEM = "toDoItem";
    public static final int RESULT_DELETE_ITEM = 10 ;
    public static final int RESULT_UPDATE_ITEM = 25;
    protected static String logger = DetailviewActivity.class.getSimpleName();

    private TextView itemNameText, itemDescriptionText;
    private Long dateTime;

    private TextView itemDueDateDate, itemDueTime;
    private Button datePickButton, timePickButton;

    private ToggleButton favouriteToggle, doneToggle;

    private Button saveItemButton;

    private int  dateYear, dateMonth, dateDay, timeHour, timeMinute;
    private ToDo item;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // select layout
        setContentView(R.layout.activity_detailview);

        // read out ui elements
        itemNameText = (TextView)findViewById(R.id.itemName);
        itemDescriptionText = (TextView)findViewById(R.id.itemDescription);

        itemDueDateDate = (TextView)findViewById(R.id.itemDueDate);
        itemDueTime = (TextView)findViewById(R.id.itemDueTime);

        datePickButton = (Button) findViewById(R.id.date_pick_button);
        timePickButton = (Button) findViewById(R.id.time_pick_button);

        favouriteToggle = (ToggleButton)findViewById(R.id.toggle_favButton);
        doneToggle = (ToggleButton)findViewById(R.id.toggle_doneButton);

        saveItemButton = (Button)findViewById(R.id.saveItem);

        // set content on ui elements
        setTitle(R.string.title_detailview);
        item = (ToDo)getIntent().getSerializableExtra(TODO_ITEM);
        if (item != null) {
            itemNameText.setText(item.getName());
            itemDescriptionText.setText(item.getDescription());
            /* TODO Convert item.getDueDate to DateString and TimeString */
            itemDueDateDate.setText(String.valueOf(item.getDueDate()));
            favouriteToggle.setChecked(item.isFavourite());
            doneToggle.setChecked(item.isDone());
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
        itemDueTime.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == datePickButton || v == itemDueDateDate) {
            final Calendar c = Calendar.getInstance();
            dateYear = c.get(Calendar.YEAR);
            dateMonth = c.get(Calendar.MONTH);
            dateDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    String m = "";
                    String d = "";
                    if (monthOfYear +1 < 10) m = "0";
                    if (dayOfMonth < 10) d = "0";
                    itemDueDateDate.setText(d + dayOfMonth + ". " + m + (monthOfYear + 1) + ". " + year);
                }
            }, dateYear, dateMonth, dateDay);
            dpd.show();
        }

        if (v == timePickButton || v == itemDueTime) {
            final Calendar c = Calendar.getInstance();
            timeHour = c.get(Calendar.HOUR_OF_DAY);
            timeMinute = c.get(Calendar.MINUTE);

            TimePickerDialog tpd = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    String m = "";
                    String h = "";
                    if (minute < 10) m = "0";
                    if (hourOfDay < 10) h = "0";
                    itemDueTime.setText(h + hourOfDay + ":" + m + minute);
                }
            }, timeHour,timeMinute, true);
            tpd.show();
        }
    }

    private void saveItem() {
        Intent returnIntent = new Intent();
        String itemName = itemNameText.getText().toString();
        String itemDescription = itemDescriptionText.getText().toString();
        Long itemDueDate = Long.valueOf(itemDueDateDate.getText().toString());
        boolean favourite = favouriteToggle.isChecked();
        boolean done = doneToggle.isChecked();
        ToDo item = new ToDo(itemName, itemDescription, itemDueDate, favourite, done);
        returnIntent.putExtra(TODO_ITEM, item);
        Log.i(logger,"Creating item " + item);
        setResult(Activity.RESULT_OK, returnIntent);
/*        Log.i(logger,"Updating item " + item);
        setResult(RESULT_UPDATE_ITEM, returnIntent);*/
        Log.i(logger,"returnintent: " + returnIntent);
        finish();
    }

    private void deleteItem() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(TODO_ITEM, item);
        //Hier reicht die Übergabe der ID
        setResult(RESULT_DELETE_ITEM,returnIntent);
        Log.i("DetailviewActivity","finishing");
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_overview, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.saveItem) {
            saveItem();
            return true;
        }
        else if (item.getItemId() == R.id.deleteItem) {
            deleteItem();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
