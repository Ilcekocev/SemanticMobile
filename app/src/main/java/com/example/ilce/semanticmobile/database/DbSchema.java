package com.example.ilce.semanticmobile.database;

/**
 * Created by Ilce on 8/11/2017.
 */

public class DbSchema {

    public static final class TaskTable {

        public static final String NAME="tasks";

        public static final class Cols {
            public static final String NAME="name";
            public static final String DESC="description";
        }

    }

    public static final class QueryTable {

        public static final String NAME="queries";

        public static final class Cols {
            public static final String NAME="name";
            public static final String BODY="body";
        }
    }

}
