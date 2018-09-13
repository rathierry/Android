package com.team.lezomadetana.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.team.lezomadetana.BuildConfig;
import com.team.lezomadetana.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by RaThierry on 06/09/2018.
 **/

public class BaseFragment extends Fragment {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    // time
    public static int SPLASH_TIME_OUT = 2000;
    public static int LOADING_TIME_OUT = 3000;

    private ProgressDialog _progressDialog;
    private android.support.v7.app.AlertDialog _alertDialog;

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
    public void onPause() {
        super.onPause();
        if (_progressDialog != null && _progressDialog.isShowing()) {
            _progressDialog.dismiss();
        }
    }

    // ===========================================================
    // Methods for Interfaces
    // ===========================================================

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * Enable spinner
     */
    public void showLoadingView(String message) {
        this.hideLoadingView();
        _progressDialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dark_Dialog);
        _progressDialog.setIndeterminate(true);
        _progressDialog.setMessage(message);
        _progressDialog.setCancelable(false);
        _progressDialog.show();
    }

    /**
     * Disable spinner
     */
    public void hideLoadingView() {
        if (_progressDialog != null && _progressDialog.isShowing()) {
            _progressDialog.dismiss();
        }
        _progressDialog = null;
    }

    /**
     * Display Internet Connection Message
     */
    public void showNoInternetErrorDialog() {
        this.hideAlertDialog();
        Context context = getView().getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle(R.string.app_internet_error_title);
        builder.setMessage(R.string.app_internet_error_message);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setIcon(R.drawable.ic_wifi_black);
        _alertDialog = builder.show();

    }

    /**
     * Hide Internet Connection Message
     */
    public void hideAlertDialog() {
        if (_alertDialog != null && _alertDialog.isShowing()) {
            _alertDialog.dismiss();
        }
        _alertDialog = null;
    }

    /**
     * Show alert dialog
     */
    public void showAlertDialog(String title, int icon, String message) {
        // hide previous alert dialog
        this.hideAlertDialog();
        // create
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom));
        builder.setCancelable(false);
        builder.setIcon(icon);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, null);
        // show
        _alertDialog = builder.show();
    }

    /**
     * Show long toast
     */
    public void showLongToast(Context context, String message) {
        if (BuildConfig.DEBUG) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Show short toast
     */
    public void showShortToast(Context context, String message) {
        if (BuildConfig.DEBUG) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Switch to payment menu fragment
     */
    public void switchToMenuPaymentFragment(Fragment fragment) {
        // manager
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        // transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_payment_content_layout, fragment, fragment.toString());

        // Add fragment one in back stack.So it will not be destroyed. Press back menu can pop it up from the stack.
        fragmentTransaction.addToBackStack(fragment.toString());

        // commit
        fragmentTransaction.commit();
    }

    /**
     * Text listener for amount
     */
    public TextWatcher onTextAmountChangedListener(final EditText editText) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 15) {
                    View view = getActivity().getCurrentFocus();
                    if (view != null) {
                        // hide keyboard
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                editText.removeTextChangedListener(this);

                try {
                    String originalString = s.toString();

                    Long longVal;
                    if (originalString.contains(",")) {
                        originalString = originalString.replaceAll(",", "");
                    }
                    longVal = Long.parseLong(originalString);

                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                    formatter.applyPattern("#,###,###,###");
                    String formattedString = formatter.format(longVal);

                    //setting text after format to EditText
                    editText.setText(formattedString);
                    editText.setSelection(editText.getText().length());
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }

                editText.addTextChangedListener(this);
            }
        };
    }

    /**
     * Text listener for phone number
     */
    public TextWatcher onTextPhoneNumberChangedListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 10) {
                    View view = getActivity().getCurrentFocus();
                    if (view != null) {
                        // hide keyboard
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}
