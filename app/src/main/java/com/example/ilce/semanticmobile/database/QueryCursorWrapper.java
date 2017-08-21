package com.example.ilce.semanticmobile.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.ilce.semanticmobile.model.Query;

import static com.example.ilce.semanticmobile.database.DbSchema.*;

/**
 * Created by Ilce on 8/12/2017.
 */

public class QueryCursorWrapper extends CursorWrapper {

    public QueryCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Query getQuery() {
        String name = getString(getColumnIndex(QueryTable.Cols.NAME));
        String body = getString(getColumnIndex(QueryTable.Cols.BODY));

        Query query = new Query();
        query.setName(name);
        query.setBody(body);

        return query;
    }
}
