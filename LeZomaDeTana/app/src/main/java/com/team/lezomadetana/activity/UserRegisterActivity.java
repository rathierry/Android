package com.team.lezomadetana.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.team.lezomadetana.R;
import com.team.lezomadetana.api.Client;
import com.team.lezomadetana.api.Service;
import com.team.lezomadetana.model.receive.UserCredentialResponse;
import com.team.lezomadetana.model.send.UserCheckCredential;
import com.team.lezomadetana.model.send.UserRegisterSend;
import com.team.lezomadetana.utils.CameraUtils;
import com.team.lezomadetana.view.UrlImageView;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by team on 28/08/2018.
 **/

public class UserRegisterActivity extends BaseActivity implements View.OnClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private RelativeLayout _layout;
    private UrlImageView _avatarImage;
    private EditText _nameText;
    private EditText _firstNameText;
    private EditText _phoneText;
    private Spinner _regionSpinner;
    private EditText _addressText;
    private EditText _passwordText;
    private EditText _rePasswordText;
    private Button _btnSignUp;

    // input values
    private String name;
    private String firstName;
    private String phone;
    private String selectedItemOfRegions;
    private String address;
    private String password;
    private String rePassword;
    private Bitmap _bitmapImage;

    private List<String> regions;

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

        // init view
        _layout = (RelativeLayout) findViewById(R.id.user_register_relativeLayout);
        _avatarImage = (UrlImageView) findViewById(R.id.user_register_imageView_logo);
        _nameText = (EditText) findViewById(R.id.user_register_input_name);
        _firstNameText = (EditText) findViewById(R.id.user_register_input_firstname);
        _phoneText = (EditText) findViewById(R.id.user_register_input_phone);
        _regionSpinner = (Spinner) findViewById(R.id.user_register_region_spinner);
        _addressText = (EditText) findViewById(R.id.user_regitser_input_address);
        _passwordText = (EditText) findViewById(R.id.user_register_input_password);
        _rePasswordText = (EditText) findViewById(R.id.user_register_input_re_password);
        _btnSignUp = (Button) findViewById(R.id.user_register_btn_validate);

        // init function
        phoneNumberTextChangedListener();
        initRegionSpinnerData();
        passwordOnFocusChange();

        // set image avatar to rounded
        _avatarImage.useRoundedBitmap = true;

        // event listener
        _avatarImage.setOnClickListener(this);
        _btnSignUp.setOnClickListener(this);

        // restoring storage image path from saved instance state
        // otherwise the path will be null on device rotation
        restoreFromBundle(savedInstanceState);
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
        //_regionSpinner.setError(null);
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
        resetAllInputText();
        finish();
        overridePendingTransitionExit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_register_imageView_logo:
                getUserAvatar();
                break;
            case R.id.user_register_btn_validate:
                submit();
                break;
        }
    }

    // store the file url as it will be null after returning from camera app
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation changes
        outState.putString(KEY_IMAGE_STORAGE_PATH, imageStoragePath);
    }

    // restore activity state
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        imageStoragePath = savedInstanceState.getString(KEY_IMAGE_STORAGE_PATH);
    }

    // receiving activity result method will be called after closing the camera
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CAMERA_CAPTURE_IMAGE_REQUEST_CODE:
                // Make sure the request was successful (captured the image)
                if (resultCode == RESULT_OK) {
                    // refreshing the gallery
                    CameraUtils.refreshGallery(getApplicationContext(), imageStoragePath);

                    // successfully captured the image
                    // display it in image view
                    previewCapturedImage();
                } else if (resultCode == RESULT_CANCELED) {
                    // user cancelled Image capture
                    showCameraAlertDialog(getResources().getString(R.string.user_register_camera_error_canceled));
                } else {
                    // failed to capture image
                    showCameraAlertDialog(getResources().getString(R.string.user_register_camera_error_failed));
                }
                break;

            case CAMERA_GALLERY_IMAGE_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    // Recycle unused bitmaps
                    if (_bitmapImage != null) {
                        _bitmapImage.recycle();
                    }
                    _bitmapImage = CameraUtils.getImageFromGallery(this, data);
                    // preview image
                    _avatarImage.setImageBitmap(_bitmapImage);
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // user cancelled Image capture
                    showCameraAlertDialog(getResources().getString(R.string.user_register_camera_error_canceled));
                } else {
                    // failed to capture image
                    showCameraAlertDialog(getResources().getString(R.string.user_register_camera_error_failed));
                }
                break;

            default:
                break;
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

    /**
     * Event onClick on avatar image
     */
    private void getUserAvatar() {
        new AlertDialog.Builder(new ContextThemeWrapper(UserRegisterActivity.this, R.style.AlertDialogCustom))
                .setTitle(getResources().getString(R.string.user_register_camera_title))
                .setIcon(ContextCompat.getDrawable(UserRegisterActivity.this, R.drawable.ic_photo_camera_black))
                .setMessage(getResources().getString(R.string.user_register_camera_message))
                .setCancelable(true)
                .setPositiveButton(getResources().getString(R.string.user_register_camera_galery), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        imageTypeFile();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.user_register_camera_app), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        imageTypeCamera();
                    }
                })
                .show();
    }

    /**
     * Camera type : camera
     */
    private void imageTypeCamera() {
        // checking availability of the camera
        if (!CameraUtils.isDeviceSupportCamera(getApplicationContext())) {
            showNotSupportedCameraErrorDialog();
        } else {
            if (CameraUtils.checkPermissions(getApplicationContext())) {
                captureImage();
            } else {
                requestCameraPermission(MEDIA_TYPE_IMAGE);
            }
        }
    }

    /**
     * Camera type : galery
     */
    private void imageTypeFile() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, CAMERA_GALLERY_IMAGE_REQUEST_CODE);
    }

    /**
     * Capturing Camera error/failed
     */
    private void showCameraAlertDialog(String message) {
        new AlertDialog.Builder(new ContextThemeWrapper(UserRegisterActivity.this, R.style.AlertDialogCustom))
                .setTitle(getResources().getString(R.string.user_register_camera_title))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, null)
                .setCancelable(false)
                .show();
    }

    /**
     * Capturing Camera Image will launch camera app requested image capture
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File file = CameraUtils.getOutputMediaFile(MEDIA_TYPE_IMAGE);
        if (file != null) {
            imageStoragePath = file.getAbsolutePath();
        }

        Uri fileUri = CameraUtils.getOutputMediaFileUri(getApplicationContext(), file);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    /**
     * Requesting permissions using Dexter library
     */
    private void requestCameraPermission(final int type) {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            if (type == MEDIA_TYPE_IMAGE) {
                                // capture picture
                                captureImage();
                            } else {
                                // alert
                                new AlertDialog.Builder(new ContextThemeWrapper(UserRegisterActivity.this, R.style.AlertDialogCustom))
                                        .setTitle(getResources().getString(R.string.user_register_camera_title))
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setMessage(getResources().getString(R.string.user_register_camera_error_permission))
                                        .setPositiveButton(android.R.string.yes, null)
                                        .setCancelable(false)
                                        .show();
                            }
                        } else if (report.isAnyPermissionPermanentlyDenied()) {
                            showPermissionsAlert();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    /**
     * Display image from gallery
     */
    private void previewCapturedImage() {
        try {
            Bitmap bitmap = CameraUtils.optimizeBitmap(BITMAP_SAMPLE_SIZE, imageStoragePath);
            _avatarImage.setImageBitmap(bitmap);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Restoring store image path from saved instance state
     */
    private void restoreFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_IMAGE_STORAGE_PATH)) {
                imageStoragePath = savedInstanceState.getString(KEY_IMAGE_STORAGE_PATH);
                if (!TextUtils.isEmpty(imageStoragePath)) {
                    if (imageStoragePath.substring(imageStoragePath.lastIndexOf(".")).equals("." + IMAGE_EXTENSION)) {
                        previewCapturedImage();
                    }
                }
            }
        }
    }

    /**
     * Alert dialog to navigate to app settings
     * to enable necessary permissions
     */
    private void showPermissionsAlert() {
        new AlertDialog.Builder(new ContextThemeWrapper(UserRegisterActivity.this, R.style.AlertDialogCustom))
                .setTitle(getResources().getString(R.string.user_register_camera_title))
                .setMessage(getResources().getString(R.string.user_register_camera_error_to_permission))
                .setPositiveButton(getResources().getString(R.string.user_register_camera_btn_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CameraUtils.openSettings(UserRegisterActivity.this);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.user_register_camera_btn_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

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
                        // take focus on selectedItemOfRegions spinner drop down
                        //_regionSpinner.requestFocus();
                        // hide keyboard
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
     * Initialize user selectedItemOfRegions'spinner (drop down list)
     */
    private void initRegionSpinnerData() {
        // drop down element
        regions = Arrays.asList(getResources().getStringArray(R.array.array_regions));

        // set adapter for spinner
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, regions) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;

                // color
                if (position == 0) {
                    // set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                    tv.setAllCaps(false);
                } else {
                    tv.setTextColor(Color.BLACK);
                    tv.setAllCaps(true);
                }

                // background
                if (position % 2 == 1) {
                    // set the alternate item background color
                    tv.setBackgroundColor(getResources().getColor(R.color.md_blue_grey_50));
                } else {
                    // Set the item background color
                    tv.setBackgroundColor(getResources().getColor(R.color.md_blue_grey_100));
                }

                return view;
            }
        };

        // set drop down
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        _regionSpinner.setAdapter(arrayAdapter);

        // event onClick
        _regionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItemOfRegions = (String) parent.getItemAtPosition(position);
                // showing selected spinner item name and position
                if (position != 0) {
                    showLongToast(parent.getContext(), "Region selected : " + selectedItemOfRegions + "\n(at position n° " + position + ")");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Displaying Error if no item from a spinner is selected
     */
    private void setSpinnerError(Spinner spinner, String error) {
        View selectedView = spinner.getSelectedView();
        if (selectedView != null && selectedView instanceof TextView) {
            spinner.requestFocus();
            TextView selectedTextView = (TextView) selectedView;
            selectedTextView.setError(error); // any name of the error will do
            selectedTextView.setTextColor(Color.RED); //text color in which you want your error message to be displayed
            // spinner.performClick(); // to open the spinner list if error is found.

        }
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
     * Event onClick on validate button
     */
    private void submit() {
        // validate form
        if (!validate()) {
            onRegisterFailed();
            return;
        }

        // disable login button
        _btnSignUp.setEnabled(false);

        // show spinner
        showLoadingView(getResources().getString(R.string.app_spinner));

        // set user values
        UserRegisterSend user = new UserRegisterSend(phone, name, password, selectedItemOfRegions);

        // set retrofit api
        Service api = Client.getClient(ROOT_MDZ_USER_API).create(Service.class);

        // create basic authentication
        String auth = BasicAuth();

        // send query
        Call<ResponseBody> call = api.userRegisterJSON(auth, user);

        // request
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 201) {
                    // user credential
                    UserCheckCredential user = new UserCheckCredential(phone, password);

                    // set retrofit api
                    Service api = Client.getClient(ROOT_MDZ_USER_API).create(Service.class);

                    // create basic authentication
                    String auth = BasicAuth();

                    // send query
                    Call<UserCredentialResponse> call2 = api.checkCredential(auth, user);

                    // request
                    call2.enqueue(new Callback<UserCredentialResponse>() {
                        @Override
                        public void onResponse(Call<UserCredentialResponse> call, Response<UserCredentialResponse> response) {
                            if (response.raw().code() != 200) {
                                _btnSignUp.setEnabled(true);
                                showAlertDialog(getResources().getString(R.string.user_register_error_title), android.R.drawable.ic_dialog_alert, getResources().getString(R.string.user_register_error_licence));
                            } else {
                                if (response.body().getSuccess()) {
                                    // save user in cache
                                    saveCurrentUser(UserRegisterActivity.this, response.body());

                                    // push user phone number
                                    Intent intent = new Intent(UserRegisterActivity.this, MainActivity.class);
                                    intent.putExtra("PHONE", phone);

                                    // on complete call either onRegisterSuccess or onRegisterFailed
                                    onRegisterSuccess();

                                    // start activity
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                    finish();
                                } else {
                                    _btnSignUp.setEnabled(true);
                                    showAlertDialog(getResources().getString(R.string.user_register_error_title), android.R.drawable.ic_dialog_alert, getResources().getString(R.string.user_register_error_request));
                                }
                            }
                            hideLoadingView();
                        }

                        @Override
                        public void onFailure(Call<UserCredentialResponse> call, Throwable t) {
                            _btnSignUp.setEnabled(true);
                            showAlertDialog(getResources().getString(R.string.user_register_error_title), android.R.drawable.ic_dialog_alert, getResources().getString(R.string.app_internet_error_message));
                            hideLoadingView();
                        }
                    });
                } else {
                    _btnSignUp.setEnabled(true);
                    showAlertDialog(getResources().getString(R.string.user_register_error_title), android.R.drawable.ic_dialog_alert, "You need license for this app, contact your provider");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                _btnSignUp.setEnabled(true);
                showAlertDialog(getResources().getString(R.string.user_register_error_title), android.R.drawable.ic_dialog_alert, getResources().getString(R.string.app_internet_error_message));
                hideLoadingView();
            }
        });
    }

    /**
     * Validate form
     */
    private boolean validate() {
        boolean valid = true;

        // get input values
        name = _nameText.getText().toString();
        firstName = _firstNameText.getText().toString();
        phone = _phoneText.getText().toString();
        address = _addressText.getText().toString();
        password = _passwordText.getText().toString();
        rePassword = _rePasswordText.getText().toString();

        // get avatar's default bitmap
        /*Bitmap bitmap = ((BitmapDrawable)_avatarImage.getDrawable()).getBitmap();
        Drawable myDrawable = getResources().getDrawable(R.drawable.ic_splash);
        Bitmap defaultAvatar = ((BitmapDrawable) myDrawable).getBitmap();

        // verify avatar
        if(bitmap.sameAs(defaultAvatar)) {
            showAlertDialog(getResources().getString(R.string.user_register_btn_submit), android.R.drawable.ic_dialog_alert, getResources().getString(R.string.user_register_input_error_avatar));
            valid = false;
        }
        // name
        else*/
        if (name.isEmpty() || TextUtils.isEmpty(name) || !(name.matches(nameRegex))) {
            _nameText.setError(getResources().getString(R.string.user_register_input_error_name));
            _nameText.requestFocus();
            valid = false;
        }
        // firstName
        /*else if (firstName.isEmpty() || TextUtils.isEmpty(firstName) || !(firstName.matches(nameRegex))) {
            _firstNameText.setError(getResources().getString(R.string.user_register_input_error_firstName));
            _firstNameText.requestFocus();
            valid = false;
        }*/
        // phone number
        else if (phone.isEmpty() || TextUtils.isEmpty(phone) || !(phone.matches(numberRegex)) || phone.length() != 10) {
            _phoneText.setError(getResources().getString(R.string.user_login_input_error_phone));
            _phoneText.requestFocus();
            valid = false;
        }
        // selectedItemOfRegions
        else if (selectedItemOfRegions.isEmpty() || TextUtils.isEmpty(selectedItemOfRegions) || selectedItemOfRegions.contains(getResources().getString(R.string.user_register_input_error_region))) {
            setSpinnerError(_regionSpinner, getResources().getString(R.string.user_register_input_error_region));
            valid = false;
        }
        // address
        /*else if (address.isEmpty() || TextUtils.isEmpty(address) || !(address.matches(nameRegex))) {
            _addressText.setError(getResources().getString(R.string.user_register_input_error_address));
            _addressText.requestFocus();
            valid = false;
        }*/
        // password
        else if (password.isEmpty() || TextUtils.isEmpty(password)) {
            _passwordText.setError(getResources().getString(R.string.user_login_input_error_password));
            _passwordText.requestFocus();
            valid = false;
        }
        // confirm password
        else if (rePassword.isEmpty() || TextUtils.isEmpty(rePassword)) {
            _rePasswordText.setError(getResources().getString(R.string.user_login_input_error_password));
            _rePasswordText.requestFocus();
            valid = false;
        }
        // password different values
        else if (!password.equals(rePassword)) {
            _rePasswordText.setError(getResources().getString(R.string.user_register_input_error_diff_password));
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
        //resetAllInputText();
        clearAllInputError();
        _btnSignUp.setEnabled(true);

    }

    /**
     * Failed register
     */
    private void onRegisterFailed() {
        showLongToast(getBaseContext(), "Register failed");
        _btnSignUp.setEnabled(true);
    }

    /**
     * Clear inputs error
     */
    private void clearAllInputError() {
        _nameText.setError(null);
        _firstNameText.setError(null);
        _phoneText.setError(null);
        //_regionSpinner.setError(null);
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
        _regionSpinner.clearFocus();
        _addressText.clearFocus();
        _passwordText.clearFocus();
        _rePasswordText.clearFocus();
    }

    /**
     * Reset inputs text
     */
    private void resetAllInputText() {
        // avatar
        Drawable myDrawable = getResources().getDrawable(R.drawable.ic_user_black);
        Bitmap bitmap = ((BitmapDrawable) myDrawable).getBitmap();
        _avatarImage.setImageBitmap(bitmap);

        // input
        _nameText.setText("");
        _firstNameText.setText("");
        _phoneText.setText("");
        selectedItemOfRegions = getResources().getString(R.string.user_register_input_text_region);
        _regionSpinner.setPrompt(getResources().getString(R.string.user_register_input_text_region));
        _addressText.setText("");
        _passwordText.setText("");
        _rePasswordText.setText("");
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}
