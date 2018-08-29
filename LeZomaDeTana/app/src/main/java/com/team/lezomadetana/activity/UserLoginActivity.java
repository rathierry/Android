package com.team.lezomadetana.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
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

    private static final int REQUEST_SIGNUP = 0;

    // ===========================================================
    // Fields
    // ===========================================================

    //@BindView(R.id.user) EditText username;
    //@BindView(R.id.pass) EditText password;
    //@BindString(R.string.login_error) String loginErrorMessage;
    @BindView(R.id.user_login_btn_validate) Button _btnLogIn;

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods from SuperClass
    // ===========================================================

    @OnClick(R.id.user_login_btn_validate)
    void login() {
        //
    }

    @OnClick(R.id.user_login_btn_register)
    void submit() {
        // start the SignUp activity
        Intent intent = new Intent(getApplicationContext(), UserRegisterActivity.class);
        startActivityForResult(intent, REQUEST_SIGNUP);
        overridePendingTransitionEnter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        ButterKnife.bind(this);
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

    public void onLoginSuccess() {
        _btnLogIn.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        _btnLogIn.setEnabled(true);
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}
