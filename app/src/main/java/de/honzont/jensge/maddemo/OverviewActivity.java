package de.honzont.jensge.maddemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.honzont.jensge.maddemo.model.IToDoCRUDOperations;
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
    private IToDoCRUDOperationsASync crudOperationsRemote;
    private IToDoCRUDOperations crudOperationsLocal;

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
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setMessage("Server not found, running Local Mode");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
//        else {
//            Toast.makeText(getApplicationContext(), "Remote Mode", Toast.LENGTH_LONG).show();
//        }

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
                    ToggleButton itemDoneToggle = (ToggleButton) itemView.findViewById(R.id.toggle_doneButton_ov);
                    ToggleButton itemFavToggle = (ToggleButton) itemView.findViewById(R.id.toggle_favButton_ov);

                    TextView itemDueDateView = (TextView) itemView.findViewById(R.id.itemDueDate);

                    // create a new instance of the view holder
                    ItemViewHolder itemViewHolder = new ItemViewHolder();

                    //set the itemNameView attribut on view holder to text view
                    itemViewHolder.itemNameView = itemNameView;
                    itemViewHolder.itemDescriptionView = itemDescriptionView;
                    itemViewHolder.itemDoneToggle = itemDoneToggle;
                    itemViewHolder.itemFavToggle = itemFavToggle;

                    itemViewHolder.itemDueDateView = itemDueDateView;

                    // set the view holder on the list item view
                    itemView.setTag(itemViewHolder);
                }

                ItemViewHolder viewHolder = (ItemViewHolder) itemView.getTag();
                final ToDo item = getItem(position);
                Log.i(logger, "creating view for position " + position + " and item " + item);

                String descriptionSubstring = "";

                try {
                    descriptionSubstring = item.getDescription();
                    if (descriptionSubstring.length() > 40) {
                        descriptionSubstring = item.getDescription().substring(0, 37) + " ...";
                    }
                    if (descriptionSubstring.contains("\n")) {
                        descriptionSubstring = descriptionSubstring.substring(0, descriptionSubstring.indexOf("\n")) + " ...";
                    }

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }


                viewHolder.itemNameView.setText(item.getName());
                viewHolder.itemDescriptionView.setText(descriptionSubstring);


                // set OnCheckedChangeListeners null to prevent Sortproblems
                viewHolder.itemDoneToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    }
                });
                viewHolder.itemFavToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    }
                });

                viewHolder.itemDoneToggle.setChecked(item.isDone());
                viewHolder.itemFavToggle.setChecked(item.isFavourite());
                if (item.getDueDate() != 0) {
                    viewHolder.itemDueDateView.setText(new SimpleDateFormat("dd. MM. yyyy", Locale.GERMANY).format(new Date(item.getDueDate())));
                }

                if (item.getDueDate() < System.currentTimeMillis() && item.getDueDate() != 0) {
                    viewHolder.itemDueDateView.setTextColor(Color.RED);
                } else {
                    viewHolder.itemDueDateView.setTextColor(Color.BLACK);

                }
                if (item.isDone()) {
                    viewHolder.itemNameView.setTypeface(null, Typeface.ITALIC);
                } else {
                    viewHolder.itemNameView.setTypeface(null, Typeface.NORMAL);
                }

                if (item.getDueDate() == 0) {
                    viewHolder.itemDueDateView.setText("");
                }


                viewHolder.itemDoneToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        item.setDone(isChecked);
                        Log.i(logger, "Check set: " + isChecked);
                        updateItem(item);
                    }
                });

                viewHolder.itemFavToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        item.setFavourite(isChecked);
                        Log.i(logger, "Favourite set: " + isChecked);
                        updateItem(item);
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

        crudOperationsRemote = ((ToDoApplication) getApplication()).getCRUDOperationsImpl();
        crudOperationsLocal = new LocalToDoCRUDOperationsImpl(this);


        syncAndStartOverview();


