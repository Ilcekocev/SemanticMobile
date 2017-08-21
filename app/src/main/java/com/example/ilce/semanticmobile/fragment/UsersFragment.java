package com.example.ilce.semanticmobile.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ilce.semanticmobile.R;
import com.example.ilce.semanticmobile.adapter.UsersRecyclerViewAdapter;
import com.example.ilce.semanticmobile.model.User;
import com.example.ilce.semanticmobile.util.ItemDivider;
import com.example.ilce.semanticmobile.util.Utills;

import java.util.ArrayList;
import java.util.List;


public class UsersFragment extends Fragment {

    private List<User> users = new ArrayList<>();
    private  RecyclerView recyclerView;
    private UsersRecyclerViewAdapter adapter;
    private  ProgressBar progressBar;
    private TextView infoTextView;

    public static UsersFragment newInstance() {
        UsersFragment fragment = new UsersFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.usersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        progressBar = (ProgressBar)view.findViewById(R.id.progressBarUsers);
        infoTextView = (TextView)view.findViewById(R.id.textViewInfoUsers);

        progressBar.setVisibility(View.VISIBLE);
        GetUsersTask task = new GetUsersTask(getActivity());
        task.execute(getString(R.string.url_get_users));

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public class GetUsersTask extends AsyncTask<String, Void, List<User>> {

        private Context mContext;

        GetUsersTask(Context context) {
            mContext=context;
        }

        @Override
        protected List<User> doInBackground(String... params) {
            String url = params[0];
            return Utills.getUsers(url);
        }

        @Override
        protected void onPostExecute(List<User> list) {
            if (list==null) {
                Toast.makeText(mContext,"Unable to connect to server",Toast.LENGTH_SHORT).show();
                infoTextView.setVisibility(View.VISIBLE);
            }
            if (list.size()==0) {
                infoTextView.setVisibility(View.VISIBLE);
            } else {
                users = list;
                adapter = new UsersRecyclerViewAdapter(users);
                recyclerView.setAdapter(adapter);
                recyclerView.addItemDecoration(new ItemDivider(getActivity()));
            }
            progressBar.setVisibility(View.GONE);
        }
    }

}
