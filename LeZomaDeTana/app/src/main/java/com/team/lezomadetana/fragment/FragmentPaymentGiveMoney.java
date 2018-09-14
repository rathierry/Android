package com.team.lezomadetana.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.google.zxing.integration.android.IntentIntegrator;
import com.team.lezomadetana.R;
import com.team.lezomadetana.activity.BaseActivity;
import com.team.lezomadetana.api.APIClient;
import com.team.lezomadetana.api.APIInterface;
import com.team.lezomadetana.model.send.TransactionAriaryJeton;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

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
    private MaterialBetterSpinner spinnerOperatorCode;
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

        // set navItemIndex value
        activity.navItemIndex = 3;

        // toolBar title
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.fragment_payment_toolbar_give_money));

        // init view
        editTextAmount = (EditText) rootView.findViewById(R.id.fragment_payment_give_money_edit_text_amount);
        spinnerOperatorCode = (MaterialBetterSpinner) rootView.findViewById(R.id.fragment_payment_give_money_spinner_operator);
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
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, codeOperator);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerOperatorCode.setAdapter(arrayAdapter);

        // event onClick
        spinnerOperatorCode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // item'clicked name
                codeOperatorText = parent.getItemAtPosition(position).toString();

                // showing clicked spinner item name and position
                showLongToast(parent.getContext(), "Code selected: " + codeOperatorText + "\nPosition: " + position + ")");
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
                codeOperatorText = spinnerOperatorCode.getText().toString();
                phoneText = editTextPhone.getText().toString();
                passwordText = editTextPassword.getText().toString();
                String phoneNumberText = codeOperatorText + phoneText;

                // amount
                if (amountText.isEmpty() || TextUtils.isEmpty(amountText)) {
                    editTextAmount.setError(getResources().getString(R.string.fragment_payment_charge_amount_text));
                    editTextAmount.requestFocus();
                }
                // code operator
                else if (codeOperatorText.isEmpty() || TextUtils.isEmpty(codeOperatorText) || codeOperatorText.contains(getResources().getString(R.string.fragment_payment_charge_phone_hint_spinner))) {
                    spinnerOperatorCode.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    spinnerOperatorCode.setTextColor(getResources().getColor(R.color.color_white));
                    editTextPhone.setError(getResources().getString(R.string.fragment_payment_charge_phone_code_error));
                    editTextPhone.requestFocus();
                }
                // phone
                else if (phoneNumberText.isEmpty() || TextUtils.isEmpty(phoneNumberText) || phoneNumberText.length() != 10) {
                    spinnerOperatorCode.setBackgroundColor(getResources().getColor(R.color.transparent));
                    spinnerOperatorCode.setTextColor(getResources().getColor(R.color.color_black));
                    // // //
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
                    clearAllInputError();
                    clearAllInputFocus();

                    // showing formatted text and original text of "editTextAmount" to toast
                    showLongToast(getContext(),
                            "Formatted amount value: " + editTextAmount.getText().toString() +
                                    "\nOriginal input amount: " + amountText +
                                    "\nPhone: " + phoneNumberText +
                                    "\nPassword: " + passwordText);


                    resetAllInputText();
                }

                BaseActivity baseActivity = (BaseActivity) getActivity();
                final String auth = baseActivity.BasicAuth();


                APIInterface api = APIClient.getClient(BaseActivity.ROOT_MDZ_USER_API).create(APIInterface.class);

                // create basic authentication


                TransactionAriaryJeton transactionSend = new TransactionAriaryJeton();
                transactionSend.setUserId(baseActivity.getCurrentUser(getContext()).getId());
                transactionSend.setPhone("0346655762");
                transactionSend.setAmount(1f);
                transactionSend.setOperator(TransactionAriaryJeton.Operator.TELMA);

                transactionSend.setType(TransactionAriaryJeton.Type.WITHDRAWAL);
                // send query
                Call<Void> call = api.commitTransactionAriary2Jeton(auth,transactionSend);

                // request
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response)
                    {
                        if(response.code() == 201)
                        {

                            Log.d("Payment","METY lesy dada");
                        }

                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });
            }
        });
    }

    /**
     * Clear inputs error
     */
    private void clearAllInputError() {
        editTextAmount.setError(null);
        spinnerOperatorCode.setBackgroundColor(getResources().getColor(R.color.transparent));
        spinnerOperatorCode.setTextColor(getResources().getColor(R.color.color_black));
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
        spinnerOperatorCode.setText(getResources().getString(R.string.fragment_payment_charge_phone_hint_spinner));
        editTextPhone.setText("");
        editTextPassword.setText("");
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}
