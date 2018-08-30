package com.team.lezomadetana.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.team.lezomadetana.R;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by team on 28/08/2018.
 **/

public class UserRegisterActivity extends BaseActivity {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    //@BindString(R.string.register_error) String registerErrorMessage;
    @BindView(R.id.user_register_relativeLayout)
    RelativeLayout _layout;
    @BindView(R.id.user_register_imageView_logo)
    ImageView _avatarImage;
    @BindView(R.id.user_register_input_name)
    EditText _nameText;
    @BindView(R.id.user_register_input_firstname)
    EditText _firstNameText;
    @BindView(R.id.user_register_input_phone)
    EditText _phoneText;
    @BindView(R.id.user_register_material_design_spinner)
    MaterialBetterSpinner _occupationSpinner;
    @BindView(R.id.user_regitser_input_address)
    EditText _addressText;
    @BindView(R.id.user_register_input_password)
    EditText _passwordText;
    @BindView(R.id.user_register_input_re_password)
    EditText _rePasswordText;
    @BindView(R.id.user_register_btn_validate)
    Button _btnSignUp;

    // input values
    private String name;
    private String firstName;
    private String phone;
    private String userOccupation;
    private String address;
    private String password;
    private String rePassword;

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
        setContentView(R.layout.activity_user_register);

