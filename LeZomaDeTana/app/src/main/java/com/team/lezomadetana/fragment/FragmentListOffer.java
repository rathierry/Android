package com.team.lezomadetana.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.team.lezomadetana.R;
import com.team.lezomadetana.model.receive.Request;

/**
 * Created by RaThierry on 10/09/2018.
 **/

public class FragmentListOffer extends BaseFragment {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private View rootView;
    private Request request;

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
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflate the layout for this fragment or reuse the existing one
        rootView = getView() != null ? getView() :
                inflater.inflate(R.layout.fragment_list_offer, container, false);

        showLoadingView("Miandry");

        // fetch data in serializable
        request = (Request) getArguments().getSerializable("request");

        String totalOffer = "..null..";
        if (request.getOffers() != null) {
            totalOffer = String.valueOf(request.getOffers().size());
        }
        String userName = "..null..";
        String userImageUrl = "..null";
        if (request.getUser() != null) {
            userName = request.getUser().getName();
            userImageUrl = request.getUser().getProfileImageUrl();
        }
        String itemImageUrl = "..null..";
        if (request.getAssetUrls().size() != 0) {
            itemImageUrl = request.getAssetUrls().get(0);
        }
        String itemUnitType = request.getUnitType().name();
        String itemPrice = String.valueOf(request.getPrice());
        String itemProduct = request.getProduct();
        String itemQuantity = request.getQuantity().toString();
        String itemType = request.getType().name();

        Toast.makeText(getContext(),
                "--------------------" +
                        "\ncurrent item selected" +
                        "\n--------------------" +
                        "\ntotalOffer: " + totalOffer +
                        "\nuserName: " + userName +
                        "\nuserImageUrl: " + userImageUrl +
                        "\nitemImageUrl: " + itemImageUrl +
                        "\nitemUnitType: " + itemUnitType +
                        "\nitemPrice: " + itemPrice +
                        "\nitemProduct: " + itemProduct +
                        "\nitemQuantity: " + itemQuantity +
                        "\nitemType: " + itemType +
                        "\n--------------------",
                Toast.LENGTH_LONG).show();

        hideLoadingView();

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
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_search:
                showLongToast(getActivity(), "(frg lst offer) FIKAROHANA");
                return true;
            case R.id.action_payment:
                showLongToast(getActivity(), "(frg lst offer) RESA-BOLA");
                break;
        }

        return super.onOptionsItemSelected(item);
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

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================

}
