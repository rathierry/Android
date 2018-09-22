package com.team.lezomadetana.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
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
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.team.lezomadetana.R;
import com.team.lezomadetana.activity.BaseActivity;
import com.team.lezomadetana.activity.MainActivity;
import com.team.lezomadetana.adapter.RequestsAdapter;
import com.team.lezomadetana.api.Client;
import com.team.lezomadetana.api.Service;
import com.team.lezomadetana.model.receive.Page;
import com.team.lezomadetana.model.receive.ProductTemplate;
import com.team.lezomadetana.model.receive.Request;
import com.team.lezomadetana.model.send.OfferSend;
import com.team.lezomadetana.model.send.RequestSend;
import com.team.lezomadetana.utils.PaginationScrollListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.team.lezomadetana.activity.BaseActivity.BasicAuth;

/**
 * Created by RaThierry on 21/09/2018.
 */

public class FragmentRequestSell extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,
        RequestsAdapter.RequestAdapterListener {

    // ===========================================================
    // Constants
    // ===========================================================

    private static int PAGE_START = 0;
    private static int PAGE_ELEM = 6;
    // limiting to 20 for this screen, since total pages in actual API is very large.
    private int TOTAL_PAGES = 0;
    private int PAGE_SIZE = PAGE_ELEM;
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
    private RequestsAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private String selectedItemOfTemplates;
    private String actualTemplatePosition;
    private String selectedItemOfUnitType;

    private boolean isLoading = false;
    private boolean isLastPage = false;
    private boolean startFirst = false;
    private boolean isOnResume = false;

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
                inflater.inflate(R.layout.fragment_request_sell, container, false);

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
        mAdapter = new RequestsAdapter(getContext(), requests, this, this);
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        // ... = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        // scroll recycler
        onScrollRecyclerView();

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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && rootView != null) {
            if (!startFirst) {
                //showShortToast(getContext(), "< download category >");
                getAllCategory();
                startFirst = true;
            } else {
                isOnResume = true;
                onResume();
            }
        }
    }

    @Override
    public void onResume() {
        /*if (getUserVisibleHint()) {
            showShortToast(getContext(), "< getUserVisibleHint >");
        }*/
        if (isOnResume) {
            onRefresh();
            //showShortToast(getContext(), "< onResume >");
            isOnResume = false;
        }
        super.onResume();
    }

    @Override
    public void onRefresh() {
        resetPagination();
        loadFirstPage();
        //showShortToast(getContext(), "< onRefresh >");
    }

    @Override
    public void onIconClicked(int position, Request request) {
        if (request.getUser() != null) {
            showLongToast(getContext(), "Nalefan\'i " + request.getUser().getName());
        }
    }

    @Override
    public void onMessageRowClicked(int position, Request request) {
        // go to list offer fragment
        switchToListOfferFragment(new FragmentListOffer(), request);
    }

    @Override
    public void onButtonAnswerClicked(int position, Request request) {
        // show answer popup
        answerRequest(request);
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
     * Scroll in recycle view
     */
    private void onScrollRecyclerView() {
        recyclerView.addOnScrollListener(new PaginationScrollListener(mLayoutManager) {
            @Override
            protected void loadMoreItems() {
                //
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
    }

    /**
     * Load all category list
     */
    private void getAllCategory() {
        // show spinner
        showLoadingView("Download Category ...");

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
                    hideLoadingView();
                    showLongToast(getContext(), getResources().getString(R.string.app_response_body_null));
                } else if (response.code() == 200) {
                    // sort using result(s)
                    JsonArray filter = response.body().get("_embedded").getAsJsonObject().get("productTemplates").getAsJsonArray();

                    if (filter == null || (filter.size() == 0)) {
                        hideLoadingView();
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

                        // hide spinner
                        hideLoadingView();

                        // show loader and fetch messages
                        swipeRefreshLayout.post(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        loadFirstPage();
                                    }
                                }
                        );
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
                                dialog.dismiss();
                                hideLoadingView();
                                getAllCategory();
                            }
                        })
                        .show();
            }
        });
    }

    /**
     * Reset pagination
     */
    private void resetPagination() {
        showLongToast(getContext(), "> PAGE_SIZE = " + PAGE_SIZE +
                "\n> TOTAL_PAGES = " + TOTAL_PAGES +
                "\n> currentPage = " + currentPage);
        PAGE_SIZE = PAGE_ELEM;
        currentPage = 0;
    }

    /**
     * Load first page
     */
    private void loadFirstPage() {
        //showShortToast(getContext(), "< loadFirstPage >");

        swipeRefreshLayout.setRefreshing(true);

        // set retrofit api
        Service api = Client.getClient(baseActivity.ROOT_MDZ_API).create(Service.class);

        // create basic authentication
        String auth = baseActivity.BasicAuth();

        // params
        showShortToast(getContext(), "<FIRST> page\ncurrentPage = " + currentPage);

        // for example, http://api.madawin.mg/rest/requests?sort=creationTime,desc&type=BUY&page=0&size=20
        Map map = new HashMap<>();
        // filter
        map.put("sort", "creationTime,desc");
        map.put("type", "SELL");
        // first page is always begin by 0
        map.put("page", String.valueOf(currentPage));
        // get first page of 21 element
        map.put("size", String.valueOf(PAGE_SIZE));

        // send query
        Call<JsonObject> call = api.getAllRequest(auth, map);

        // request
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                // verification
                if (response.body() == null) {
                    swipeRefreshLayout.setRefreshing(false);
                    showAlertDialog(getResources().getString(R.string.fragment_buy_toolBar_title), R.drawable.ic_warning_black, getResources().getString(R.string.app_response_body_null));
                } else if (response.code() == 200) {
                    // info page
                    final Page pageInfo;
                    pageInfo = new Gson().fromJson(response.body().get("page").getAsJsonObject(), Page.class);
                    Log.d("REQUESTS", "" + pageInfo);

                    // set total pages value
                    TOTAL_PAGES = pageInfo.getTotalPages();

                    // array filter
                    JsonArray filter = response.body().get("_embedded").getAsJsonObject().get("requests").getAsJsonArray();

                    if (filter == null || (filter.size() == 0)) {
                        hideLoadingView();
                        showAlertDialog(getResources().getString(R.string.fragment_buy_toolBar_title), R.drawable.ic_warning_black, getResources().getString(R.string.app_filter_data_null));
                    } else {
                        // clear the inbox
                        requests.clear();

                        // parsing gson
                        for (int i = 0; i < filter.size(); i++) {
                            // class model to mapping gson
                            Request request = new Gson().fromJson(filter.get(i), Request.class);

                            // generate a random color
                            request.setColor(getRandomMaterialColor("500"));

                            // adding request to requests array
                            requests.add(request);

                            // check last page
                            if (currentPage <= TOTAL_PAGES - 1) {
                                mAdapter.addLoadingFooter();
                            } else {
                                isLastPage = true;
                            }
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
                showAlertDialog(getResources().getString(R.string.app_send_request_on_failure_title), R.drawable.ic_warning_black, "Unable to fetch json: " + t.getMessage());
            }
        });
    }

    /**
     * Load next page
     */
    public void loadNextPage() {
        // specific values for the next page
        isLoading = true;
        currentPage += 1;

        // set retrofit api
        Service api = Client.getClient(baseActivity.ROOT_MDZ_API).create(Service.class);

        // create basic authentication
        String auth = baseActivity.BasicAuth();

        // params
        showShortToast(getContext(), "<NEXT> page\ncurrentPage = " + currentPage);

        // for example, http://api.madawin.mg/rest/requests?sort=creationTime,desc&type=BUY&page=0&size=20
        Map map = new HashMap<>();
        // filter
        map.put("sort", "creationTime,desc");
        map.put("type", "SELL");
        // then page is begin by 1
        map.put("page", String.valueOf(currentPage));
        // get first page of 21 element
        map.put("size", String.valueOf(PAGE_SIZE));

        // send query
        Call<JsonObject> call = api.getAllRequest(auth, map);

        // request
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                // verification
                if (response.body() == null) {
                    hideLoadingView();
                    showAlertDialog(getResources().getString(R.string.fragment_buy_toolBar_title), R.drawable.ic_warning_black, getResources().getString(R.string.app_response_body_null));
                } else if (response.code() == 200) {
                    // info page
                    final Page pageInfo;
                    pageInfo = new Gson().fromJson(response.body().get("page").getAsJsonObject(), Page.class);
                    Log.d("REQUESTS", "" + pageInfo);

                    // set total pages value
                    TOTAL_PAGES = pageInfo.getTotalPages();

                    // array filter
                    JsonArray filter = response.body().get("_embedded").getAsJsonObject().get("requests").getAsJsonArray();

                    // verification
                    if (filter == null || (filter.size() == 0)) {
                        hideLoadingView();
                        showAlertDialog(getResources().getString(R.string.fragment_buy_toolBar_title), R.drawable.ic_warning_black, getResources().getString(R.string.app_filter_data_null));
                    } else {
                        mAdapter.removeLoadingFooter();
                        isLoading = false;

                        // parsing gson
                        for (int i = 0; i < filter.size(); i++) {
                            // class model to mapping gson
                            Request request = new Gson().fromJson(filter.get(i), Request.class);

                            // generate a random color
                            request.setColor(getRandomMaterialColor("500"));

                            // adding request to requests array
                            requests.add(request);

                            // check last page
                            if (currentPage == TOTAL_PAGES - 1) {
                                isLastPage = true;
                            } else {
                                mAdapter.addLoadingFooter();
                            }
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
                showAlertDialog(getResources().getString(R.string.app_send_request_on_failure_title), R.drawable.ic_warning_black, "Unable to fetch json: " + t.getMessage());
            }
        });
    }

    /**
     * Display popup post new item
     */
    private void addNewRequest() {
        // get prompts xml view
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getContext());
        final View mView = layoutInflaterAndroid.inflate(R.layout.post_new_request, null);

        // create alert builder and cast view
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AlertDialogCustom));

        // set prompts xml to alert dialog builder
        builder.setView(mView);

        // init view
        final Spinner spinnerCategory = (Spinner) mView.findViewById(R.id.fragment_post_item_design_spinner_category);
        final Spinner spinnerUnitType = (Spinner) mView.findViewById(R.id.fragment_post_item_design_spinner_unit_type);
        final EditText editTextQuantity = (EditText) mView.findViewById(R.id.fragment_post_item_editText_quantity);
        final EditText editTextPrice = (EditText) mView.findViewById(R.id.fragment_post_item_editText_price);
        final EditText editTextProduct = (EditText) mView.findViewById(R.id.fragment_post_item_editText_product);

        // drop down element
        List<String> listTemplates = new ArrayList<>();
        final List<String> itemsId = new ArrayList<>();

        // set array values
        listTemplates.add(getResources().getString(R.string.fragment_buy_post_request_category_hint));
        for (int i = 0; i < templates.size(); i++) {
            listTemplates.add(templates.get(i).getName());
            itemsId.add(templates.get(i).getId());
        }
        listTemplates.add(getResources().getString(R.string.app_other_category));

        // set adapter for spinner
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, listTemplates) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;

                // color
                if (position == 0) {
                    // set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                    tv.setAllCaps(false);
                } else {
                    tv.setTextColor(Color.BLACK);
                    tv.setAllCaps(true);
                }

                // background
                if (position % 2 == 1) {
                    // set the alternate item background color
                    tv.setBackgroundColor(getResources().getColor(R.color.md_blue_grey_50));
                } else {
                    // Set the item background color
                    tv.setBackgroundColor(getResources().getColor(R.color.md_blue_grey_100));
                }

                return view;
            }
        };

        // set drop down
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);

        // attaching data adapter to spinner
        spinnerCategory.setAdapter(arrayAdapter);

        // event onClick
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItemOfTemplates = (String) parent.getItemAtPosition(position).toString();
                actualTemplatePosition = itemsId.get(position);

                // showing selected spinner item name and position
                if (selectedItemOfTemplates.equals(getResources().getString(R.string.fragment_buy_post_request_category_hint))) {
                    showShortToast(parent.getContext(), selectedItemOfTemplates);
                } else if (selectedItemOfTemplates.equals(getResources().getString(R.string.app_other_category))) {
                    showShortToast(parent.getContext(), selectedItemOfTemplates);
                } else {
                    // toast
                    showShortToast(parent.getContext(), ">>>\nItem: " + selectedItemOfTemplates + "\nPosition: " + position + "\nid Item: " + actualTemplatePosition);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // // //

        // drop down unit element
        String[] unitTypeName = baseActivity.getNames(Request.UnitType.class);

        // unit type list of data
        List<String> listUnitType = new ArrayList<>();

        // set array values
        listUnitType.add(getResources().getString(R.string.fragment_buy_post_request_unity_type_hint));
        for (int i = 0; i < unitTypeName.length; i++) {
            listUnitType.add(unitTypeName[i]);
        }

        // set adapter for spinner
        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, listUnitType) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;

                // color
                if (position == 0) {
                    // set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                    tv.setAllCaps(false);
                } else {
                    tv.setTextColor(Color.BLACK);
                    tv.setAllCaps(true);
                }

                // background
                if (position % 2 == 1) {
                    // set the alternate item background color
                    tv.setBackgroundColor(getResources().getColor(R.color.md_blue_grey_50));
                } else {
                    // Set the item background color
                    tv.setBackgroundColor(getResources().getColor(R.color.md_blue_grey_100));
                }

                return view;
            }
        };

        // set drop down
        arrayAdapter2.setDropDownViewResource(R.layout.spinner_item);

        // attaching data adapter to spinner
        spinnerUnitType.setAdapter(arrayAdapter2);

        // event onSelect
        spinnerUnitType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItemOfUnitType = parent.getItemAtPosition(position).toString();
                if (position != 0) {
                    showShortToast(parent.getContext(), "Item: " + selectedItemOfUnitType);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
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
                        String category = selectedItemOfTemplates;
                        String quantity = editTextQuantity.getText().toString();
                        String unitType = selectedItemOfUnitType;
                        String price = editTextPrice.getText().toString();
                        String product = editTextProduct.getText().toString();
                        // product
                        if (product.isEmpty() || TextUtils.isEmpty(product)) {
                            editTextProduct.setError(getResources().getString(R.string.fragment_buy_post_request_product_hint));
                            editTextProduct.requestFocus();
                            return;
                        }
                        // category
                        if (category.isEmpty() || TextUtils.isEmpty(category) || category.contains(getResources().getString(R.string.fragment_buy_post_request_category_hint))) {
                            setSpinnerError(spinnerCategory, getResources().getString(R.string.fragment_buy_post_request_category_hint));
                            return;
                        }
                        // quantity
                        if (quantity.isEmpty() || TextUtils.isEmpty(quantity)) {
                            editTextQuantity.setError(getResources().getString(R.string.fragment_buy_post_request_quantity_error_empty));
                            editTextQuantity.requestFocus();
                            return;
                        }
                        // unitType
                        if (unitType.isEmpty() || TextUtils.isEmpty(unitType) || unitType.contains(getResources().getString(R.string.fragment_buy_post_request_unity_type_hint))) {
                            setSpinnerError(spinnerUnitType, getResources().getString(R.string.fragment_buy_post_request_unity_type_hint));
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
                        Service api = Client.getClient(BaseActivity.ROOT_MDZ_API).create(Service.class);

                        // create basic authentication
                        String auth = BasicAuth();

                        //
                        BaseActivity baseActivity = (BaseActivity) getActivity();

                        //
                        RequestSend request = new RequestSend(baseActivity.getCurrentUser(getContext()).getId(), product, Request.UnitType.valueOf(unitType), Float.parseFloat(price), Request.Type.SELL, actualTemplatePosition, true);

                        // send query
                        Call<Void> call = api.sendRequest(auth, request);

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
        showLongToast(getContext(), "< searchRequest >");
    }

    /**
     * Display popup post new offer
     */
    private void answerRequest(final Request request) {
        // get prompts xml view
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getContext());
        final View mView = layoutInflaterAndroid.inflate(R.layout.post_answer_offer, null);

        // create alert builder and cast view
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AlertDialogCustom));

        // set prompts xml to alert dialog builder
        builder.setView(mView);

        // init view
        final Spinner spinnerUnitType = (Spinner) mView.findViewById(R.id.dialog_offer_unity);
        final EditText editTextQuantity = (EditText) mView.findViewById(R.id.dialog_offer_quantity_text);
        final EditText editTextPrice = (EditText) mView.findViewById(R.id.dialog_offer_price_text);

        // drop down unit element
        String[] unitTypeName = BaseActivity.getNames(Request.UnitType.class);

        // unit type list of data
        List<String> listUnitType = new ArrayList<>();

        // set array values
        listUnitType.add(getResources().getString(R.string.fragment_buy_post_request_unity_type_hint));
        for (int i = 0; i < unitTypeName.length; i++) {
            listUnitType.add(unitTypeName[i]);
        }

        // set adapter for spinner
        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, listUnitType) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;

                // color
                if (position == 0) {
                    // set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                    tv.setAllCaps(false);
                } else {
                    tv.setTextColor(Color.BLACK);
                    tv.setAllCaps(true);
                }

                // background
                if (position % 2 == 1) {
                    // set the alternate item background color
                    tv.setBackgroundColor(getResources().getColor(R.color.md_blue_grey_50));
                } else {
                    // Set the item background color
                    tv.setBackgroundColor(getResources().getColor(R.color.md_blue_grey_100));
                }

                return view;
            }
        };

        // set drop down
        arrayAdapter2.setDropDownViewResource(R.layout.spinner_item);

        // attaching data adapter to spinner
        spinnerUnitType.setAdapter(arrayAdapter2);

        // event onSelect
        spinnerUnitType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItemOfUnitType = parent.getItemAtPosition(position).toString();
                if (position != 0) {
                    showShortToast(parent.getContext(), "Item: " + selectedItemOfUnitType);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // set unit type value from DB
        setSpinnerSelection(spinnerUnitType, listUnitType, request.getUnitType().name());

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
                        String unitType = selectedItemOfUnitType;
                        String price = editTextPrice.getText().toString();

                        // quantity
                        if (quantity.isEmpty() || TextUtils.isEmpty(quantity)) {
                            editTextQuantity.setError(getResources().getString(R.string.fragment_buy_post_request_quantity_error_empty));
                            editTextQuantity.requestFocus();
                            return;
                        }
                        // unitType
                        if (unitType.isEmpty() || TextUtils.isEmpty(unitType) || unitType.contains(getResources().getString(R.string.fragment_buy_post_request_unity_type_hint))) {
                            setSpinnerError(spinnerUnitType, getResources().getString(R.string.fragment_buy_post_request_unity_type_hint));
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
                        Service api = Client.getClient(BaseActivity.ROOT_MDZ_API).create(Service.class);

                        // create basic authentication
                        String auth = BasicAuth();

                        //
                        BaseActivity baseActivity = (BaseActivity) getActivity();

                        // set unit type value from DB
                        showShortToast(baseActivity, "requestId : " + request.getId());

                        // mapping class model
                        OfferSend offerSend = new OfferSend(request.getId(), baseActivity.getCurrentUser(getContext()).getId(), Integer.parseInt(quantity), Request.UnitType.valueOf(unitType), Float.parseFloat(price), true);

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
