package com.example.ilce.semanticmobile.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.ilce.semanticmobile.database.DbSchema.*;

/**
 * Created by Ilce on 8/11/2017.
 */

public class BaseHelper extends SQLiteOpenHelper {

    private static final int VERSION=1;
    private static final String DATABASE_NAME="dataBase.db";

    public BaseHelper (Context context) {
        super(context,DATABASE_NAME,null,VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table "+ TaskTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                TaskTable.Cols.NAME + ", " +
                TaskTable.Cols.DESC +
                ")"
        );

        db.execSQL("create table "+ QueryTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                QueryTable.Cols.NAME + ", " +
                QueryTable.Cols.BODY +
                ")"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
