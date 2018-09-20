package com.team.lezomadetana.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.team.lezomadetana.R;
import com.team.lezomadetana.api.Client;
import com.team.lezomadetana.api.Service;
import com.team.lezomadetana.model.receive.User;
import com.team.lezomadetana.model.receive.UserCredentialResponse;
import com.team.lezomadetana.model.send.UserCheckCredential;
import com.team.lezomadetana.utils.CameraUtils;
import com.team.lezomadetana.view.UrlImageView;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.io.File;
import java.util.Arrays;
import java.util.List;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserUpdateInfoActivity extends BaseActivity {

    RelativeLayout _layout;
    UrlImageView _avatarImage;
    EditText _nameText;
    EditText _firstNameText;
    EditText _phoneText;
    MaterialBetterSpinner _regionSpinner;
    EditText _addressText;
    EditText _passwordText;
    EditText _rePasswordText;
    Button _btnSignUp;

    // input values
    private String name;
    private String firstName;
    private String phone;
    private String region;
    private String address;
    private String password;
    private String rePassword;
    private Bitmap _bitmapImage;

    private User defaultUser = null;
    private User changedUser = null;

    private Context context;
    private Activity activity;

    private  boolean isImageChanged;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        activity = this;

        setContentView(R.layout.activity_user_update_info);
        _layout = findViewById(R.id.user_update_relativeLayout);
        _avatarImage = findViewById(R.id.user_update_imageView_logo);
        _nameText = findViewById(R.id.user_update_input_name);
        _firstNameText = findViewById(R.id.user_update_input_firstname);
        _phoneText = findViewById(R.id.user_update_input_phone);
        _regionSpinner = findViewById(R.id.user_update_material_design_spinner_region);
        _addressText = findViewById(R.id.user_update_input_address);
        _passwordText = findViewById(R.id.user_update_input_password);
        _rePasswordText = findViewById(R.id.user_update_input_re_password);
        _btnSignUp = findViewById(R.id.user_update_btn_validate);
        // set image avatar to rounded
        _avatarImage.useRoundedBitmap = true;

        /*((BaseActivity)activity).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((BaseActivity)activity).getSupportActionBar().setTitle("NY momba anao");*/

        _avatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserAvatar();
            }
        });
        passwordOnFocusChange();
        showDefaultValue();



        _btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    changedUser.setName(_nameText.getText().toString());
                    changedUser.setUsername(_phoneText.getText().toString());
                    changedUser.setRegion(_regionSpinner.getText().toString());
                    changedUser.setPassword(_passwordText.getText().toString());

                    if(IsUserInfoChange()){
                        showConfirmPopup();
                    }
                    else {
                        showLongToast(context,"Tsy nisy niova");
                    }

                }
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


    private void showDefaultValue()
    {
        showLoadingView("Miandry ...");

        Service api = Client.getClient(BaseActivity.ROOT_MDZ_USER_API).create(Service.class);

        // create basic authentication
        String auth = BasicAuth();




        // send query

        final UserCredentialResponse cUser = getCurrentUser(this);
        Call<JsonObject> call = api.getUserById(auth,cUser.getId());
        // request
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response)
            {
                if(response.code() == 200)
                {
                    JsonObject jUser = response.body().getAsJsonObject();
                    User user = new Gson().fromJson(jUser,User.class);
                    defaultUser = user;
                    changedUser = new User();

                    changedUser.setId(user.getId());
                    changedUser.setProfileImageUrl(user.getProfileImageUrl());
                    changedUser.setCreationTime(user.getCreationTime());


                    Log.d("pouaaa",user.toString());

                    _nameText.setText(user.getName());
                    //_firstNameText;
                    _phoneText.setText(user.getUsername());
                    _regionSpinner.setText(user.getRegion());

                    _passwordText.setText(user.getPassword());
                    _rePasswordText.setText(user.getPassword());

                    if(user.getProfileImageUrl() !=null && !user.getProfileImageUrl().isEmpty() && !TextUtils.isEmpty(user.getProfileImageUrl())){
                        applyProfilePicture(_avatarImage,user.getProfileImageUrl());
                    }
                    hideLoadingView();

                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private void initSpinnerForRegion() {
        // drop down element
        List<String> regions = Arrays.asList(getResources().getStringArray(R.array.array_regions));

        // set adapter for spinner
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, regions);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        _regionSpinner.setAdapter(arrayAdapter);

        // event onClick
        _regionSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // item'clicked name
                region = parent.getItemAtPosition(position).toString();

                // showing clicked spinner item name and position
                showLongToast(parent.getContext(), "Region selected : " + region + "\n(at position n° " + position + ")");
            }
        });
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
                    isImageChanged = true;
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
                    isImageChanged = true;
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

    /**
     * Event onClick on avatar image
     */
    private void getUserAvatar() {
        new AlertDialog.Builder(new ContextThemeWrapper(UserUpdateInfoActivity.this, R.style.AlertDialogCustom))
                .setTitle(getResources().getString(R.string.user_register_camera_title))
                .setIcon(ContextCompat.getDrawable(UserUpdateInfoActivity.this, R.drawable.ic_photo_camera_black))
                .setMessage(getResources().getString(R.string.user_register_camera_message))
                .setCancelable(true)
                .setPositiveButton(getResources().getString(R.string.user_register_camera_app), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        imageTypeCamera();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.user_register_camera_galery), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        imageTypeFile();
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
        new AlertDialog.Builder(new ContextThemeWrapper(UserUpdateInfoActivity.this, R.style.AlertDialogCustom))
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
                                new AlertDialog.Builder(new ContextThemeWrapper(UserUpdateInfoActivity.this, R.style.AlertDialogCustom))
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
        new AlertDialog.Builder(new ContextThemeWrapper(UserUpdateInfoActivity.this, R.style.AlertDialogCustom))
                .setTitle(getResources().getString(R.string.user_register_camera_title))
                .setMessage(getResources().getString(R.string.user_register_camera_error_to_permission))
                .setPositiveButton(getResources().getString(R.string.user_register_camera_btn_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CameraUtils.openSettings(UserUpdateInfoActivity.this);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.user_register_camera_btn_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }


    private boolean IsUserInfoChange()
    {
        boolean isChanged = true;


        if(defaultUser.getName().equals(changedUser.getName()) && defaultUser.getUsername().equals(changedUser.getUsername()) && defaultUser.getRegion().equals(changedUser.getRegion()) && defaultUser.getPassword().equals(changedUser.getPassword())){
            isChanged = false;
        }

        return isChanged;
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
        region = _regionSpinner.getText().toString();
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
        // region
        else if (region.isEmpty() || TextUtils.isEmpty(region) || region.contains(getResources().getString(R.string.user_register_input_text_region))) {
            _regionSpinner.setError(getResources().getString(R.string.user_register_input_error_region));
            _regionSpinner.requestFocus();
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
     * Clear inputs error
     */
    private void clearAllInputError() {
        _nameText.setError(null);
        _firstNameText.setError(null);
        _phoneText.setError(null);
        _regionSpinner.setError(null);
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



    private void showConfirmPopup() {
        // get prompts xml view
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        final View mView = layoutInflaterAndroid.inflate(R.layout.confirm_user_update, null);

        // create alert builder and cast view
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(new android.view.ContextThemeWrapper(context, R.style.AlertDialogCustom));

        // set prompts xml to alert dialog builder
        builder.setView(mView);

        // init view

        final EditText passEdit = (EditText) mView.findViewById(R.id.confirm_user_update_passworda);

        // drop down element



        // set dialog message
        builder
                .setTitle("Fanamarihana")
                .setIcon(R.drawable.ic_info_black)
                .setCancelable(false)
                .setPositiveButton(R.string.user_login_forgot_pass_btn_ok, null)
                .setNegativeButton(R.string.user_login_forgot_pass_btn_cancel, null);

        // create alert dialog
        final android.app.AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button buttonOK = ((android.app.AlertDialog) dialog).getButton(android.app.AlertDialog.BUTTON_POSITIVE);
                Button buttonCancel = ((android.app.AlertDialog) dialog).getButton(android.app.AlertDialog.BUTTON_NEGATIVE);

                // validate
                buttonOK.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
                buttonOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // values


                        String pass = passEdit.getText().toString();
                        // product

                        // category
                        if (password.isEmpty() || TextUtils.isEmpty(password)) {
                            passEdit.setError("tsy mety");
                            return;
                        }

                        // show spinner
                        showLoadingView(getResources().getString(R.string.app_spinner));

                        //
                        final BaseActivity baseActivity = (BaseActivity) activity;
                        Service api = Client.getClient(baseActivity.ROOT_MDZ_USER_API).create(Service.class);

                        // create basic authentication
                        String auth = baseActivity.BasicAuth();

                        UserCredentialResponse userCredentialResponse = baseActivity.getCurrentUser(context);
                        UserCheckCredential userCredential = new UserCheckCredential();
                        userCredential.setUsername(userCredentialResponse.getUsername());
                        userCredential.setPassword(pass);

                        Call<UserCredentialResponse> call = api.checkCredential(auth,userCredential);

                        // request
                        call.enqueue(new Callback<UserCredentialResponse>() {
                            @Override
                            public void onResponse(Call<UserCredentialResponse> call, Response<UserCredentialResponse> response) {
                                if (response.raw().code() == 200) {

                                    // set retrofit api
                                    Service api2 = Client.getClient(ROOT_MDZ_USER_API).create(Service.class);

                                    // create basic authentication
                                    String auth = BasicAuth();

                                    final UserCredentialResponse urep = response.body();

                                    // send query
                                    Call<ResponseBody> call2 = api2.userUpdateJSON(auth, changedUser);

                                    // request
                                    call2.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            if (response.code() == 201) {

                                                baseActivity.saveCurrentUser(context,urep);
                                                baseActivity.onBackPressed();
                                            }
                                            else
                                                {
                                                    hideLoadingView();
                                                    new AlertDialog.Builder(new android.view.ContextThemeWrapper(context, R.style.AlertDialogCustom))
                                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                                            .setTitle("Olana")
                                                            .setMessage("Misy tsy mety")
                                                            .setCancelable(false)
                                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                                    dialog.dismiss();
                                                                }
                                                            })
                                                            .show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            hideLoadingView();

                                            // alert
                                            new AlertDialog.Builder(new android.view.ContextThemeWrapper(context, R.style.AlertDialogCustom))
                                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                                    .setTitle("Olana")
                                                    .setMessage("Misy olana ny tambanjotra")
                                                    .setCancelable(false)
                                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int whichButton) {
                                                            dialog.dismiss();
                                                        }
                                                    })
                                                    .show();
                                        }
                                    });


                                }
                                    else{


                                    hideLoadingView();
                                    new AlertDialog.Builder(new android.view.ContextThemeWrapper(context, R.style.AlertDialogCustom))
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .setTitle("Olana")
                                            .setMessage("Diso ny teny miafina")
                                            .setCancelable(false)
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .show();
                                }
                            }

                            @Override
                            public void onFailure(Call<UserCredentialResponse> call, Throwable t) {
                                // hide swipeRefresh / shimmerAnim / spinner

                                hideLoadingView();

                                // alert
                                new AlertDialog.Builder(new android.view.ContextThemeWrapper(context, R.style.AlertDialogCustom))
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setTitle("Olana")
                                        .setMessage("Misy olana ny tamanjotra")
                                        .setCancelable(false)
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();
                            }
                        });

                    }
                });

                // cancel
                buttonCancel.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
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

}
