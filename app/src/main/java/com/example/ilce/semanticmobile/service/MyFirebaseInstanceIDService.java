package com.example.ilce.semanticmobile.service;

/**
 * Created by Ilce on 8/14/2017.
 */

import android.content.SharedPreferences;
import android.util.Log;

import com.example.ilce.semanticmobile.R;
import com.example.ilce.semanticmobile.util.Utills;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    private static final String Preferences = "preferences";
    private static final String USER = "user";
    private SharedPreferences sharedPreferences;


    @Override
    public void onTokenRefresh() {
        sharedPreferences = getSharedPreferences(Preferences,MODE_PRIVATE);
        String user = sharedPreferences.getString(USER,null);

        if (user!=null) {
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            Log.e(TAG, "Refreshed token: " + refreshedToken);
            sendRegistrationToServer(refreshedToken,user);
        }
    }

    private void sendRegistrationToServer(String token,String user) {

        JSONObject data = new JSONObject();
        try {
            data.put("username",user);
            data.put("token", FirebaseInstanceId.getInstance().getToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        boolean isSend = Utills.sendPostRequest(getString(R.string.url_token),data.toString());

    }

}