package com.example.ilce.semanticmobile.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.ilce.semanticmobile.R;
import com.example.ilce.semanticmobile.model.Task;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class TaskRecyclerViewAdapter extends RecyclerView.Adapter<TaskRecyclerViewAdapter.ViewHolder> {

    private static final String Preferences = "preferences";
    private SharedPreferences sharedPreferences;
    private static final String ACTIVE_TASK = "activeTask";
    private final List<Task> mTasks;
    private int lastCheckedPosition = 0;


    public TaskRecyclerViewAdapter(Context context,List<Task> items) {
        mTasks = items;
        sharedPreferences = context.getSharedPreferences(Preferences,MODE_PRIVATE);
       // mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mTask = mTasks.get(position);
        holder.bindData();

        String activeTask = sharedPreferences.getString(ACTIVE_TASK,"");
        holder.mRadioButton.setChecked(holder.mTask.getName().equals(activeTask));
    }

    @Override
    public int getItemCount() {
        return mTasks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView mNameView;
        public final TextView mDescView;
        public final RadioButton mRadioButton;
        public Task mTask;

        public ViewHolder(View view) {
            super(view);
            mNameView = (TextView) view.findViewById(R.id.textViewName);
            mDescView = (TextView) view.findViewById(R.id.textViewDesc);
            mRadioButton = (RadioButton) view.findViewById(R.id.taskRadioBox);

            mRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RadioButton rb = (RadioButton)v;
                    if (rb.isChecked()) {
                        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
                        preferencesEditor.putString(ACTIVE_TASK,mTask.getName());
                        preferencesEditor.apply();
                    }
                    notifyDataSetChanged();
                }
            });
        }

        public void bindData() {
            mNameView.setText(mTask.getName());
            mDescView.setText(mTask.getDescription());
        }
    }
}
