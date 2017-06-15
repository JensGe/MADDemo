package de.honzont.jensge.maddemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import de.honzont.jensge.maddemo.model.IToDoCRUDOperationsASync;
import de.honzont.jensge.maddemo.model.ToDo;

import static de.honzont.jensge.maddemo.DetailviewActivity.TODO_ITEM;

public class OverviewActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int EDIT_ITEM = 2;
    public static final int CREATE_ITEM = 1;
    protected static String logger = OverviewActivity.class.getSimpleName();

    private TextView helloText;
    private ViewGroup listView;
    private View addItemAction;
    private ArrayAdapter<ToDo> listViewAdapter;

    private ProgressDialog progressDialog;
    private IToDoCRUDOperationsASync crudOperations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. select the view to be controlled
        setContentView(R.layout.activity_overview);

        // 2. read out elements from the view
        helloText = (TextView) findViewById(R.id.helloText);
        Log.i(logger, "HelloText: " + helloText);
        listView = (ViewGroup) findViewById(R.id.listView);
        Log.i(logger, "listView: " + listView);
        addItemAction = findViewById(R.id.addItemAction);

        progressDialog = new ProgressDialog(this);

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

        // instantiate listview with adapter
        listViewAdapter = new ArrayAdapter<ToDo>(this, R.layout.itemview_overview) {
            @NonNull
            @Override
            public View getView(int position, View itemView, ViewGroup parent) {

                if (itemView != null) {
                    Log.i(logger, "reusing existing itemView for element at position: " + position);
                } else {
                    Log.i(logger, "creating new itemView for element at position: " + position);
                    // create a new instance of list item view
                    itemView = getLayoutInflater().inflate(R.layout.itemview_overview, null);
                    // read out the text view for the item name
                    TextView itemNameView = (TextView) itemView.findViewById(R.id.itemName);
                    // create a new instance of the view holder
                    ItemViewHolder itemViewHolder = new ItemViewHolder();
                    //set the itemNameView attribut on view holder to text view
                    itemViewHolder.itemNameView = itemNameView;
                    // set thte view holder on the list item view
                    itemView.setTag(itemViewHolder);
                }

                ItemViewHolder viewHolder = (ItemViewHolder) itemView.getTag();
                ToDo item = getItem(position);
                Log.i(logger,"creating view for position " + position + " and item " + item);

                viewHolder.itemNameView.setText(item.getName());

                return itemView;
            }


        };
        ((ListView) listView).setAdapter(listViewAdapter);
        listViewAdapter.setNotifyOnChange(true);

        ((ListView) listView).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToDo selectedItem = listViewAdapter.getItem(position);
                showDetailviewForItemName(selectedItem);
            }
        });

        crudOperations = ((ToDoApplication)getApplication()).getCRUDOperationsImpl();

        readItemsAndFillListView();
    }

    private void readItemsAndFillListView() {

        progressDialog.show();

        crudOperations.readAllToDos(new IToDoCRUDOperationsASync.CallbackFunction<List<ToDo>>() {
            @Override
            public void process(List<ToDo> result) {
                progressDialog.hide();
                for (ToDo item : result) {
                    addItemToListView(item);
                }

            }
    });

}

    private void createAndShowItem(/*final*/ ToDo item) {

        progressDialog.show();

        crudOperations.createToDo(item, new IToDoCRUDOperationsASync.CallbackFunction<ToDo>() {
            @Override
            public void process(ToDo result) {
                addItemToListView(result);
                progressDialog.hide();
            }
        });

    }

    private void addItemToListView(ToDo item) {

        listViewAdapter.add(item);

    }

    private void showDetailviewForItemName(ToDo item) {
        Intent detailviewIntent = new Intent(this, DetailviewActivity.class);
        detailviewIntent.putExtra(TODO_ITEM, item);

        startActivityForResult(detailviewIntent, EDIT_ITEM);
    }

    private void addNewItem() {
        Intent addNewItemIntent = new Intent(this, DetailviewActivity.class);

        startActivityForResult(addNewItemIntent, CREATE_ITEM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i(logger,"onActivityResult(): " + requestCode + ", " + resultCode + ": " + data);


        if (requestCode == CREATE_ITEM && resultCode == Activity.RESULT_OK) {
            ToDo item = (ToDo) data.getSerializableExtra(TODO_ITEM);
            createAndShowItem(item);
        }
        else if (requestCode == EDIT_ITEM) {
            if (resultCode == DetailviewActivity.RESULT_DELETE_ITEM) {
                ToDo item = (ToDo) data.getSerializableExtra(TODO_ITEM);
                deleteAndRemoveItem(item);
            }
        }
    }

    private void deleteAndRemoveItem(final ToDo item) {

        crudOperations.deleteToDo(item.getId(), new IToDoCRUDOperationsASync.CallbackFunction<Boolean>() {
            @Override
            public void process(Boolean deleted) {
                if (deleted) {
                    listViewAdapter.remove(findDataItemInList(item.getId()));
                }

                }
            });

    }

    private ToDo findDataItemInList(long id) {
        for (int i=0;i<listViewAdapter.getCount();i++) {
            if (listViewAdapter.getItem(i).getId() == id) {
                return listViewAdapter.getItem(i);
            }
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        if (v == helloText) {
            Log.i(logger, "onClick(): " + v);
        } else {
            Log.i(logger, "onClick() on unknown element: " + v);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /****/
    }

    private class ItemViewHolder {
        public TextView itemNameView;
    }
}
