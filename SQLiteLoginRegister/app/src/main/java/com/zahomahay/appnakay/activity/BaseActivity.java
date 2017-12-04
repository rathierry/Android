package com.zahomahay.appnakay.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zahomahay.appnakay.R;
import com.zahomahay.appnakay.model.User;

/**
 * Created by Thierry on 01/08/2017.
 */

public class BaseActivity extends AppCompatActivity {

    // ===========================================================
    // Constants
    // ===========================================================

    public static int SPLASH_TIME_OUT = 2000;
    public static int LOADING_TIME_OUT = 1000;

    public static final String PREFS_NAME = "PREFS";
    public static final String PREFS_KEY_USER = "KEY_USER";

    // ===========================================================
    // Fields
    // ===========================================================

    private ProgressDialog _progressDialog;
    private AlertDialog _alertDialog;

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
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);

        //...
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

    public void saveCurrentUser(Context context, User user) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        //settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString(PREFS_KEY_USER, json);
        editor.commit();
    }

    public User getCurrentUser(Context context) {
        SharedPreferences settings;

        //settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = settings.getString(PREFS_KEY_USER, "");
        User user = gson.fromJson(json, User.class);

        return user;
    }

    public void disconnectUser() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();

        startActivity(new Intent(BaseActivity.this, UserLoginActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
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

    protected void showSnackBarMessage(NestedScrollView nestedScrollView, String message, int color) {
        Snackbar mSnackBar = Snackbar.make(nestedScrollView, message, Snackbar.LENGTH_LONG);

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Exo2.0_Regular.otf");
        TextView mainTextView = (TextView) (mSnackBar.getView()).findViewById(android.support.design.R.id.snackbar_text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            mainTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        else
            mainTextView.setGravity(Gravity.CENTER_HORIZONTAL);

        // snackBar color
        mSnackBar.getView().setBackgroundColor(Color.WHITE);
        // gravity message
        mainTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        // message color
        mainTextView.setTextColor(color);
        // custom fonts for message
        mainTextView.setTypeface(font);

        mSnackBar.show();
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}
