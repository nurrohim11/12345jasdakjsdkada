package com.fiberstream.tv.main;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;

import android.util.Log;

import com.fiberstream.tv.R;
import com.fiberstream.tv.utils.ServerURL;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import co.id.gmedia.coremodul.ApiVolley;
import co.id.gmedia.coremodul.AppRequestCallback;
import co.id.gmedia.coremodul.SessionManager;

public class SplashScreenFragment extends androidx.leanback.app.ErrorFragment {

    private static final String TAG = "SplashScreenFragment";
    private static final boolean TRANSLUCENT = true;
    String device_token ="";
    SessionManager  sessionManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(getActivity());

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                device_token = instanceIdResult.getToken();
                sessionManager.saveFcmId(device_token);
                getProfileDevice();
            }
        });

    }

    private void getProfileDevice(){
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("fcm_id",sessionManager.getFcmid());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new ApiVolley(getActivity(), jBody, "post", ServerURL.url_profile_device,
                new AppRequestCallback(new AppRequestCallback.ResponseListener() {
                    @Override
                    public void onSuccess(String response, String message) {
                        try {
                            Log.d(TAG,">> "+response);
                            JSONObject object = new JSONObject(response);
                            String nama = object.getString("nama");
                            if(!nama.equals("")){
                                String result_nama = nama.substring(0, 1).toUpperCase() + nama.substring(1).toLowerCase();
                                setTitle(result_nama);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

    void setKonten() {
//        setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.lb_ic_sad_cloud));
//        setMessage(getResources().getString(R.string.please_wait));
        setDefaultBackground(TRANSLUCENT);
        Drawable bg = ResourcesCompat.getDrawable(getResources(), R.drawable.splash, null);
        setBackgroundDrawable(bg);
    }
}
