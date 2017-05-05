package de.honzont.jensge.maddemo;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import static de.honzont.jensge.maddemo.DetailviewActivity.TODO_ITEM;

public class OverviewActivity extends AppCompatActivity implements View.OnClickListener{

    protected static String logger = OverviewActivity.class.getSimpleName();

    private TextView helloText;
    private ViewGroup listView;
    private View addItemAction;

    private List<ToDo> items = Arrays.asList(new ToDo[]{new ToDo("lorem"), new ToDo("ipsum"), new ToDo("dolor"), new ToDo("sit"), new ToDo("amet"), new ToDo("adispicsing"), new ToDo("elit")});

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. select the view to be controlled
        setContentView(R.layout.activity_overview);

        // 2. read out elements from the view
        helloText = (TextView)findViewById(R.id.helloText);
        Log.i(logger,"HelloText: " + helloText);
        listView = (ViewGroup)findViewById(R.id.listView);
        Log.i(logger,"listView: " + listView);
        addItemAction = findViewById(R.id.addItemAction);


        // 3. set content on the elements
        setTitle(R.string.title_overview);
        helloText.setText(R.string.app_content);

        // 4. set listeners to allow user interactions
        helloText.setOnClickListener(this);

        addItemAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewItem();
            }
        });

        readItemsAndFillListView();
    }

    private void readItemsAndFillListView() {

        for (ToDo item : items) {
            addItemToListView(item);
        }
    }

    private void addItemToListView(ToDo item) {

        View listItemView = getLayoutInflater().inflate(R.layout.itemview_overview, null);
        TextView itemNameView = (TextView)listItemView.findViewById(R.id.itemName);

        listItemView.setTag(item);
        itemNameView.setText(item.getName());

        listItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToDo item = (ToDo) v.getTag();
                showDetailviewForItemName(item);

            }
        });

        listView.addView(listItemView);
    }

    private void showDetailviewForItemName(ToDo item) {
        Intent detailviewIntent = new Intent(this, DetailviewActivity.class);
        detailviewIntent.putExtra(TODO_ITEM, item);

        startActivity(detailviewIntent);
    }

    private void addNewItem() {
        Intent addNewItemIntent = new Intent(this, DetailviewActivity.class);

        startActivityForResult(addNewItemIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            ToDo item = (ToDo)data.getSerializableExtra(TODO_ITEM);
            addItemToListView(item);
        }
    }

    @Override
    public void onClick(View v) {
        if (v== helloText) {
        Log.i(logger,"onClick(): " + v);
    }
    else {
            Log.i(logger,"onClick() on unknown element: " + v);
        }
        }
}
