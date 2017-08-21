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
import com.example.ilce.semanticmobile.adapter.TaskRecyclerViewAdapter;
import com.example.ilce.semanticmobile.database.TaskDb;
import com.example.ilce.semanticmobile.model.Task;
import com.example.ilce.semanticmobile.util.ItemDivider;

import java.util.ArrayList;
import java.util.List;


public class TasksFragment extends Fragment {


    private List<Task> tasks = new ArrayList<>();
    private TaskRecyclerViewAdapter adapter;

    public static TasksFragment newInstance() {
        TasksFragment fragment = new TasksFragment();
        //Bundle args = new Bundle();
       // args.putString(userArg,user);
       // fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
           // user = getArguments().getString(userArg);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.tasksRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        TaskDb taskDb = TaskDb.get(getActivity());
        tasks = taskDb.getTasks();
        TextView textView =(TextView) view.findViewById(R.id.textViewInfo);

        if (tasks.size()==0) {
            recyclerView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
            adapter = new TaskRecyclerViewAdapter(getActivity(),tasks);
            recyclerView.setAdapter(adapter);
            recyclerView.addItemDecoration(new ItemDivider(getActivity()));
            recyclerView.setVisibility(View.VISIBLE);
        }

        FloatingActionButton addButton = (FloatingActionButton)view.findViewById(R.id.addTaskFloatingButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddTaskFragment fragment = AddTaskFragment.newInstance(tasks.size()==0);
                fragment.setEnterTransition(new Explode());
               // fragment.setExitTransition(new Fade());
                FragmentTransaction transaction =getActivity().getSupportFragmentManager().beginTransaction();
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
