package de.honzont.jensge.maddemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Jens on 26.04.2017.
 */

public class DetailviewActivity extends AppCompatActivity {

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
        String itemName = getIntent().getStringExtra("itemName");
        if (itemName != null) {
            itemNameText.setText(itemName);
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
        returnIntent.putExtra("itemName", itemName);

        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
