package com.team.lezomadetana.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
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

    /**
     * Create activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        // initialize
        ButterKnife.bind(this);
        // getSimCardInfo();
        phoneNumberTextChangedListener();
    }

    /**
     * When app is on pause'state
     */
    @Override
    protected void onPause() {
        super.onPause();
        _layout.requestFocus();
        _phoneText.setError(null);
        _passwordText.setError(null);
    }

    /**
     * Device's back button
     */
    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(false);
    }

    /**
     * Event onClick on validate button
     */
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
        showLoadingView(getResources().getString(R.string.app_spinner));

        // save form inputs
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

    /**
     * Event onClick on register button
     */
    @OnClick(R.id.user_login_btn_register)
    void showUserRegisterActivity() {
        // start the SignUp activity
        startActivity(new Intent(getApplicationContext(), UserRegisterActivity.class));
        overridePendingTransitionEnter();
    }

    /**
     * Event onClick on forgot password textView
     */
    @OnClick(R.id.user_login_textView_forgot_password)
    void showUserForgotPasswordActivity() {
        // get prompts xml view
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(UserLoginActivity.this);
        final View mView = layoutInflaterAndroid.inflate(R.layout.user_login_forgot_password, null);

        // create alert builder and cast view
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));

        // set prompts xml to alertdialog builder
        builder.setView(mView);

        // edit text
        final EditText inputDialog = (EditText) mView.findViewById(R.id.user_login_forgot_password_editText);
        inputDialog.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 10) {
                    // hide keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mView.getWindowToken(), 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO
            }
        });

        // set dialog message
        builder
                .setTitle(R.string.user_login_forgot_pass_text_title)
                .setIcon(R.drawable.info_black)
                .setCancelable(false)
                .setPositiveButton(R.string.user_login_forgot_pass_btn_ok, null)
                .setNegativeButton(R.string.user_login_forgot_pass_btn_cancel, null);

        // create alert dialog
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button buttonOK = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                Button buttonCancel = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);

                // validate
                buttonOK.setTextColor(ContextCompat.getColor(UserLoginActivity.this, android.R.color.holo_green_dark));
                buttonOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // value
                        String phone = inputDialog.getText().toString();

                        // check phone number
                        if (phone.equals("") || !(phone.matches("^[0-9-]+$")) || phone.length() != 10) {
                            inputDialog.setError(getResources().getString(R.string.user_login_input_error_phone));
                            inputDialog.requestFocus();
                        } else {
                            // show spinner
                            showLoadingView(getResources().getString(R.string.app_spinner));
                            // hide dialog
                            dialog.dismiss();

                            // TODO: Implement your own forgotten password logic here.

                            // after few times
                            new Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            // on complete call either action
                                            inputDialog.setText("");
                                            inputDialog.setError(null);
                                            hideLoadingView();
                                        }
                                    }, LOADING_TIME_OUT);
                        }
                    }
                });

                // cancel
                buttonCancel.setTextColor(ContextCompat.getColor(UserLoginActivity.this, android.R.color.holo_red_dark));
                buttonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        // change the alert dialog background color
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.white);
        dialog.show();
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

    /**
     * Change listener on phone number's text
     */
    private void phoneNumberTextChangedListener() {
        // phone edit text
        _phoneText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // TODO in future
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 10) {
                    View view = UserLoginActivity.this.getCurrentFocus();
                    if (view != null) {
                        // take focus on password edit text
                        _passwordText.requestFocus();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // TODO in future
            }
        });
    }

    /**
     * Validate form
     */
    private boolean validate() {
        boolean valid = true;
        String phone = _phoneText.getText().toString();
        String password = _passwordText.getText().toString();

        // phone number
        if (phone.isEmpty() || TextUtils.isEmpty(phone) || !(phone.matches(numberRegex)) || phone.length() != 10) {
            _phoneText.setError(getResources().getString(R.string.user_login_input_error_phone));
            _phoneText.requestFocus();
            valid = false;
        }
        // password
        else if (password.isEmpty() || TextUtils.isEmpty(password)) {
            _passwordText.setError(getResources().getString(R.string.user_login_input_error_password));
            _passwordText.requestFocus();
            valid = false;
        } else {
            clearAllInputError();
            clearAllInputFocus();
        }
        return valid;
    }

    /**
     * Success register
     */
    private void onLoginSuccess() {
        _layout.requestFocus();
        resetAllInputText();
        clearAllInputError();
        _btnLogIn.setEnabled(true);
        hideLoadingView();
    }

    /**
     * Failed register
     */
    private void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        _btnLogIn.setEnabled(true);
    }

    /**
     * Clear inputs error
     */
    private void clearAllInputError() {
        _phoneText.setError(null);
        _passwordText.setError(null);
    }

    /**
     * Clear inputs focus
     */
    private void clearAllInputFocus() {
        _phoneText.clearFocus();
        _passwordText.clearFocus();
    }

    /**
     * Reset inputs text
     */
    private void resetAllInputText() {
        _phoneText.setText("");
        _passwordText.setText("");
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}
