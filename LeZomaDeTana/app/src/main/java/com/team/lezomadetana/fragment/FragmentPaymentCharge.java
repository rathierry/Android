package com.team.lezomadetana.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.team.lezomadetana.R;
import com.team.lezomadetana.activity.BaseActivity;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.Arrays;
import java.util.List;

/**
 * Created by RaThierry on 13/09/2018.
 **/

public class FragmentPaymentCharge extends BaseFragment {

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
    private Button btnSend;
    private View rootView;

    private String amountText;
    private String codeOperatorText;
    private String phoneText;

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
                inflater.inflate(R.layout.fragment_payment_charge, container, false);

        // current activity
        activity = ((BaseActivity) getActivity());

        // set navItemIndex value
        activity.navItemIndex = 3;

        // toolBar title
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.fragment_payment_toolbar_charge));

        // init view
        editTextAmount = (EditText) rootView.findViewById(R.id.fragment_payment_charge_edit_text_amount);
        spinnerOperatorCode = (MaterialBetterSpinner) rootView.findViewById(R.id.fragment_payment_charge_spinner_operator);
        editTextPhone = (EditText) rootView.findViewById(R.id.fragment_payment_charge_edit_text_phone);
        btnSend = (Button) rootView.findViewById(R.id.fragment_payment_charge_btn_send);

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

    @Override
    public void onResume() {
        super.onResume();
        clearAllInputFocus();
        clearAllInputError();
        resetAllInputText();
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
                    editTextPhone.setError(getResources().getString(R.string.fragment_payment_charge_phone_text));
                    editTextPhone.requestFocus();
                }
                // call api
                else {
                    clearAllInputError();
                    clearAllInputFocus();

                    // showing formatted text and original text of "editTextAmount" to toast
                    showLongToast(getContext(),
                            "Formatted amount value: " + editTextAmount.getText().toString() +
                                    "\nOriginal input amount: " + amountText +
                                    "\nPhone: " + phoneNumberText);


                    resetAllInputText();
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
        spinnerOperatorCode.setTextColor(getResources().getColor(R.color.color_black));
        editTextPhone.setError(null);
    }

    /**
     * Clear inputs focus
     */
    private void clearAllInputFocus() {
        editTextAmount.clearFocus();
        spinnerOperatorCode.clearFocus();
        editTextPhone.clearFocus();
    }

    /**
     * Reset inputs text
     */
    private void resetAllInputText() {
        editTextAmount.setText("");
        spinnerOperatorCode.setText(getResources().getString(R.string.fragment_payment_charge_phone_hint_spinner));
        editTextPhone.setText("");
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}
