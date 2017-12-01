package com.nakay.zeroandroid.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.nakay.zeroandroid.R;
import com.nakay.zeroandroid.model.User;
import com.nakay.zeroandroid.utils.CircleTransform;

/**
 * Created by Thierry on 13/10/2017.
 */

public class BaseFragmentActivity extends AppCompatActivity {

    // ===========================================================
    // Constants
    // ===========================================================

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    public static final String TAG_HOME = "home";
    public static final String TAG_SETTINGS = "settings";
    public static final String TAG_USER = "user";

    public static String CURRENT_TAG = TAG_HOME;

    // ===========================================================
    // Fields
    // ===========================================================

    private Context _context;

    private Dialog _dialog;
    private AlertDialog _alertDialog;

    private ProgressDialog _progressDialog;

    // - check user inactivity -
    private Boolean isInactivity = false;
    private Handler logOutHandler = new Handler() {
        public void handleMessage(Message msg) {
        }
    };
    private Runnable logOutCallback = new Runnable() {
        @Override
        public void run() {
            isInactivity = true;
            showPreview();
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
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        _context = BaseFragmentActivity.this;
        _dialog = new Dialog(_context, R.style.DialogTheme);
        _dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // font otf
        BaseFragmentActivity.FontChangeCrawler fontChanger = new BaseFragmentActivity.FontChangeCrawler(getAssets(), "fonts/Exo2.0_Regular.otf");
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //
    }

    @Override
    public void onUserInteraction() {
        resetDisconnectTimer();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isInactivity) {
            resetDisconnectTimer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        stopDisconnectTimer();
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

    public void disconnectUser() {
        SharedPreferences preferences = getSharedPreferences(BaseActivity.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();

        startActivity(new Intent(BaseFragmentActivity.this, UserLoginActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    public User getCurrentUser(Context context) {
        SharedPreferences settings;

        //settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(BaseActivity.PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = settings.getString(BaseActivity.PREFS_KEY_USER, "");
        User user = gson.fromJson(json, User.class);

        return user;
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

    protected int getImage(String imageName) {
        int drawableResourceId = this.getResources().getIdentifier(imageName, "drawable", this.getPackageName());
        return drawableResourceId;
    }

    private void stopDisconnectTimer() {
        logOutHandler.removeCallbacks(logOutCallback);
    }

    private void resetDisconnectTimer() {
        logOutHandler.removeCallbacks(logOutCallback);
        logOutHandler.postDelayed(logOutCallback, BaseActivity.DISCONNECT_TIMEOUT);
    }

    protected void hidePreview() {
        isInactivity = false;
        resetDisconnectTimer();
    }

    protected void logoutUser() {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.app_name))
                .setMessage(getResources().getString(R.string.logout))
                .setIcon(R.drawable.ic_power_black)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();

                        showLoadingView();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                hideLoadingView();
                                disconnectUser();
                            }
                        }, BaseActivity.LOADING_TIME_OUT);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    protected void showPreview() {
        this.hideAlertDialog();

        Rect displayRectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        // layout
        LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_layout_preview, null);
        layout.setMinimumWidth((int) (displayRectangle.width() * 0.9f));
        layout.setMinimumHeight((int) (displayRectangle.height() * 0.9f));

        _dialog.setContentView(layout);
        _dialog.setCancelable(false);

        // btn close
        ImageView close = (ImageView) _dialog.findViewById(R.id.activity_pub_preview_btn_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _dialog.dismiss();
                hidePreview();
            }
        });

        // image pub
        ImageView imageViewInPreview = (ImageView) _dialog.findViewById(R.id.activity_pub_preview_imageView);
        imageViewInPreview.setVisibility(View.VISIBLE);
        Glide.with(_context)
                .load(getImage("admin"))
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.failure)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(_context))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageViewInPreview);

        // dialog background alpha
        ColorDrawable dialogColor = new ColorDrawable(Color.BLACK);
        dialogColor.setAlpha(250);

        _dialog.getWindow().setBackgroundDrawable(dialogColor);
        _dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        _dialog.show();
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================

}