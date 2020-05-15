package com.fiberstream.tv.app.settings;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.leanback.app.GuidedStepFragment;

public class SettingsActivity extends Activity {

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#21272A")));

        if (savedInstanceState == null) {
            GuidedStepFragment fragment = new SettingsFragment();
            GuidedStepFragment.addAsRoot(this, fragment, android.R.id.content);
        }
    }
}