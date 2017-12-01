package com.nakay.zeroandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nakay.zeroandroid.R;
import com.nakay.zeroandroid.model.User;
import com.nakay.zeroandroid.sql.DatabaseHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserLoginActivity extends BaseActivity implements View.OnClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private final AppCompatActivity activity = UserLoginActivity.this;

    private DatabaseHelper databaseHelper;

    @BindView(R.id.input_email)
    EditText _emailText;
    @BindView(R.id.input_password)
    EditText _passwordText;
    @BindView(R.id.btn_login)
    Button _loginButton;
    @BindView(R.id.btn_signUp)
    TextView _signUpLink;

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
        ButterKnife.bind(this);

        initListeners();
        initObjects();
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
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
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_signUp:
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

    private boolean validate() {

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError(getString(R.string.error_message_email));
            _emailText.requestFocus();
            return false;
        }
        if (password.isEmpty() || password.length() < 4) {
            _passwordText.setError(getResources().getString(R.string.error_message_password));
            _passwordText.requestFocus();
            return false;
        } else {
            _emailText.setError(null);
            _emailText.clearFocus();

            _passwordText.setError(null);
            _passwordText.clearFocus();
        }

        return true;
    }

    /**
     * This method is to initialize listeners
     */
    private void initListeners() {
        _loginButton.setOnClickListener(this);
        _signUpLink.setOnClickListener(this);
    }

    /**
     * This method is to initialize objects to be used
     */
    private void initObjects() {
        databaseHelper = new DatabaseHelper(activity);

    }

    /**
     * This method is to validate the input text fields and verify login credentials from SQLite
     */
    private void login() {
        if (!validate()) {
            return;
        }

        _loginButton.setEnabled(false);
        showLoadingView();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (databaseHelper.checkUser(_emailText.getText().toString().trim(), _passwordText.getText().toString().trim())) {
                    // setup current user
                    User currentUser = databaseHelper.getUser(_emailText.getText().toString().trim());
                    saveCurrentUser(UserLoginActivity.this, currentUser);

                    // navigate to ListActivity
                    Intent accountsIntent = new Intent(activity, MainActivity.class);
                    accountsIntent.putExtra("EMAIL", _emailText.getText().toString().trim());

                    hideLoadingView();
                    emptyInputEditText();

                    startActivity(accountsIntent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                } else {
                    hideLoadingView();
                    _loginButton.setEnabled(true);
                    showAlertDialog(getString(R.string.activity_user_button_text_login), getString(R.string.activity_user_login_error_valid_email_password));
                }
            }
        }, LOADING_TIME_OUT);
    }

    /**
     * This method is to empty all input edit text
     */
    private void emptyInputEditText() {
        _emailText.setText(null);
        _passwordText.setText(null);
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}
