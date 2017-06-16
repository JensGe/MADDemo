package de.honzont.jensge.maddemo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
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
import android.widget.ToggleButton;

import java.util.Calendar;

import de.honzont.jensge.maddemo.model.ToDo;

/**
 * Created by Jens on 26.04.2017.
 */


public class DetailviewActivity extends AppCompatActivity {

    public static final String TODO_ITEM = "toDoItem";
    public static final int RESULT_DELETE_ITEM = 10 ;
    public static final int RESULT_UPDATE_ITEM = 25;
    protected static String logger = DetailviewActivity.class.getSimpleName();

    private TextView itemNameText;
    private TextView itemDescriptionText;
    private ToggleButton favouriteToggle;
    private ToggleButton doneToggle;
    private Long dateTime;
    private TextView itemDueDateDate;
    private TextView itemDueTime;
    private Button datePickButton;
    private Button timePickButton;
    private Button saveItemButton;

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

        datePickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(logger, "DatePicker Clicked");
                // Show Dialog DateDialogID
                DialogFragment newDateFragment = new DatePickerFragment();
                newDateFragment.show(getFragmentManager(), "datepicker");
            }
        });

        timePickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(logger, "TimePicker Clicked");
                // Show Dialog DateDialogID
                DialogFragment newTimeFragment = new TimePickerFragment();
                newTimeFragment.show(getFragmentManager(), "timepicker");
            }
        });

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

    public class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        public EditText editText;
        DatePicker dpResult;

        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            itemDueDateDate.setText(String.valueOf(day) + ". " + String.valueOf(month+1) + ". " + String.valueOf(year));
        }
    }

    public class TimePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        public EditText editText;
        DatePicker dpResult;

        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            itemDueDateDate.setText(String.valueOf(day) + ". " + String.valueOf(month+1) + ". " + String.valueOf(year));
        }
    }
}
