package com.team.lezomadetana.fragment;

import android.content.DialogInterface;
import android.graphics.Color;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.team.lezomadetana.R;
import com.team.lezomadetana.activity.BaseActivity;
import com.team.lezomadetana.activity.MainActivity;
import com.team.lezomadetana.api.Client;
import com.team.lezomadetana.api.Service;
import com.team.lezomadetana.model.send.TransactionAriaryJeton;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by RaThierry on 13/09/2018.
 **/

public class FragmentPaymentGiveMoney extends BaseFragment {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private BaseActivity activity;
    private MainActivity mainActivity;
    private Spinner spinnerOperatorCode;
    private EditText editTextAmount;
    private EditText editTextPhone;
    private EditText editTextPassword;
    private Button btnSend;
    private View rootView;

    private String amountText;
    private String codeOperatorText;
    private String phoneText;
    private String passwordText;

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
                inflater.inflate(R.layout.fragment_payment_give_money, container, false);

        // current activity
        activity = ((BaseActivity) getActivity());
        mainActivity = ((MainActivity) getActivity());

        // set navItemIndex value
        activity.navItemIndex = 3;

        // toolBar title
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.fragment_payment_toolbar_give_money));

        // init view
        editTextAmount = (EditText) rootView.findViewById(R.id.fragment_payment_give_money_edit_text_amount);
        spinnerOperatorCode = (Spinner) rootView.findViewById(R.id.fragment_payment_give_money_spinner_operator);
        editTextPhone = (EditText) rootView.findViewById(R.id.fragment_payment_give_money_edit_text_phone);
        editTextPassword = (EditText) rootView.findViewById(R.id.fragment_payment_give_money_edit_text_password);
        btnSend = (Button) rootView.findViewById(R.id.fragment_payment_give_money_btn_send);

        // listener, event
        initializeListenerAndEvent();

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
        // amount edit text
        editTextAmount.addTextChangedListener(onTextAmountChangedListener(editTextAmount));

        // drop down element
        List<String> codeOperator = Arrays.asList(getResources().getStringArray(R.array.array_code_operator));

        // set adapter for spinner
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item_grey, codeOperator) {
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
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item_grey);

        // attaching data adapter to spinner
        spinnerOperatorCode.setAdapter(arrayAdapter);

        // event onClick
        spinnerOperatorCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // item'clicked name
                codeOperatorText = parent.getItemAtPosition(position).toString();
                // showing clicked spinner item name and position
                if (position != 0) {
                    showLongToast(parent.getContext(), "Code selected: " + codeOperatorText + "\nPosition: " + position + ")");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // phone edit text
        editTextPhone.addTextChangedListener(onTextPhoneNumberChangedListenerForPayment());

        // btn submit
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set values
                amountText = editTextAmount.getText().toString().replaceAll(",", "");
                phoneText = editTextPhone.getText().toString();
                passwordText = editTextPassword.getText().toString();
                String phoneNumberText = codeOperatorText + phoneText;

                // amount
                if (amountText.isEmpty() || TextUtils.isEmpty(amountText)) {
                    editTextAmount.setError(getResources().getString(R.string.fragment_payment_charge_amount_text));
                    editTextAmount.requestFocus();
                }
                // code operator
                else if (codeOperatorText.isEmpty() || TextUtils.isEmpty(codeOperatorText) || codeOperatorText.contains(getResources().getString(R.string.fragment_payment_charge_phone_code_error))) {
                    setSpinnerError(spinnerOperatorCode, getResources().getString(R.string.fragment_payment_charge_phone_code_error));
                }
                // phone
                else if (phoneNumberText.isEmpty() || TextUtils.isEmpty(phoneNumberText) || phoneNumberText.length() != 10) {
                    editTextPhone.setError(getResources().getString(R.string.fragment_payment_charge_phone_text));
                    editTextPhone.requestFocus();
                }
                // password
                else if (passwordText.isEmpty() || TextUtils.isEmpty(passwordText)) {
                    editTextPassword.setError(getResources().getString(R.string.fragment_payment_give_money_password_text));
                    editTextPassword.requestFocus();
                }
                // call api
                else {
                    // clear all error/focus
                    clearAllInputError();
                    clearAllInputFocus();

                    // showing formatted text and original text of "editTextAmount" to toast
                    showLongToast(getContext(),
                            "Formatted amount value: " + editTextAmount.getText().toString() +
                                    "\nOriginal input amount: " + amountText +
                                    "\nPhone: " + phoneNumberText +
                                    "\nPassword: " + passwordText);

                    // show spinner
                    showLoadingView(getResources().getString(R.string.app_spinner));

                    // basic authentication
                    final String auth = activity.BasicAuth();

                    // api interface
                    Service api = Client.getClient(BaseActivity.ROOT_MDZ_USER_API).create(Service.class);

                    // mapping model
                    TransactionAriaryJeton transactionSend = new TransactionAriaryJeton();
                    transactionSend.setUserId(activity.getCurrentUser(getContext()).getId());
                    transactionSend.setPhone("phoneNumberText");
                    transactionSend.setAmount(Float.valueOf(amountText));

                    // model operator
                    TransactionAriaryJeton.Operator operator = null;

                    // check operator code
                    if (codeOperatorText.equals("032")) {
                        operator = TransactionAriaryJeton.Operator.ORANGE;
                    } else if (codeOperatorText.equals("033")) {
                        operator = TransactionAriaryJeton.Operator.AIRTEL;
                    } else if (codeOperatorText.equals("034")) {
                        operator = TransactionAriaryJeton.Operator.TELMA;
                    }

                    // set value and type
                    transactionSend.setOperator(operator);
                    transactionSend.setType(TransactionAriaryJeton.Type.WITHDRAWAL);

                    // send query
                    Call<Void> call = api.commitTransactionAriary2Jeton(auth, transactionSend);

                    // request
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.code() == 201) {
                                // reset input text
                                resetAllInputText();
                                // hide spinner
                                hideLoadingView();
                                // // //
                                new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AlertDialogCustom))
                                        .setIcon(R.drawable.ic_info_black)
                                        .setTitle("Famoahana Vola")
                                        .setMessage("Tontosa ny fangatahanao.")
                                        .setCancelable(false)
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                dialog.dismiss();
                                                mainActivity.getFragmentPayment().refreshMadCoin();
                                                // back to payment fragment
                                                activity.navItemIndex = 3;
                                                mainActivity.onBackPressed();
                                                // toast
                                                showLongToast(activity, "- FragmentPayment / Hamoaka Vola -");
                                            }
                                        })
                                        .show();
                            } else {
                                hideLoadingView();
                                showAlertDialog("Famoahana Vola", R.drawable.ic_error_outline_black, "\"response.code\" : " + response.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            hideLoadingView();
                            showAlertDialog("Famoahana Vola", R.drawable.ic_error_outline_black, "Misy tsy fihetezana ilay \"commitTransactionAriary2Jeton\"");
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
        spinnerOperatorCode.setBackgroundColor(getResources().getColor(R.color.transparent));
        editTextPhone.setError(null);
        editTextPassword.setError(null);
    }

    /**
     * Clear inputs focus
     */
    private void clearAllInputFocus() {
        editTextAmount.clearFocus();
        editTextPhone.clearFocus();
        editTextPassword.clearFocus();
    }

    /**
     * Reset inputs text
     */
    private void resetAllInputText() {
        editTextAmount.setText("");
        spinnerOperatorCode.setPrompt(getResources().getString(R.string.fragment_payment_charge_phone_code_error));
        editTextPhone.setText("");
        editTextPassword.setText("");
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}
