package com.team.lezomadetana.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.team.lezomadetana.BuildConfig;
import com.team.lezomadetana.R;
import com.team.lezomadetana.model.receive.Request;

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
    private BaseFragment activeFragment = null;

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
        fragmentTransaction.replace(R.id.fragment_payment_content_diff_menu, fragment, fragment.toString());

        // add fragment one in back stack.So it will not be destroyed. Press back menu can pop it up from the stack.
        fragmentTransaction.addToBackStack(/*fragment.toString()*/null);

        // commit
        fragmentTransaction.commit();

        // set current fragment
        activeFragment = (BaseFragment) fragment;
    }

    /**
     * Switch to list offer fragment
     */
    public void switchToListOfferFragment(Fragment fragment, Request request) {
        // use bundle to pass data
        Bundle args = new Bundle();

        // put string, int, etc in bundle with a key value
        args.putSerializable("request", request);

        // manager
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        // set argument bundle to fragment
        fragment.setArguments(args);

        // transaction
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.hide(fragmentManager.findFragmentByTag("homeFragment"));
        ft.add(R.id.fragment_home_content, fragment);
        //ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }

    /**
     * Get payment fragment
     */
    public BaseFragment getCurrentFragment() {
        return activeFragment;
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
    public TextWatcher onTextPhoneNumberChangedListenerForPayment() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 7) {
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

    /**
     * Enable Shimmer Animation
     */
    public void showShimmerAnimation(ShimmerFrameLayout shimmerFrameLayout) {
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmerAnimation();
    }

    /**
     * Disable Shimmer Animation
     */
    public void hideShimmerAnimation(ShimmerFrameLayout shimmerFrameLayout) {
        shimmerFrameLayout.stopShimmerAnimation();
        shimmerFrameLayout.setVisibility(View.INVISIBLE);
    }

    /**
     * Displaying Error if no item from a spinner is selected
     */
    public void setSpinnerError(Spinner spinner, String error) {
        View selectedView = spinner.getSelectedView();
        if (selectedView != null && selectedView instanceof TextView) {
            spinner.requestFocus();
            TextView selectedTextView = (TextView) selectedView;
            selectedTextView.setError(error); // any name of the error will do
            selectedTextView.setTextColor(Color.RED); //text color in which you want your error message to be displayed
            // spinner.performClick(); // to open the spinner list if error is found.

        }
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}
