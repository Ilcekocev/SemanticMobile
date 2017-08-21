package com.example.ilce.semanticmobile.model;

/**
 * Created by Ilce on 8/18/2017.
 */

public class User {

    private String name;
    private int queries;

    public User(String name,int br) {
        this.name=name;
        queries = br;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQueries() {
        return queries;
    }

    public void setQueries(int queries) {
        this.queries = queries;
    }
}
