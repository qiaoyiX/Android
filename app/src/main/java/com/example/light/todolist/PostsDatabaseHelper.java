package com.example.light.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Light on 6/22/2016.
 */
public class PostsDatabaseHelper extends SQLiteOpenHelper {
    // Database Info
    final String TAG = "post db";
    private static PostsDatabaseHelper sInstance;
    private static final String DATABASE_NAME = "postsDatabase";
    private static final int DATABASE_VERSION = 1;

    public static synchronized PostsDatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new PostsDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */

    // Table Names
    private static final String TABLE_POSTS = "posts";
//    private static final String TABLE_USERS = "users";

    // Post Table Columns
    private static final String KEY_POST_ID = "id";
//    private static final String KEY_POST_USER_ID_FK = "userId";
    private static final String KEY_POST_TEXT = "text";

    // User Table Columns
//    private static final String KEY_USER_ID = "id";
//    private static final String KEY_USER_NAME = "userName";
//    private static final String KEY_USER_PROFILE_PICTURE_URL = "profilePictureUrl";

    public PostsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void addPost(Todo todo) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            // The user might already exist in the database (i.e. the same user created multiple posts).
            long userId = addOrUpdateText(todo);

            ContentValues values = new ContentValues();
//            values.put(KEY_POST_USER_ID_FK, userId);
            values.put(KEY_POST_TEXT, todo.text);

            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.insertOrThrow(TABLE_POSTS, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add post to database");
        } finally {
            db.endTransaction();
        }
    }

    public boolean deleteOneTodoItem(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_POSTS, KEY_POST_ID + "=" + id, null) > 0;
    }
    // Insert or update a user in the database
    // Since SQLite doesn't support "upsert" we need to fall back on an attempt to UPDATE (in case the
    // user already exists) optionally followed by an INSERT (in case the user does not already exist).
    // Unfortunately, there is a bug with the insertOnConflict method
    // (https://code.google.com/p/android/issues/detail?id=13045) so we need to fall back to the more
    // verbose option of querying for the user's primary key if we did an update.
    public long addOrUpdateText(Todo todo) {
        // The database connection is cached so it's not expensive to call getWriteableDatabase() multiple times.
        SQLiteDatabase db = getWritableDatabase();
        long userId = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_POST_ID, todo.id);
            values.put(KEY_POST_TEXT, todo.text);

            // First try to update the user in case the user already exists in the database
            // This assumes userNames are unique
            int rows = db.update(TABLE_POSTS, values, KEY_POST_ID + "= ?", new String[]{Integer.toString(todo.id)});
            // Check if update succeeded
            if (rows == 1) {
                // Get the primary key of the user we just updated
                String usersSelectQuery = String.format("SELECT %s FROM %s WHERE %s = ?",
                        TABLE_POSTS, KEY_POST_ID, todo.text);
                Cursor cursor = db.rawQuery(usersSelectQuery, new String[]{String.valueOf(todo.text)});
                try {
                    if (cursor.moveToFirst()) {
                        userId = cursor.getInt(0);
                        db.setTransactionSuccessful();
                    }
                } finally {
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }
                }
            } else {
                // user with this userName did not already exist, so insert new user
                userId = db.insertOrThrow(TABLE_POSTS, null, values);
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add or update user");
        } finally {
            db.endTransaction();
        }
        return userId;
    }

    public List<Todo> getAllPosts() {
        List<Todo> posts = new ArrayList<>();

        // SELECT * FROM POSTS
        String POSTS_SELECT_QUERY =
                String.format("SELECT * FROM %s",
                        TABLE_POSTS);

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(POSTS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Todo newTodo = new Todo();
                    newTodo.text = cursor.getString(cursor.getColumnIndex(KEY_POST_TEXT));
                    newTodo.id = cursor.getInt(cursor.getColumnIndex(KEY_POST_ID));
                    posts.add(newTodo);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return posts;
    }

    public void deleteAllPostsAndUsers() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            // Order of deletions is important when foreign key relationships exist.
            db.delete(TABLE_POSTS, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete all posts and users");
        } finally {
            db.endTransaction();
        }
    }

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_POSTS_TABLE = "CREATE TABLE " + TABLE_POSTS +
                "(" +
                KEY_POST_ID + " INTEGER PRIMARY KEY," + // Define a primary key
                //KEY_POST_USER_ID_FK + " INTEGER REFERENCES " + TABLE_USERS + "," + // Define a foreign key
                KEY_POST_TEXT + " TEXT" +
                ")";

//        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS +
//                "(" +
//                KEY_USER_ID + " INTEGER PRIMARY KEY," +
//                KEY_USER_NAME + " TEXT," +
//                KEY_USER_PROFILE_PICTURE_URL + " TEXT" +
//                ")";

        db.execSQL(CREATE_POSTS_TABLE);
//        db.execSQL(CREATE_USERS_TABLE);
    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSTS);
//            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            onCreate(db);
        }
    }
}