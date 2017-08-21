package com.example.ilce.semanticmobile.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ilce.semanticmobile.database.DbSchema.TaskTable;
import com.example.ilce.semanticmobile.model.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ilce on 8/11/2017.
 */

public class TaskDb {

    private static TaskDb sTaskDb;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public  static TaskDb get(Context context) {
        if (sTaskDb == null) {
            sTaskDb = new TaskDb(context);
        }
        return sTaskDb;
    }

    private TaskDb (Context context) {
        mContext=context.getApplicationContext();
        mDatabase= new BaseHelper(mContext).getWritableDatabase();
    }

    public void addTask(Task t) {
        ContentValues values = getContentValues(t);

        mDatabase.insert(TaskTable.NAME,null,values);
    }

    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();

        TaskCursorWrapper cursor = queryPlaces(null,null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                tasks.add(cursor.getTask());
                cursor.moveToNext();}
        }
        finally {
            cursor.close();
        }
        return tasks;
    }

    private TaskCursorWrapper queryPlaces(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                TaskTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new TaskCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(Task task) {
        ContentValues values = new ContentValues();
        values.put(TaskTable.Cols.NAME,task.getName());
        values.put(TaskTable.Cols.DESC,task.getDescription());

        return values;
    }

}
