package com.tresor.mobilemoney.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.aakira.expandablelayout.ExpandableLayoutListener;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.tresor.mobilemoney.R;
import com.tresor.mobilemoney.adapter.CustomOnItemSelectedListener;
import com.tresor.mobilemoney.utils.ImageCaptureUtil;

/**
 * Created by Priscilla on 01/09/2017.
 */
public class UserRegisterActivity extends BaseActivity implements View.OnClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private final AppCompatActivity activity = UserRegisterActivity.this;

    private LinearLayout _linearLayoutCINDuplicata;

    private ExpandableRelativeLayout _expandableLayoutCredential;
    private ExpandableRelativeLayout _expandableLayoutInfo;
    private ExpandableRelativeLayout _expandableLayoutContact;

    private RadioGroup _radioGroupNationality;
    private RadioGroup _radioGroupSex;

    private RadioButton _radioButtonNationality;
    private RadioButton _radioButtonSex;

    private CheckBox _checkBoxCINDuplicata;

    private Spinner _spinnerProvince;
    private Spinner _spinnerRegion;
    private Spinner _spinnerDistrict;
    private Spinner _spinnerCommune;

    private Boolean isInputRecto;
    private Uri _fileUri;

    private EditText _editTextCINNumber;
    private EditText _editTextCINNumberDate;
    private EditText _editTextCINNumberDoneAt;
    private EditText _editTextCINNumberDuplicataDate;
    private EditText _editTextCINNumberDuplicataDoneAt;
    private EditText _editTextCINPhotoRecto;
    private EditText _editTextCINPhotoVerso;
    private EditText _editTextName;
    private EditText _editTextFirstName;
    private EditText _editTextBirthday;
    private EditText _editTextPhone;
    private EditText _editTextEmail;
    private EditText _editTextAddress;

    private AppCompatButton _buttonAccount;
    private Button _buttonExpandableCredential;
    private Button _buttonExpandableInfo;
    private Button _buttonExpandableContact;
    private Button _buttonCINNumberVerify;
    private Button _buttonCINNumberDuplicateDate;
    private Button _buttonCINNumberDuplicateDoneAt;

    private CompoundButton.OnClickListener clickListener;

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

        initFont();
        initActionBarTitle(getResources().getString(R.string.activity_user_register_title_toolBar));
        initViews();
        disableLayoutCINDuplicata();
        initListeners();
    }

    // ===========================================================
    // Methods for Interfaces
    // ===========================================================

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * This method is to initialize click (views)
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.activityUserRegisterButtonCINNumberVerify:
                Toast.makeText(UserRegisterActivity.this, "C.I.N / PASSEPORT : " + _editTextCINNumber.getText(), Toast.LENGTH_SHORT).show();
                break;

            case R.id.activityUserRegisterEditTextCINNumberDate:
                setDatePickerDialog(_editTextCINNumberDate);
                break;

            case R.id.activityUserRegisterCheckBoxCINDuplicata:
                if (((CheckBox) view).isChecked()) {
                    enableLayoutCINDuplicata();
                } else {
                    disableLayoutCINDuplicata();
                }
                break;

            case R.id.activityUserRegisterEditTextCINNumberDuplicata:
                setDatePickerDialog(_editTextCINNumberDuplicataDate);
                break;

            case R.id.activityUserRegisterEditTextCINPhotoRecto:
                isInputRecto = true;
                choiceYourPhoto();
                break;

            case R.id.activityUserRegisterEditTextCINPhotoVerso:
                isInputRecto = false;
                choiceYourPhoto();
                break;

            case R.id.activityUserRegisterEditTextBirthday:
                setDatePickerDialog(_editTextBirthday);
                break;

            case R.id.userRegisterActivityButtonAccount:

                Toast.makeText(UserRegisterActivity.this,
                        "-------------------------------" +
                                "\nNationalité : " + checkNationality() +
                                "\nCIN/PASSEPORT : " + _editTextCINNumber.getText() +
                                "\nDu : " + _editTextCINNumberDate.getText() +
                                "\nFait à : " + _editTextCINNumberDoneAt.getText() +
                                "\nDuplicata ? " + checkDuplicataStatus(_checkBoxCINDuplicata) +
                                "\nDu : " + getDuplicataValues(_editTextCINNumberDuplicataDate) +
                                "\nFait à : " + getDuplicataValues(_editTextCINNumberDuplicataDoneAt) +
                                "\nSexe : " + checkSex() +
                                "\nNom(s) : " + _editTextName.getText() +
                                "\nPrénom(s) : " + _editTextFirstName.getText() +
                                "\nDate de naissance : " + _editTextBirthday.getText() +
                                "\nTéléphone : " + _editTextPhone.getText() +
                                "\nE-mail : " + _editTextEmail.getText() +
                                "\nAdresse : " + _editTextAddress.getText() +
                                "\nProvince : " + String.valueOf(_spinnerProvince.getSelectedItem()) +
                                "\nRégion : " + String.valueOf(_spinnerRegion.getSelectedItem()) +
                                "\nDistrict : " + String.valueOf(_spinnerDistrict.getSelectedItem()) +
                                "\nCommune : " + String.valueOf(_spinnerCommune.getSelectedItem()) +
                                "\n-------------------------------",
                        Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }
    }

    // Here we store the file url as it will be null after returning from camera app
    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation changes
        outState.putParcelable("file_uri", _fileUri);
    }

    // Restore Activity State
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        _fileUri = savedInstanceState.getParcelable("file_uri");
    }

    // Receiving activity result method will be called after closing the camera
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CAMERA_IMAGE_REQUEST_CODE:
                // Make sure the request was successful (captured the image)
                if (resultCode == RESULT_OK) {
                    if (isInputRecto)
                        _editTextCINPhotoRecto.setText(_fileUri.getPath());
                    else
                        _editTextCINPhotoVerso.setText(_fileUri.getPath());
                } else if (resultCode == RESULT_CANCELED) {
                    // user cancelled Image capture
                } else {
                    // failed to capture image
                    new AlertDialog.Builder(UserRegisterActivity.this)
                            .setTitle(getString(R.string.avatar_error_title))
                            .setMessage(getString(R.string.avatar_failed_error_message))
                            .setPositiveButton(android.R.string.yes, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setCancelable(false)
                            .show();
                }
                break;

            case GALLERY_IMAGE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);

                    if (isInputRecto)
                        _editTextCINPhotoRecto.setText(picturePath);
                    else
                        _editTextCINPhotoVerso.setText(picturePath);

                    cursor.close();
                } else if (resultCode == RESULT_CANCELED) {
                    // user cancelled Image capture
                }
                break;

            default:
                break;
        }
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * This method is to initialize views
     */
    private void initViews() {
        // listener
        clickListener = new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleExpandableRelativeLayout(v);
            }
        };

        // linearLayout
        _linearLayoutCINDuplicata = (LinearLayout) findViewById(R.id.activityUserRegisterLinearLayoutCINDuplicata);

        // expandable
        _expandableLayoutCredential = (ExpandableRelativeLayout) findViewById(R.id.userRegisterActivityExpandableLayoutCredential);
        _expandableLayoutInfo = (ExpandableRelativeLayout) findViewById(R.id.userRegisterActivityExpandableLayoutInfo);
        _expandableLayoutContact = (ExpandableRelativeLayout) findViewById(R.id.userRegisterActivityExpandableLayoutContact);

        // button
        _buttonExpandableCredential = (Button) findViewById(R.id.userRegisterActivityExpandableButtonCredential);
        _buttonExpandableInfo = (Button) findViewById(R.id.userRegisterActivityExpandableButtonInfo);
        _buttonExpandableContact = (Button) findViewById(R.id.userRegisterActivityExpandableButtonContact);
        _buttonCINNumberVerify = (Button) findViewById(R.id.activityUserRegisterButtonCINNumberVerify);
        _buttonCINNumberDuplicateDate = (Button) findViewById(R.id.activityUserRegisterButtonCINNumberDuplicataFrom);
        _buttonCINNumberDuplicateDoneAt = (Button) findViewById(R.id.activityUserRegisterButtonCINNumberDuplicataDoneAt);
        _buttonAccount = (AppCompatButton) findViewById(R.id.userRegisterActivityButtonAccount);

        // radio, checkBox
        _radioGroupNationality = (RadioGroup) findViewById(R.id.activityUserRegisterRadioGroupNationality);
        _radioGroupSex = (RadioGroup) findViewById(R.id.activityUserRegisterRadioGroupSex);
        _checkBoxCINDuplicata = (CheckBox) findViewById(R.id.activityUserRegisterCheckBoxCINDuplicata);

        // editText
        _editTextCINNumber = (EditText) findViewById(R.id.activityUserRegisterEditTextCINNumber);
        _editTextCINNumberDate = (EditText) findViewById(R.id.activityUserRegisterEditTextCINNumberDate);
        _editTextCINNumberDoneAt = (EditText) findViewById(R.id.activityUserRegisterEditTextCINNumberDoneAt);
        _editTextCINNumberDuplicataDate = (EditText) findViewById(R.id.activityUserRegisterEditTextCINNumberDuplicata);
        _editTextCINNumberDuplicataDoneAt = (EditText) findViewById(R.id.activityUserRegisterEditTextCINNumberDuplicataDoneAt);
        _editTextCINPhotoRecto = (EditText) findViewById(R.id.activityUserRegisterEditTextCINPhotoRecto);
        _editTextCINPhotoVerso = (EditText) findViewById(R.id.activityUserRegisterEditTextCINPhotoVerso);
        _editTextName = (EditText) findViewById(R.id.activityUserRegisterEditTextName);
        _editTextFirstName = (EditText) findViewById(R.id.activityUserRegisterEditTextFirstName);
        _editTextBirthday = (EditText) findViewById(R.id.activityUserRegisterEditTextBirthday);
        _editTextPhone = (EditText) findViewById(R.id.activityUserRegisterEditTextPhone);
        _editTextEmail = (EditText) findViewById(R.id.activityUserRegisterEditTextEmail);
        _editTextAddress = (EditText) findViewById(R.id.activityUserRegisterEditTextAddress);

        // spinner
        _spinnerProvince = (Spinner) findViewById(R.id.activityUserRegisterSpinnerProvince);
        _spinnerRegion = (Spinner) findViewById(R.id.activityUserRegisterSpinnerRegion);
        _spinnerDistrict = (Spinner) findViewById(R.id.activityUserRegisterSpinnerDistrict);
        _spinnerCommune = (Spinner) findViewById(R.id.activityUserRegisterSpinnerCommune);

        _spinnerProvince.setPrompt(getResources().getString(R.string.text_select));
        _spinnerProvince.setOnItemSelectedListener(new CustomOnItemSelectedListener());

    }

    /**
     * This method is to initialize listeners
     */
    private void initListeners() {
        // button expandable
        _buttonExpandableCredential.setOnClickListener(clickListener);
        _buttonExpandableInfo.setOnClickListener(clickListener);
        _buttonExpandableContact.setOnClickListener(clickListener);

        _buttonCINNumberVerify.setOnClickListener(this);
        _buttonAccount.setOnClickListener(this);
        _editTextCINNumberDate.setOnClickListener(this);
        _checkBoxCINDuplicata.setOnClickListener(this);
        _editTextCINNumberDuplicataDate.setOnClickListener(this);
        _editTextCINPhotoRecto.setOnClickListener(this);
        _editTextCINPhotoVerso.setOnClickListener(this);
        _editTextBirthday.setOnClickListener(this);

        // CINNumber
        _editTextCINNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() == 0) {
                    // remove icon drawableLeft
                    _editTextCINNumber.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                } else if (charSequence.length() == MAX_LENGHT_UserRegisterEditTextCINNumber) {
                    // check if no view has focus:
                    View view = UserRegisterActivity.this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }

                    // add check icon drawableLeft
                    _editTextCINNumber.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_checked_green, 0);

                } else {
                    // add warning icon drawableLeft
                    _editTextCINNumber.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_warning_red, 0);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //
            }
        });

        // PhoneNumber
        _editTextPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() == MAX_LENGHT_UserRegisterEditTextPhoneNumber) {
                    // check if no view has focus:
                    View view = UserRegisterActivity.this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //
            }
        });
    }

    /**
     * This method is to setListener on expandable
     */
    private void setListenerExpandable(ExpandableRelativeLayout expandable, final Button button, final int drawable) {
        expandable.setListener(new ExpandableLayoutListener() {
            @Override
            public void onAnimationStart() {
            }

            @Override
            public void onAnimationEnd() {
            }

            // You can get notification that your expandable layout is going to open or close.
            // So, you can set the animation synchronized with expanding animation.
            @Override
            public void onPreOpen() {
            }

            @Override
            public void onPreClose() {
            }

            @Override
            public void onOpened() {
                button.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, android.R.drawable.arrow_up_float, 0);
                button.setBackgroundColor(ContextCompat.getColor(activity, R.color.userRegisterColorExpandable));
                button.setTextColor(ContextCompat.getColor(activity, R.color.color_white));
            }

            @Override
            public void onClosed() {
                button.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, android.R.drawable.arrow_down_float, 0);
                button.setBackgroundColor(ContextCompat.getColor(activity, R.color.color_background));
                button.setTextColor(ContextCompat.getColor(activity, R.color.color_black));
            }
        });
    }

    private void toggleExpandableRelativeLayout(View v) {
        switch (v.getId()) {
            case R.id.userRegisterActivityExpandableButtonCredential:
                _expandableLayoutCredential.toggle();
                setListenerExpandable(_expandableLayoutCredential, _buttonExpandableCredential, R.drawable.ic_insert_drive_file_black);
                break;

            case R.id.userRegisterActivityExpandableButtonInfo:
                _expandableLayoutInfo.toggle();
                setListenerExpandable(_expandableLayoutInfo, _buttonExpandableInfo, R.drawable.ic_person_black);
                break;

            case R.id.userRegisterActivityExpandableButtonContact:
                _expandableLayoutContact.toggle();
                setListenerExpandable(_expandableLayoutContact, _buttonExpandableContact, R.drawable.ic_language_black);
                break;

            default:
                break;
        }
    }

    private String checkNationality() {
        // get selected radio button from radioGroup
        int selectedId = _radioGroupNationality.getCheckedRadioButtonId();

        // find the radioButton by returned id
        _radioButtonNationality = (RadioButton) findViewById(selectedId);

        return String.valueOf(_radioButtonNationality.getText());
    }

    private String checkSex() {
        int selectedId = _radioGroupSex.getCheckedRadioButtonId();
        _radioButtonSex = (RadioButton) findViewById(selectedId);

        /*if (_radioGroupSex.getCheckedRadioButtonId() == -1) {
            // no radio buttons are checked
        } else {
            // one of the radio buttons is checked
        }*/

        if (_radioButtonSex != null)
            return String.valueOf(_radioButtonSex.getText());
        else
            return "";
    }

    private boolean checkDuplicataStatus(View view) {
        CheckBox checkBox = ((CheckBox) view);
        Boolean isChecked;

        if (checkBox.isChecked()) {
            isChecked = true;
        } else
            isChecked = false;

        return isChecked;
    }

    private String getDuplicataValues(EditText editText) {
        if (checkDuplicataStatus(_checkBoxCINDuplicata)) {
            editText.getText();
            editText.getText();
        } else {
            editText.setText("");
            editText.setText("");
        }

        return String.valueOf(editText.getText());
    }

    private void enableLayoutCINDuplicata() {
        _linearLayoutCINDuplicata.setBackgroundColor(ContextCompat.getColor(activity, R.color.color_white));

        _buttonCINNumberDuplicateDate.setBackgroundColor(ContextCompat.getColor(activity, R.color.color_white));
        _buttonCINNumberDuplicateDoneAt.setBackgroundColor(ContextCompat.getColor(activity, R.color.color_white));

        _editTextCINNumberDuplicataDate.setBackgroundResource(R.drawable.rounded_bgk_btn_white);
        _editTextCINNumberDuplicataDoneAt.setBackgroundResource(R.drawable.rounded_bgk_btn_white);

        _editTextCINNumberDuplicataDate.setEnabled(true);
        _editTextCINNumberDuplicataDoneAt.setEnabled(true);
        _editTextCINNumberDuplicataDoneAt.setFocusable(true);
        _editTextCINNumberDuplicataDoneAt.setClickable(true);
        _editTextCINNumberDuplicataDoneAt.setFocusableInTouchMode(true);
    }

    private void disableLayoutCINDuplicata() {
        _linearLayoutCINDuplicata.setBackgroundColor(ContextCompat.getColor(activity, R.color.color_gray_light));

        _buttonCINNumberDuplicateDate.setBackgroundColor(ContextCompat.getColor(activity, R.color.color_gray_light));
        _buttonCINNumberDuplicateDoneAt.setBackgroundColor(ContextCompat.getColor(activity, R.color.color_gray_light));

        _editTextCINNumberDuplicataDate.setBackgroundResource(R.drawable.rounded_bgk_btn_gray_light);
        _editTextCINNumberDuplicataDoneAt.setBackgroundResource(R.drawable.rounded_bgk_btn_gray_light);

        _editTextCINNumberDuplicataDate.setEnabled(false);
        _editTextCINNumberDuplicataDate.setText("");
        _editTextCINNumberDuplicataDoneAt.setEnabled(false);
        _editTextCINNumberDuplicataDoneAt.setFocusable(false);
        _editTextCINNumberDuplicataDoneAt.setText("");
    }

    private void choiceYourPhoto() {
        new AlertDialog.Builder(UserRegisterActivity.this)
                .setTitle(getString(R.string.avatar_take_picture_title))
                .setMessage(getString(R.string.avatar_take_picture_message))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.avatar_picture_type_file_button), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        imageTypeGallery();
                    }
                })
                .setNegativeButton(getString(R.string.avatar_picture_type_camera_button), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        imageTypeCamera();
                    }
                })
                .show();
    }

    // image from gallery
    private void imageTypeGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, GALLERY_IMAGE_REQUEST_CODE);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    // image from camera
    private void imageTypeCamera() {
        // Checking camera availability
        if (!isDeviceSupportCamera()) {
            showNotSupportedCameraErrorDialog();
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            _fileUri = ImageCaptureUtil.getOutputMediaFileUri();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, _fileUri);
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST_CODE);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================

}