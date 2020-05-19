package com.fiberstream.tv.services;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

public class MyAccessibilityService extends AccessibilityService {

    private String TAG = MyAccessibilityService.class.getSimpleName();

    @Override
    public boolean onKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        if (action == KeyEvent.ACTION_UP) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                Log.d("Hello", "KeyUp");
            } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                Log.d("Hello", "KeyDown");
            }
            return true;
        } else {
            return super.onKeyEvent(event);
        }
    }

    /**
     * Passes information to AccessibilityServiceInfo.
     */
    @Override
    public void onServiceConnected() {
        Log.v(TAG, "on Service Connected");
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.packageNames = new String[] { "com.camacc" };
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.notificationTimeout = 100;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN;
        setServiceInfo(info);

    }// end onServiceConnected

    /**
     * Called on an interrupt.
     */
    @Override
    public void onInterrupt() {
        Log.v(TAG, "***** onInterrupt");

    }// end onInterrupt

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // TODO Auto-generated method stub
        Log.d(TAG,"on AccessbilityEvent");

    }
}// end Accessibility_Service class
