package com.fiberstream.tv.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.fiberstream.tv.BrowseErrorActivity;
import com.fiberstream.tv.R;
import com.fiberstream.tv.utils.ServerURL;
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
        sessionManager = new SessionManager(this);
    }

    private void error(){
        ConnectivityManager connManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mInternet = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo mBluetooth = connManager.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH);
        NetworkInfo mEthernet = connManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        if(mWifi.isConnected() || mInternet.isConnected() || mBluetooth.isConnected() || mEthernet.isConnected()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent home=new Intent(MainActivity.this, BrowseErrorActivity.class);
                    startActivity(home);
                    finish();
                }
            },TIMER_SCREEN);
        }
    }

    private void startBackgroundTimer() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                error();
            }
        }, TIMER_SCREEN);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundTimer();
        FirebaseApp.getInstance();
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                device_token = instanceIdResult.getToken();
                sessionManager.saveFcmId(device_token);
                Log.d(TAG,">>"+device_token);
                try {
                    saveFcmId();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void saveFcmId() throws JSONException {
        JSONObject jBody = new JSONObject();
        jBody.put("fcm_id",device_token);
        new ApiVolley(this, jBody, "post", ServerURL.post_fcmid,
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
}
