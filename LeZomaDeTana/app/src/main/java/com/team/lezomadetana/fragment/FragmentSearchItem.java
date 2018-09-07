package com.team.lezomadetana.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.team.lezomadetana.R;
import com.team.lezomadetana.activity.BaseActivity;
import com.team.lezomadetana.adapter.RequestAdapter;
import com.team.lezomadetana.api.APIClient;
import com.team.lezomadetana.api.APIInterface;
import com.team.lezomadetana.model.receive.Request;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by RaThierry on 04/09/2018.
 **/

public class FragmentSearchItem extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private LinearLayout _postLayout;
    private LinearLayout _searchLayout;
    private LinearLayout _listLayout;
    private TextView _editTextPost;
    private MaterialBetterSpinner _itemSpinner;
    private Button _buttonPost;
    private EditText _editTextSearch;
    private String itemNameSelected;
    private SwipeRefreshLayout _swipeRefreshSearchItem;
    private List<Request> requestList = new ArrayList<Request>();
    private ListView _listViewSearchItem;
    private RequestAdapter _requestAdapter;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_item, container, false);

        // init view
        _postLayout = (LinearLayout) rootView.findViewById(R.id.fragment_search_item_layout_post);
        _searchLayout = (LinearLayout) rootView.findViewById(R.id.fragment_search_item_layout_search);
        _listLayout = (LinearLayout) rootView.findViewById(R.id.fragment_search_item_layout_list);
        _editTextPost = (TextView) rootView.findViewById(R.id.fragment_search_item_text_view_post);
        _editTextPost.setKeyListener(null);
        _editTextPost.setOnClickListener(this);
        _itemSpinner = (MaterialBetterSpinner) rootView.findViewById(R.id.fragment_search_item_material_design_spinner);
        _buttonPost = (Button) rootView.findViewById(R.id.fragment_search_item_button_post);
        _buttonPost.setOnClickListener(this);
        _editTextSearch = (EditText) rootView.findViewById(R.id.fragment_search_item_text_view_search);
        _editTextSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (_editTextSearch.getRight() - _editTextSearch.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        showShortToast(getContext(), "icon search clicked");
                        return true;
                    }
                }
                return false;
            }
        });

        // refresh
        _swipeRefreshSearchItem = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_search_item_swipe_refresh_layout_post);
        _swipeRefreshSearchItem.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        _swipeRefreshSearchItem.setOnRefreshListener(this);
        // showing Swipe Refresh animation on activity create
        // as animation won't start on onCreate, post runnable is used
        _swipeRefreshSearchItem.post(new Runnable() {
                                         @Override
                                         public void run() {
                                             // fetch all requests
                                             fetchAllRequests();
                                         }
                                     }
        );

        // list view and adapter
        _listViewSearchItem = (ListView) rootView.findViewById(R.id.fragment_search_item_list_view_item);
        _requestAdapter = new RequestAdapter(getActivity(), requestList);
        _listViewSearchItem.setAdapter(_requestAdapter);
        _listViewSearchItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(getContext(), "item position = " + position, Toast.LENGTH_SHORT).show();
            }
        });

        // initialize item data in spinner
        initSpinnerForItem();

        // return current view
        return rootView;
    }

    /**
     * View method
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_search_item_text_view_post:
                // showShortToast(getContext(), "Edit text clicked");
                ShowPostItemPopup();
                break;
            case R.id.fragment_search_item_button_post:
                // showShortToast(getContext(), "Your post: \n" + _editTextPost.getText().toString() + "\nItem selected is " + itemNameSelected);
                ShowPostItemPopup();
                break;
        }
    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        showShortToast(getContext(), "onRefresh");
        fetchAllRequests();
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
     * Fetch all requests
     */
    private void fetchAllRequests() {
        // showing refresh animation before making http call
        _swipeRefreshSearchItem.setRefreshing(true);

        // show spinner
        showLoadingView(getResources().getString(R.string.app_spinner));

        // set retrofit api
        APIInterface api = APIClient.getClient(BaseActivity.ROOT_MDZ_API).create(APIInterface.class);

        // create basic authentication
        String auth = BaseActivity.BasicAuth();

        // send query
        Call<JsonObject> call = api.getAllRequest(auth);

        // request
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    // sort using result(s)
                    JsonArray filter = response.body().get("_embedded").getAsJsonObject().get("requests").getAsJsonArray();

                    // count data
                    if (filter.size() > 0) {
                        // parsing gson
                        for (int i = 0; i < filter.size(); i++) {
                            // class model to mapping gson
                            Request request = new Gson().fromJson(filter.get(i), Request.class);

                            // new class model to set all values
                            Request req = new Request();
                            // set values
                            req.setUserId(request.getUserId());

                            // verify server's response
                            String _userId = (TextUtils.equals(request.getTemplateId(), "null") ? "null" : request.getUserId());
                            String _productName = (request.getProduct().isEmpty() ? "null" : request.getProduct());
                            String _price = String.valueOf((TextUtils.equals(request.getPrice().toString(), "null") ? "null" : request.getQuantity()));
                            String _quantity = String.valueOf((TextUtils.equals(request.getQuantity().toString(), "null") ? "null" : request.getQuantity()));

                            // set values
                            req.setUserId(_userId);
                            req.setProduct(_productName);
                            req.setQuantity(Integer.valueOf(_quantity));
                            req.setUnitType(request.getUnitType());
                            req.setPrice(Float.valueOf(_price));

                            // assetUrls is json array
                            JsonArray assetArray = filter.get(i).getAsJsonObject().get("assetUrls").getAsJsonArray();
                            ArrayList<String> assetUrl = new ArrayList<String>();
                            for (int j = 0; j < assetArray.size(); j++) {
                                assetUrl.add(String.valueOf(assetArray.get(j)));
                            }
                            req.setAssetUrls(assetUrl);

                            // offer is json array
                            JsonArray offerArray = filter.get(i).getAsJsonObject().get("offers").getAsJsonArray();
                            ArrayList<String> offerName = new ArrayList<String>();
                            for (int j = 0; j < offerArray.size(); j++) {
                                offerName.add(String.valueOf(offerArray.get(j)));
                            }
                            req.setAssetUrls(offerName);

                            // offer
                            /*if (request.getOffers().size() == 0) {
                                req.setOffers(null);
                            } else {
                                req.setOffers(request.getOffers());
                            }*/

                            // adding request to requests array
                            requestList.add(req);
                        }

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        _requestAdapter.notifyDataSetChanged();
                    }

                    // stopping swipe refresh
                    _swipeRefreshSearchItem.setRefreshing(false);

                    // enable layout
                    _postLayout.setVisibility(View.VISIBLE);
                    _searchLayout.setVisibility(View.VISIBLE);
                    _listLayout.setVisibility(View.VISIBLE);

                    // hide spinner
                    hideLoadingView();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                // stopping swipe refresh
                _swipeRefreshSearchItem.setRefreshing(false);
                hideLoadingView();
                new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AlertDialogCustom))
                        .setIcon(R.drawable.ic_wifi_black)
                        .setTitle(getResources().getString(R.string.app_internet_error_title))
                        .setMessage(getResources().getString(R.string.app_internet_error_message))
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                                showLoadingView(getResources().getString(R.string.app_spinner));
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        hideLoadingView();
                                        fetchAllRequests();
                                    }
                                }, BaseActivity.LOADING_TIME_OUT);
                            }
                        })
                        .show();
            }
        });
    }

    /**
     * Initialize item'spinner (drop down list)
     */
    private void initSpinnerForItem() {
        // drop down element
        List<String> items = Arrays.asList(getResources().getStringArray(R.array.array_items));

        // set adapter for spinner
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, items);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        //_itemSpinner.setAdapter(arrayAdapter);

        // event onClick
        _itemSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // item'clicked name
                itemNameSelected = parent.getItemAtPosition(position).toString();

                // showing clicked spinner item name and position
                showShortToast(parent.getContext(), "Item selected : " + itemNameSelected + "\n(at position n° " + position + ")");
            }
        });
    }

    private void ShowPostItemPopup() {
        // get prompts xml view
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getContext());
        final View mView = layoutInflaterAndroid.inflate(R.layout.post_item, null);

        // create alert builder and cast view
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AlertDialogCustom));

        // set prompts xml to alert dialog builder
        builder.setView(mView);

        // init view
        final MaterialBetterSpinner spinnerCategory = (MaterialBetterSpinner) mView.findViewById(R.id.fragment_post_item_design_spinner_category);
        final EditText editTextQuantity = (EditText) mView.findViewById(R.id.fragment_post_item_editText_quantity);
        final EditText editTextUnitType = (EditText) mView.findViewById(R.id.fragment_post_item_editText_unity_type);
        final EditText editTextPrice = (EditText) mView.findViewById(R.id.fragment_post_item_editText_price);

        // set dialog message
        builder
                .setTitle("About your offer or your need")
                .setIcon(R.drawable.ic_info_black)
                .setCancelable(false)
                .setPositiveButton(R.string.user_login_forgot_pass_btn_ok, null)
                .setNegativeButton(R.string.user_login_forgot_pass_btn_cancel, null);

        // create alert dialog
        final android.app.AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button buttonOK = ((android.app.AlertDialog) dialog).getButton(android.app.AlertDialog.BUTTON_POSITIVE);
                Button buttonCancel = ((android.app.AlertDialog) dialog).getButton(android.app.AlertDialog.BUTTON_NEGATIVE);

                // validate
                buttonOK.setTextColor(ContextCompat.getColor(getContext(), android.R.color.holo_green_dark));
                buttonOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // values
                        String category = spinnerCategory.getText().toString();
                        String quantity = editTextQuantity.getText().toString();
                        String unitType = editTextUnitType.getText().toString();
                        String price = editTextPrice.getText().toString();

                        // category
                        /*if (category.isEmpty() || TextUtils.isEmpty(category) || category.contains("Choose")) {
                            spinnerCategory.setError("Select category");
                            spinnerCategory.requestFocus();
                            return;
                        }*/
                        // quantity
                        if (quantity.isEmpty() || TextUtils.isEmpty(quantity)) {
                            editTextQuantity.setError("Add item quantity");
                            editTextQuantity.requestFocus();
                            return;
                        }
                        // unitType
                        if (unitType.isEmpty() || TextUtils.isEmpty(unitType)) {
                            editTextUnitType.setError("Add price unit type");
                            editTextUnitType.requestFocus();
                            return;
                        }
                        // price
                        if (price.isEmpty() || TextUtils.isEmpty(price)) {
                            editTextPrice.setError("Add item price");
                            editTextPrice.requestFocus();
                            return;
                        }

                        // show spinner
                        showLoadingView(getResources().getString(R.string.app_spinner));

                        // set action after loading time
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                hideLoadingView();
                                dialog.dismiss();
                            }
                        }, LOADING_TIME_OUT);
                    }
                });

                // cancel
                buttonCancel.setTextColor(ContextCompat.getColor(getContext(), android.R.color.holo_red_dark));
                buttonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        // change the alert dialog background color
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.white);
        dialog.show();
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}
