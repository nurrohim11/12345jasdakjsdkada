package com.fiberstream.tv.app.settings;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.leanback.app.GuidedStepFragment;
import androidx.leanback.widget.GuidanceStylist;
import androidx.leanback.widget.GuidedAction;

import com.fiberstream.tv.R;

import java.util.List;

public class SettingsFragment extends GuidedStepFragment {

    private static final int ACTION_ID_WIFI = 1;
    private static final int ACTION_ID_SETTING = 2;

    @NonNull
    @Override
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
//        GuidanceStylist.Guidance guidance = new GuidanceStylist.Guidance(getString(R.string.dialog_example_title),
//                getString(R.string.dialog_example_description),
//                "", null);
//        return guidance;
        return new GuidanceStylist.Guidance(getString(R.string.dialog_example_title), "Selected menu settings for setting your android tv and this simple", "", ResourcesCompat.getDrawable(getResources(), R.drawable.logo_fiber, null));

    }

    @Override
    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        GuidedAction action = new GuidedAction.Builder()
                .id(ACTION_ID_SETTING)
                .title("Setting").build();
        actions.add(action);
        action = new GuidedAction.Builder()
                .id(ACTION_ID_WIFI)
                .title("Wifi").build();
        actions.add(action);
    }

    @Override
    public void onGuidedActionClicked(GuidedAction action) {
        if (ACTION_ID_WIFI == action.getId()) {
            Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
            startActivity(intent);
        } else if(ACTION_ID_SETTING == action.getId()){
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            startActivity(intent);
        }
    }
}

