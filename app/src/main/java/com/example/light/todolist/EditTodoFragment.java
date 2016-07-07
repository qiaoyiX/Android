package com.example.light.todolist;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Light on 7/4/2016.
 */
public class EditTodoFragment extends DialogFragment implements TextView.OnEditorActionListener {
    private EditText mEditText;


    public void EditTodoFragment() {

    }

    public interface EditTodoFragmentListener {
        void onFinishEditDialog(String inputText);
    }

    public static EditTodoFragment newInstance(String desc) {
        EditTodoFragment frag = new EditTodoFragment();
        Bundle args = new Bundle();
        args.putString("desc", desc);
        frag.setArguments(args);
        return frag;
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.custom_dialog, container);
//    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        System.out.println("Hello");
        super.onViewCreated(view, savedInstanceState);
        mEditText.setText(getArguments().getString("desc"));
        mEditText.requestFocus();
        mEditText.setOnEditorActionListener(this);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }



    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            System.out.println("onEditorAction");
            EditTodoFragmentListener listener = (EditTodoFragmentListener)getActivity();
            listener.onFinishEditDialog(mEditText.getText().toString());
            dismiss();
            return true;
        }
        return false;
    }

}
