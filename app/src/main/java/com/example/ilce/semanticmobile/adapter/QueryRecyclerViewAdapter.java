package com.example.ilce.semanticmobile.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ilce.semanticmobile.R;
import com.example.ilce.semanticmobile.model.Query;

import java.util.List;


public class QueryRecyclerViewAdapter extends RecyclerView.Adapter<QueryRecyclerViewAdapter.ViewHolder> {

    private final List<Query> mQueries;

    public QueryRecyclerViewAdapter(List<Query> items) {
        mQueries = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item_query, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mQueries.get(position);
        holder.bindData();
    }

    @Override
    public int getItemCount() {
        return mQueries.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mNameView;
        public final TextView mContentView;
        public Query mItem;

        public ViewHolder(View view) {
            super(view);
            mNameView = (TextView) view.findViewById(R.id.textViewQueryName);
            mContentView = (TextView) view.findViewById(R.id.textViewQueryContent);
        }

        public void bindData() {
            mNameView.setText(mItem.getName());
            mContentView.setText(mItem.getBody());
        }

    }
}
