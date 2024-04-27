package com.example.task41;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TASK_TABLE = "task_table";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_ID = "ID";

    public DBHelper(Context context) {
        super(context, "Taskmanager.db", null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + TASK_TABLE + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_DATE + " TEXT)";
        db.execSQL(createTableStatement);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TASK_TABLE);
        onCreate(db);
    }


    public boolean addOne(TaskModel task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, task.getTitle());
        cv.put(COLUMN_DESCRIPTION, task.getDescription());
        cv.put(COLUMN_DATE, task.getDate());
        long insert = db.insert(TASK_TABLE, null, cv);
        db.close();
        return insert != -1;
    }

    public boolean deleteOne(TaskModel task) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deleteCount = db.delete(TASK_TABLE, COLUMN_ID + " = ?", new String[]{String.valueOf(task.getId())});
        db.close();
        return deleteCount > 0;
    }

    public boolean editTask(TaskModel task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, task.getTitle());
        cv.put(COLUMN_DESCRIPTION, task.getDescription());
        cv.put(COLUMN_DATE, task.getDate());
        int updateCount = db.update(TASK_TABLE, cv, COLUMN_ID + " = ?", new String[]{String.valueOf(task.getId())});
        db.close();
        return updateCount > 0;
    }


    public List<TaskModel> getAll() {
        List<TaskModel> returnList = new ArrayList<>();
        String queryString = "SELECT * FROM " + TASK_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        if (cursor.moveToFirst()) {
            do {
                int taskID = cursor.getInt(0);
                String taskTitle = cursor.getString(1);
                String taskDescription = cursor.getString(2);
                String taskDate = cursor.getString(3);
                TaskModel newTask = new TaskModel(taskID, taskTitle, taskDescription, taskDate);
                returnList.add(newTask);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return returnList;
    }

}





