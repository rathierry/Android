package com.rathierry.mychatapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
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
import com.firebase.client.Firebase;
import com.rathierry.mychatapplication.R;
import com.rathierry.mychatapplication.manager.SessionManager;
import com.rathierry.mychatapplication.model.UserDetails;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends BaseActivity {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private EditText username, password;
    private Button registerButton;
    private TextView login;

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
        setContentView(R.layout.activity_register);

        // variable
        username = (EditText) this.findViewById(R.id.username);
        password = (EditText) this.findViewById(R.id.password);
        registerButton = (Button) this.findViewById(R.id.registerButton);
        login = (TextView) this.findViewById(R.id.login);

        // Session manager
        _sessionManager = new SessionManager(getApplicationContext());
        if (_sessionManager.isLoggedIn()) {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else {
            // toolBar
            SpannableString spannableString = new SpannableString(getResources().getString(R.string.activity_register_toolbar_title));
            spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_grey_blue)), 0,
                    getResources().getString(R.string.activity_register_toolbar_title).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            getSupportActionBar().setTitle(spannableString);


            Firebase.setAndroidContext(this);

            // event onClick
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            });

            registerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user = username.getText().toString();
                    pass = password.getText().toString();

                    if (user.equals("")) {
                        username.setError(getResources().getString(R.string.activity_register_error_message_userName));
                    } else if (pass.equals("")) {
                        password.setError(getResources().getString(R.string.activity_register_error_message_password));
                    } else if (!user.matches("[A-Za-z0-9]+")) {
                        username.setError(getResources().getString(R.string.activity_register_error_message_userName_matches));
                    } else if (user.length() < 5) {
                        username.setError(getResources().getString(R.string.activity_register_error_message_length));
                    } else if (pass.length() < 5) {
                        password.setError(getResources().getString(R.string.activity_register_error_message_length));
                    } else if (!isConnectedToInternet()) {
                        showInternetConnexionError();
                    } else {
                        // show spinner
                        showLoadingView();

                        StringRequest request = new StringRequest(Request.Method.GET, rootUrlUserJson, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                Firebase reference = new Firebase(rootUrlUserList);

                                if (s.equals("null")) {
                                    Log.i("TAG", getResources().getString(R.string.activity_register_success_message));

                                    reference.child(user).child("password").setValue(pass);
                                    reference.child(user).child("status").setValue(userOnline);

                                    intentToUsersActivity();
                                } else {
                                    try {
                                        JSONObject jsonObject = new JSONObject(s);

                                        if (!jsonObject.has(user)) {
                                            Log.i("TAG", getResources().getString(R.string.activity_register_success_message));

                                            reference.child(user).child("password").setValue(pass);
                                            reference.child(user).child("status").setValue(userOnline);

                                            intentToUsersActivity();
                                        } else {
                                            showCustomMessage(getResources().getString(R.string.activity_register_error_dialog_title),
                                                    getResources().getString(R.string.activity_register_success_message_user_exist));
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

                        RequestQueue rQueue = Volley.newRequestQueue(RegisterActivity.this);
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

    private void intentToUsersActivity() {
        // create login session
        _sessionManager.setLogin(true);
        _sessionManager.setPreferences(RegisterActivity.this, _sessionManager.KEY_SESSION_STATUS, "1");

        // start new activity
        UserDetails.username = user;
        UserDetails.password = pass;

        startActivity(new Intent(RegisterActivity.this, UsersActivity.class));
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================

}