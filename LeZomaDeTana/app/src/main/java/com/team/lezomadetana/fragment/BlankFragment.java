package com.team.lezomadetana.fragment;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.team.lezomadetana.R;
import com.team.lezomadetana.activity.BaseActivity;
import com.team.lezomadetana.adapter.RequestsAdapter;
import com.team.lezomadetana.api.Client;
import com.team.lezomadetana.api.Service;
import com.team.lezomadetana.model.receive.Request;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by RaThierry on 18/09/2018.
 */

public class BlankFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,
        RequestsAdapter.RequestAdapterListener {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private BaseActivity baseActivity;
    private View rootView;
    private List<Request> requests = new ArrayList<>();
    private RecyclerView recyclerView;
    private RequestsAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

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
                inflater.inflate(R.layout.fragment_blank, container, false);

        // current activity
        baseActivity = ((BaseActivity) getActivity());

        // init view
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout_s);
        swipeRefreshLayout.setOnRefreshListener(this);

        // adapter
        mAdapter = new RequestsAdapter(getContext(), requests, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        // show loader and fetch messages
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        getAllRequests();
                    }
                }
        );

        return rootView;
    }

    @Override
    public void onRefresh() {
        // swipe refresh is performed, fetch the messages again
        getAllRequests();
    }

    @Override
    public void onIconClicked(int position) {
        //
    }

    @Override
    public void onMessageRowClicked(int position) {
        //
    }

    @Override
    public void onRowClicked(int position) {
        //
    }

    @Override
    public void onButtonSumClicked(int position) {
        //
    }

    // ===========================================================
    // Methods for Interfaces
    // ===========================================================

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * chooses a random color from array.xml
     */
    public int getRandomMaterialColor(String typeColor) {
        int returnColor = Color.GRAY;

        Activity activity = getActivity();
        if (activity != null) {
            int arrayId = getResources().getIdentifier("mdcolor_" + typeColor, "array", activity.getPackageName());
            if (arrayId != 0) {
                TypedArray colors = getResources().obtainTypedArray(arrayId);
                int index = (int) (Math.random() * colors.length());
                returnColor = colors.getColor(index, Color.GRAY);
                colors.recycle();
            }
        }

        return returnColor;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * Fetch all requests
     */
    private void getAllRequests() {
        swipeRefreshLayout.setRefreshing(true);

        // set retrofit api
        Service api = Client.getClient(baseActivity.ROOT_MDZ_API).create(Service.class);

        // create basic authentication
        String auth = baseActivity.BasicAuth();

        // send query
        Call<JsonObject> call = api.getAllRequest(auth);

        // request
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                // verification
                if (response.body() == null) {
                    showLongToast(getContext(), getResources().getString(R.string.app_response_body_null));
                } else if (response.code() == 200) {
                    // array filter
                    JsonArray filter = response.body().get("_embedded").getAsJsonObject().get("requests").getAsJsonArray();

                    if (filter == null || (filter.size() == 0)) {
                        showLongToast(getContext(), getResources().getString(R.string.app_filter_data_null));
                    } else {
                        // clear the inbox
                        requests.clear();

                        // parsing gson
                        for (int i = 0; i < filter.size(); i++) {
                            // class model to mapping gson
                            Request request = new Gson().fromJson(filter.get(i), Request.class);

                            // // // Request.Type.valueOf("BUY").ordinal()
                            if (request.getType() == Request.Type.BUY) {
                                // new class model to set all values
                                Request req = new Request();

                                // verify server's response
                                String _id = TextUtils.equals(request.getId(), "null") ? "null" : request.getId();
                                String _userId = TextUtils.equals(request.getUserId(), "null") ? "null" : request.getUserId();
                                String _productName = TextUtils.equals(request.getProduct(), "null") ? "null" : request.getProduct();
                                String _price = String.valueOf((TextUtils.equals(request.getPrice().toString(), "null") ? "null" : request.getPrice()));
                                String _quantity = String.valueOf((TextUtils.equals(request.getQuantity().toString(), "null") ? "null" : request.getQuantity()));
                                String _templateId = TextUtils.equals(request.getTemplateId(), "null") ? "null" : request.getTemplateId();

                                // offer
                                if (request.getOffers() == null) {
                                    req.setOffers(null);
                                } else {
                                    req.setOffers(request.getOffers());
                                }

                                // set user who create request item
                                if (request.getUser() == null) {
                                    req.setUser(null);
                                } else {
                                    req.setUser(request.getUser());
                                }

                                // assetUrls is json array
                                JsonArray assetArray = filter.get(i).getAsJsonObject().get("assetUrls").getAsJsonArray();
                                ArrayList<String> assetUrl = new ArrayList<String>();
                                for (int j = 0; j < assetArray.size(); j++) {
                                    assetUrl.add(String.valueOf(assetArray.get(j)));
                                }
                                req.setAssetUrls(assetUrl);

                                // set values
                                req.setUnitType(request.getUnitType());
                                req.setPrice(Float.valueOf(_price));
                                req.setUserId(_userId);
                                req.setProduct(_productName);
                                req.setTemplateId(_templateId);
                                req.setQuantity(Integer.valueOf(_quantity));
                                req.setId(_id);
                                req.setType(request.getType());

                                // generate a random color
                                req.setColor(getRandomMaterialColor("400"));

                                // adding request to requests array
                                requests.add(req);
                            }
                        }

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        mAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                // alert
                new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AlertDialogCustom))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(getResources().getString(R.string.app_send_request_on_failure_title))
                        .setMessage("Unable to fetch json: " + t.getMessage())
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                                getAllRequests();
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