        // initialize
        ButterKnife.bind(this);
        phoneNumberTextChangedListener();
        initSpinnerOccupation();
        passwordOnFocusChange();
    }

    /**
     * When app is on pause'state
     */
    @Override
    protected void onPause() {
        super.onPause();
        _layout.requestFocus();
        _nameText.setError(null);
        _firstNameText.setError(null);
        _phoneText.setError(null);
        _occupationSpinner.setError(null);
        _addressText.setError(null);
        _passwordText.setError(null);
        _rePasswordText.setError(null);
    }

    /**
     * Device's back button
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransitionExit();
    }

    /**
     * Event onClick on validate button
     */
    @OnClick(R.id.user_register_btn_validate)
    void submit() {
        // input values
        name = _nameText.getText().toString();
        firstName = _firstNameText.getText().toString();
        phone = _phoneText.getText().toString();
        userOccupation = _occupationSpinner.getText().toString();
        address = _addressText.getText().toString();
        password = _passwordText.getText().toString();
        rePassword = _rePasswordText.getText().toString();

        // validate form
        if (!validate()) {
            onRegisterFailed();
            return;
        }

        // disable login button
        _btnSignUp.setEnabled(false);

        // show spinner
        showLoadingView(getResources().getString(R.string.app_spinner));

        // TODO: Implement your own inscription logic here.

        // hide spinner
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // on complete call either onLoginSuccess or onLoginFailed
                        onRegisterSuccess();
                    }
                }, LOADING_TIME_OUT);
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
                    View view = UserRegisterActivity.this.getCurrentFocus();
                    if (view != null) {
                        // take focus on occupation spinner drop down
                        _occupationSpinner.requestFocus();
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
     * Initialize user occupation'spinner (drop down list)
     */
    private void initSpinnerOccupation() {
        // drop down element
        List<String> occupation = new ArrayList<String>();
        occupation.add("Mpiompy");
        occupation.add("Mpivaro-mandeha");
        occupation.add("Mpamboly");
        occupation.add("Mpihaza");
        occupation.add("Mpitrandraka volamena/vato soa");
        occupation.add("Mpandrafitra");

        // set adapter for spinner
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, occupation);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        _occupationSpinner.setAdapter(arrayAdapter);
        // logic: click
        _occupationSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // item'clicked name
                userOccupation = parent.getItemAtPosition(position).toString();

                // showing clicked spinner item name and position
                //Toast.makeText(parent.getContext(), "Asa atao dia : " + userOccupation + "\n(position n° " + position + ")", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * On focus change password and re-password's input
     */
    private void passwordOnFocusChange() {
        _passwordText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View vws, boolean hasFocus) {
                if (!hasFocus) {
                    String password = _passwordText.getText().toString();
                    String rePassword = _rePasswordText.getText().toString();

                    if (password.equals("")) {
                        _passwordText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        _rePasswordText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    } else {
                        _passwordText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_valid, 0);
                        if (!password.equals(rePassword)) {
                            _passwordText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_valid, 0);
                            if (!rePassword.equals("")) {
                                _rePasswordText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_not_valid, 0);
                            }
                        } else {
                            _rePasswordText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_valid, 0);
                        }
                    }
                }
            }
        });
        _rePasswordText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View vws, boolean hasFocus) {
                if (!hasFocus) {
                    String password = _passwordText.getText().toString();
                    String rePassword = _rePasswordText.getText().toString();

                    if (rePassword.equals("")) {
                        _passwordText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        _rePasswordText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    } else {
                        if (!rePassword.equals("") && rePassword.equals(password)) {
                            _rePasswordText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_valid, 0);
                        } else if (!rePassword.equals(password)) {
                            _rePasswordText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_not_valid, 0);
                        } else {
                            _passwordText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                            _rePasswordText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        }
                    }
                }
            }
        });
    }

    /**
     * Validate form
     */
    private boolean validate() {
        boolean valid = true;

        // name
        if (name.isEmpty() || TextUtils.isEmpty(name) /*|| !(name.matches(nameRegex))*/) {
            _nameText.setError("Mampidira anarana");
            _nameText.requestFocus();
            valid = false;
        }
        // firstName
        else if (firstName.isEmpty() || TextUtils.isEmpty(firstName) /*|| !(firstName.matches(nameRegex))*/) {
            _firstNameText.setError("Mampidira fanamin'anarana");
            _firstNameText.requestFocus();
            valid = false;
        }
        // phone number
        else if (phone.isEmpty() || TextUtils.isEmpty(phone) || !(phone.matches(numberRegex)) || phone.length() != 10) {
            _phoneText.setError(getResources().getString(R.string.user_login_input_error_phone));
            _phoneText.requestFocus();
            valid = false;
        }
        // occupation
        else if (userOccupation.isEmpty() || TextUtils.isEmpty(userOccupation) || userOccupation.contains("Safidio")) {
            _occupationSpinner.setError("Misafidiana asa");
            _occupationSpinner.requestFocus();
            valid = false;
        }
        // address
        else if (address.isEmpty() || TextUtils.isEmpty(address) /*|| !(address.matches(nameRegex))*/) {
            _addressText.setError("Mampidira adiresy");
            _addressText.requestFocus();
            valid = false;
        }
        // password
        else if (password.isEmpty() || TextUtils.isEmpty(password)) {
            _passwordText.setError(getResources().getString(R.string.user_login_input_error_password));
            _passwordText.requestFocus();
            valid = false;
        }
        // confirm password
        else if (rePassword.isEmpty() || TextUtils.isEmpty(rePassword)) {
            _rePasswordText.setError("Avereno ampidirina ny teny miafina");
            _rePasswordText.requestFocus();
            valid = false;
        }
        // password different values
        else if (!password.equals(rePassword)) {
            _rePasswordText.setError("Hamarino ny teny miafina");
            _rePasswordText.requestFocus();
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
    private void onRegisterSuccess() {
        _layout.requestFocus();
        resetAllInputText();
        clearAllInputError();
        _btnSignUp.setEnabled(true);
        hideLoadingView();
    }

    /**
     * Failed register
     */
    private void onRegisterFailed() {
        Toast.makeText(getBaseContext(), "Register failed", Toast.LENGTH_LONG).show();
        _btnSignUp.setEnabled(true);
    }

    /**
     * Clear inputs error
     */
    private void clearAllInputError() {
        _nameText.setError(null);
        _firstNameText.setError(null);
        _phoneText.setError(null);
        _occupationSpinner.setError(null);
        _addressText.setError(null);
        _passwordText.setError(null);
        _rePasswordText.setError(null);
    }

    /**
     * Clear inputs focus
     */
    private void clearAllInputFocus() {
        _nameText.clearFocus();
        _firstNameText.clearFocus();
        _phoneText.clearFocus();
        _occupationSpinner.clearFocus();
        _addressText.clearFocus();
        _passwordText.clearFocus();
        _rePasswordText.clearFocus();
    }

    /**
     * Reset inputs text
     */
    private void resetAllInputText() {
        _nameText.setText("");
        _firstNameText.setText("");
        _phoneText.setText("");
        _occupationSpinner.setText("Safidio ny asa");
        _addressText.setText("");
        _passwordText.setText("");
        _rePasswordText.setText("");
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}
