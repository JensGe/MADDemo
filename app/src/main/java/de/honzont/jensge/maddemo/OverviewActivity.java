package de.honzont.jensge.maddemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import de.honzont.jensge.maddemo.model.IToDoCRUDItemOperations;
import de.honzont.jensge.maddemo.model.LocalToDoCRUDOperations;
import de.honzont.jensge.maddemo.model.SimpleToDoCRUDOperationsImpl;
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
    private IToDoCRUDItemOperations crudOperations;

    private class ItemViewHolder {
        public TextView itemNameView;
    }


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
                //Log.i(logger,"creating view for position " + position + " and item " + item);

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
        crudOperations = /*new SimpleToDoCRUDOperationsImpl()*/ new LocalToDoCRUDOperations(this);

        readItemsAndFillListView();
    }

    private void readItemsAndFillListView() {


//        List<ToDo> items = crudOperations.readAllToDos();
//
//        for (ToDo item : items) {
//            addItemToListView(item);
//        }

        new AsyncTask<Void, Void, List<ToDo>>() {

            @Override
            protected void onPreExecute() {
                progressDialog.setMessage("Loading ToDo Items");
                progressDialog.show();
            }

            @Override
            protected List<ToDo> doInBackground(Void... params) {
                return crudOperations.readAllToDos();
            }

            @Override
            protected void onPostExecute(List<ToDo> toDos) {
                progressDialog.hide();
                for (ToDo item : toDos) {
                    addItemToListView(item);
                }
            }
        }.execute();


    }

    private void createAndShowItem(/*final*/ ToDo item) {

       /* progressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                ToDo createdItem = crudOperations.createToDo(item);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addItemToListView(item);
                        progressDialog.hide();
                    }
                });


            }
        }).start();*/

        new AsyncTask<ToDo, Void, ToDo>() {

            @Override
            protected void onPreExecute() {
                progressDialog.setMessage("Creating ToDo Item");
                progressDialog.show();
            }

            @Override
            protected ToDo doInBackground(ToDo... params) {
                ToDo createdItem = crudOperations.createToDo(params[0]);
                return createdItem;
            }

            @Override
            protected void onPostExecute(ToDo item) {
                addItemToListView(item);
                progressDialog.hide();
            }
        }.execute(item);

    }

    private void addItemToListView(ToDo item) {

        listViewAdapter.add(item);
/*        View listItemView = getLayoutInflater().inflate(R.layout.itemview_overview, null);
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

        listView.addView(listItemView); */
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

    private void deleteAndRemoveItem(ToDo item) {

        boolean deleted = crudOperations.deleteToDo(item.getId());
        if (deleted) {
            listViewAdapter.remove(findDataItemInList(item.getId()));
        }
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
}
