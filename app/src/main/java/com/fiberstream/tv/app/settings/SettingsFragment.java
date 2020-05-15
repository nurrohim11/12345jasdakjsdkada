package com.fiberstream.tv.app.settings;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.leanback.app.GuidedStepFragment;
import androidx.leanback.widget.GuidanceStylist;
import androidx.leanback.widget.GuidedAction;

import com.fiberstream.tv.R;

import java.util.List;

public class SettingsFragment extends GuidedStepFragment {

    private static final int ACTION_ID_POSITIVE = 1;
    private static final int ACTION_ID_NEGATIVE = ACTION_ID_POSITIVE + 1;

    @NonNull
    @Override
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        GuidanceStylist.Guidance guidance = new GuidanceStylist.Guidance(getString(R.string.dialog_example_title),
                getString(R.string.dialog_example_description),
                "", null);
        return guidance;
    }

    @Override
    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        GuidedAction action = new GuidedAction.Builder()
                .id(ACTION_ID_POSITIVE)
                .title("Wifi").build();
        actions.add(action);
        action = new GuidedAction.Builder()
                .id(ACTION_ID_NEGATIVE)
                .title(getString(R.string.dialog_example_button_negative)).build();
        actions.add(action);
    }

    @Override
    public void onGuidedActionClicked(GuidedAction action) {
        if (ACTION_ID_POSITIVE == action.getId()) {
            Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), R.string.dialog_example_button_toast_negative_clicked,
                    Toast.LENGTH_SHORT).show();
        }
        getActivity().finish();
    }
}

