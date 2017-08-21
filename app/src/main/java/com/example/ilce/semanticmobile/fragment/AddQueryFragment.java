package com.example.ilce.semanticmobile.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
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
import com.example.ilce.semanticmobile.model.Query;
import com.example.ilce.semanticmobile.util.Utills;

import org.json.JSONException;
import org.json.JSONObject;


public class AddQueryFragment extends Fragment {

    private AutoCompleteTextView mQueryNameView;
    private EditText mQueryBodyView;
    private View mProgressView;
    private View mAddQueryFormView;
    private String mUser;



    public static AddQueryFragment newInstance() {
        AddQueryFragment fragment = new AddQueryFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = ((MainActivity)getActivity()).getUser();
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_query, container, false);

        mQueryNameView = (AutoCompleteTextView)view.findViewById(R.id.queryName);
        mQueryBodyView = (EditText)view.findViewById(R.id.queryBody);

        Button addQueryButton = (Button)view.findViewById(R.id.addQuery_button);
        addQueryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               addQuery();
            }
        });

        mAddQueryFormView = view.findViewById(R.id.query_form);
        mProgressView = view.findViewById(R.id.addQuery_progress);

        return view;
    }


    private void addQuery() {
        mQueryNameView.setError(null);
        mQueryBodyView.setError(null);

        String queryName =  mQueryNameView.getText().toString();
        String queryBody = mQueryBodyView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(queryName)){
            mQueryNameView.setError(getString(R.string.error_field_required));
            focusView = mQueryNameView;
            cancel = true;

        } else if (!isQueryNameValid(queryName)) {
            mQueryNameView.setError(getString(R.string.error_invalid_queryName));
            focusView = mQueryNameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(queryBody)){
            mQueryBodyView.setError(getString(R.string.error_field_required));
            focusView = mQueryBodyView;
            cancel = true;
        } else if (!isBodyValid(queryBody)) {
            mQueryBodyView.setError(getString(R.string.error_invalid_queryBody));
            focusView = mQueryBodyView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            Query query = new Query();
            query.setName(queryName);
            query.setBody(queryBody);
            AddQuery addAsyncQuery = new AddQuery(getActivity(),query);
            addAsyncQuery.execute((Void) null);
        }

    }


    private boolean isQueryNameValid(String name) {
        return name.length()>4;
    }

    private boolean isBodyValid(String desc) {
        return desc.length() > 20;
    }



    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mAddQueryFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mAddQueryFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mAddQueryFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mAddQueryFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    public class AddQuery extends AsyncTask<Void, Void, Boolean> {

        private Context mContext;
        private Query mQuery;


        public AddQuery(Context context, Query query) {
            mContext = context;
            mQuery = query;
        }


        @Override
        protected Boolean doInBackground(Void... params) {

            JSONObject data = new JSONObject();
            try {
                //for testing
                String testQuery = "REGISTER QUERY Locations AS PREFIX ex:" +
                        " <http://myexample.org/> PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>" +
                        " SELECT ?s ?p ?o FROM STREAM <http://myexample.org/stream> [RANGE 30s STEP 10s]" +
                        " WHERE { ?s ?p . }";
                mQuery.setBody(testQuery);
                data.put("definition",mQuery.getBody());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            boolean isSend = Utills.sendPostRequest(getString(R.string.url_query,mUser),data.toString());

            if (isSend) return true;
            else return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                QueryDb queryDb = QueryDb.get(mContext);
                queryDb.addQuery(mQuery);
                QueriesFragment fragment = QueriesFragment.newInstance();
                getActivity().getSupportFragmentManager().popBackStack();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainer,fragment);
                transaction.commit();
                hideKeyboard();
            } else {
                Toast.makeText(mContext,R.string.error_creating_query,Toast.LENGTH_LONG).show();
            }
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
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
