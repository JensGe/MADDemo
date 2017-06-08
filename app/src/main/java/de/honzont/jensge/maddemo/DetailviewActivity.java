package de.honzont.jensge.maddemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import de.honzont.jensge.maddemo.model.ToDo;

/**
 * Created by Jens on 26.04.2017.
 */

public class DetailviewActivity extends AppCompatActivity {

    public static final String TODO_ITEM = "toDoItem";
    public static final int RESULT_DELETE_ITEM = 10 ;
    protected static String logger = DetailviewActivity.class.getSimpleName();

    private TextView itemNameText;
    private Button saveItemButton;

    private ToDo item;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // select layout
        setContentView(R.layout.activity_detailview);

        // read out ui elements
        itemNameText = (TextView)findViewById(R.id.itemName);
        saveItemButton = (Button)findViewById(R.id.saveItem);

        // set content on ui elements
        setTitle(R.string.title_detailview);
        item = (ToDo)getIntent().getSerializableExtra(TODO_ITEM);
        if (item != null) {
            itemNameText.setText(item.getName());
        }

        // prepare for user interaction
        saveItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveItem();
            }
        });

    }

    private void saveItem() {
        Intent returnIntent = new Intent();
        String itemName = itemNameText.getText().toString();
        ToDo item = new ToDo(itemName);
        returnIntent.putExtra(TODO_ITEM, item);
        Log.i(logger,"Creating item " + item);
        setResult(Activity.RESULT_OK, returnIntent);
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
