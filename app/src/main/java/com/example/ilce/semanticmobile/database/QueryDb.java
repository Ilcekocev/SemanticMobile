package com.example.ilce.semanticmobile.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ilce.semanticmobile.database.DbSchema.QueryTable;
import com.example.ilce.semanticmobile.model.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ilce on 8/12/2017.
 */

public class QueryDb {

    private static QueryDb sQueryDb;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static QueryDb get(Context context) {
        if (sQueryDb==null) {
            sQueryDb = new QueryDb(context);
        }
        return sQueryDb;
    }

    private QueryDb (Context context) {
        mContext=context.getApplicationContext();
        mDatabase= new BaseHelper(mContext).getWritableDatabase();
    }


    public void addQuery(Query q) {
        ContentValues values = getContentValues(q);

        mDatabase.insert(QueryTable.NAME,null,values);
    }

    public List<Query> getQueries() {
        List<Query> queries = new ArrayList<>();

        QueryCursorWrapper cursor = queryQueries(null,null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                queries.add(cursor.getQuery());
                cursor.moveToNext();}
        }
        finally {
            cursor.close();
        }
        return queries;
    }

    private QueryCursorWrapper queryQueries(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                QueryTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new QueryCursorWrapper(cursor);
    }


    private static ContentValues getContentValues(Query query) {
        ContentValues values = new ContentValues();
        values.put(QueryTable.Cols.NAME,query.getName());
        values.put(QueryTable.Cols.BODY,query.getBody());

        return values;
    }

}
