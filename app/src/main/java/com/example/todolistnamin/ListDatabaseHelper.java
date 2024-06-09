package com.example.todolistnamin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ListDatabaseHelper extends SQLiteOpenHelper {

    public static final String TITLE_COL_ID = "todo_id";
    public static final String TITLE_COL_TITLE = "todo_title";
    public static final String TITLE_COL_DATE_ADDED = "date_added";

    public static final String ENTRY_COL_ID_TITLE = "title_id";
    public static final String ENTRY_COL_ID_ENTRY = "entry_id";
    public static final String ENTRY_COL_ENTRY = "todo_entry";
    public static final String ENTRY_COL_TIME = "todo_time";
    public static final String ENTRY_COL_STATUS = "todo_status";

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "todo.db";

    public ListDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTodoTitleTableQuery = "CREATE TABLE todo_titles (" +
                TITLE_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                TITLE_COL_TITLE  + " VARCHAR(255), " +
                TITLE_COL_DATE_ADDED + " VARCHAR(255))";
        db.execSQL(createTodoTitleTableQuery);

        String createTodoEntryTableQuery = "CREATE TABLE todo_entries (" +
                ENTRY_COL_ID_ENTRY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ENTRY_COL_ID_TITLE + " INTEGER, " +
                ENTRY_COL_ENTRY + " VARCHAR(255), " +
                ENTRY_COL_TIME + " VARCHAR(255), " +
                ENTRY_COL_STATUS + " VARCHAR(255) DEFAULT 'pending' )";
        db.execSQL(createTodoEntryTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String titleTableDropper = "DROP TABLE IF EXISTS todo_titles";
        String entryTableDropper = "DROP TABLE IF EXISTS todo_entries";

        db.execSQL(titleTableDropper);
        db.execSQL(entryTableDropper);

        onCreate(db);
    }

    public long insertTitle(TodoModel todo){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(TITLE_COL_TITLE, todo.getTitle());
        cv.put(TITLE_COL_DATE_ADDED, todo.getDate());

        long titleID = db.insert("todo_titles", null, cv);
        db.close();

        return titleID;

    }

    public void insertTitleAndTodo(TodoModel todo) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cvTitle = new ContentValues();
        cvTitle.put(TITLE_COL_TITLE, todo.getTitle());
        cvTitle.put(TITLE_COL_DATE_ADDED, todo.getDate());
        long titleId = db.insert("todo_titles", null, cvTitle);

        if (titleId != -1) {
            ContentValues cvTodo = new ContentValues();
            cvTodo.put(ENTRY_COL_ID_TITLE, titleId);
            cvTodo.put(ENTRY_COL_ENTRY, todo.getEntry());
            cvTodo.put(ENTRY_COL_TIME, todo.getTime());
            db.insert("todo_entries", null, cvTodo);
        }

        db.close();
    }

    public long insertEntryAndTime(long titleId, String entry, String time) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(ENTRY_COL_ID_TITLE, titleId);
        cv.put(ENTRY_COL_ENTRY, entry);
        cv.put(ENTRY_COL_TIME, time);

        long entryidInsert = db.insert("todo_entries", null, cv);
        db.close();

        return entryidInsert;
    }


    public TodoModel fetchTodoByID(int selectedTodo) {
        TodoModel fetched;
        SQLiteDatabase db = getReadableDatabase();
        String queryForTitle = "SELECT * FROM todo_titles WHERE " + TITLE_COL_ID + " = ?";
        Cursor titleCursor = db.rawQuery(queryForTitle, new String[]{String.valueOf(selectedTodo)});
        if (titleCursor.moveToFirst()) {
            int titleID = titleCursor.getInt(titleCursor.getColumnIndexOrThrow(TITLE_COL_ID));
            String titleText = titleCursor.getString(titleCursor.getColumnIndexOrThrow(TITLE_COL_TITLE));
            String dateAdded = titleCursor.getString(titleCursor.getColumnIndexOrThrow(TITLE_COL_DATE_ADDED));

            fetched = new TodoModel(titleID, titleText, dateAdded);

            titleCursor.close();
        } else {
            fetched = null;
        }
        db.close();
        return fetched;
    }

    public List<TodoModel> fetchTodoListByID(int selectedTodo) {
        List<TodoModel> todoList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        // Query to fetch title details
        String queryForTitle = "SELECT * FROM todo_titles WHERE " + TITLE_COL_ID + " = ?";
        Cursor titleCursor = db.rawQuery(queryForTitle, new String[]{String.valueOf(selectedTodo)});

        if (titleCursor.moveToFirst()) {
            // Fetch title details
            int titleID = titleCursor.getInt(titleCursor.getColumnIndexOrThrow(TITLE_COL_ID));
            String titleText = titleCursor.getString(titleCursor.getColumnIndexOrThrow(TITLE_COL_TITLE));
            String dateAdded = titleCursor.getString(titleCursor.getColumnIndexOrThrow(TITLE_COL_DATE_ADDED));

            // Query to fetch todo entries associated with the title
            String queryForTodoList = "SELECT * FROM todo_entries WHERE " + ENTRY_COL_ID_TITLE + " = ?";
            Cursor listCursor = db.rawQuery(queryForTodoList, new String[]{String.valueOf(titleID)});

            while (listCursor.moveToNext()) {
                // Fetch todo entry details
                int entryID = listCursor.getInt(listCursor.getColumnIndexOrThrow(ENTRY_COL_ID_ENTRY));
                int entryTitleID = listCursor.getInt(listCursor.getColumnIndexOrThrow(ENTRY_COL_ID_TITLE));
                String entryText = listCursor.getString(listCursor.getColumnIndexOrThrow(ENTRY_COL_ENTRY));
                String accomplishTime = listCursor.getString(listCursor.getColumnIndexOrThrow(ENTRY_COL_TIME));
                String status = listCursor.getString(listCursor.getColumnIndexOrThrow(ENTRY_COL_STATUS));

                // Create TodoModel object and add to list
                TodoModel todo = new TodoModel(titleID, entryID, entryTitleID, entryText, titleText, dateAdded, accomplishTime, status);
                todoList.add(todo);
            }

            // Close cursors
            listCursor.close();
        }

        // Close title cursor and database
        titleCursor.close();
        db.close();

        return todoList;
    }

    public List<TodoModel> fetchTodoTitles () {
        List<TodoModel> todoTitleList = new ArrayList<>();
        TodoModel todo;

        SQLiteDatabase db = getReadableDatabase();

        String fetchTodoTitles = "SELECT * FROM todo_titles";

        Cursor pointer = db.rawQuery(fetchTodoTitles, null);

        while (pointer.moveToNext()){

            int id = pointer.getInt(pointer.getColumnIndexOrThrow(TITLE_COL_ID));
            String todo_title = pointer.getString(pointer.getColumnIndexOrThrow(TITLE_COL_TITLE));
            String date_added = pointer.getString(pointer.getColumnIndexOrThrow(TITLE_COL_DATE_ADDED));

            todo = new TodoModel();

            todo.setId(id);
            todo.setTitle(todo_title);
            todo.setDate(date_added);

            todoTitleList.add(todo);
        }

        pointer.close();
        db.close();

        for (int i = 0; i < todoTitleList.size(); i++) {
            Log.d("Bro", "HELLO");
        }

        return todoTitleList;
    }

    public List<TodoModel> fetchTodoTitlesByDate(String selectedDate) {
        List<TodoModel> todoTitleList = new ArrayList<>();
        TodoModel todo;

        SQLiteDatabase db = getReadableDatabase();

        // kunin lahat ng title kung saan ung date is equals = ? <- placeholder
        String fetchTodoTitlesByDate = "SELECT * FROM todo_titles WHERE " + TITLE_COL_DATE_ADDED + " = ?";

        // use the selectDate parameter to replace the placeholder
        Cursor pointer = db.rawQuery(fetchTodoTitlesByDate, new String[]{selectedDate});

        while (pointer.moveToNext()) {
            int id = pointer.getInt(pointer.getColumnIndexOrThrow(TITLE_COL_ID));
            String todo_title = pointer.getString(pointer.getColumnIndexOrThrow(TITLE_COL_TITLE));
            String date_added = pointer.getString(pointer.getColumnIndexOrThrow(TITLE_COL_DATE_ADDED));

            todo = new TodoModel();
            todo.setId(id);
            todo.setTitle(todo_title);
            todo.setDate(date_added);

            todoTitleList.add(todo);
        }

        pointer.close();
        db.close();

        return todoTitleList;
    }

    public void deleteTodoAndEntriesById(int titleId) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete("todo_entries", ENTRY_COL_ID_TITLE + " = ?", new String[]{String.valueOf(titleId)});

        db.delete("todo_titles", TITLE_COL_ID + " = ?", new String[]{String.valueOf(titleId)});

        db.close();
    }

    public void deleteEntryByID (long id){
        SQLiteDatabase db = getWritableDatabase();

        db.delete("todo_entries", ENTRY_COL_ID_ENTRY + "= ?", new String[]{String.valueOf(id)});

        db.close();
    }


}
