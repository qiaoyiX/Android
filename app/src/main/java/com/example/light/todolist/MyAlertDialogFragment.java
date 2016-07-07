package com.example.light.todolist;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Light on 7/6/2016.
 */
public class MyAlertDialogFragment extends DialogFragment{
    public MyAlertDialogFragment() {}

    public static MyAlertDialogFragment newInstance(String desc) {
        MyAlertDialogFragment frag = new MyAlertDialogFragment();
        Bundle args = new Bundle();
        args.putString("desc", desc);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        final View layout = View.inflate(MainActivity.getAppContext(), R.layout.custom_dialog, null);
        alertDialogBuilder.setView(layout);
        final EditText mEditText = (EditText) layout.findViewById(R.id.EditDesc);
        String desc = getArguments().getString("desc");
        mEditText.setText(desc);
        System.out.println("EditText is " + mEditText.getText().toString());

        alertDialogBuilder.setTitle("Edit to-do list");
        alertDialogBuilder.setPositiveButton("OK",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // on success
                EditTodoFragment.EditTodoFragmentListener listener = (EditTodoFragment.EditTodoFragmentListener)getActivity();
                listener.onFinishEditDialog(mEditText.getText().toString());
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return alertDialogBuilder.create();
    }
}
