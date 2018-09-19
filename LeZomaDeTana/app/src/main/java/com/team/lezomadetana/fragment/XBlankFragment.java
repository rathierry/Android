package com.team.lezomadetana.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.team.lezomadetana.R;
import com.team.lezomadetana.activity.BaseActivity;
import com.team.lezomadetana.activity.MainActivity;
import com.team.lezomadetana.adapter.XRequestsAdapter;
import com.team.lezomadetana.api.Client;
import com.team.lezomadetana.api.Service;
import com.team.lezomadetana.model.receive.Page;
import com.team.lezomadetana.model.receive.ProductTemplate;
import com.team.lezomadetana.model.receive.Request;
import com.team.lezomadetana.model.send.OfferSend;
import com.team.lezomadetana.model.send.RequestSend;
import com.team.lezomadetana.utils.PaginationScrollListener;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by RaThierry on 18/09/2018.
 */

public class XBlankFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,
        XRequestsAdapter.RequestAdapterListener {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final int PAGE_START = 0;
    // limiting to 20 for this screen, since total pages in actual API is very large.
    private int TOTAL_PAGES = 0;
    private int PAGE_SIZE = 20;
    private int currentPage = PAGE_START;

    // ===========================================================
    // Fields
    // ===========================================================

    private BaseActivity baseActivity;
    private MainActivity mainActivity;
    private View rootView;
    private LinearLayoutManager mLayoutManager;
    private List<Request> requests = new ArrayList<>();
    private List<ProductTemplate> templates = new ArrayList<>();
    private RecyclerView recyclerView;
    private XRequestsAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private ProgressBar progressBar;

    private String itemNameSelected;
    private String itemIdSelected;
    private String itemUnitTypeSelected;

    private boolean isLoading = false;
    private boolean isLastPage = false;

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
        mainActivity = ((MainActivity) getActivity());

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewRequest();
            }
        });

        // init view
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout_s);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        swipeRefreshLayout.setOnRefreshListener(this);

        // adapter
        mAdapter = new XRequestsAdapter(getContext(), requests, this);
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        // ... = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnScrollListener(new PaginationScrollListener(mLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                // mocking network delay for API call
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadNextPage();
                    }
                }, 1000);
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        // show loader and fetch messages
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        // getAllRequests();
                        loadFirstPage();
                    }
                }
        );

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
                searchRequest();
                break;
            case R.id.action_payment:
                mainActivity.launchPaymentFragment();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        // swipe refresh is performed, fetch the messages again
        getAllRequests();
    }

    @Override
    public void onIconClicked(int position, Request request) {
        showLongToast(getContext(), "Nalefan\'i " + request.getUser().getName());
    }

    @Override
    public void onMessageRowClicked(int position, Request request) {
        // go to list offer fragment
        switchToListOfferFragment(new FragmentListOffer(), request);
    }

    @Override
    public void onButtonAnswerClicked(int position, Request request) {
        // show answer popup
        answerRequest(request.getId());
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
     * Load first page
     */
    private void loadFirstPage() {
        swipeRefreshLayout.setRefreshing(true);

        // set retrofit api
        Service api = Client.getClient(baseActivity.ROOT_MDZ_API).create(Service.class);

        // create basic authentication
        String auth = baseActivity.BasicAuth();

        Map map = new HashMap<>();
        // filter
        map.put("type", "BUY");
        // get first page of 10 element
        map.put("size", "" + PAGE_SIZE);
        // first page is always begin by 0
        map.put("page", "" + currentPage);

        // send query
        Call<JsonObject> call = api.searchRequest(auth, map);

        // request
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                // verification
                if (response.body() == null) {
                    showLongToast(getContext(), getResources().getString(R.string.app_response_body_null));
                } else if (response.code() == 200) {
                    // info page
                    final Page pageInfo;
                    pageInfo = new Gson().fromJson(response.body().get("page").getAsJsonObject(), Page.class);
                    Log.d("REQUESTS Page Info", "" + pageInfo);

                    TOTAL_PAGES = pageInfo.getTotalPages();

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

                            // adding request to requests array
                            requests.add(request);

                            if (currentPage <= TOTAL_PAGES - 1) mAdapter.addLoadingFooter();
                            else isLastPage = true;
                        }

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        mAdapter.notifyDataSetChanged();

                        // hide swipe refresh
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
                                // TODO :)
                                //loadFirstPage();
                            }
                        })
                        .show();
            }
        });
    }

    /**
     * Load next page
     */
    private void loadNextPage() {
        // TODO TODO TODO

        // set retrofit api
        Service api = Client.getClient(baseActivity.ROOT_MDZ_API).create(Service.class);

        // create basic authentication
        String auth = baseActivity.BasicAuth();

        Map map = new HashMap<>();
        // get first page of 10 element
        map.put("size", TOTAL_PAGES);
        // first page is always begin by 0
        map.put("page", currentPage);

        // send query
        Call<JsonObject> call = api.getAllRequest(auth, map);

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
                        // TODO TODO TODO
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
                    }

                    // get category list
                    getAllCategory();
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

    /**
     * Load all category list
     */
    private void getAllCategory() {
        // set retrofit api
        Service api = Client.getClient(baseActivity.ROOT_MDZ_API).create(Service.class);

        // create basic authentication
        String auth = baseActivity.BasicAuth();

        // send query
        Call<JsonObject> call = api.getAllProductTemplate(auth);

        // request
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                // verification
                if (response.body() == null) {
                    showLongToast(getContext(), getResources().getString(R.string.app_response_body_null));
                } else if (response.code() == 200) {
                    // sort using result(s)
                    JsonArray filter = response.body().get("_embedded").getAsJsonObject().get("productTemplates").getAsJsonArray();

                    if (filter == null || (filter.size() == 0)) {
                        showLongToast(getContext(), getResources().getString(R.string.app_filter_data_null));
                    } else {
                        // class model to mapping gson
                        List<ProductTemplate> productTemplates = null;

                        // new class model to set all values
                        productTemplates = new ArrayList<ProductTemplate>();

                        // boucle
                        for (int i = 0; i < filter.size(); i++) {
                            ProductTemplate productTemplate = new Gson().fromJson(filter.get(i), ProductTemplate.class);
                            productTemplates.add(productTemplate);
                        }

                        // init spinner
                        templates = productTemplates;

                        // hide swipeRefresh
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                // hide swipeRefresh
                swipeRefreshLayout.setRefreshing(false);

                // alert
                new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AlertDialogCustom))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(getResources().getString(R.string.app_send_request_on_failure_title))
                        .setMessage(getResources().getString(R.string.app_send_request_on_failure_message))
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // hide dialog
                                dialog.dismiss();
                                // show swipe refresh
                                swipeRefreshLayout.setRefreshing(false);
                                // get category list
                                getAllCategory();
                            }
                        })
                        .show();
            }
        });
    }

    /**
     * Display popup post new item
     */
    private void addNewRequest() {
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
        for (int i = 0; i < templates.size(); i++) {
            itemsName.add(templates.get(i).getName());
            itemsId.add(templates.get(i).getId());
        }
        itemsName.add(getResources().getString(R.string.app_other_category));

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

                if (!((AppCompatTextView) view).getText().equals(getResources().getString(R.string.app_other_category))) {
                    // item'clicked position
                    itemIdSelected = itemsId.get(position);

                    // showing clicked spinner item name and position
                    showShortToast(parent.getContext(), "Item: " + itemNameSelected + "\nPosition: " + position + "\nid Item: " + itemIdSelected);
                }
            }
        });


        // drop down unit element
        String[] unitTypeName = baseActivity.getNames(Request.UnitType.class);


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
                showShortToast(parent.getContext(), "Item: " + itemUnitTypeSelected + "\nPosition: " + position + "\nid Item: " + itemIdSelected);
            }
        });

        // set dialog message
        builder
                .setTitle(getResources().getString(R.string.fragment_buy_post_request_title))
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
                        Service api = Client.getClient(baseActivity.ROOT_MDZ_API).create(Service.class);

                        // create basic authentication
                        String auth = baseActivity.BasicAuth();

                        // request
                        RequestSend postRequest = new RequestSend(baseActivity.getCurrentUser(getContext()).getId(), product, Request.UnitType.valueOf(unitType), Integer.parseInt(quantity), Request.Type.BUY, Float.parseFloat(price), itemIdSelected, true);

                        // send query
                        Call<Void> call = api.sendRequest(auth, postRequest);

                        // request
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.code() == 201) {
                                    dialog.dismiss();
                                    requests.clear();
                                    hideLoadingView();
                                    onRefresh();
                                }

                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                // hide spinner
                                hideLoadingView();

                                // alert
                                new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AlertDialogCustom))
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setTitle(getResources().getString(R.string.app_send_request_on_failure_title))
                                        .setMessage(getResources().getString(R.string.app_send_request_on_failure_message))
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
     * Display popup search request
     */
    private void searchRequest() {
        // TODO
        showLongToast(getContext(), "searchRequest");
    }

    /**
     * Display popup post new offer
     */
    private void answerRequest(final String requestId) {
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
        String[] unitTypeName = baseActivity.getNames(Request.UnitType.class);

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
                .setTitle(getResources().getString(R.string.fragment_buy_post_request_title))
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
                        Service api = Client.getClient(baseActivity.ROOT_MDZ_API).create(Service.class);

                        // create basic authentication
                        String auth = baseActivity.BasicAuth();

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
                                    requests.clear();
                                    hideLoadingView();
                                    onRefresh();
                                } else {
                                    dialog.dismiss();
                                    hideLoadingView();

                                    // alert
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
                                // hide spinner
                                hideLoadingView();

                                // alert
                                new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AlertDialogCustom))
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setTitle(getResources().getString(R.string.app_send_request_on_failure_title))
                                        .setMessage(getResources().getString(R.string.app_send_request_on_failure_message))
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

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================

}
