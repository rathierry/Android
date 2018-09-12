package com.team.lezomadetana.fragment;


import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.team.lezomadetana.R;
import com.team.lezomadetana.adapter.ListOfferAdapter;
import com.team.lezomadetana.model.receive.Offer;
import com.team.lezomadetana.model.receive.Request;
import com.team.lezomadetana.utils.CircleTransform;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RaThierry on 10/09/2018.
 **/

public class FragmentListOffer extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private View rootView;
    private SwipeRefreshLayout swipeRefreshItem;
    private List<Offer> offerList = new ArrayList<Offer>();
    private ListView listViewItem;
    private Request request;
    private ListOfferAdapter listOfferAdapter;
    private TextView txt_no_list;

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

        // toolBar title
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Lisitra");

        // initialize view
        ImageView imageView = (ImageView) rootView.findViewById(R.id.icon_profile);
        TextView from = (TextView) rootView.findViewById(R.id.from);
        TextView txt_primary = (TextView) rootView.findViewById(R.id.txt_primary);
        TextView txt_secondary = (TextView) rootView.findViewById(R.id.txt_secondary);
        TextView txt_footer = (TextView) rootView.findViewById(R.id.txt_footer);
        txt_no_list = (TextView) rootView.findViewById(R.id.no_list_offer);
        Button buttonAnswer = (Button) rootView.findViewById(R.id.answer);

        // loading
        showLoadingView("Miandry");

        // fetch data in serializable
        request = (Request) getArguments().getSerializable("request");

        // initialize request's data
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

        // set user image avatar
        Glide.with(getActivity())
                .load(userImageUrl)
                .thumbnail(0.5f)
                .crossFade()
                .transform(new CircleTransform(getActivity()))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.ic_launcher_round)
                .into(imageView);
        imageView.setColorFilter(null);

        // test
        /*Toast.makeText(getContext(),
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
                Toast.LENGTH_LONG).show();*/

        // set values
        from.setText(Html.fromHtml("Nalefan\'i <b>" + userName + "</b>"));
        txt_primary.setText(Html.fromHtml("<b>" + itemProduct + "</b>"));
        txt_secondary.setText(Html.fromHtml("Lanjany: <b>" + itemQuantity + " " + itemUnitType + "</b>"));
        txt_footer.setText(Html.fromHtml("Vidin\'iray " + itemUnitType + ": <b>" + itemPrice + "</b> Ariary"));

        // event onClick
        buttonAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Hamaly ve?", Toast.LENGTH_SHORT).show();
            }
        });

        // ===========================================================
        // List View
        // ===========================================================

        // refresh
        swipeRefreshItem = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_list_offer_swipe_refresh_layout_post);
        swipeRefreshItem.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        swipeRefreshItem.setOnRefreshListener(this);

        // list view and adapter
        listViewItem = (ListView) rootView.findViewById(R.id.fragment_list_offer_list_view_item);
        listOfferAdapter = new ListOfferAdapter(getActivity(), offerList);
        listViewItem.setAdapter(listOfferAdapter);

        // hide spinner
        hideLoadingView();

        // return view
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
        swipeRefreshItem.setRefreshing(true);
        fetchAllOfferData();
        // Toast.makeText(getContext(), "onResume", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRefresh() {
        super.onResume();
        swipeRefreshItem.setRefreshing(true);
        fetchAllOfferData();
        // Toast.makeText(getContext(), "onRefresh", Toast.LENGTH_SHORT).show();
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
     * Fetch all offers
     */
    private void fetchAllOfferData() {
        // verification
        if (request.getOffers().size() != 0) {
            // clear list request
            offerList.clear();

            Offer offer = null;
            for (int i = 0; i < request.getOffers().size(); i++) {
                // class model to mapping gson
                offer = request.getOffers().get(i);

                // adding request to requests array
                offerList.add(offer);
            }

            // notifying list adapter about data changes
            // so that it renders the list view with updated data
            listOfferAdapter.notifyDataSetChanged();
        } else {
            txt_no_list.setVisibility(View.VISIBLE);
        }

        // hide swipe refresh
        swipeRefreshItem.setRefreshing(false);
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================

}
