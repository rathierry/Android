package com.team.lezomadetana.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.team.lezomadetana.R;
import com.team.lezomadetana.activity.BaseActivity;
import com.team.lezomadetana.activity.MainActivity;
import com.team.lezomadetana.api.APIClient;
import com.team.lezomadetana.api.APIInterface;
import com.team.lezomadetana.model.receive.UserCredentialResponse;
import com.team.lezomadetana.model.send.TransactionSend;
import com.team.lezomadetana.model.send.UserCheckCredential;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.team.lezomadetana.activity.BaseActivity.PREFS_KEY_ID_USER_FROM_SCAN;
import static com.team.lezomadetana.activity.BaseActivity.PREFS_NAME;
import static com.team.lezomadetana.activity.BaseActivity.ROOT_MDZ_USER_API;

/**
 * Created by RaThierry on 13/09/2018.
 **/

public class FragmentPaymentSendMoney extends BaseFragment {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private BaseActivity activity;
    private MainActivity mainActivity;
    private EditText editTextAmount;
    private EditText editTextPassword;
    private Button btnSend;
    private View rootView;
    public ImageView qrCodeTemplate;

    private String amountText;
    private String passwordText;
    private String sendedId = "";

    private APIInterface api;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // inflate the layout for this fragment or reuse the existing one
        rootView = getView() != null ? getView() :
                inflater.inflate(R.layout.fragment_payment_send_money, container, false);

        // current activity
        activity = ((BaseActivity) getActivity());
        mainActivity = ((MainActivity) getActivity());

        // set navItemIndex value
        activity.navItemIndex = 3;

