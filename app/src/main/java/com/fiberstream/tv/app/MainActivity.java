package com.fiberstream.tv.app;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.fiberstream.tv.BrowseErrorActivity;
import com.fiberstream.tv.R;
import com.fiberstream.tv.app.apps.AppsActivity;
import com.fiberstream.tv.app.register.RegisterActivity;
import com.fiberstream.tv.app.settings.SettingsActivity;
import com.fiberstream.tv.services.GpsTracker;
import com.fiberstream.tv.services.MyAccessibilityService;
import com.fiberstream.tv.utils.ServerURL;
import com.fiberstream.tv.utils.Utils;
import com.google.firebase.FirebaseApp;

import org.json.JSONException;
import org.json.JSONObject;

import co.id.gmedia.coremodul.ApiVolley;
import co.id.gmedia.coremodul.AppRequestCallback;
import co.id.gmedia.coremodul.SessionManager;

/*
 * Main Activity class that loads {@link MainFragment}.
 */
public class MainActivity extends Activity  {

    SessionManager sessionManager;
    public static String device_token ="";
    private static final String TAG= "Mainactivity";
    private static final int TIMER_DELAY = 3000;
    private static final int TIMER_SCREEN = 0;
    private static final int SPINNER_WIDTH = 100;
    private static final int SPINNER_HEIGHT = 100;
    private final Handler mHandler = new Handler();
    public static Activity activity;
    Context mContext;
    GpsTracker gps;
    int start =0;
    int counter =1;
    double latitude,longitude;
    int s =0;
    int c =1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        mContext = this;
        start = 0;
        s= 0;

        FirebaseApp.initializeApp(this);
        saveDevice();

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        } else {
            gps = new GpsTracker(mContext, MainActivity.this);

            // Check if GPS enabled
            if (gps.canGetLocation()) {

                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
            } else {
                // Can't get location.
                // GPS or network is not enabled.
                // Ask user to enable GPS/network in settings.
                gps.showSettingsAlert();
            }
        }

        sessionManager = new SessionManager(this);
    }


    private void saveDevice(){
        JSONObject jBody = new JSONObject();
        try {
            String deviceName = Settings.Global.getString(getContentResolver(), "device_name");
            int deviceOs = android.os.Build.VERSION.SDK_INT;
            String model = Settings.Global.getString(getContentResolver(), "model");
            Log.d(">>>>>>", Utils.deviceName(getApplicationContext()));
            jBody.put("model_name",deviceName);
            jBody.put("model_os",deviceOs);
            jBody.put("model_device",model);
            jBody.put("model_sn","");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new ApiVolley(this, jBody, "post", ServerURL.post_device,
                new AppRequestCallback(new AppRequestCallback.ResponseListener() {
                    @Override
                    public void onSuccess(String response, String message) {
                    }
                    @Override
                    public void onEmpty(String message) {
                    }
                    @Override
                    public void onFail(String message) {
                    }
                })
        );
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    private void error(){
        Log.d(TAG,String.valueOf(isOnline()));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!isOnline()) {
                    Intent home=new Intent(MainActivity.this, BrowseErrorActivity.class);
                    startActivity(home);
                    finish();
                }
            }
        },3000);
    }

    private void startBackgroundTimer() {
        error();
    }

    @Override
    protected void onResume() {
        super.onResume();
        counter = 0;
        startBackgroundTimer();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the

                    // contacts-related task you need to do.

                    gps = new GpsTracker(mContext, MainActivity.this);

                    // Check if GPS enabled
                    if (gps.canGetLocation()) {

                        latitude = gps.getLatitude();
                        longitude = gps.getLongitude();
                    } else {
                        // Can't get location.
                        // GPS or network is not enabled.
                        // Ask user to enable GPS/network in settings.
                        gps.showSettingsAlert();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

//                    Toast.makeText(mContext, "You need to grant permission", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            counterBack();
            return true;
        }else if(keyCode == KeyEvent.KEYCODE_HOME){
            Log.d(TAG,"Keycode home");
            return true;
        }else if(keyCode == 20){
            counterAppsDrawer();
        }else if (keyCode == 19){
            s = 0;
        }else if(keyCode == 22){
            s = 0;
        }else if(keyCode == 21){
            s = 0;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void counterAppsDrawer(){
        s+=c;
        if(s == 10){
            Log.d(TAG, String.valueOf(s));
            Intent intent = new Intent(getBaseContext(),
                    AppsActivity.class);
            s = 0;
            startActivity(intent);
        }
    }

    private void counterBack(){
        start+=counter;
        if(start == 10){
            Intent intent = new Intent(getBaseContext(),
                    RegisterActivity.class);
            start = 0;
            startActivity(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
