package com.team.lezomadetana.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.team.lezomadetana.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by team on 28/08/2018.
 **/

public class UserLoginActivity extends BaseActivity {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    //@BindString(R.string.login_error) String loginErrorMessage;
    @BindView(R.id.user_login_relativeLayout)
    RelativeLayout _layout;
    @BindView(R.id.user_login_input_phone)
    EditText _phoneText;
    @BindView(R.id.user_login_input_password)
    EditText _passwordText;
    @BindView(R.id.user_login_btn_validate)
    Button _btnLogIn;

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
        ButterKnife.bind(this);
        getSimCardInfo();

        // phone edit text
        _phoneText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // TODO
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 10) {
                    View view = UserLoginActivity.this.getCurrentFocus();
                    if (view != null) {
                        // hide keyboard
                        /*InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);*/
                        // take focus on password edit text
                        _passwordText.requestFocus();
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                // TODO
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        _layout.requestFocus();
        _phoneText.setError(null);
        _passwordText.setError(null);
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(false);
    }

    @OnClick(R.id.user_login_btn_validate)
    void submit() {
        // validate form
        if (!validate()) {
            onLoginFailed();
            return;
        }

        // disable login button
        _btnLogIn.setEnabled(false);

        // show spinner
        showLoadingView("Authentication .....");

        // save form inouts
        String email = _phoneText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.

        // hide spinner
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // on complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                    }
                }, LOADING_TIME_OUT);
    }

    @OnClick(R.id.user_login_btn_register)
    void showUserRegisterActivity() {
        // start the SignUp activity
        startActivity(new Intent(getApplicationContext(), UserRegisterActivity.class));
        overridePendingTransitionEnter();
    }

    @OnClick(R.id.user_login_textView_forgot_password)
    void showUserForgotPasswordActivity() {
        Toast.makeText(this, "work in progress", Toast.LENGTH_SHORT).show();
    }

    // ===========================================================
    // Methods for Interfaces
    // ===========================================================

    // ===========================================================
    // Public Methods
    // ===========================================================

    public void onLoginSuccess() {
        _layout.requestFocus();
        _phoneText.setText("");
        _passwordText.setText("");
        _phoneText.setError(null);
        _passwordText.setError(null);
        _btnLogIn.setEnabled(true);
        hideLoadingView();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        _btnLogIn.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;
        String phone = _phoneText.getText().toString();
        String password = _passwordText.getText().toString();

        // phone number
        if (phone.isEmpty() || TextUtils.isEmpty(phone) || !(phone.matches("^[0-9-]+$")) || phone.length() != 10) {
            _phoneText.setError("mampidira laharana finday (tarehimarika 10)");
            _phoneText.requestFocus();
            valid = false;
        }
        // password
        else if (password.isEmpty()) {
            _passwordText.setError("mampidira teny miafina");
            _passwordText.requestFocus();
            valid = false;
        } else {
            _phoneText.setError(null);
            _phoneText.clearFocus();
            _passwordText.setError(null);
            _passwordText.clearFocus();
        }
        return valid;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}