        // toolBar title
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.fragment_payment_toolbar_send_money));

        // init view
        editTextAmount = (EditText) rootView.findViewById(R.id.fragment_payment_send_money_edit_text_amount);
        editTextPassword = (EditText) rootView.findViewById(R.id.fragment_payment_send_money_edit_text_password);
        btnSend = (Button) rootView.findViewById(R.id.fragment_payment_send_money_btn_send);
        qrCodeTemplate = (ImageView) rootView.findViewById(R.id.fragment_payment_send_money_imageView_qr_code);
        qrCodeTemplate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanQrCode();
            }
        });

        // listener, event
        initializeListenerAndEvent();

        // set retrofit api
        api = APIClient.getClient(ROOT_MDZ_USER_API).create(APIInterface.class);

        return rootView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem itemMenuSearch = menu.findItem(R.id.action_search);
        MenuItem itemMenuPayment = menu.findItem(R.id.action_payment);
        MenuItem itemMenuInfo = menu.findItem(R.id.action_information);

        itemMenuSearch.setEnabled(false);
        itemMenuSearch.setVisible(false);
        itemMenuPayment.setEnabled(false);
        itemMenuPayment.setVisible(false);
        itemMenuInfo.setEnabled(false);
        itemMenuInfo.setVisible(false);
    }

    @Override
    public void onResume() {
        // init pref key
        activity.sharedPrefForPaymentSendMoney = activity.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        // check key
        if (activity.sharedPrefForPaymentSendMoney.contains(PREFS_KEY_ID_USER_FROM_SCAN)) {
            // get key value
            onQrFindSomething(activity.sharedPrefForPaymentSendMoney.getString(PREFS_KEY_ID_USER_FROM_SCAN, ""));
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        // init pref key
        activity.sharedPrefForPaymentSendMoney = activity.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        // check key
        if (activity.sharedPrefForPaymentSendMoney.contains(PREFS_KEY_ID_USER_FROM_SCAN)) {
            // remove key
            SharedPreferences.Editor editor = activity.sharedPrefForPaymentSendMoney.edit();
            editor.remove(PREFS_KEY_ID_USER_FROM_SCAN);
            editor.commit();
            // set image view default drawable
            qrCodeTemplate.setImageDrawable(getResources().getDrawable(R.drawable.ic_qr_code));
        }
        super.onPause();
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
     * Init listener and event
     */
    private void initializeListenerAndEvent() {
        // amount/phone edit text
        editTextAmount.addTextChangedListener(onTextAmountChangedListener(editTextAmount));

        // btn submit
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set values
                amountText = editTextAmount.getText().toString().replaceAll(",", "");
                passwordText = editTextPassword.getText().toString();

                // id
                showLongToast(getContext(), "sendedId: " + sendedId);

                // test
                if (sendedId.isEmpty() || TextUtils.isEmpty(sendedId) || sendedId == null) {
                    showAlertDialog("Fandefasana Vola", R.drawable.ic_warning_black, "Ataovy \"scan\" ny \"QR Code\" an\'ilay olona handefasana ny vola.");
                }
                // amount
                else if (amountText.isEmpty() || TextUtils.isEmpty(amountText)) {
                    editTextAmount.setError(getResources().getString(R.string.fragment_payment_send_money_amount_text));
                    editTextAmount.requestFocus();
                }
                // password
                else if (passwordText.isEmpty() || TextUtils.isEmpty(passwordText)) {
                    editTextPassword.setError(getResources().getString(R.string.fragment_payment_give_money_password_text));
                    editTextPassword.requestFocus();
                }
                // call api
                else {
                    // clear error/focus
                    clearAllInputError();
                    clearAllInputFocus();

                    // showing formatted text and original text of "editTextAmount" to toast
                    showLongToast(getContext(),
                            "Formatted amount value: " + editTextAmount.getText().toString() +
                                    "\nOriginal input amount: " + amountText +
                                    "\nPassword: " + passwordText);

                    // show spinner
                    showLoadingView(getResources().getString(R.string.app_spinner));

                    // user credential
                    final UserCredentialResponse user = activity.getCurrentUser(getContext());

                    // user response credential
                    UserCheckCredential userResponse = new UserCheckCredential(user.getUsername(), passwordText);

                    // create basic authentication
                    final String auth = activity.BasicAuth();

                    // send query
                    Call<UserCredentialResponse> call = api.checkCredential(auth, userResponse);

                    // request
                    call.enqueue(new Callback<UserCredentialResponse>() {
                        @Override
                        public void onResponse(Call<UserCredentialResponse> call, Response<UserCredentialResponse> response) {
                            if (response.raw().code() == 200) {
                                // mapping model
                                TransactionSend transaction = new TransactionSend(user.getId(), sendedId, Float.parseFloat(amountText), "", TransactionSend.Status.PENDING);

                                // send query
                                Call<Void> call2 = api.commitTransaction(auth, transaction);
                                call2.enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        if (response.code() == 201) {
                                            // reset input text
                                            sendedId = "";
                                            resetAllInputText();
                                            // hide spinner
                                            hideLoadingView();
                                            // // //
                                            new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AlertDialogCustom))
                                                    .setIcon(R.drawable.ic_info_black)
                                                    .setTitle("Fandefasana Vola")
                                                    .setMessage("Tontosa ny fangatahanao.")
                                                    .setCancelable(false)
                                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int whichButton) {
                                                            dialog.dismiss();
                                                            // back to payment fragment
                                                            activity.navItemIndex = 3;
                                                            mainActivity.onBackPressed();
                                                            // toast
                                                            showLongToast(activity, "- FragmentPayment / Handoha Vola -");
                                                        }
                                                    })
                                                    .show();
                                        } else {
                                            hideLoadingView();
                                            showAlertDialog("Fandefasana Vola", R.drawable.ic_error_outline_black, "Misy tsy fihetezana ny \"TransactionSend\"");
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        showAlertDialog(getResources().getString(R.string.user_login_error_title), R.drawable.ic_error_outline_black, getResources().getString(R.string.app_internet_error_message));
                                        hideLoadingView();
                                    }
                                });
                            } else {
                                hideLoadingView();
                                showAlertDialog("Fandefasana Vola", R.drawable.ic_error_outline_black, "Misy tsy fihetezana ilay \"checkCredential\"");
                            }
                        }

                        @Override
                        public void onFailure(Call<UserCredentialResponse> call, Throwable t) {
                            showAlertDialog(getResources().getString(R.string.user_login_error_title), R.drawable.ic_error_outline_black, getResources().getString(R.string.app_internet_error_message));
                            hideLoadingView();
                        }
                    });
                }
            }
        });
    }

    /**
     * Clear inputs error
     */
    private void clearAllInputError() {
        editTextAmount.setError(null);
        editTextPassword.setError(null);
    }

    /**
     * Clear inputs focus
     */
    private void clearAllInputFocus() {
        editTextAmount.clearFocus();
        editTextPassword.clearFocus();
    }

    /**
     * Reset inputs text
     */
    private void resetAllInputText() {
        qrCodeTemplate.setImageDrawable(getResources().getDrawable(R.drawable.ic_qr_code));
        editTextAmount.setText("");
        editTextPassword.setText("");
    }

    /**
     * Start scan QR
     */
    public void scanQrCode() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(getActivity());
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        intentIntegrator.setPrompt("Scan");
        intentIntegrator.setBeepEnabled(false);
        intentIntegrator.setCameraId(0);
        intentIntegrator.setBarcodeImageEnabled(false);
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.initiateScan();
    }

    /**
     * Find QR result
     */
    public void onQrFindSomething(String response) {
        qrCodeTemplate.setImageDrawable(getResources().getDrawable(R.drawable.ic_valid));
        sendedId = response;
    }


    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}
