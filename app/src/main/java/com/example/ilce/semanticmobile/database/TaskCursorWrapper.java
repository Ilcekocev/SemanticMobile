package com.example.ilce.semanticmobile.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.ilce.semanticmobile.model.Task;

import static com.example.ilce.semanticmobile.database.DbSchema.*;

/**
 * Created by Ilce on 8/11/2017.
 */

public class TaskCursorWrapper extends CursorWrapper {

    public TaskCursorWrapper (Cursor cursor) {
        super(cursor);
    }

    public Task getTask() {
        String name = getString(getColumnIndex(TaskTable.Cols.NAME));
        String desc = getString(getColumnIndex(TaskTable.Cols.DESC));

        Task task = new Task();
        task.setName(name);
        task.setDescription(desc);

        return task;
    }

}


