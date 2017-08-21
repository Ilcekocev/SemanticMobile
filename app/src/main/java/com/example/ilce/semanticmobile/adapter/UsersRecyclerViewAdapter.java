package com.example.ilce.semanticmobile.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ilce.semanticmobile.R;
import com.example.ilce.semanticmobile.model.User;

import java.util.List;

/**
 * Created by Ilce on 8/18/2017.
 */

public class UsersRecyclerViewAdapter extends RecyclerView.Adapter<UsersRecyclerViewAdapter.ViewHolder> {

    private final List<User> users;

    public UsersRecyclerViewAdapter(List<User> users) {
        this.users=users;
    }


    @Override
    public UsersRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UsersRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.mUser=users.get(position);
        holder.bindData();
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView mNameView;
        public final TextView mContentView;
        public User mUser;

        public ViewHolder(View itemView) {
            super(itemView);
            mNameView = (TextView) itemView.findViewById(R.id.textViewUserName);
            mContentView = (TextView)itemView.findViewById(R.id.textViewUserContent);
        }

        public void bindData() {
            mNameView.setText(mUser.getName());
            mContentView.setText("Registered queries: "+mUser.getQueries());
        }
    }
}
