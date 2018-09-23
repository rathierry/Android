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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
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
 * Created by RaThierry on 18/09/2018.
 */

public class FragmentRequestBuy extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,
        RequestsAdapter.RequestAdapterListener {

    // ===========================================================
    // Constants
    // ===========================================================

    // limiting to 20 for this screen, since total pages in actual API is very large.
    private int TOTAL_PAGES = 0;
    private int PAGE_SIZE = PAGE_ELEM;
    private int currentPage = PAGE_START;

    // ===========================================================
    // Fields
    // ===========================================================

    private BaseActivity baseActivity;
    private MainActivity mainActivity;
    private FragmentRequestBuy fragmentRequestBuy;
    private View rootView;
    private LinearLayoutManager mLayoutManager;
    private List<Request> requests = new ArrayList<>();
    private List<ProductTemplate> templates = new ArrayList<>();
    private RecyclerView recyclerView;
    private RequestsAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout relativeLayoutForSearchResult;
    private TextView textViewForSearchTitle;
    private Switch switchSearchButton;

    private String selectedItemOfTemplates;
    private String actualTemplatePosition;
    private String selectedItemOfUnitType;
    private String product;

    private boolean isLoading = false;
    private boolean isLastPage = false;
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
                inflater.inflate(R.layout.fragment_request_buy, container, false);

        // current activity
        baseActivity = ((BaseActivity) getActivity());
        mainActivity = ((MainActivity) getActivity());
        fragmentRequestBuy = this;

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewRequest();
            }
        });

        // init view
        relativeLayoutForSearchResult = (RelativeLayout) rootView.findViewById(R.id.fragment_request_buy_switch_layout_search_result);
        textViewForSearchTitle = (TextView) rootView.findViewById(R.id.fragment_request_buy_switch_text_view_title);
        switchSearchButton = (Switch) rootView.findViewById(R.id.fragment_request_buy_switch_button);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout_s);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        swipeRefreshLayout.setOnRefreshListener(this);

        // recycle view
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        // ... = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        // check switch button status
        checkSwitchStatus();

        // scroll recycler
        onScrollRecyclerView();

        // download all category
        getAllCategory();

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
            isOnResume = true;
            onResume();
        }
    }

    @Override
    public void onResume() {
        if (isOnResume) {
            onRefresh();
            //showShortToast(getContext(), "- onResume -");
            isOnResume = false;
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        hideSearchLayoutTitle();
    }

    @Override
    public void onRefresh() {
        hideSearchLayoutTitle();
        resetPagination();
        initSimpleAdapter();
        loadFirstPage();
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
     * call adapter no search item
     */
    private void initSimpleAdapter() {
        mAdapter = new RequestsAdapter(getContext(), requests, fragmentRequestBuy, fragmentRequestBuy, false);
        recyclerView.setAdapter(mAdapter);
    }

    /**
     * call adapter with search item
     */
    private void initSearchAdapter() {
        mAdapter = new RequestsAdapter(getContext(), requests, fragmentRequestBuy, fragmentRequestBuy, true);
        recyclerView.setAdapter(mAdapter);
    }

    /**
     * Check switch button status
     */
    private void checkSwitchStatus() {
        switchSearchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (!bChecked) {
                    hideSearchLayoutTitle();
                    onRefresh();
                }
            }
        });
    }

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
                                        initSimpleAdapter();
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
        showLongToast(getContext(), "- PAGE_SIZE = " + PAGE_SIZE +
                "\n- TOTAL_PAGES = " + TOTAL_PAGES +
                "\n- currentPage = " + currentPage);
        PAGE_SIZE = PAGE_ELEM;
        currentPage = 0;
    }

    /**
     * Load first page
     */
    private void loadFirstPage() {
        swipeRefreshLayout.setRefreshing(true);

        // set retrofit api
        Service api = Client.getClient(baseActivity.ROOT_MDZ_API).create(Service.class);

        // create basic authentication
        String auth = baseActivity.BasicAuth();

        // params
        showShortToast(getContext(), "[FIRST] page\ncurrentPage = " + currentPage);

        // for example, http://api.madawin.mg/rest/requests?sort=creationTime,desc&type=BUY&page=0&size=20
        Map map = new HashMap<>();
        // filter
        map.put("sort", "creationTime,desc");
        map.put("type", "BUY");
        // first page is always begin by 0
        map.put("page", String.valueOf(currentPage));
        // get first page of 5 element
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
                    showAlertDialog(getResources().getString(R.string.fragment_buy_toolBar_title), R.drawable.ic_notification_important_black, getResources().getString(R.string.app_response_body_null));
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
                        showAlertDialog(getResources().getString(R.string.fragment_buy_toolBar_title), R.drawable.ic_notification_important_black, getResources().getString(R.string.app_filter_data_null));
                    } else {
                        // clear the inbox
                        requests.clear();

                        // parsing gson
                        for (int i = 0; i < filter.size(); i++) {
                            // class model to mapping gson
                            Request request = new Gson().fromJson(filter.get(i), Request.class);

                            // generate a random color
                            request.setColor(getRandomMaterialColor("400"));

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
        showShortToast(getContext(), "[NEXT] page\ncurrentPage = " + currentPage);

        // for example, http://api.madawin.mg/rest/requests?sort=creationTime,desc&type=BUY&page=0&size=20
        Map map = new HashMap<>();
        // filter
        map.put("sort", "creationTime,desc");
        map.put("type", "BUY");
        // then page is begin by 1
        map.put("page", String.valueOf(currentPage));
        // get first page of 5 element
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
                    showAlertDialog(getResources().getString(R.string.fragment_buy_toolBar_title), R.drawable.ic_notification_important_black, getResources().getString(R.string.app_response_body_null));
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
                        showAlertDialog(getResources().getString(R.string.fragment_buy_toolBar_title), R.drawable.ic_notification_important_black, getResources().getString(R.string.app_filter_data_null));
                    } else {
                        mAdapter.removeLoadingFooter();
                        isLoading = false;

                        // parsing gson
                        for (int i = 0; i < filter.size(); i++) {
                            // class model to mapping gson
                            Request request = new Gson().fromJson(filter.get(i), Request.class);

                            // generate a random color
                            request.setColor(getRandomMaterialColor("400"));

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
            showShortToast(getContext(), "name: " + templates.get(i).getName() + "\nid: " + templates.get(i).getId());
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

        // event onSelect
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // get item name
                selectedItemOfTemplates = (String) parent.getItemAtPosition(position).toString();
                // check
                if (((AppCompatTextView) view).getText().equals(getResources().getString(R.string.fragment_buy_post_request_category_hint))) {
                    //
                }
                else if (((AppCompatTextView) view).getText().equals(getResources().getString(R.string.app_other_category))) {
                    //
                }
                else {
                    // get selected item position
                    actualTemplatePosition = itemsId.get(position - 1);
                    // toast
                    showShortToast(parent.getContext(), "[[[\nItem: " + selectedItemOfTemplates + "\nPosition: " + (position - 1) + "\nid Item: " + actualTemplatePosition);
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

        // event onClick
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
                .setCancelable(true)
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
                        Service api = Client.getClient(baseActivity.ROOT_MDZ_API).create(Service.class);

                        // create basic authentication
                        String auth = baseActivity.BasicAuth();

                        // request
                        RequestSend postRequest = new RequestSend(baseActivity.getCurrentUser(getContext()).getId(), product, Request.UnitType.valueOf(unitType), Integer.parseInt(quantity), Request.Type.BUY, Float.parseFloat(price), actualTemplatePosition, true);

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
        String[] unitTypeName = baseActivity.getNames(Request.UnitType.class);

        // unit type list of data
        final List<String> listUnitType = new ArrayList<>();

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
                .setTitle(getResources().getString(R.string.fragment_buy_post_request_title))
                .setIcon(R.drawable.ic_info_black)
                .setCancelable(true)
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

                        // api
                        Service api = Client.getClient(baseActivity.ROOT_MDZ_API).create(Service.class);

                        // create basic authentication
                        String auth = baseActivity.BasicAuth();

                        // test
                        showShortToast(baseActivity, "requestId : " + request.getId());

                        // mapping model
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

    /**
     * Display popup search request
     */
    private void searchRequest() {
        // get prompts xml view
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getContext());
        final View mView = layoutInflaterAndroid.inflate(R.layout.search_request, null);

        // create alert builder and cast view
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AlertDialogCustom));

        // set prompts xml to alert dialog builder
        builder.setView(mView);

        // init view
        final Spinner spinnerCategory = (Spinner) mView.findViewById(R.id.search_category);
        final EditText editTextProduct = (EditText) mView.findViewById(R.id.search_product);

        // drop down element
        List<String> listTemplates = new ArrayList<>();
        final List<String> itemsId = new ArrayList<>();

        // set array values
        listTemplates.add(getResources().getString(R.string.fragment_buy_post_request_category_hint));
        for (int i = 0; i < templates.size(); i++) {
            listTemplates.add(templates.get(i).getName());
            showShortToast(getContext(), "name: " + templates.get(i).getName() + "\nid: " + templates.get(i).getId());
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

        // event onSelect
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // get item name
                selectedItemOfTemplates = (String) parent.getItemAtPosition(position).toString();
                // check
                if (((AppCompatTextView) view).getText().equals(getResources().getString(R.string.fragment_buy_post_request_category_hint))) {
                    //
                }
                else if (((AppCompatTextView) view).getText().equals(getResources().getString(R.string.app_other_category))) {
                    //
                }
                else {
                    // get selected item position
                    actualTemplatePosition = itemsId.get(position - 1);
                    // toast
                    showShortToast(parent.getContext(), ">>>\nItem: " + selectedItemOfTemplates + "\nPosition: " + (position - 1) + "\nid Item: " + actualTemplatePosition);
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
                .setCancelable(true)
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
                        product = editTextProduct.getText().toString();

                        // category
                        if (category.isEmpty() || TextUtils.isEmpty(category) || category.contains(getResources().getString(R.string.fragment_buy_post_request_category_hint))) {
                            setSpinnerError(spinnerCategory, getResources().getString(R.string.fragment_buy_post_request_category_hint));
                            spinnerCategory.requestFocus();
                            return;
                        }

                        // hide dialog
                        dialog.dismiss();

                        // show spinner
                        showLoadingView(getResources().getString(R.string.app_spinner));

                        // api
                        Service api = Client.getClient(BaseActivity.ROOT_MDZ_API).create(Service.class);

                        // create basic authentication
                        String auth = BasicAuth();

                        // add body params for API
                        Map<String, String> map = new HashMap<>();
                        // filter
                        map.put("sort", "creationTime,desc");
                        // first page is always begin by 0
                        currentPage = PAGE_START;
                        map.put("page", String.valueOf(currentPage));
                        // get first page of 6 element
                        map.put("size", String.valueOf(PAGE_SIZE));
                        // type of search
                        map.put("type", "BUY");
                        // id of template
                        if (category != getResources().getString(R.string.app_other_category)) {
                            String id = "";
                            for (int i = 0; i < templates.size(); i++) {
                                if (templates.get(i).equals(category)) {
                                    id = templates.get(i).getId();
                                    break;
                                }
                            }
                            map.put("templateId", id);
                        }
                        // product search
                        if (!product.isEmpty() || !TextUtils.isEmpty(product)) {
                            map.put("product", product);
                        }

                        // send query
                        Call<JsonObject> call = api.searchRequest(auth, map);

                        // request
                        call.enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                // verification
                                if (response.body() == null) {
                                    swipeRefreshLayout.setRefreshing(false);
                                    showAlertDialog(getResources().getString(R.string.fragment_buy_toolBar_title), R.drawable.ic_notification_important_black, getResources().getString(R.string.app_response_body_null));
                                } else if (response.code() == 200) {
                                    Log.e("REQUESTS", "" + response.body().toString());
                                    showShortToast(getContext(), response.body().toString());

                                    // info page
                                    final Page pageInfo;
                                    pageInfo = new Gson().fromJson(response.body().get("page").getAsJsonObject(), Page.class);
                                    Log.e("REQUESTS", "" + pageInfo);
                                    showShortToast(getContext(), pageInfo.toString());

                                    // set total pages value
                                    TOTAL_PAGES = pageInfo.getTotalPages();
                                    showShortToast(getContext(), "currentPage = " + currentPage +
                                            "\nTOTAL_PAGES = " + String.valueOf(TOTAL_PAGES));

                                    // array filter
                                    JsonArray filter = response.body().get("_embedded").getAsJsonObject().get("requests").getAsJsonArray();

                                    if (filter == null || (filter.size() == 0)) {
                                        showShortToast(getContext(), "Filter NULL or SIZE = " + filter.size());
                                        showAlertDialog(getResources().getString(R.string.fragment_buy_switch_text_title), R.drawable.ic_notification_important_black, "Tsy nahitana valiny");
                                    } else {
                                        showShortToast(getContext(), "filter.size() = " + filter.size());

                                        // display search layout
                                        showSearchLayoutTitle();

                                        // clear list request
                                        requests.clear();

                                        // call correct adapter
                                        initSearchAdapter();

                                        // parsing gson
                                        for (int i = 0; i < filter.size(); i++) {
                                            // class model to mapping gson
                                            Request request = new Gson().fromJson(filter.get(i), Request.class);

                                            // generate a random color
                                            request.setColor(getRandomMaterialColor("400"));

                                            // adding request to requests array
                                            requests.add(request);

                                            // check last page
                                            if (currentPage == TOTAL_PAGES - 1) {
                                                isLastPage = true;
                                                showShortToast(getContext(), "isLastPage");
                                            } else {
                                                mAdapter.addLoadingFooter();
                                                showShortToast(getContext(), "mAdapter.addLoadingFooter()");
                                            }
                                        }

                                        // notifying list adapter about data changes
                                        // so that it renders the list view with updated data
                                        mAdapter.notifyDataSetChanged();

                                        showShortToast(getContext(), "requests.size() = " + requests.size());
                                    }
                                }
                                hideLoadingView();
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                // hide alert
                                hideLoadingView();
                                // alert
                                showAlertDialog(getResources().getString(R.string.app_send_request_on_failure_title), R.drawable.ic_warning_black, getResources().getString(R.string.app_send_request_on_failure_message));
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
     * Load next search page
     */
    public void loadSearchNextPage() {
        // specific values for the next page
        isLoading = true;
        currentPage += 1;

        // set retrofit api
        Service api = Client.getClient(baseActivity.ROOT_MDZ_API).create(Service.class);

        // create basic authentication
        String auth = baseActivity.BasicAuth();

        // params
        showShortToast(getContext(), "[NEXT] page\ncurrentPage = " + currentPage);

        // add body params for API
        Map<String, String> map = new HashMap<>();
        // filter
        map.put("sort", "creationTime,desc");
        // then page is begin by 1
        map.put("page", String.valueOf(currentPage));
        // get first page of 6 element
        map.put("size", String.valueOf(PAGE_SIZE));
        // type of search
        map.put("type", "BUY");
        // id of template
        if (selectedItemOfTemplates != getResources().getString(R.string.app_other_category)) {
            String id = "";
            for (int i = 0; i < templates.size(); i++) {
                if (templates.get(i).equals(selectedItemOfTemplates)) {
                    id = templates.get(i).getId();
                    break;
                }
            }
            map.put("templateId", id);
        }
        // product search
        if (!product.isEmpty() || !TextUtils.isEmpty(product)) {
            map.put("product", product);
        }

        // send query
        Call<JsonObject> call = api.searchRequest(auth, map);

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

                    if (filter == null || (filter.size() == 0)) {
                        showLongToast(getContext(), "Filter NULL or SIZE = 0");
                        showAlertDialog(getResources().getString(R.string.fragment_buy_switch_text_title), R.drawable.ic_notification_important_black, "Tsy nahitana valiny");
                    } else {
                        // call correct adapter
                        // initSearchAdapter();
                        mAdapter.removeLoadingFooter();
                        isLoading = false;

                        // parsing gson
                        for (int i = 0; i < filter.size(); i++) {
                            // class model to mapping gson
                            Request request = new Gson().fromJson(filter.get(i), Request.class);

                            // generate a random color
                            request.setColor(getRandomMaterialColor("400"));

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
                hideLoadingView();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                showAlertDialog(getResources().getString(R.string.app_send_request_on_failure_title), R.drawable.ic_warning_black, getResources().getString(R.string.app_send_request_on_failure_message));
            }
        });
    }

    /**
     * Enable search layout title
     */
    private void showSearchLayoutTitle() {
        textViewForSearchTitle.setText(getResources().getString(R.string.fragment_buy_switch_text_title));
        switchSearchButton.setChecked(true);
        relativeLayoutForSearchResult.setVisibility(View.VISIBLE);
    }

    /**
     * Disable search layout title
     */
    private void hideSearchLayoutTitle() {
        textViewForSearchTitle.setText("");
        switchSearchButton.setChecked(false);
        relativeLayoutForSearchResult.setVisibility(View.GONE);
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================

}
