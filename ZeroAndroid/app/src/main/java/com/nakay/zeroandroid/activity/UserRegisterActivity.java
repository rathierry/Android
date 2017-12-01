package com.nakay.zeroandroid.activity;

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

public class UserRegisterActivity extends BaseActivity implements View.OnClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private final AppCompatActivity activity = UserRegisterActivity.this;
    private DatabaseHelper databaseHelper;

    private User user;

    @BindView(R.id.input_name)
    EditText _nameText;
    @BindView(R.id.input_email)
    EditText _emailText;
    @BindView(R.id.input_password)
    EditText _passwordText;
    @BindView(R.id.btn_signup)
    Button _signUpButton;
    @BindView(R.id.link_login)
    TextView _loginLink;

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

        setContentView(R.layout.activity_user_register);
        getSupportActionBar().hide();
        ButterKnife.bind(this);

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

            case R.id.btn_signup:
                signUp();
                break;

            case R.id.link_login:
                finish();
                break;
        }
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    public boolean validate() {

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError(getString(R.string.error_message_name));
            _nameText.requestFocus();
            return false;
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError(getString(R.string.error_message_email));
            _emailText.requestFocus();
            return false;
        }
        if (password.isEmpty() || password.length() < 4) {
            _passwordText.setError(getString(R.string.error_message_password));
            _passwordText.requestFocus();
            return false;
        } else {
            _nameText.setError(null);
            _nameText.clearFocus();

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
        _signUpButton.setOnClickListener(this);
        _loginLink.setOnClickListener(this);

    }

    /**
     * This method is to initialize objects to be used
     */
    private void initObjects() {
        databaseHelper = new DatabaseHelper(activity);
        user = new User();

    }

    /**
     * This method is to validate the input text fields and post data to SQLite
     */
    private void signUp() {
        if (!validate()) {
            return;
        }

        _signUpButton.setEnabled(false);
        showLoadingView();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!databaseHelper.checkUser(_emailText.getText().toString().trim())) {
                    user.setName(_nameText.getText().toString().trim());
                    user.setEmail(_emailText.getText().toString().trim());
                    user.setPassword(_passwordText.getText().toString().trim());

                    databaseHelper.addUser(user);

                    // setup current user
                    User currentUser = databaseHelper.getUser(_emailText.getText().toString().trim());
                    saveCurrentUser(getApplicationContext(), currentUser);

                    hideLoadingView();
                    emptyInputEditText();
                    showAlertDialog(getString(R.string.activity_user_button_text_register), getString(R.string.success_message));
                } else {
                    hideLoadingView();
                    _signUpButton.setEnabled(true);
                    showAlertDialog(getString(R.string.activity_user_button_text_register), getString(R.string.error_email_exists));
                }
            }
        }, LOADING_TIME_OUT);
    }

    /**
     * This method is to empty all input edit text
     */
    private void emptyInputEditText() {
        _nameText.setText(null);
        _emailText.setText(null);
        _passwordText.setText(null);
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}