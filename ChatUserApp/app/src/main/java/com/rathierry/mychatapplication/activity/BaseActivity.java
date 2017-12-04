package com.rathierry.mychatapplication.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.rathierry.mychatapplication.R;

/**
 * Created by ThierryNakay on 21/02/17.
 */
public class BaseActivity extends AppCompatActivity {

    // ===========================================================
    // Constants
    // ===========================================================

    public final static int SPLASH_TIME_OUT = 1500;

    public String rootUrlUserList = "https://android-chat-app-e711d.firebaseio.com/users";
    public String rootUrlUserJson = "https://android-chat-app-e711d.firebaseio.com/users.json";
    public String rootUrlUserMessage = "https://android-chat-app-e711d.firebaseio.com/messages/";
    public String userOnline = "online";

    // ===========================================================
    // Fields
    // ===========================================================

    private ProgressDialog _progressDialog;
    private AlertDialog _alertDialog;

    public static Context applicationContext;

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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        applicationContext = getApplicationContext();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (_progressDialog != null && _progressDialog.isShowing()) {
            _progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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

    protected void showLoadingView() {
        _progressDialog = new ProgressDialog(this, R.style.WidgetProgressBarSmall);
        _progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        _progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        _progressDialog.setCancelable(false);
        _progressDialog.show();
    }

    protected void showLoadingView(String message) {
        _progressDialog = new ProgressDialog(this);
        _progressDialog.setMessage(message);
        _progressDialog.setCancelable(false);
        _progressDialog.show();
    }

    protected void hideLoadingView() {
        if (_progressDialog != null && _progressDialog.isShowing()) {
            _progressDialog.dismiss();
        }
        _progressDialog = null;
    }

    protected static boolean isConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected());
    }

    protected void showDefaultToastMessage(String toastMessage) {
        Toast.makeText(BaseActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
    }

    protected void showDefaultError() {
        this.hideAlertDialog();
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);

        builder.setCancelable(false);
        builder.setTitle(R.string.default_error_title);
        builder.setMessage(R.string.default_error_message);
        builder.setPositiveButton(android.R.string.ok, null);

        _alertDialog = builder.show();
    }

    protected void showInternetConnexionError() {
        this.hideAlertDialog();
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);

        builder.setCancelable(false);
        builder.setTitle(R.string.internet_connexion_error_title);
        builder.setMessage(R.string.internet_connexion_error_message);
        builder.setPositiveButton(android.R.string.ok, null);

        _alertDialog = builder.show();
    }

    protected void showCustomMessage(String title, String message) {
        this.hideAlertDialog();
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);

        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, null);

        _alertDialog = builder.show();
    }

    protected void hideAlertDialog() {
        if (_alertDialog != null && _alertDialog.isShowing()) {
            _alertDialog.dismiss();
        }
        _alertDialog = null;
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================

}