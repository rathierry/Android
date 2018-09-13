package com.team.lezomadetana.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.zxing.integration.android.IntentIntegrator;
import com.team.lezomadetana.R;
import com.team.lezomadetana.activity.BaseActivity;
import com.team.lezomadetana.api.APIClient;
import com.team.lezomadetana.api.APIInterface;
import com.team.lezomadetana.api.ExampleUseOfAPI;
import com.team.lezomadetana.model.receive.UserCredentialResponse;
import com.team.lezomadetana.model.receive.Wallet;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.team.lezomadetana.activity.BaseActivity.BasicAuth;

/**
 * Created by RaThierry on 11/09/2018.
 **/

public class FragmentPayment extends BaseFragment implements View.OnClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private View rootView;
    private BaseActivity activity;
    private UserCredentialResponse user;

    private TextView textViewMadCoin;
    private RelativeLayout layout_payment_charge;
    private RelativeLayout layout_payment_give_money;
    private RelativeLayout layout_payment_send_money;
    private RelativeLayout layout_payment_get_money;

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
                inflater.inflate(R.layout.fragment_payment, container, false);

        // current activity
        activity = ((BaseActivity) getActivity());

        // current user
        user = activity.getCurrentUser(activity);

        // set view
        textViewMadCoin = (TextView) rootView.findViewById(R.id.mad_coin_text);
        layout_payment_charge = (RelativeLayout) rootView.findViewById(R.id.fragment_payment_charge);
        layout_payment_give_money = (RelativeLayout) rootView.findViewById(R.id.fragment_payment_give_money);
        layout_payment_send_money = (RelativeLayout) rootView.findViewById(R.id.fragment_payment_send_money);
        layout_payment_get_money = (RelativeLayout) rootView.findViewById(R.id.fragment_payment_get_money);

        // event onClick
        layout_payment_charge.setOnClickListener(this);
        layout_payment_give_money.setOnClickListener(this);
        layout_payment_send_money.setOnClickListener(this);
        layout_payment_get_money.setOnClickListener(this);

        //ExampleUseOfAPI.sendTransactionAr2Jt();



        // fetch user mad coin
        refreshMadCoin();

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_payment_charge:

                switchToMenuPaymentFragment(new FragmentPaymentCharge());
                break;
            case R.id.fragment_payment_give_money:
                switchToMenuPaymentFragment(new FragmentPaymentGiveMoney());
                break;
            case R.id.fragment_payment_send_money:
                switchToMenuPaymentFragment(new FragmentPaymentSendMoney());
                break;
            case R.id.fragment_payment_get_money:
                switchToMenuPaymentFragment(new FragmentPaymentGetMoney());
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
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

    public void refreshMadCoin() {
        APIInterface api = APIClient.getClient(BaseActivity.ROOT_MDZ_USER_API).create(APIInterface.class);

        // create basic authentication
        String auth = BasicAuth();

        // send query
        Call<JsonObject> call = api.getAllWallet(auth);

        // request
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    JsonArray filter = response.body().get("_embedded").getAsJsonObject().get("userWallets").getAsJsonArray();

                    List<Wallet> wallets = null;

                    if (filter.size() > 0) {
                        wallets = new ArrayList<Wallet>();
                        for (int i = 0; i < filter.size(); i++) {
                            Wallet wallet = new Gson().fromJson(filter.get(i), Wallet.class);
                            wallets.add(wallet);
                        }

                        for (int i = 0; i < wallets.size(); i++) {
                            if (wallets.get(i).getUserId().equals(user.getId())) {
                                textViewMadCoin.setText(getResources().getString(R.string.fragment_payment_solde_compte_text) + " " + wallets.get(i).getBalance() + " " + getResources().getString(R.string.app_payment_symbole_type_ariary_text));
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AlertDialogCustom))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(getResources().getString(R.string.app_send_request_on_failure_title))
                        .setMessage(getResources().getString(R.string.app_send_request_on_failure_message))
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                                showLoadingView(getResources().getString(R.string.app_spinner));
                                refreshMadCoin();
                            }
                        })
                        .show();
            }
        });

    }




    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}
