package de.honzont.jensge.maddemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Jens on 26.04.2017.
 */

public class DetailviewActivity extends AppCompatActivity {

    public static final String TODO_ITEM = "toDoItem";
    private TextView itemNameText;
    private Button saveItemButton;

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
        ToDo item = (ToDo)getIntent().getSerializableExtra(TODO_ITEM);
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

        setResult(Activity.RESULT_OK, returnIntent);
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

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
