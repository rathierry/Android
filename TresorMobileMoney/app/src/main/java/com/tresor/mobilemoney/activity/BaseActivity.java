package com.tresor.mobilemoney.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tresor.mobilemoney.R;

import java.util.Calendar;

/**
 * Created by Priscilla on 29/08/2017.
 */

public class BaseActivity extends AppCompatActivity {

    // ===========================================================
    // Constants
    // ===========================================================

    public static int SPLASH_TIME_OUT = 2000;
    public static int LOADING_TIME_OUT = 1000;

    public static final int CAMERA_IMAGE_REQUEST_CODE = 1;
    public static final int GALLERY_IMAGE_REQUEST_CODE = 2;

    public static int MAX_LENGHT_UserRegisterEditTextCINNumber = 12;
    public static int MAX_LENGHT_UserRegisterEditTextPhoneNumber = 10;

    public static String URL_API_SERVER = "http://......................";

    // ===========================================================
    // Fields
    // ===========================================================

    private Context _context;

    private ProgressDialog _progressDialog;
    private AlertDialog _alertDialog;
    private Dialog _dialog;

    private Typeface _font;

    private int mYear, mMonth, mDay;

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

        //
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

    public class FontChangeCrawler {
        private Typeface typeface;

        public FontChangeCrawler(Typeface typeface) {
            this.typeface = typeface;
        }

        public FontChangeCrawler(AssetManager assets, String assetsFontFileName) {
            typeface = Typeface.createFromAsset(assets, assetsFontFileName);
        }

        public void replaceFonts(ViewGroup viewTree) {
            View child;
            for (int i = 0; i < viewTree.getChildCount(); ++i) {
                child = viewTree.getChildAt(i);
                if (child instanceof ViewGroup) {
                    // recursive call
                    replaceFonts((ViewGroup) child);
                } else if (child instanceof TextView) {
                    // base case
                    ((TextView) child).setTypeface(typeface);
                } else if (child instanceof EditText) {
                    // base case
                    ((EditText) child).setTypeface(typeface);
                } else if (child instanceof Button) {
                    // base case
                    ((Button) child).setTypeface(typeface);
                }
            }
        }
    }

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

    protected void hideAlertDialog() {
        if (_alertDialog != null && _alertDialog.isShowing()) {
            _alertDialog.dismiss();
        }
        _alertDialog = null;
    }

    protected AlertDialog showNoInternetErrorDialog() {
        this.hideAlertDialog();
        this.hideLoadingView();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_action_network_wifi);
        builder.setTitle(R.string.internet_connexion_error_title);
        builder.setMessage(R.string.internet_connexion_error_message);
        builder.setPositiveButton(android.R.string.ok, null);

        _alertDialog = builder.show();

        return _alertDialog;
    }

    protected void initFont() {
        // font otf
        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "fonts/Exo2.0_Regular.otf");
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
    }

    protected void showSnackBarMessage(NestedScrollView nestedScrollView, String message, int color) {
        Snackbar mSnackBar = Snackbar.make(nestedScrollView, message, Snackbar.LENGTH_LONG);

        TextView mainTextView = (TextView) (mSnackBar.getView()).findViewById(android.support.design.R.id.snackbar_text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            mainTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        else
            mainTextView.setGravity(Gravity.CENTER_HORIZONTAL);

        // snackBar color
        mSnackBar.getView().setBackgroundColor(Color.WHITE);
        mainTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        mainTextView.setTextColor(color);
        mainTextView.setTypeface(_font);

        mSnackBar.show();
    }

    protected void initActionBarTitle(String title) {
        ActionBar actionbar = getSupportActionBar();
        TextView textview = new TextView(BaseActivity.this);
        RelativeLayout.LayoutParams layoutparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        textview.setLayoutParams(layoutparams);
        textview.setText(title);
        textview.setTypeface(_font);
        textview.setTextColor(Color.WHITE);
        textview.setGravity(Gravity.LEFT);
        textview.setTextSize(18);
        actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionbar.setCustomView(textview);
    }

    protected void setDatePickerDialog(final EditText editText) {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(BaseActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        editText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    protected void showNotSupportedCameraErrorDialog() {

        this.hideAlertDialog();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(false);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle(R.string.avatar_error_title);
        builder.setMessage(R.string.avatar_device_unsupported_error_message);
        builder.setPositiveButton(android.R.string.ok, null);

        _alertDialog = builder.show();

    }

    /**
     * Checking device has camera hardware or not
     */
    protected boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * Checking phone number in Line 1
     */
    protected void checkLine1PhoneNumber() {
        /*TelephonyManager telephonyManager = (TelephonyManager) _context.getSystemService(Context.TELEPHONY_SERVICE);
        String test = telephonyManager.getLine1Number();// + telephonyManager.getSimOperatorName();
        return test;*/

        //return ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getLine1Number();
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}
