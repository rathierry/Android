package com.team.lezomadetana.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class FragmentChat extends BaseFragment {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    TextView madCoin;
    TextView madCointText;
    ImageView telma;
    ImageView orange;
    ImageView airtel;
    UserCredentialResponse user;

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

        user = ((BaseActivity) getActivity()).getCurrentUser(getActivity());


        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        madCoin = view.findViewById(R.id.madcoin_text);
        madCointText = view.findViewById(R.id.madcoin_text);
        telma = view.findViewById(R.id.m_vola_item);
        orange = view.findViewById(R.id.orange_money_item);
        airtel = view.findViewById(R.id.airtel_money_item);


        telma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "a", Toast.LENGTH_LONG);
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(Uri.parse("tel:" + "*#06") + Uri.encode("#")));
                startActivity(intent);
            }
        });


        refreshMadcoin();

        return inflater.inflate(R.layout.fragment_chat, container, false);
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
                                madCointText.setText("" + wallets.get(i).getBalance());
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
    // Methods for Interfaces
    // ===========================================================

    // ===========================================================
    // Public Methods
    // ===========================================================

    // ===========================================================
    // Private Methods
    // ===========================================================

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}
