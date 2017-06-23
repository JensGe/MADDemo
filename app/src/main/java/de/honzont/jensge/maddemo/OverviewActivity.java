package de.honzont.jensge.maddemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.honzont.jensge.maddemo.model.IToDoCRUDOperationsASync;
import de.honzont.jensge.maddemo.model.LocalToDoCRUDOperationsImpl;
import de.honzont.jensge.maddemo.model.ToDo;

import static de.honzont.jensge.maddemo.DetailviewActivity.TODO_ITEM;
import static de.honzont.jensge.maddemo.LoginviewActivity.SERVCON;

public class OverviewActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int EDIT_ITEM = 2;
    public static final int CREATE_ITEM = 1;
    protected static String logger = OverviewActivity.class.getSimpleName();

    private TextView helloText;
    private ViewGroup listView;
    private View addItemAction;
    private ArrayAdapter<ToDo> listViewAdapter;
    private List<ToDo> itemsList = new ArrayList<ToDo>();

    private boolean serverConnection;

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
        serverConnection = getIntent().getExtras().getBoolean(SERVCON);
        if (!serverConnection) {
            Toast.makeText(getApplicationContext(), "No Server Connection available. \nrunning locally", Toast.LENGTH_LONG).show();
        }
        else if (serverConnection) {
            Toast.makeText(getApplicationContext(), "Connection and Login accepted, \nrunning remotely", Toast.LENGTH_LONG).show();
        }

        // 4. set listeners to allow user interactions
        helloText.setOnClickListener(this);

        addItemAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewItem();
            }
        });

        // instantiate listview with adapter
        listViewAdapter = new ArrayAdapter<ToDo>(this, R.layout.itemview_overview, itemsList) {
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
                    TextView itemDescriptionView = (TextView) itemView.findViewById(R.id.itemDescription);
                    CheckBox itemDoneView = (CheckBox) itemView.findViewById(R.id.itemDone);
                    TextView itemDueDateView = (TextView) itemView.findViewById(R.id.itemDueDate);

                    // create a new instance of the view holder
                    ItemViewHolder itemViewHolder = new ItemViewHolder();
                    //set the itemNameView attribut on view holder to text view
                    itemViewHolder.itemNameView = itemNameView;
                    itemViewHolder.itemDescriptionView = itemDescriptionView;
                    itemViewHolder.itemDoneView = itemDoneView;
                    itemViewHolder.itemDueDateView = itemDueDateView;

                    // set the view holder on the list item view
                    itemView.setTag(itemViewHolder);
                }

                ItemViewHolder viewHolder = (ItemViewHolder) itemView.getTag();
                final ToDo item = getItem(position);
                Log.i(logger, "creating view for position " + position + " and item " + item);

                String descriptionSubstring;
                if (item.getDescription().length() > 20) {
                    descriptionSubstring = item.getDescription().substring(0, 17) + "...";
                } else {
                    descriptionSubstring = item.getDescription();
                }

                viewHolder.itemNameView.setText(item.getName());
                viewHolder.itemDescriptionView.setText(descriptionSubstring);
                viewHolder.itemDoneView.setChecked(item.isDone());
                viewHolder.itemDueDateView.setText(new SimpleDateFormat("dd. MM. yyyy", Locale.GERMANY).format(new Date(item.getDueDate())));

                viewHolder.itemDoneView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        item.setDone(isChecked);
                        Log.i(logger, "Check set: " + isChecked);
                    }
                });

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

        crudOperations = ((ToDoApplication) getApplication()).getCRUDOperationsImpl();

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
                Log.i(logger, "items: " + itemsList);
                sortByDone();
            }
        });
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

        Log.i(logger, "onActivityResult(): " + requestCode + ", " + resultCode + ": " + data);


        if (requestCode == CREATE_ITEM && resultCode == Activity.RESULT_OK) {
            ToDo item = (ToDo) data.getSerializableExtra(TODO_ITEM);
            createAndShowItem(item);
        } else if (requestCode == EDIT_ITEM) {
            if (resultCode == DetailviewActivity.RESULT_DELETE_ITEM) {
                ToDo item = (ToDo) data.getSerializableExtra(TODO_ITEM);
                deleteAndRemoveItem(item);
            } else if (resultCode == DetailviewActivity.RESULT_UPDATE_ITEM) {
                ToDo item = (ToDo) data.getSerializableExtra(TODO_ITEM);
                updateItem(item);
            }
        }
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

    private void updateItem(final ToDo item) {

        progressDialog.show();

        crudOperations.updateToDo(item.getId(), item, new IToDoCRUDOperationsASync.CallbackFunction<ToDo>() {
            @Override
            public void process(ToDo result) {
                listViewAdapter.remove(findDataItemInList(item.getId()));
                listViewAdapter.add(item);
                progressDialog.hide();

            }
        });
    }


    private ToDo findDataItemInList(long id) {
        for (int i = 0; i < listViewAdapter.getCount(); i++) {
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
        progressDialog.dismiss();
    }

    private class ItemViewHolder {
        public TextView itemNameView;
        public TextView itemDescriptionView;
        public CheckBox itemDoneView;
        public TextView itemDueDateView;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_overview, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sortByNames:
                sortByNameDone();
                return true;
            case R.id.sortByDateNFav:
                sortByDateFavDone();
                return true;
            case R.id.sortByFavNDate:
                sortByFavDateDone();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Do you want to Quit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.exit(0);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void sortByFavDateDone() {
        sortByDate();
        sortByFav();
        sortByDone();
        this.listViewAdapter.notifyDataSetChanged();
            }

    private void sortByDateFavDone() {
        sortByFav();
        sortByDate();
        sortByDone();
        this.listViewAdapter.notifyDataSetChanged();
            }

    private void sortByNameDone() {
        sortByName();
        sortByDone();
        this.listViewAdapter.notifyDataSetChanged();
    }

    private void sortByDone() {
        Collections.sort(itemsList, new Comparator<ToDo>() {
            @Override
            public int compare(ToDo o1, ToDo o2) {
                return Boolean.compare(o2.isDone(), o1.isDone());
            }
        });
    }

    private void sortByFav() {
        Collections.sort(itemsList, new Comparator<ToDo>() {
            @Override
            public int compare(ToDo o1, ToDo o2) {
                return Boolean.compare(o2.isFavourite(), o1.isFavourite());
            }
        });
    }

    private void sortByDate() {
        Collections.sort(itemsList, new Comparator<ToDo>() {
            @Override
            public int compare(ToDo o1, ToDo o2) {
                return Long.compare(o1.getDueDate(), o2.getDueDate());
            }
        });
    }

    private void sortByName() {
        Collections.sort(itemsList, new Comparator<ToDo>() {
            @Override
            public int compare(ToDo o1, ToDo o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }

}

