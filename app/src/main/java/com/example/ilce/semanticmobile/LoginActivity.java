package com.example.ilce.semanticmobile;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ilce.semanticmobile.util.Utills;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {



    private static final String Preferences = "preferences";
    private static final String USER = "user";
    private static final String PASSWORD = "password";
    private UserLoginTask mAuthTask = null;
    private SharedPreferences sharedPreferences;
    private static final String testUser = "Test";
    private boolean showDialog = false;

    // UI references.
    private AutoCompleteTextView mUserNameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mUserNameView = (AutoCompleteTextView) findViewById(R.id.username);
        sharedPreferences = getSharedPreferences(Preferences,MODE_PRIVATE);



//        if (args!=null) showDialog=true;
//        if (showDialog) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("Notification");
//            builder.setMessage("There is a user near you that works on the same task");
//            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    showDialog=false;
//                }
//            });
//            builder.create();
//        }


        String user = sharedPreferences.getString(USER,"");
        if (!user.equals("")) {
            Intent intent = new Intent(this,MainActivity.class);
            //intent.putExtra("user",user);
            startActivity(intent);
        }

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }




    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mUserNameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUserNameView.getText().toString();
        String password = mPasswordView.getText().toString();

        if (username.equals(testUser)) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("user", username);
            startActivity(intent);
        } else {
            boolean cancel = false;
            View focusView = null;

            // Check for a valid password, if the user entered one.
            if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
                mPasswordView.setError(getString(R.string.error_invalid_password));
                focusView = mPasswordView;
                cancel = true;
            }

            // Check for a valid  username.
            if (TextUtils.isEmpty(username)) {
                mUserNameView.setError(getString(R.string.error_field_required));
                focusView = mUserNameView;
                cancel = true;
            } else if (!isUserNameValid(username)) {
                mUserNameView.setError(getString(R.string.error_invalid_username));
                focusView = mUserNameView;
                cancel = true;
            }

            if (cancel) {
                // There was an error; don't attempt login and focus the first
                // form field with an error.
                focusView.requestFocus();
            } else {
                String user = sharedPreferences.getString(USER,"");
                if (!user.equals("")) {
                    String pass = sharedPreferences.getString(PASSWORD,"");
                    if (username.equals(user)) {
                        if (!pass.equals("")) {
                            if (pass.equals(password)) {
                                // Toast.makeText(this,"Welcome back "+user,Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(this,MainActivity.class);
                                //intent.putExtra("user",username);
                                startActivity(intent);
                            } else Toast.makeText(this,R.string.error_invalid_login,Toast.LENGTH_LONG).show();
                        } else {
                            //Toast.makeText(this,"Welcome back "+user,Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(this,MainActivity.class);
                           // intent.putExtra("user",username);
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(this,R.string.error_invalid_login,Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Show a progress spinner, and kick off a background task to
                    // perform the user login attempt.
                    showProgress(true);
                    mAuthTask = new UserLoginTask(username,password,this);
                    mAuthTask.execute((Void) null);
                }
            }
        }
    }

    private boolean isUserNameValid(String username) {
        return username.length()>3;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUserName;
        private final String mPassword;
        private Context mContext;

        UserLoginTask(String username,String password,Context context) {
            mUserName = username;
            mPassword = password;
            mContext = context;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            JSONObject data = new JSONObject();
            try {
                data.put("username",mUserName);
                data.put("token", FirebaseInstanceId.getInstance().getToken());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            boolean isSend = Utills.sendPostRequest(getString(R.string.url_user),data.toString());

            if (isSend) return true;
            else return false;

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);

            if (success) {
                SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
                preferencesEditor.putString(USER,mUserName);
                if (mPassword!=null && !mPassword.equals("")) {
                    preferencesEditor.putString(PASSWORD,mPassword);
                }
                preferencesEditor.apply();
                Toast.makeText(mContext,"Login succesfull with username: "+mUserName,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext,MainActivity.class);
               // intent.putExtra("user",mUserName);
                startActivity(intent);
            } else {
                Toast.makeText(mContext,"Login was unsuccesfull, Try again !",Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
    }

}

