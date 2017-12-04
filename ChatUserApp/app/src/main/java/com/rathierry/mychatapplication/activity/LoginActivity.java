package com.rathierry.mychatapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rathierry.mychatapplication.R;
import com.rathierry.mychatapplication.manager.SessionManager;
import com.rathierry.mychatapplication.model.UserDetails;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends BaseActivity {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private TextView register;
    private EditText username, password;
    private Button loginButton;

    private String user, pass;

    private SessionManager _sessionManager;

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
        setContentView(R.layout.activity_login);

        // variable
        register = (TextView) this.findViewById(R.id.register);
        username = (EditText) this.findViewById(R.id.username);
        password = (EditText) this.findViewById(R.id.password);
        loginButton = (Button) this.findViewById(R.id.loginButton);

        // Session manager
        _sessionManager = new SessionManager(getApplicationContext());
        if (_sessionManager.isLoggedIn()) {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else {
            // toolBar
            SpannableString spannableString = new SpannableString(getResources().getString(R.string.activity_login_toolbar_title));
            spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_grey_blue)), 0,
                    getResources().getString(R.string.activity_login_toolbar_title).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            getSupportActionBar().setTitle(spannableString);

            // check if user is already logged in or not
            String status = _sessionManager.getPreferences(LoginActivity.this, _sessionManager.KEY_SESSION_STATUS);
            if (status == null || (TextUtils.equals(status, "null")) || (TextUtils.isEmpty(status))) {
                register.setVisibility(View.VISIBLE);
            } else if (status.equals("0") && _sessionManager.isLoggedIn()) {
                register.setVisibility(View.VISIBLE);
            } else if (status.equals("0") && !_sessionManager.isLoggedIn()) {
                register.setVisibility(View.GONE);
            }

            // event onClick
            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            });

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user = username.getText().toString();
                    pass = password.getText().toString();

                    if (user.equals("")) {
                        username.setError(getResources().getString(R.string.activity_login_error_message_userName));
                    } else if (pass.equals("")) {
                        password.setError(getResources().getString(R.string.activity_login_error_message_password));
                    } else if (!isConnectedToInternet()) {
                        showInternetConnexionError();
                    } else {
                        // show spinner
                        showLoadingView();

                        StringRequest request = new StringRequest(Request.Method.GET, rootUrlUserJson, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                if (s.equals("null")) {
                                    showCustomMessage(getResources().getString(R.string.activity_login_error_dialog_title),
                                            getResources().getString(R.string.activity_login_error_message_user_not_found));
                                } else {
                                    try {
                                        JSONObject jsonObject = new JSONObject(s);
                                        if (!jsonObject.has(user)) {
                                            showCustomMessage(getResources().getString(R.string.activity_login_error_dialog_title),
                                                    getResources().getString(R.string.activity_login_error_message_user_not_found));
                                        } else if (jsonObject.getJSONObject(user).getString("password").equals(pass)) {
                                            // create login session
                                            _sessionManager.setLogin(true);
                                            _sessionManager.setPreferences(LoginActivity.this, _sessionManager.KEY_SESSION_STATUS, "1");

                                            UserDetails.username = user;
                                            UserDetails.password = pass;
                                            UserDetails.status = userOnline;

                                            // start new activity
                                            startActivity(new Intent(LoginActivity.this, UsersActivity.class));
                                            finish();
                                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                        } else {
                                            showCustomMessage(getResources().getString(R.string.activity_login_error_dialog_title),
                                                    getResources().getString(R.string.activity_login_error_message_password_not_correct));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                // hide spinner
                                hideLoadingView();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                System.out.println("" + volleyError);

                                // hide spinner
                                hideLoadingView();
                            }
                        });

                        RequestQueue rQueue = Volley.newRequestQueue(LoginActivity.this);
                        rQueue.add(request);
                    }
                }
            });
        }
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