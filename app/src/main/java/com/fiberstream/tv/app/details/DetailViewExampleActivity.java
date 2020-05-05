package com.fiberstream.tv.app.details;

import android.app.Activity;
import android.os.Bundle;

import com.fiberstream.tv.R;

public class DetailViewExampleActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_example);

        if (savedInstanceState == null) {
            DetailViewExampleFragment fragment = new DetailViewExampleFragment();
            getFragmentManager().beginTransaction()
                    .replace(R.id.details_fragment, fragment)
                    .commit();
        }
    }
}
