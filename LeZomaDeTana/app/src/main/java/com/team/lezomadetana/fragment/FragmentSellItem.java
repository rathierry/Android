package com.team.lezomadetana.fragment;

import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.team.lezomadetana.R;
import com.team.lezomadetana.activity.BaseActivity;
import com.team.lezomadetana.adapter.SELLAdapter;
import com.team.lezomadetana.api.APIClient;
import com.team.lezomadetana.api.APIInterface;
import com.team.lezomadetana.model.receive.ProductTemplate;
import com.team.lezomadetana.model.receive.Request;
import com.team.lezomadetana.model.send.OfferSend;
import com.team.lezomadetana.model.send.RequestSend;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.team.lezomadetana.activity.BaseActivity.BasicAuth;

/**
 * Created by RaThierry on 04/09/2018.
 **/

public class FragmentSellItem extends BaseFragment implements
        View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, SELLAdapter.RequestAdapterListener {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private View rootView;
    private String itemNameSelected;
    private String itemIdSelected;
    private String itemUnitTypeSelected;
    private SwipeRefreshLayout swipeRefreshItem;
    private List<Request> requestList = new ArrayList<Request>();
    private ListView listViewItem;
    private SELLAdapter sellAdapter;
    private List<ProductTemplate> listCategory = new ArrayList<ProductTemplate>();
    private boolean isRefresh = false;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflate the layout for this fragment or reuse the existing one
        rootView = getView() != null ? getView() :
                inflater.inflate(R.layout.fragment_list_sell_item, container, false);

        // floating btn to add new item
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fragment_available_item_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open post popup
                showPostRequestPopup();
            }
        });

        // refresh
        swipeRefreshItem = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_available_item_swipe_refresh_layout_post);
        swipeRefreshItem.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        swipeRefreshItem.setOnRefreshListener(this);
        swipeRefreshItem.post(new Runnable() {
                                  @Override
                                  public void run() {
                                      fetchAllRequests();
                                  }
                              }
        );

        // list view and adapter
        listViewItem = (ListView) rootView.findViewById(R.id.fragment_available_item_list_view_item);

        sellAdapter = new SELLAdapter(getActivity(), requestList, this, this);
        listViewItem.setAdapter(sellAdapter);

        // return current view
        return rootView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem itemMenuSearch = menu.findItem(R.id.action_search);
        MenuItem itemMenuPayment = menu.findItem(R.id.action_payment);
        MenuItem itemMenuInfo = menu.findItem(R.id.action_information);

        itemMenuSearch.setEnabled(true);
        itemMenuSearch.setVisible(true);
        itemMenuPayment.setEnabled(true);
        itemMenuPayment.setVisible(true);
        itemMenuInfo.setEnabled(false);
        itemMenuInfo.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_search:
                showLongToast(getActivity(), "2) FIKAROHANA");
                break;
            case R.id.action_payment:
                showLongToast(getActivity(), "2) RESA-BOLA");
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && rootView != null) {
            onResume();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!getUserVisibleHint()) {
            return;
        }
        isRefresh = false;
        fetchAllRequests();
    }

    @Override
    public void onRefresh() {
        isRefresh = true;
        fetchAllRequests();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*case ...:
                break;*/
        }
    }

    @Override
    public void onIconClicked(int position) {
        // TODO
    }

    @Override
    public void onIconImportantClicked(int position) {
        // TODO
    }

    @Override
    public void onMessageRowClicked(int position) {
        // read the "message" which removes bold from the row
        Request request = requestList.get(position);
        request.setRead(true);
        requestList.set(position, request);
        sellAdapter.notifyDataSetChanged();
        // // //
        showShortToast(getContext(), "Read: " + request.getUserId());
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
        if (isRefresh) {
            swipeRefreshItem.setRefreshing(true);
            hideLoadingView();
        } else {
            swipeRefreshItem.setRefreshing(false);
            showLoadingView(getResources().getString(R.string.app_spinner));
        }

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

                            if (request.getType() == 0) {
                                // new class model to set all values
                                Request req = new Request();

                                // verify server's response
                                String _id = String.valueOf(TextUtils.equals(request.getId(), "null") ? "null" : request.getId().toString());
                                String _userId = (TextUtils.equals(request.getUserId(), "null") ? "null" : request.getUserId().toString());
                                String _productName = (request.getProduct().isEmpty() ? "null" : request.getProduct().toString());
                                String _price = String.valueOf((TextUtils.equals(request.getPrice().toString(), "null") ? "null" : request.getPrice().toString()));
                                String _quantity = String.valueOf((TextUtils.equals(request.getQuantity().toString(), "null") ? "null" : request.getQuantity().toString()));
                                String _type = TextUtils.equals(request.getType().toString(), "null") ? "null" : request.getType().toString();
                                String _templateId = TextUtils.equals(request.getTemplateId(), "null") ? "null" : request.getTemplateId();

                                // set values
                                req.setId(_id);
                                req.setUserId(_userId);
                                req.setProduct(_productName);
                                req.setQuantity(Integer.valueOf(_quantity));
                                req.setUnitType(request.getUnitType());
                                req.setPrice(Float.valueOf(_price));
                                req.setType(Integer.valueOf(_type));
                                req.setTemplateId(_templateId);

                                // assetUrls is json array
                                JsonArray assetArray = filter.get(i).getAsJsonObject().get("assetUrls").getAsJsonArray();
                                ArrayList<String> assetUrl = new ArrayList<String>();
                                for (int j = 0; j < assetArray.size(); j++) {
                                    assetUrl.add(String.valueOf(assetArray.get(j)));
                                }
                                req.setAssetUrls(assetUrl);

                                // offer
                                if (request.getOffers() == null) {
                                    req.setOffers(null);
                                } else {
                                    req.setOffers(request.getOffers());
                                }

                                // generate a random color
                                req.setColor(getRandomMaterialColor("500"));

                                // adding request to requests array
                                requestList.add(req);
                            }
                        }

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        sellAdapter.notifyDataSetChanged();
                    }

                    // stopping swipe refresh
                    swipeRefreshItem.setRefreshing(false);
                    fetchAllCategory();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                // stopping swipe refresh
                swipeRefreshItem.setRefreshing(false);
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
     * Load all category list
     */
    private void fetchAllCategory() {
        // set retrofit api
        APIInterface api = APIClient.getClient(BaseActivity.ROOT_MDZ_API).create(APIInterface.class);

        // create basic authentication
        String auth = BasicAuth();

        // send query
        Call<JsonObject> call = api.getAllProductTemplate(auth);

        // request
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    // sort using result(s)
                    JsonArray filter = response.body().get("_embedded").getAsJsonObject().get("productTemplates").getAsJsonArray();

                    // class model to mapping gson
                    List<ProductTemplate> productTemplates = null;

                    // count data
                    if (filter.size() > 0) {
                        // new class model to set all values
                        productTemplates = new ArrayList<ProductTemplate>();

                        // boucle
                        for (int i = 0; i < filter.size(); i++) {
                            ProductTemplate productTemplate = new Gson().fromJson(filter.get(i), ProductTemplate.class);
                            productTemplates.add(productTemplate);
                        }

                        // init spinner
                        listCategory = productTemplates;

                        // verif in LOG
                        Log.d("REQUESTS", "" + productTemplates);

                        // hide spinner
                        hideLoadingView();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                hideLoadingView();
                // // //
                new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AlertDialogCustom))
                        .setIcon(R.drawable.ic_wifi_black)
                        .setTitle(getResources().getString(R.string.app_internet_error_title))
                        .setMessage(getResources().getString(R.string.app_internet_error_message))
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                                showLoadingView(getResources().getString(R.string.app_spinner));
                                fetchAllCategory();
                            }
                        })
                        .show();
            }
        });
    }

    /**
     * Display post item popup
     */
    private void showPostRequestPopup() {
        // get prompts xml view
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getContext());
        final View mView = layoutInflaterAndroid.inflate(R.layout.layout_post_request, null);

        // create alert builder and cast view
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AlertDialogCustom));

        // set prompts xml to alert dialog builder
        builder.setView(mView);

        // init view
        final MaterialBetterSpinner spinnerCategory = (MaterialBetterSpinner) mView.findViewById(R.id.fragment_post_item_design_spinner_category);
        final MaterialBetterSpinner spinnerUnitType = (MaterialBetterSpinner) mView.findViewById(R.id.fragment_post_item_design_spinner_unit_type);
        final EditText editTextQuantity = (EditText) mView.findViewById(R.id.fragment_post_item_editText_quantity);
        final EditText editTextPrice = (EditText) mView.findViewById(R.id.fragment_post_item_editText_price);
        final EditText editTextProduct = (EditText) mView.findViewById(R.id.fragment_post_item_editText_product);

        // drop down element
        List<String> itemsName = new ArrayList<>();
        final List<String> itemsId = new ArrayList<>();
        for (int i = 0; i < listCategory.size(); i++) {
            itemsName.add(listCategory.get(i).getName());
            itemsId.add(listCategory.get(i).getId());
        }

        // set adapter for spinner
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, itemsName);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerCategory.setAdapter(arrayAdapter);

        // event onClick
        spinnerCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // item'clicked name
                itemNameSelected = parent.getItemAtPosition(position).toString();

                // item'clicked position
                itemIdSelected = itemsId.get(position);

                // showing clicked spinner item name and position
                showShortToast(parent.getContext(), "Item selected : " + itemNameSelected + "\n(at position n° " + position + ") \nitemIdSelected = " + itemIdSelected);
            }
        });


        // drop down unit element
        String[] unitTypeName = BaseActivity.getNames(Request.UnitType.class);


        // set adapter for spinner
        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, unitTypeName);
        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerUnitType.setAdapter(arrayAdapter2);

        // event onClick
        spinnerUnitType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // item'clicked name
                itemUnitTypeSelected = parent.getItemAtPosition(position).toString();


                // showing clicked spinner item name and position
                showShortToast(parent.getContext(), "Item selected : " + itemUnitTypeSelected + "\n(at position n° " + position + ") \nitemIdSelected = " + itemIdSelected);
            }
        });

        // set dialog message
        builder
                .setTitle(getResources().getString(R.string.fragment_sell_post_request_title))
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
                        String unitType = spinnerUnitType.getText().toString();
                        String price = editTextPrice.getText().toString();
                        String product = editTextProduct.getText().toString();
                        // product
                        if (product.isEmpty() || TextUtils.isEmpty(product)) {
                            editTextProduct.setError(getResources().getString(R.string.fragment_buy_post_request_product_hint));
                            editTextProduct.requestFocus();
                            return;
                        }
                        // category
                        if (category.isEmpty() || TextUtils.isEmpty(category) || category.contains(getResources().getString(R.string.fragment_buy_post_request_category_select))) {
                            spinnerCategory.setError(getResources().getString(R.string.fragment_buy_post_request_category_text));
                            spinnerCategory.requestFocus();
                            return;
                        }
                        // quantity
                        if (quantity.isEmpty() || TextUtils.isEmpty(quantity)) {
                            editTextQuantity.setError(getResources().getString(R.string.fragment_buy_post_request_quantity_error_empty));
                            editTextQuantity.requestFocus();
                            return;
                        }
                        // unitType
                        if (unitType.isEmpty() || TextUtils.isEmpty(unitType) || unitType.contains(getResources().getString(R.string.fragment_buy_post_request_category_select))) {
                            spinnerUnitType.setError(getResources().getString(R.string.fragment_buy_post_request_unity_type_hint));
                            spinnerUnitType.requestFocus();
                            return;
                        }
                        // price
                        if (price.isEmpty() || TextUtils.isEmpty(price)) {
                            editTextPrice.setError(getResources().getString(R.string.fragment_buy_post_request_price_error_empty));
                            editTextPrice.requestFocus();
                            return;
                        }

                        // show spinner
                        showLoadingView(getResources().getString(R.string.app_spinner));

                        //
                        APIInterface api = APIClient.getClient(BaseActivity.ROOT_MDZ_API).create(APIInterface.class);

                        // create basic authentication
                        String auth = BasicAuth();

                        //
                        BaseActivity baseActivity = (BaseActivity) getActivity();

                        //
                        RequestSend request = new RequestSend(baseActivity.getCurrentUser(getContext()).getId(), product, Request.UnitType.valueOf(unitType), Float.parseFloat(price), Request.Type.SELL, itemIdSelected, true);

                        // send query
                        Call<Void> call = api.sendRequest(auth, request);

                        // request
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.code() == 201) {
                                    dialog.dismiss();
                                    hideLoadingView();

                                    // clear list request
                                    requestList.clear();

                                    // refresh list
                                    onRefresh();
                                }

                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                // stopping swipe refresh
                                swipeRefreshItem.setRefreshing(false);
                                hideLoadingView();
                                new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AlertDialogCustom))
                                        .setIcon(R.drawable.ic_wifi_black)
                                        .setTitle(getResources().getString(R.string.app_internet_error_title))
                                        .setMessage(getResources().getString(R.string.app_internet_error_message))
                                        .setCancelable(false)
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();
                            }
                        });

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

    /**
     * Popup to write new sell answer
     */
    public void showPostOfferPopup(final String requestId) {
        // get prompts xml view
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getContext());
        final View mView = layoutInflaterAndroid.inflate(R.layout.layout_post_offer, null);

        // create alert builder and cast view
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AlertDialogCustom));

        // set prompts xml to alert dialog builder
        builder.setView(mView);

        // init view
        final MaterialBetterSpinner spinnerUnitType = (MaterialBetterSpinner) mView.findViewById(R.id.dialog_offer_unity);
        final EditText editTextQuantity = (EditText) mView.findViewById(R.id.dialog_offer_quantity_text);

        // drop down unit element
        String[] unitTypeName = BaseActivity.getNames(Request.UnitType.class);

        // set adapter for spinner
        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, unitTypeName);
        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerUnitType.setAdapter(arrayAdapter2);

        // event onClick
        spinnerUnitType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // item'clicked name
                itemUnitTypeSelected = parent.getItemAtPosition(position).toString();

                // showing clicked spinner item name and position
                showShortToast(parent.getContext(), "Item selected : " + itemUnitTypeSelected + "\n(at position n° " + position);
            }
        });

        // set dialog message
        builder
                .setTitle(getResources().getString(R.string.fragment_sell_post_request_title))
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
                        String quantity = editTextQuantity.getText().toString();
                        String unitType = spinnerUnitType.getText().toString();

                        // quantity
                        if (quantity.isEmpty() || TextUtils.isEmpty(quantity)) {
                            editTextQuantity.setError(getResources().getString(R.string.fragment_buy_post_request_quantity_error_empty));
                            editTextQuantity.requestFocus();
                            return;
                        }
                        // unitType
                        if (unitType.isEmpty() || TextUtils.isEmpty(unitType) || unitType.contains(getResources().getString(R.string.fragment_buy_post_request_category_select))) {
                            spinnerUnitType.setError(getResources().getString(R.string.fragment_buy_post_request_unity_type_hint));
                            spinnerUnitType.requestFocus();
                            return;
                        }

                        // show spinner
                        showLoadingView(getResources().getString(R.string.app_spinner));

                        //
                        APIInterface api = APIClient.getClient(BaseActivity.ROOT_MDZ_API).create(APIInterface.class);

                        // create basic authentication
                        String auth = BasicAuth();

                        //
                        BaseActivity baseActivity = (BaseActivity) getActivity();

                        showShortToast(baseActivity, "requestId : " + requestId);

                        OfferSend offerSend = new OfferSend(requestId, baseActivity.getCurrentUser(getContext()).getId(), Integer.parseInt(quantity), Request.UnitType.valueOf(unitType), true);

                        // send query
                        Call<Void> call = api.sendOffer(auth, offerSend);

                        // request
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.code() == 201) {
                                    dialog.dismiss();
                                    requestList.clear();
                                    hideLoadingView();

                                    // refresh list
                                    onRefresh();
                                } else {
                                    dialog.dismiss();
                                    hideLoadingView();
                                    // // //
                                    new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AlertDialogCustom))
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .setTitle(getResources().getString(R.string.app_server_error_title))
                                            .setMessage(Html.fromHtml("<b>" + getResources().getString(R.string.app_server_error_message) + "</b>"))
                                            .setCancelable(false)
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                // stopping swipe refresh
                                swipeRefreshItem.setRefreshing(false);
                                hideLoadingView();
                                new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AlertDialogCustom))
                                        .setIcon(R.drawable.ic_wifi_black)
                                        .setTitle(getResources().getString(R.string.app_internet_error_title))
                                        .setMessage(getResources().getString(R.string.app_internet_error_message))
                                        .setCancelable(false)
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();
                            }
                        });

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

    /**
     * chooses a random color from array.xml
     */
    private int getRandomMaterialColor(String typeColor) {
        int returnColor = Color.GRAY;
        int arrayId = getResources().getIdentifier("mdcolor_" + typeColor, "array", getActivity().getPackageName());

        if (arrayId != 0) {
            TypedArray colors = getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.GRAY);
            colors.recycle();
        }
        return returnColor;
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}
