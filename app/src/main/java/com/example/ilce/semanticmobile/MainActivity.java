package com.example.ilce.semanticmobile;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.ilce.semanticmobile.fragment.QueriesFragment;
import com.example.ilce.semanticmobile.fragment.TasksFragment;
import com.example.ilce.semanticmobile.fragment.UsersFragment;
import com.example.ilce.semanticmobile.service.LocationService;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String Preferences = "preferences";
    private SharedPreferences sharedPreferences;
    private static final String USER = "user";
   // private SensorManager mSensorManager;
    private String user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPreferences = getSharedPreferences(Preferences,MODE_PRIVATE);
        user = sharedPreferences.getString(USER,"");
        onNewIntent(getIntent());

       // mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
       // getAllSensors();
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
        if (fragment == null) {
            fragment = TasksFragment.newInstance();
            fragment.setEnterTransition(new Fade());
            fragment.setExitTransition(new Explode());
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        }
        
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView =  navigationView.getHeaderView(0);
        TextView nav_user = (TextView)hView.findViewById(R.id.nav_header_user);
        nav_user.setText(user);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem toggleItem = menu.findItem(R.id.menu_item_toggle_sending);
        if (isMyServiceRunning(LocationService.class))
            toggleItem.setTitle(R.string.action_stop_sending);
        else toggleItem.setTitle(R.string.action_start_sending);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu_item_toggle_sending) {
            startBackgroundService();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startBackgroundService() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(
                            this,Manifest.permission.ACCESS_COARSE_LOCATION)) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(this);
                builder.setMessage(R.string.permission_explanation);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       ActivityCompat.requestPermissions(getParent(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}
                       ,LOCATION_PERMISSION_REQUEST_CODE);
                    }
                });
                builder.create().show();
            } else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}
                        ,LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            //start background service
            startService();
        }
    }


    private void startService() {
        if (!isMyServiceRunning(LocationService.class)) {
            startService(new Intent(this,LocationService.class));
        } else {
            stopService(new Intent(this,LocationService.class));
        }
        invalidateOptionsMenu();
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
        if (id == R.id.nav_tasks) {
            if (!(fragment instanceof TasksFragment)) {
                TasksFragment tasksFragment = TasksFragment.newInstance();
                tasksFragment.setEnterTransition(new Explode());
                tasksFragment.setExitTransition(new Slide());
                getSupportFragmentManager().popBackStack();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainer,tasksFragment);
                transaction.commit();
            }
        } else if (id == R.id.nav_gueries) {
            if (!(fragment instanceof QueriesFragment)) {
                QueriesFragment queriesFragment = QueriesFragment.newInstance();
                queriesFragment.setEnterTransition(new Explode());
                queriesFragment.setExitTransition(new Slide());
                getSupportFragmentManager().popBackStack();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainer,queriesFragment);
                transaction.commit();
            }
        } else if(id == R.id.nav_users) {
            if (!(fragment instanceof UsersFragment)) {
                UsersFragment usersFragment = UsersFragment.newInstance();
                getSupportFragmentManager().popBackStack();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainer,usersFragment);
                transaction.commit();
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED &&
                        grantResults[1]==PackageManager.PERMISSION_GRANTED) {

                    startService();
                }
                return;
        }
    }



    @Override
    public void onNewIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras!=null) {
            if (extras.containsKey("ARG1")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Notification");
            builder.setMessage("There is a user near you that works on the same task");
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
                builder.create().show();
                intent.removeExtra("ARG1");
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public String getUser() {
        return user;
    }
}
