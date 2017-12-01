package com.tresor.mobilemoney.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.TextView;

import com.tresor.mobilemoney.R;
import com.tresor.mobilemoney.helper.InputValidation;

/**
 * Created by Priscilla on 01/09/2017.
 */
public class UserLoginActivity extends BaseActivity implements View.OnClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private final AppCompatActivity activity = UserLoginActivity.this;

    private NestedScrollView nestedScrollView;
    private LinearLayoutCompat linearLayoutCompat;

    private TextInputLayout textInputLayoutPseudo;
    private TextInputLayout textInputLayoutPassword;

    private TextInputEditText textInputEditTextPseudo;
    private TextInputEditText textInputEditTextPassword;

    private AppCompatButton appCompatButtonLogin;
    private AppCompatButton appCompatButtonLinkRegister;

    private InputValidation inputValidation;

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
        setContentView(R.layout.activity_user_login);

        initFont();
        initActionBarTitle(getResources().getString(R.string.activity_user_login_title_toolBar));
        initViews();
        initListeners();
        initObjects();
    }

    // ===========================================================
    // Methods for Interfaces
    // ===========================================================

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * This implemented method is to listen the click on view
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.appCompatButtonLogin:
                verifyFromDataBase();
                break;
            case R.id.appCompatButtonLinkRegister:
                // navigate to UserRegisterActivity
                startActivity(new Intent(getApplicationContext(), UserRegisterActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            default:
                break;
        }
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * This method is to initialize views
     */
    private void initViews() {
        linearLayoutCompat = (LinearLayoutCompat) findViewById(R.id.userLoginActivityLinearLayoutCompat);
        nestedScrollView = (NestedScrollView) findViewById(R.id.userLoginActivityNestedScrollView);

        TextView textViewTitleLayout = (TextView) findViewById(R.id.textViewLayoutTitle);
        textViewTitleLayout.setPaintFlags(textViewTitleLayout.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        textInputLayoutPseudo = (TextInputLayout) findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);

        textInputEditTextPseudo = (TextInputEditText) findViewById(R.id.textInputEditTextPseudo);
        textInputEditTextPassword = (TextInputEditText) findViewById(R.id.textInputEditTextPassword);

        appCompatButtonLogin = (AppCompatButton) findViewById(R.id.appCompatButtonLogin);
        appCompatButtonLinkRegister = (AppCompatButton) findViewById(R.id.appCompatButtonLinkRegister);

    }

    /**
     * This method is to initialize listeners
     */
    private void initListeners() {
        appCompatButtonLogin.setOnClickListener(this);
        appCompatButtonLinkRegister.setOnClickListener(this);
    }

    /**
     * This method is to initialize objects to be used
     */
    private void initObjects() {
        // TODO : databaseHelper = new DatabaseHelper(activity);
        inputValidation = new InputValidation(activity);

    }

    /**
     * This method is to validate the input text fields and verify login credentials from SQLite
     */
    private void verifyFromDataBase() {
        if (!inputValidation.isInputEditTextFilled(textInputEditTextPseudo, textInputLayoutPseudo, getString(R.string.activity_user_login_error_message_pseudo))) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextPassword, textInputLayoutPassword, getString(R.string.activity_user_login_error_message_password))) {
            return;
        }

        showLoadingView();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideLoadingView();
                emptyInputEditText();
                linearLayoutCompat.requestFocus();
                showSnackBarMessage(nestedScrollView, getString(R.string.activity_user_login_success_valid_pseudo_password), getResources().getColor(android.R.color.holo_green_dark));
            }
        }, LOADING_TIME_OUT);
    }

    /**
     * This method is to empty all input edit text
     */
    private void emptyInputEditText() {
        textInputEditTextPseudo.setText(null);
        textInputEditTextPassword.setText(null);
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}
