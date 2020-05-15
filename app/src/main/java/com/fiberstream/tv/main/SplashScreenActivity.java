package com.fiberstream.tv.main;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.Toast;

import com.fiberstream.tv.R;
import com.fiberstream.tv.app.MainActivity;

public class SplashScreenActivity extends Activity {

    private static final int TIMER_DELAY = 2000;
    private static final int move_activity = 3000;
    private static final int SPINNER_WIDTH = 100;
    private static final int SPINNER_HEIGHT = 100;

    private SplashScreenFragment mSplashFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        homePage();
//        resetPreferedLauncher();
    }

    private void homePage(){
        mSplashFragment = new SplashScreenFragment();
        getFragmentManager().beginTransaction().add(R.id.main_browse_fragment,mSplashFragment).commit();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSplashFragment.setKonten();
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent home=new Intent(SplashScreenActivity.this, MainActivity.class);
                        startActivity(home);
//                        overridePendingTransition(R, R.anim.exit_out_left);
                        finish();
                    }
                },move_activity);
            }
        }, TIMER_DELAY);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case 4:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
