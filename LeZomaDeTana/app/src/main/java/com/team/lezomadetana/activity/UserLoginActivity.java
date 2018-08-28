package com.team.lezomadetana.activity;

import android.content.Intent;
import android.os.Bundle;

import com.team.lezomadetana.R;

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

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods from SuperClass
    // ===========================================================

    @OnClick(R.id.user_login_btn_register)
    void submit() {
        // start the SignUp activity
        Intent intent = new Intent(getApplicationContext(), UserRegisterActivity.class);
        startActivityForResult(intent, REQUEST_SIGNUP);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        ButterKnife.bind(this);
    }

    // ===========================================================
    // Methods for Interfaces
    // ===========================================================

    // ===========================================================
    // Public Methods
    // ===========================================================

    // ===========================================================
    // Private Methods
    // ===========================================================

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}
