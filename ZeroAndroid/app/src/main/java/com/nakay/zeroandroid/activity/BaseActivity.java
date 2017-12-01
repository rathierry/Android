package com.nakay.zeroandroid.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nakay.zeroandroid.R;
import com.nakay.zeroandroid.model.User;

/**
 * Created by Thierry on 01/08/2017.
 */

public class BaseActivity extends AppCompatActivity {

    // ===========================================================
    // Constants
    // ===========================================================

    public static int SPLASH_TIME_OUT = 2000;
    public static int LOADING_TIME_OUT = 1000;
    public static final long DISCONNECT_TIMEOUT = 5 * 1000;

    public static final String PREFS_NAME = "PREFS";
    public static final String PREFS_KEY_USER = "KEY_USER";

    // ===========================================================
    // Fields
    // ===========================================================

    private Context _context;

    private ProgressDialog _progressDialog;
    private AlertDialog _alertDialog;
    private Dialog _dialog;

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

        // font otf
        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "fonts/Exo2.0_Regular.otf");
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //
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

    protected void showAlertDialog(String title, String message) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.custom_alert_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(title);
        dialogBuilder.setCancelable(false);

        final AlertDialog dialog = dialogBuilder.create();

        final TextView textView = (TextView) dialogView.findViewById(R.id.txt_dia);
        textView.setText(message);

        final Button button = (Button) dialogView.findViewById(R.id.btn_yes);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}
