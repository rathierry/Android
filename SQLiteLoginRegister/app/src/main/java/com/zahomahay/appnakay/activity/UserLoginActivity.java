package com.zahomahay.appnakay.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.ViewGroup;

import com.zahomahay.appnakay.R;
import com.zahomahay.appnakay.helper.InputValidation;
import com.zahomahay.appnakay.model.User;
import com.zahomahay.appnakay.sql.DatabaseHelper;

public class UserLoginActivity extends BaseActivity implements View.OnClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private final AppCompatActivity activity = UserLoginActivity.this;

    private NestedScrollView nestedScrollView;

    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;

    private TextInputEditText textInputEditTextEmail;
    private TextInputEditText textInputEditTextPassword;

    private AppCompatButton appCompatButtonLogin;

    private AppCompatTextView textViewLinkRegister;

    private InputValidation inputValidation;
    private DatabaseHelper databaseHelper;

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
        getSupportActionBar().hide();

        // font otf
        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "fonts/Exo2.0_Regular.otf");
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));

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
                verifyFromSQLite();
                break;
            case R.id.textViewLinkRegister:
                // navigate to RegisterActivity
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

        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);

        textInputLayoutEmail = (TextInputLayout) findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);

        textInputEditTextEmail = (TextInputEditText) findViewById(R.id.textInputEditTextEmail);
        textInputEditTextPassword = (TextInputEditText) findViewById(R.id.textInputEditTextPassword);

        appCompatButtonLogin = (AppCompatButton) findViewById(R.id.appCompatButtonLogin);

        textViewLinkRegister = (AppCompatTextView) findViewById(R.id.textViewLinkRegister);

    }

    /**
     * This method is to initialize listeners
     */
    private void initListeners() {
        appCompatButtonLogin.setOnClickListener(this);
        textViewLinkRegister.setOnClickListener(this);
    }

    /**
     * This method is to initialize objects to be used
     */
    private void initObjects() {
        databaseHelper = new DatabaseHelper(activity);
        inputValidation = new InputValidation(activity);

    }

    /**
     * This method is to validate the input text fields and verify login credentials from SQLite
     */
    private void verifyFromSQLite() {
        if (!inputValidation.isInputEditTextFilled(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return;
        }
        if (!inputValidation.isInputEditTextEmail(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextPassword, textInputLayoutPassword, getString(R.string.error_message_email))) {
            return;
        }

        showLoadingView();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (databaseHelper.checkUser(textInputEditTextEmail.getText().toString().trim()
                        , textInputEditTextPassword.getText().toString().trim())) {
                    // setup current user
                    User currentUser = databaseHelper.getUser(textInputEditTextEmail.getText().toString().trim());
                    saveCurrentUser(UserLoginActivity.this, currentUser);

                    // navigate to ListActivity
                    Intent accountsIntent = new Intent(activity, UserListActivity.class);
                    accountsIntent.putExtra("EMAIL", textInputEditTextEmail.getText().toString().trim());

                    emptyInputEditText();
                    hideLoadingView();

                    startActivity(accountsIntent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else {
                    hideLoadingView();
                    showSnackBarMessage(nestedScrollView, getString(R.string.activity_user_login_error_valid_email_password), getResources().getColor(android.R.color.holo_red_dark));
                }
            }
        }, LOADING_TIME_OUT);
    }

    /**
     * This method is to empty all input edit text
     */
    private void emptyInputEditText() {
        textInputEditTextEmail.setText(null);
        textInputEditTextPassword.setText(null);
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}
