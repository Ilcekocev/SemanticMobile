package com.example.ilce.semanticmobile.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ilce.semanticmobile.R;
import com.example.ilce.semanticmobile.adapter.QueryRecyclerViewAdapter;
import com.example.ilce.semanticmobile.database.QueryDb;
import com.example.ilce.semanticmobile.model.Query;
import com.example.ilce.semanticmobile.util.ItemDivider;

import java.util.ArrayList;
import java.util.List;


public class QueriesFragment extends Fragment {


    private List<Query> queries = new ArrayList<>();
    private QueryRecyclerViewAdapter adapter;

    public static QueriesFragment newInstance() {
        QueriesFragment fragment = new QueriesFragment();

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
        View view = inflater.inflate(R.layout.fragment_query_list, container, false);

        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.queriesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        QueryDb queryDb = QueryDb.get(getActivity());
        queries = queryDb.getQueries();

        TextView textView = (TextView)view.findViewById(R.id.textViewInfoQuery);

        if (queries.size()==0) {
            recyclerView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
            adapter = new QueryRecyclerViewAdapter(queries);
            recyclerView.setAdapter(adapter);
            recyclerView.addItemDecoration(new ItemDivider(getActivity()));
            recyclerView.setVisibility(View.VISIBLE);
        }

        FloatingActionButton addButton = (FloatingActionButton)view.findViewById(R.id.addQueryFloatingButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddQueryFragment fragment = AddQueryFragment.newInstance();
                fragment.setEnterTransition(new Explode());
               // fragment.setExitTransition(new Fade());
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainer,fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

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

}
