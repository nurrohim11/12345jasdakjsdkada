package com.fiberstream.tv.services;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.graphics.Region;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;

import com.fiberstream.tv.R;

import java.util.ArrayList;
import java.util.List;

public class RemoteLockService extends AccessibilityService {

    private static final String LOG_TAG = "MagnificationService";
    private static final String TAG = "RemoteLockService";

    @Override
    public void onAccessibilityEvent (AccessibilityEvent event) {
        final int eventType = event.getEventType();
        switch(eventType) {
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                Log.d(TAG,"clicked");
                break;
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                Log.d(TAG,"focused");
                break;
        }
    }

    @Override
    public void onInterrupt() {

    }
    @Override
    public boolean onKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        Log.d(TAG,String.valueOf(keyCode));

//        switch (keyCode) {
//            case KeyEvent.KEYCODE_BACK:
//                Log.e(TAG, "Back");
//
//            case KeyEvent.KEYCODE_HOME:
//                Log.e(TAG, "Home");
//                return false;
//        }
        return super.onKeyEvent(event);
    }

}
