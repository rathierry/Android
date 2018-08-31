package com.team.lezomadetana.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.view.Window;
import android.widget.Toast;

import com.team.lezomadetana.R;

import java.util.List;

/**
 * Created by team on 28/08/2018.
 **/

public class BaseActivity extends AppCompatActivity {

    // ===========================================================
    // Constants
    // ===========================================================

    // regex
    public String nameRegex = "^[a-zA-Z-\\s\\w]+$";
    public String numberRegex = "^[0-9-]+$";
    public String emailRegex = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    // time
    public static int SPLASH_TIME_OUT = 2000;
    public static int LOADING_TIME_OUT = 3000;

    // take photo
    public static final int CAMERA_IMAGE_REQUEST_CODE = 1;
    public static final int GALLERY_IMAGE_REQUEST_CODE = 2;

    // url
    public static String URL_API_SERVER = "http://......................";

    // ===========================================================
    // Fields
    // ===========================================================

    public static Context _context;
    private ProgressDialog _progressDialog;
    private AlertDialog _alertDialog;
    private Dialog _dialog;
    private Typeface _font;

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
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        _context = BaseActivity.this;
        _dialog = new Dialog(_context, R.style.DialogTheme);
        _dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        _font = Typeface.createFromAsset(getAssets(), "fonts/Exo2.0_Regular.otf");
    }

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }*/

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

    /**
     * Getting the Phone Number, IMEI, and SIM Card ID
     */
    @SuppressLint("MissingPermission")
    protected void getSimCardInfo() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            SubscriptionManager sManager = (SubscriptionManager) _context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            List<SubscriptionInfo> subsInfoList = sManager.getActiveSubscriptionInfoList();
            Toast.makeText(_context, "Current list = " + subsInfoList, Toast.LENGTH_LONG).show();
            for (SubscriptionInfo subscriptionInfo : subsInfoList) {
                if (subscriptionInfo != null) {
                    String number = subscriptionInfo.getNumber();
                    Toast.makeText(_context, "SIM number = " + number, Toast.LENGTH_LONG).show();
                }
            }
        } else {
            TelephonyManager telephonyManager = (TelephonyManager) _context.getSystemService(Context.TELEPHONY_SERVICE);
            // get the SIM card ID
            String simID = telephonyManager.getSimSerialNumber();
            // get the phone number
            String telNumber = telephonyManager.getLine1Number();
            // get the IMEI number
            String IMEI = telephonyManager.getDeviceId();

            if (simID != null) {
                Toast.makeText(this, "SIM card ID: " + simID, Toast.LENGTH_LONG).show();
            }
            if (telNumber != null) {
                Toast.makeText(this, "Phone number: " + telNumber, Toast.LENGTH_LONG).show();
            }
            if (IMEI != null) {
                Toast.makeText(this, "IMEI number: " + IMEI, Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Overrides the pending Activity transition by performing the "Enter" animation.
     */
    protected void overridePendingTransitionEnter() {
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    /**
     * Overrides the pending Activity transition by performing the "Exit" animation.
     */
    protected void overridePendingTransitionExit() {
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    /**
     * Enable spinner
     */
    protected void showLoadingView(String message) {
        this.hideLoadingView();
        _progressDialog = new ProgressDialog(BaseActivity.this, R.style.AppTheme_Dark_Dialog);
        _progressDialog.setIndeterminate(true);
        _progressDialog.setMessage(message);
        _progressDialog.show();
    }

    /**
     * Disable spinner
     */
    protected void hideLoadingView() {
        if (_progressDialog != null && _progressDialog.isShowing()) {
            _progressDialog.dismiss();
        }
        _progressDialog = null;
    }

    /**
     * Hide alert dialog
     */
    protected void hideAlertDialog() {
        if (_alertDialog != null && _alertDialog.isShowing()) {
            _alertDialog.dismiss();
        }
        _alertDialog = null;
    }

    /**
     * Checking device has connecting in internet or not
     */
    public static boolean isConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected());
    }

    /**
     * Checking device has camera hardware or not
     */
    protected boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * Alerted user when his device does not support camera
     */
    protected void showNotSupportedCameraErrorDialog() {

        this.hideAlertDialog();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(false);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle("Erreur appareil photo");
        builder.setMessage("Désolé! Votre appareil ne supporte pas l\\'appareil photo.");
        builder.setPositiveButton(android.R.string.ok, null);

        _alertDialog = builder.show();

    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}