//        if (!serverConnection) {
//            readLocalItemsAndFillListView();
//        } else if (serverConnection) {
//            readItemsAndFillListView();
//        }



    }

    private void syncAndStartOverview() {
        if (serverConnection && crudOperationsLocal.readAllToDos().isEmpty()) {
            Log.i(logger, "Start Sync Remote To Local Todos");
            syncToDosRemoteToLocal();
            Log.i(logger, "End Sync Remote To Local Todos");
        }

        if (serverConnection && !crudOperationsLocal.readAllToDos().isEmpty()) {
            Log.i(logger, "Start Delete Remote Todos");
            deleteRemoteToDosAndSyncLocalToRemote();
            Log.i(logger, "End Delete Remote Todos");

        }

        if (!serverConnection) {
            readLocalItemsAndFillListView();
        }


    }

    private void deleteRemoteToDosAndSyncLocalToRemote() {
        progressDialog.setMessage("Read Remote Items and Delete them");
        progressDialog.show();
        crudOperationsRemote.readAllToDos(new IToDoCRUDOperationsASync.CallbackFunction<List<ToDo>>() {
            @Override
            public void process(List<ToDo> result) {
                Log.i(logger, "ToDo List to Delete: " + result.toString());
                for (final ToDo item : result) {
                    crudOperationsRemote.deleteToDo(item.getId(), new IToDoCRUDOperationsASync.CallbackFunction<Boolean>() {
                        @Override
                        public void process(Boolean result) {
                            Log.i(logger, "Deleted Todo" + item);
                        }
                    });
                }
                Log.i(logger, "Delete Remote Items Done");
                Log.i(logger, "Start Sync Local2Remote Todos");
                syncToDosLocalToRemote();
                Log.i(logger, "End Sync Local2Remote Todos");
            }
        });
        progressDialog.hide();
    }


    private void syncToDosRemoteToLocal() {
        progressDialog.setMessage("Read Remote Items and Fill Local DB");
        progressDialog.show();
        crudOperationsRemote.readAllToDos(new IToDoCRUDOperationsASync.CallbackFunction<List<ToDo>>() {
            @Override
            public void process(List<ToDo> result) {
                progressDialog.hide();
                for (ToDo item : result) {
                    crudOperationsLocal.createToDo(item);
                }
                Log.i(logger, "sync Todos Remote2Local Done");
                Log.i(logger, "Start Read Local Items and Fill List");
                readLocalItemsAndFillListView();
                Log.i(logger, "End Read Local Items and Fill List");
                }
        });

    }

    private void syncToDosLocalToRemote() {
        progressDialog.setMessage("Read Local Items and Fill Remote DB");
        progressDialog.show();
        final List<ToDo> localToDos = crudOperationsLocal.readAllToDos();
        for (final ToDo item : localToDos) {
            crudOperationsRemote.createToDo(item, new IToDoCRUDOperationsASync.CallbackFunction<ToDo>() {
                @Override
                public void process(ToDo result) {
                    Log.i(logger, "Item Created: " + item + result);
               }
            });
        }
        Log.i(logger, "Start Read Local Items and Fill List");
        readLocalItemsAndFillListView();
        Log.i(logger, "End Read Local Items and Fill List");
        progressDialog.hide();

        Log.i(logger, "Local2Remote Sync Done");
    }

    private void readLocalItemsAndFillListView() {
        progressDialog.setMessage("Read Local Items and Fill in List View");
        progressDialog.show();
        List<ToDo> localToDos = crudOperationsLocal.readAllToDos();
        for (ToDo item : localToDos) {
            addItemToListView(item);
        }
        Log.i(logger, "Local items: " + itemsList);
        progressDialog.hide();
        sortByNameDone();
        this.listViewAdapter.notifyDataSetChanged();
    }

//    private void readItemsAndFillListView() {
//        progressDialog.setMessage("Read Remote Items and Fill in List View");
//        progressDialog.show();
//        crudOperations.readAllToDos(new IToDoCRUDOperationsASync.CallbackFunction<List<ToDo>>() {
//            @Override
//            public void process(List<ToDo> result) {
//                progressDialog.hide();
//                for (ToDo item : result) {
//                    addItemToListView(item);
//                }
//                Log.i(logger, "items: " + itemsList);
//                sortByNameDone();
//            }
//        });
//    }


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
            Log.i(logger, "to Create: " + item.toString());
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
        progressDialog.setMessage("Create local ToDo");
        progressDialog.show();
        crudOperationsLocal.createToDo(item);
        addItemToListView(item);
        progressDialog.hide();
        Toast.makeText(getApplicationContext(), "Local ToDo created", Toast.LENGTH_SHORT).show();
        if (serverConnection) {
            progressDialog.setMessage("Create remote ToDo");
            progressDialog.show();
            crudOperationsRemote.createToDo(item, new IToDoCRUDOperationsASync.CallbackFunction<ToDo>() {
                @Override
                public void process(ToDo result) {
                    progressDialog.hide();
                    Toast.makeText(getApplicationContext(), "Remote ToDo created", Toast.LENGTH_SHORT).show();
                }
            });
        }



    }

    private void addItemToListView(ToDo item) {
        listViewAdapter.add(item);
        Log.i(logger, "Item Added to ListviewAdapter: " + item);
    }

    private void deleteAndRemoveItem(final ToDo item) {
        boolean deleted = crudOperationsLocal.deleteToDo(item.getId());
        if (deleted) {
            listViewAdapter.remove(findDataItemInList(item.getId()));
        }
        Toast.makeText(getApplicationContext(), "Local ToDo deleted", Toast.LENGTH_SHORT).show();
        if (serverConnection) {
            crudOperationsRemote.deleteToDo(item.getId(), new IToDoCRUDOperationsASync.CallbackFunction<Boolean>() {
                @Override
                public void process(Boolean deleted) {
                    if (deleted) {
//                        listViewAdapter.remove(findDataItemInList(item.getId()));
                    }
                }
            });
            Toast.makeText(getApplicationContext(), "Remote ToDo deleted", Toast.LENGTH_SHORT).show();
        }

    }

    private void updateItem(final ToDo item) {
        progressDialog.setMessage("Local ToDo updating");
        progressDialog.show();
        crudOperationsLocal.updateToDo(item.getId(), item);
        listViewAdapter.remove(findDataItemInList(item.getId()));
        listViewAdapter.add(item);
        progressDialog.hide();
//        sortByNameDone();
        Toast.makeText(getApplicationContext(), "Local ToDo updated", Toast.LENGTH_SHORT).show();
        if (serverConnection) {
            progressDialog.setMessage("Remote ToDo updating");
            progressDialog.show();
            crudOperationsRemote.updateToDo(item.getId(), item, new IToDoCRUDOperationsASync.CallbackFunction<ToDo>() {
                @Override
                public void process(ToDo result) {
//                    listViewAdapter.remove(findDataItemInList(item.getId()));
//                    listViewAdapter.add(item);
                    progressDialog.hide();
//                    sortByNameDone();
                    Toast.makeText(getApplicationContext(), "Remote ToDo updated", Toast.LENGTH_SHORT).show();
                }
            });
        }


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
        super.onDestroy();
        progressDialog.dismiss();
    }

    private class ItemViewHolder {
        public TextView itemNameView;
        public TextView itemDescriptionView;
        public ToggleButton itemDoneToggle;
        public ToggleButton itemFavToggle;
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
                return Boolean.compare(o1.isDone(), o2.isDone());
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

