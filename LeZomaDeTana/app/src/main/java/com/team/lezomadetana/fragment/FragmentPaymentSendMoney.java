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
import android.widget.Button;
import android.widget.EditText;

import com.team.lezomadetana.R;
import com.team.lezomadetana.activity.BaseActivity;

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
    private EditText editTextAmount;
    private EditText editTextPassword;
    private Button btnSend;
    private View rootView;

    private String amountText;
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
                inflater.inflate(R.layout.fragment_payment_send_money, container, false);

        // current activity
        activity = ((BaseActivity) getActivity());

        // set navItemIndex value
        activity.navItemIndex = 3;

        // toolBar title
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.fragment_payment_toolbar_send_money));

        // init view
        editTextAmount = (EditText) rootView.findViewById(R.id.fragment_payment_send_money_edit_text_amount);
        editTextPassword = (EditText) rootView.findViewById(R.id.fragment_payment_send_money_edit_text_password);
        btnSend = (Button) rootView.findViewById(R.id.fragment_payment_send_money_btn_send);

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
        // amount/phone edit text
        editTextAmount.addTextChangedListener(onTextAmountChangedListener(editTextAmount));

        // btn submit
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set values
                amountText = editTextAmount.getText().toString().replaceAll(",", "");
                passwordText = editTextPassword.getText().toString();

                // amount
                if (amountText.isEmpty() || TextUtils.isEmpty(amountText)) {
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
                    clearAllInputError();
                    clearAllInputFocus();

                    // showing formatted text and original text of "editTextAmount" to toast
                    showLongToast(getContext(),
                            "Formatted amount value: " + editTextAmount.getText().toString() +
                                    "\nOriginal input amount: " + amountText +
                                    "\nPassword: " + passwordText);

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
        editTextAmount.setText("");
        editTextPassword.setText("");
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}
