package com.example.ilce.semanticmobile.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ilce.semanticmobile.MainActivity;
import com.example.ilce.semanticmobile.R;
import com.example.ilce.semanticmobile.database.QueryDb;
import com.example.ilce.semanticmobile.database.TaskDb;
import com.example.ilce.semanticmobile.model.Query;
import com.example.ilce.semanticmobile.model.Task;
import com.example.ilce.semanticmobile.model.User;
import com.example.ilce.semanticmobile.util.Utills;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class AddTaskFragment extends Fragment {


    private static final String Preferences = "preferences";
    private SharedPreferences sharedPreferences;
    private static final String ACTIVE_TASK = "activeTask";
    private static final String SHOULD_SEND_ARG = "shouldSendQuerry";
    private boolean shouldSend;
    private AutoCompleteTextView mTaskNameView;
    private EditText mTaskDescView;
    private View mProgressView;
    private View mAddTaskFormView;
    private String mUser;

    public static AddTaskFragment newInstance(boolean shouldSend) {
        AddTaskFragment fragment = new AddTaskFragment();
        Bundle args = new Bundle();
        args.putBoolean(SHOULD_SEND_ARG,shouldSend);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(Preferences,MODE_PRIVATE);
        mUser = ((MainActivity)getActivity()).getUser();
        if (getArguments() != null) {
          shouldSend = getArguments().getBoolean(SHOULD_SEND_ARG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_task, container, false);

        mTaskNameView = (AutoCompleteTextView)view.findViewById(R.id.taskName);
        mTaskDescView = (EditText)view.findViewById(R.id.taskDescription);

        Button addTaskButton = (Button)view.findViewById(R.id.addTask_button);
        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });

        mAddTaskFormView = view.findViewById(R.id.task_form);
        mProgressView = view.findViewById(R.id.addTask_progress);

        return view;
    }



    private void addTask() {
        mTaskNameView.setError(null);
        mTaskDescView.setError(null);

        String taskName = mTaskNameView.getText().toString();
        String taskDesc = mTaskDescView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(taskName)){
            mTaskNameView.setError(getString(R.string.error_field_required));
            focusView = mTaskNameView;
            cancel = true;

        } else if (!isTaskNameValid(taskName)) {
            mTaskNameView.setError(getString(R.string.error_invalid_taskName));
            focusView = mTaskNameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(taskDesc)){
            mTaskDescView.setError(getString(R.string.error_field_required));
            focusView = mTaskDescView;
            cancel = true;
        } else if (!isDescValid(taskDesc)) {
            mTaskDescView.setError(getString(R.string.error_invalid_taskDesc));
            focusView = mTaskDescView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            Task task = new Task();
            task.setName(taskName);
            task.setDescription(taskDesc);
            AddTask addAsyncTask = new AddTask(getActivity(),task);
            addAsyncTask.execute((Void) null);
        }
    }


    private boolean isTaskNameValid(String name) {
        return name.length()>4;
    }

    private boolean isDescValid(String desc) {
        return desc.length() > 5;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mAddTaskFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mAddTaskFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mAddTaskFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mAddTaskFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }



    public class AddTask extends AsyncTask<Void, Void, Boolean> {

        private Task mTask;
        private Context mContext;
        private final String prefix = "http://myexample.org/";
        private final String subject = prefix+"user/"+mUser;
        private final String predicate = prefix+"worksOn";
        private String object;

        public AddTask(Context context,Task task) {
            mTask=task;
            mContext=context;
            object = prefix+"task/"+mTask.getName();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            JSONObject data = new JSONObject();

            List<User> users = Utills.getUsers(getString(R.string.url_get_users));

            try {
                data.put("subject",subject);
                data.put("predicate",predicate);
                data.put("object",object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            boolean isSend = Utills.sendPostRequest(getString(R.string.url_event),data.toString());

            //register default querry
            if (shouldSend && isSend) {
                data = new JSONObject();

                Query query = new Query();
                query.setName("Default location querry");
                String queryBody = "REGISTER QUERY Locations AS " +
                        "PREFIX ex: <http://myexample.org/> " +
                        "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " +
                        "SELECT ?emp1 ?emp2 ?t " +
                        "FROM STREAM <http://myexample.org/stream> [RANGE 1s STEP 1s] " +
                        "WHERE { " +
                        "?emp1 ex:worksOn ?t . " +
                        "?emp2 ex:worksOn ?t . " +
                        "?emp1 ex:easting ?east1 . " +
                        "?emp2 ex:easting ?east2 . " +
                        "?emp1 ex:northing ?north1 . " +
                        "?emp2 ex:northing ?north2 . " +
                        "FILTER (?emp1 != ?emp2) . " +
                        "FILTER (((?north1 - ?north2) * (?north1 - ?north2)) + ((?east1 - ?east2) * (?east1 - ?east2)) < 100) ." +
                        "}";
                query.setBody(queryBody);

                try {
                    data.put("definition",query.getBody());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                isSend = Utills.sendPostRequest(getString(R.string.url_query,mUser),data.toString());

                if (isSend) {
                    QueryDb queryDb = QueryDb.get(mContext);
                    queryDb.addQuery(query);
                }
            }

            if (isSend) return true;
            else return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
               if (success) {
                   TaskDb taskDb = TaskDb.get(mContext);
                   taskDb.addTask(mTask);
                   //set task active
                   SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
                   preferencesEditor.putString(ACTIVE_TASK,mTask.getName());
                   preferencesEditor.apply();
                   //switch fragments
                   TasksFragment fragment = TasksFragment.newInstance();
                   getActivity().getSupportFragmentManager().popBackStack();
                   FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                   transaction.replace(R.id.fragmentContainer,fragment);
                   transaction.commit();
                   hideKeyboard();
               } else {
                   Toast.makeText(mContext,R.string.error_creating_task,Toast.LENGTH_LONG).show();
               }
        }



        public void hideKeyboard() {
            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
            View view = getActivity().getCurrentFocus();
            if (view == null) {
                view = new View(getActivity());
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
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
