package com.example.light.todolist;

import java.util.Date;

/**
 * Created by Light on 6/22/2016.
 */
public class Todo {
    int id;
    String text;
    Date date;
    Todo () {}
    Todo (int id, String text) {
        this.id = id;
        this.text = text;
    }
}
