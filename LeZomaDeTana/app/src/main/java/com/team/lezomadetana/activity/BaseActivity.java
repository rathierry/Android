package com.team.lezomadetana.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.view.Window;
import android.widget.Toast;

import com.team.lezomadetana.BuildConfig;
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

    // activity request codes
    public static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int CAMERA_GALLERY_IMAGE_REQUEST_CODE = 200;

    // key to store image path in savedInstance state
    public static final String KEY_IMAGE_STORAGE_PATH = "image_path";
    public static final int MEDIA_TYPE_IMAGE = 1;

    // bitmap sampling size
    public static final int BITMAP_SAMPLE_SIZE = 8;

    // gallery directory name to store the images or videos
    public static final String GALLERY_DIRECTORY_NAME = "LZTCamera";

    // image file extensions
    public static final String IMAGE_EXTENSION = "jpg";
    public static String imageStoragePath;

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // different navigation tag
    public static final String TAG_BUSINESS = "business";
    public static final String TAG_OFFER = "offer";
    public static final String TAG_CHAT = "chat";
    public static final String TAG_SETTING = "setting";

    // default/current navigation tag
    public static String CURRENT_TAG = TAG_BUSINESS;

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
            showLongToast(_context, "Current list = " + subsInfoList);
            for (SubscriptionInfo subscriptionInfo : subsInfoList) {
                if (subscriptionInfo != null) {
                    String number = subscriptionInfo.getNumber();
                    showLongToast(_context, "SIM number = " + number);
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
                showLongToast(this, "SIM card ID: " + simID);
            }
            if (telNumber != null) {
                showLongToast(this, "Phone number: " + telNumber);
            }
            if (IMEI != null) {
                showLongToast(this, "IMEI number: " + IMEI);
            }
        }
    }

    /**
     * Show long toast
     */
    protected void showLongToast(Context context, String message) {
        if (BuildConfig.DEBUG) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Show short toast
     */
    protected void showShortToast(Context context, String message) {
        if (BuildConfig.DEBUG) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
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
     * Show alert dialog
     */
    protected void showAlertDialog(String title, int icon, String message) {
        // hide previous alert dialog
        this.hideAlertDialog();
        // create
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        builder.setCancelable(false);
        builder.setIcon(icon);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, null);
        // show
        _alertDialog = builder.show();
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
     * Alerted user when his device does not support camera
     */
    protected void showNotSupportedCameraErrorDialog() {
        showAlertDialog(getResources().getString(R.string.app_camera_title), android.R.drawable.ic_dialog_alert, getResources().getString(R.string.app_camera_error));
    }

    /**
     * Get image in drawable
     */
    protected int getImage(String imageName) {
        int drawableResourceId = this.getResources().getIdentifier(imageName, "drawable", this.getPackageName());
        return drawableResourceId;
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}
