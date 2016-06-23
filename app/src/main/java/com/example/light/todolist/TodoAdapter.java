package com.example.light.todolist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Light on 6/23/2016.
 */
public class TodoAdapter extends ArrayAdapter<Todo> {
    public TodoAdapter(Context context, ArrayList<Todo> todos) {
        super(context, 0, todos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Todo todo = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_display, parent, false);
        }
        // Lookup view for data population
        TextView todoId = (TextView) convertView.findViewById(R.id.todo_id);
        TextView todoText = (TextView) convertView.findViewById(R.id.todo_text);
        // Populate the data into the template view using the data object
        todoId.setText(Integer.toString(todo.id));
        todoText.setText(todo.text);
        // Return the completed view to render on screen
        return convertView;
    }

}
