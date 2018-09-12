package com.team.lezomadetana.fragment;

import android.net.wifi.hotspot2.pps.Credential;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.team.lezomadetana.R;
import com.team.lezomadetana.activity.BaseActivity;
import com.team.lezomadetana.api.APIClient;
import com.team.lezomadetana.api.APIInterface;
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

public class FragmentPayment extends BaseFragment {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private  TextView money;
    private UserCredentialResponse user;
    private LinearLayout paymentCharge;

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

        View mView = inflater.inflate(R.layout.fragment_payment, container, false);
        money = (TextView) mView.findViewById(R.id.madcoin_text);
        final BaseActivity activity =  ((BaseActivity)getActivity());
        user = activity.getCurrentUser(activity);
        paymentCharge = (LinearLayout) mView.findViewById(R.id.payement_charge);

        paymentCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"AMPIDITRA CLICKED",Toast.LENGTH_LONG);
            }
        });

        refreshMadcoin();
        return inflater.inflate(R.layout.fragment_payment, container, false);
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
    public void refreshMadcoin() {
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
                                money.setText("Vola ao anaty vata :" + wallets.get(i).getBalance() + " Ar");

                                break;
                            }

                        }

                    }


                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });

    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}
