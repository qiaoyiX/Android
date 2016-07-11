package com.example.light.todolist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Light on 6/23/2016.
 */

public class TodoAdapter extends ArrayAdapter<Todo>{

    private mClickListener mClickListener = null;

    public TodoAdapter(Context context, ArrayList<Todo> todos, mClickListener listener) {
        super(context, 0, todos);
        mClickListener = listener;
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
        CheckBox completed = (CheckBox) convertView.findViewById(R.id.isCompleted);
        // Populate the data into the template view using the data object
        todoId.setText(Integer.toString(todo.id));
        todoText.setText(todo.text);
        if (todo.isCompleted) {
            completed.setChecked(true);
        }
        else {
            completed.setChecked(false);
        }
        completed.setTag(position);
        completed.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onCheckChanged((Integer) v.getTag());
                }
            }
        });
        // Return the completed view to render on screen
        return convertView;
    }



}
