package com.fiberstream.tv.app.details;

import android.app.Activity;
import android.os.Bundle;

import com.fiberstream.tv.R;

public class DetailViewExampleWithVideoBackgroundActivity extends Activity {

    static final int BUY_MOVIE_REQUEST = 987;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_example);

        if (savedInstanceState == null) {
            DetailViewExampleWithVideoBackgroundFragment fragment =
                    new DetailViewExampleWithVideoBackgroundFragment();
            getFragmentManager().beginTransaction()
                    .replace(R.id.details_fragment, fragment)
                    .commit();
        }
    }
}
