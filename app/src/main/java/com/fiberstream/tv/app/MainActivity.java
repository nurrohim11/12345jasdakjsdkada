package com.fiberstream.tv.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.fiberstream.tv.BrowseErrorActivity;
import com.fiberstream.tv.R;
import com.fiberstream.tv.app.settings.model.SettingModel;
import com.fiberstream.tv.utils.ServerURL;
import com.fiberstream.tv.utils.Utils;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import co.id.gmedia.coremodul.ApiVolley;
import co.id.gmedia.coremodul.AppRequestCallback;
import co.id.gmedia.coremodul.SessionManager;

/*
 * Main Activity class that loads {@link MainFragment}.
 */
public class MainActivity extends Activity {
    SessionManager sessionManager;
    public static String device_token ="";
    private static final String TAG= "Mainactivity";
    private static final int TIMER_DELAY = 3000;
    private static final int TIMER_SCREEN = 0;
    private static final int SPINNER_WIDTH = 100;
    private static final int SPINNER_HEIGHT = 100;
    private final Handler mHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        saveDevice();
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
        if(!isOnline()) {
            Intent home=new Intent(MainActivity.this, BrowseErrorActivity.class);
            startActivity(home);
            finish();
        }
    }

    private void startBackgroundTimer() {
        error();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundTimer();
//        FirebaseApp.getInstance();
//        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
//            @Override
//            public void onSuccess(InstanceIdResult instanceIdResult) {
//                device_token = instanceIdResult.getToken();
//                sessionManager.saveFcmId(device_token);
//                Log.d(TAG,">>"+device_token);
//                try {
//                    saveFcmId();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }
}
