package com.team.lezomadetana.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.team.lezomadetana.BuildConfig;
import com.team.lezomadetana.R;
import com.team.lezomadetana.model.receive.UserCredentialResponse;
import com.team.lezomadetana.utils.CircleTransform;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Created by team on 28/08/2018.
 **/

public class BaseActivity extends AppCompatActivity {

    // ===========================================================
    // Constants
    // ===========================================================

    // preferences
    public static final String PREFS_NAME = "PREFS";
    public static final String PREFS_KEY_USER = "KEY_USER";
    public static final String PREFS_KEY_ID_USER_FROM_SCAN = "KEY_ID_USER_FROM_SCAN";

    // shared pref
    public SharedPreferences sharedPrefForPaymentSendMoney;

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
    public static final String TAG_PAYMENT = "payment";
    public static final String TAG_PAYMENT_MENU = "payment_menu";
    public static final String TAG_CHAT = "chat";
    public static final String TAG_SETTING = "setting";

    // default/current navigation tag
    public static String CURRENT_TAG = TAG_BUSINESS;

    // url
    public static String ROOT_MDZ_USER_API = "http://user-api.madawin.mg";
    public static String ROOT_MDZ_API = "http://api.madawin.mg";
    public static String ROOT_TEST_API = "https://api.androidhive.info/json/";
    public static String APP_USER_NAME = "app";
    public static String APP_PASSWORD = "app-password";

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

    /**
     * Create basic authentication
     */
    public static String BasicAuth() {
        return "Basic " + Base64.encodeToString((APP_USER_NAME + ":" + APP_PASSWORD).getBytes(), Base64.NO_WRAP);
    }

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
        _progressDialog.setCancelable(false);
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

    /**
     * Save current user
     */
    public void saveCurrentUser(Context context, UserCredentialResponse user) {
        // pref
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        // settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        Gson gson = new Gson();
        user.setQr(encodeBitmap2String(generateQrCode(user.getId())));
        String json = gson.toJson(user);
        editor.putString(PREFS_KEY_USER, json);
        editor.commit();
    }

    /**
     * Get current user
     */
    public UserCredentialResponse getCurrentUser(Context context) {
        // pref
        SharedPreferences settings;

        // settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(BaseActivity.PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = settings.getString(BaseActivity.PREFS_KEY_USER, "");
        UserCredentialResponse user = gson.fromJson(json, UserCredentialResponse.class);

        return user;
    }

    /**
     * Confirm log out current user
     */
    protected void logoutUser() {
        new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setTitle(getResources().getString(R.string.app_name))
                .setMessage(getResources().getString(R.string.app_logout_message))
                .setIcon(R.drawable.ic_exit_to_app_black)
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.user_login_forgot_pass_btn_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        showLoadingView(getResources().getString(R.string.app_spinner));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                hideLoadingView();
                                disconnectUser();
                            }
                        }, BaseActivity.LOADING_TIME_OUT);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.user_login_forgot_pass_btn_cancel), null)
                .show();
    }

    /**
     * Disconnect current user
     */
    public void disconnectUser() {
        // pref
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();

        startActivity(new Intent(BaseActivity.this, UserLoginActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    public static String[] getNames(Class<? extends Enum<?>> e) {
        return Arrays.toString(e.getEnumConstants()).replaceAll("^.|.$", "").split(", ");
    }

    public Bitmap generateQrCode(String text) {

        Bitmap bit = null;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            bit = bitmap;
        } catch (WriterException e) {
            e.printStackTrace();

        }


        return bit;

    }

    public String encodeBitmap2String(Bitmap bitmap) {
        String stringBitmap = null;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

        byte[] b = byteArrayOutputStream.toByteArray();

        stringBitmap = Base64.encodeToString(b, Base64.DEFAULT);

        return stringBitmap;
    }

    public Bitmap decodeString2Bitmap(String str) {
        Bitmap bitmap = null;

        byte[] b = Base64.decode(str.getBytes(), Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);

        return bitmap;
    }

    protected void applyProfilePicture(ImageView imgProfile, String url) {
        // verification
        if (!TextUtils.isEmpty(url)) {
            Glide.with(this)
                    .load(url)
                    .thumbnail(0.5f)
                    .crossFade()
                    .transform(new CircleTransform(this))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.mipmap.ic_launcher_round)
                    .into(imgProfile);
            imgProfile.setColorFilter(null);
        } else {
            imgProfile.setImageResource(R.mipmap.ic_launcher_round);
        }
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}
