package com.rathierry.mychatapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.rathierry.mychatapplication.R;
import com.rathierry.mychatapplication.manager.SessionManager;

public class SplashActivity extends BaseActivity {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private SessionManager _sessionManager;

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods from SuperClass
    // ===========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _sessionManager = new SessionManager(getApplicationContext());

        // full screen decor flags
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
        );

        setContentView(R.layout.activity_splash);

        // show loader
        findViewById(R.id.ball_spin_fade_loader).setVisibility(View.VISIBLE);

        //creating thread that will sleep for 10 seconds
        Thread t = new Thread() {
            public void run() {
                try {
                    // sleep thread for 10 seconds, time in milliseconds
                    sleep(SPLASH_TIME_OUT);

                    // start new activity
                    String status = _sessionManager.getPreferences(SplashActivity.this, _sessionManager.KEY_SESSION_STATUS);
                    if (status.equals("1")) {
                        startActivity(new Intent(SplashActivity.this, UsersActivity.class));
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    } else {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }

                    // destroying Splash activity
                    finish();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        //start thread
        t.start();
    }

    // ===========================================================
    // Methods for Interfaces
    // ===========================================================

    // ===========================================================
    // Public Methods
    // ===========================================================

    // ===========================================================
    // Private Methods
    // ===========================================================

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================

}