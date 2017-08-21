package com.example.ilce.semanticmobile.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.ilce.semanticmobile.R;
import com.example.ilce.semanticmobile.util.Utills;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ilce on 8/14/2017.
 */

public class LocationService extends Service implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG="LocationService";
    private final String prefix = "http://myexample.org/";
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private static final String Preferences = "preferences";
    private SharedPreferences sharedPreferences;
    private static final String ACTIVE_TASK = "activeTask";
    private static final String USER = "user";

    public LocationService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).
                        addConnectionCallbacks(this).
                        addOnConnectionFailedListener(this)
                .build();

        sharedPreferences = getSharedPreferences(Preferences,MODE_PRIVATE);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mGoogleApiClient.connect();
        Toast.makeText(this, "Location services started", Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onLocationChanged(Location location) {
        System.out.println("LocationService.onLocationChanged");
        //Toast.makeText(this, "LocationService.onLocationChanged", Toast.LENGTH_SHORT).show();
        float speed = location.getSpeed();
        Toast.makeText(this, "Your current speed is: "+speed+"m/s", Toast.LENGTH_SHORT).show();
        double[] eastingNorthing = Utills.getEastingNorthing(location.getLatitude(),location.getLongitude());
       // Toast.makeText(this, "The easting and norting are: "+eastingNorthing[0]+" "+eastingNorthing[1], Toast.LENGTH_SHORT).show();

        String user = sharedPreferences.getString(USER,"");
        if (user.equals("")) user = "Test";
        String activeTask = sharedPreferences.getString(ACTIVE_TASK,"");

        SendDataTask task = new SendDataTask(user,activeTask,this,eastingNorthing[0],eastingNorthing[1]);
        task.execute((Void) null);
    }

    @Override
    public void onConnected(Bundle bundle) {

        Log.e(TAG, "GoogleApiClient IS Connected !");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30000); // Update location every 30 seconds

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            Location loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.e(TAG, "GoogleApiClient connection failed: " + result.toString());
    }


    @Override
    public void onDestroy() {

        Toast.makeText(this, "Location services stopped", Toast.LENGTH_LONG).show();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            Location loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }

        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    public class SendDataTask extends AsyncTask<Void, Void, Boolean> {
        private final String user;
        private final String activeTask;
        private Context context;
        private final double easting;
        private final double northing;

        public SendDataTask(String user,String task,Context context,double easting,double northing) {
            this.user=user;
            this.activeTask=task;
            this.context=context;
            this.easting=easting;
            this.northing=northing;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean isSend;

            String subject = prefix+"user/"+user;
            String property1 = prefix+"worksOn";
            String property2 = prefix+"easting";
            String property3 = prefix+"northing";
            String object = prefix+"task/"+activeTask;

            //send first batch
            JSONObject data = new JSONObject();
            try {
                data.put("subject",subject);
                data.put("predicate",property1);
                data.put("object",object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            isSend = Utills.sendPostRequest(getString(R.string.url_event),data.toString());

            //send second batch
            data = new JSONObject();
            try {
                data.put("subject",subject);
                data.put("predicate",property2);
                data.put("object",easting);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            isSend = Utills.sendPostRequest(getString(R.string.url_event),data.toString());
            //send third batch
            data = new JSONObject();
            try {
                data.put("subject",subject);
                data.put("predicate",property3);
                data.put("object",northing);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            isSend = Utills.sendPostRequest(getString(R.string.url_event),data.toString());


            return isSend;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                Toast.makeText(context,"Data send succesfully",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context,"Data NOT send succesfully",Toast.LENGTH_SHORT).show();
            }
        }
    }

}