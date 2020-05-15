package com.fiberstream.tv.services;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.fiberstream.tv.utils.ServerURL;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import co.id.gmedia.coremodul.ApiVolley;
import co.id.gmedia.coremodul.AppRequestCallback;

public class GpsTracker extends Service {

    private Context mContext;

    // Flag for GPS status
    boolean isGPSEnabled = false;

    // Flag for network status
    boolean isNetworkEnabled = false;

    // Flag for GPS status
    boolean canGetLocation = false;

    Location location; // Location
    double latitude; // Latitude
    double longitude; // Longitude
    String device_token="";
    int level_wifi =0;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1000; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    Activity activity;

    private static final String TAG = "LocationService";
        private final int timerTime = 1000 * 60 * 10; //10 minutes
//    private final int timerTime = 10000; //10 detik
    private Timer timer = new Timer();

    public GpsTracker() {
    }

    public GpsTracker(Context context, Activity activity) {
        this.mContext = context;
        this.activity = activity;
        getLocation();
    }

    public Location getLocation() {
        try {


            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            // Getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // Getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // No network provider is enabled
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    int requestPermissionsCode = 50;

                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, mLocationListener);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
            }
            // If GPS enabled, get latitude/longitude using GPS Services
            if (isGPSEnabled) {
                if (location == null) {
                    if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 50);

                    } else {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, mLocationListener);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {

                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new mainTask(), 1000, timerTime);
        return location;
    }


    @Override
    public void onDestroy() {
        if (timer != null) {
            timer.cancel();
        }

        stopForeground(true);
        super.onDestroy();
    }

    class mainTask extends TimerTask {
        public void run()
        {
            Log.d(TAG, "onLocationChanged: " +latitude+" , "+ longitude);
            if(latitude != 0 && longitude != 0){

                ConnectivityManager cm = (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if(activeNetwork != null){
                    if(activeNetwork.getType()== ConnectivityManager.TYPE_WIFI){
                        WifiManager wifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
                        int numberOfLevels = 100;
                        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                        int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
                        level_wifi = level;
                    }else{
                        level_wifi = 0;
                    }
                }else{
                    level_wifi = 0;
                }
                saveLocation();
            }
        }
    }

    private void saveLocation(){

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                device_token = instanceIdResult.getToken();
                Log.d(TAG,"firebase "+device_token);
                JSONObject jBody = new JSONObject();
                try {
                    jBody.put("fcm_id",device_token);
                    jBody.put("sinyal_wifi",level_wifi);
                    jBody.put("latitude",latitude);
                    jBody.put("longitude",longitude);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                new ApiVolley(activity, jBody, "post", ServerURL.post_service_client,
                        new AppRequestCallback(new AppRequestCallback.ResponseListener() {
                            @Override
                            public void onSuccess(String response, String message) {
                                Log.d(TAG,"onSuccess "+message);
                            }
                            @Override
                            public void onEmpty(String message) {
                                Log.d(TAG,"onEmpty "+message);
                            }
                            @Override
                            public void onFail(String message) {
                                Log.d(TAG,"onFail "+message);
                            }
                        })
                );
            }
        });
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app.
     * */
    public void stopUsingGPS() {

    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {

            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }


    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/Wi-Fi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }


    /**
     * Function to show settings alert dialog.
     * On pressing the Settings button it will launch Settings Options.
     * */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing the Settings button.
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // On pressing the cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
