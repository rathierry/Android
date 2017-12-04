package com.rathierry.userinactivityapp.activity;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.rathierry.userinactivityapp.R;

import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final long DISCONNECT_TIMEOUT = 5000; // 1 minute in milliseconds

    // ===========================================================
    // Fields
    // ===========================================================

    private Boolean imagePUBOnFullScreen = false;

    private Handler disconnectHandler = new Handler() {
        public void handleMessage(Message msg) {
        }
    };

    private Runnable disconnectCallback = new Runnable() {
        @Override
        public void run() {
            // TODO: Perform any required operation on timer fired

            imagePUBOnFullScreen = true;

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Timer")
                    .setMessage("Detected inactivity")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            imagePUBOnFullScreen = false;

                            // restart timer
                            resetDisconnectTimer();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .show();
        }
    };

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
        setContentView(R.layout.activity_main);

        // toolBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle(R.string.activity_toolbar_title);
    }

    @Override
    public void onUserInteraction() {
        resetDisconnectTimer();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!imagePUBOnFullScreen) {
            resetDisconnectTimer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        stopDisconnectTimer();
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

    private void resetDisconnectTimer() {
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);

        TextView textView = (TextView) this.findViewById(R.id.textView);
        textView.setText("Restarted timer at :" + DateFormat.getDateTimeInstance().format(new Date()));
    }

    private void stopDisconnectTimer() {
        disconnectHandler.removeCallbacks(disconnectCallback);
    }

    /*
    Represent an action performed by user inside your app
     */
    public void buttonClicked(View sender) {
        Toast.makeText(MainActivity.this, "Button clicked", Toast.LENGTH_LONG).show();
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================

}