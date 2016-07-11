package com.example.light.todolist;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements EditTodoFragment.EditTodoFragmentListener{
    List<Todo> items;
    ArrayAdapter<Todo> itemAdapter;
    ListView lvItems;
    private PostsDatabaseHelper postsDatabaseHelper;
    private static Context context;
    private static int counter;
    private static int curPos;
    private static int ItemId;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.context = getApplicationContext();
        setContentView(R.layout.activity_main);

        items = new ArrayList<>();
        postsDatabaseHelper = PostsDatabaseHelper.getInstance(this);
        items = postsDatabaseHelper.getAllPosts();
        curPos = -1;
        ItemId = -1;

        if (items.size() == 0) {
            Todo newTodo = new Todo(counter, "Doing grocery");
            ++counter;
            items.add(newTodo);
            postsDatabaseHelper.addPost(newTodo);
        }
        itemAdapter = new TodoAdapter(MainActivity.this, (ArrayList<Todo>) items, new mClickListener() {
            @Override
            public void onCheckChanged(int position) {
                Todo temp = items.get(position);
                temp.isCompleted = !temp.isCompleted;
                postsDatabaseHelper.addOrUpdateText(temp);
            }
        });
        lvItems = (ListView) findViewById(R.id.lvItems);
        lvItems.setAdapter(itemAdapter);
        lvItems.setItemsCanFocus(true);
        setupListViewListener();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void onAddItem(View v) {
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        String itemText = etNewItem.getText().toString();
        Todo temp = new Todo(counter, itemText);
        ++counter;
        itemAdapter.add(temp);
        etNewItem.setText("");
        postsDatabaseHelper.addPost(temp);
    }

    public void setupListViewListener() {
        //long click to remove item
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapter,
                                           View item, int pos, long id) {
                System.out.printf("on long click pos %d id %d/n", pos, id);
                items.remove(pos);
                itemAdapter.notifyDataSetChanged();
                postsDatabaseHelper.deleteOneTodoItem(pos);
                return true;
            }
        });
        //short click to edit item
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position,
                                    long id) {
                curPos = position;
                TextView tv = (TextView)view.findViewById(R.id.todo_id);
                ItemId = Integer.parseInt(tv.getText().toString());
                String item = ((TextView) view.findViewById(R.id.todo_text)).getText().toString();
                showEditDialog(item);
            }
        });

    }

    public static Context getAppContext() {
        return MainActivity.context;
    }

    public void showEditDialog(String str) {
        MyAlertDialogFragment editTodoFragment = MyAlertDialogFragment.newInstance(str);
        editTodoFragment.show(this.getFragmentManager(), str);
    }

    @Override
    public void onFinishEditDialog(String inputText) {
        Todo temp = new Todo(ItemId, inputText);
        items.set(curPos, temp);
        itemAdapter.notifyDataSetChanged();
        postsDatabaseHelper.addOrUpdateText(temp);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.light.todolist/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.light.todolist/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
