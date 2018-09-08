package com.team.lezomadetana.fragment;

import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.team.lezomadetana.R;
import com.team.lezomadetana.activity.BaseActivity;
import com.team.lezomadetana.adapter.MessagesAdapter;
import com.team.lezomadetana.api.APIClient;
import com.team.lezomadetana.api.APIInterface;
import com.team.lezomadetana.model.receive.ProductTemplate;
import com.team.lezomadetana.model.receive.Request;
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

public class FragmentSearchItem extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, MessagesAdapter.MessageAdapterListener {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private String itemNameSelected;
    private String itemIdSelected;
    private String itemUnitTypeSelected;

    private List<ProductTemplate> _categoryList = new ArrayList<ProductTemplate>();

    private List<Request> messages = new ArrayList<>();
    private RecyclerView recyclerView;
    private MessagesAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_item, container, false);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fragment_search_item_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        recyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_search_item_recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_search_item_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        mAdapter = new MessagesAdapter(getContext(), messages, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        actionModeCallback = new ActionModeCallback();

        // show loader and fetch messages
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        getInbox();
                    }
                }
        );

        /*// init view
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
        });*/

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
                showLongToast(getActivity(), "1) FIKAROHANA");
                break;
            case R.id.action_payment:
                showLongToast(getActivity(), "1) RESA-BOLA");
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        showShortToast(getContext(), "onRefresh");
        //fetchAllRequests();
        // swipe refresh is performed, fetch the messages again
        getInbox();
    }

    @Override
    public void onIconClicked(int position) {
        if (actionMode == null) {
            actionMode = getActivity().startActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    @Override
    public void onIconImportantClicked(int position) {
        // Star icon is clicked,
        // mark the message as important
        Request message = messages.get(position);
        message.setImportant(!message.isImportant());
        messages.set(position, message);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMessageRowClicked(int position) {
        // verify whether action mode is enabled or not
        // if enabled, change the row state to activated
        if (mAdapter.getSelectedItemCount() > 0) {
            enableActionMode(position);
        } else {
            // read the message which removes bold from the row
            Request message = messages.get(position);
            message.setRead(true);
            messages.set(position, message);
            mAdapter.notifyDataSetChanged();

            Toast.makeText(getContext(),
                    "Read: " + message.getProduct() + "\n" + message.getQuantity() + "T\n" + message.getUserId(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRowLongClicked(int position) {
        // long press is performed, enable action mode
        enableActionMode(position);
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
    /*private void fetchAllRequests() {
        // showing refresh animation before making http call
        _swipeRefreshSearchItem.setRefreshing(true);

        // show spinner
        showLoadingView(getResources().getString(R.string.app_spinner));

        // set retrofit api
        APIInterface api = APIClient.getClient(BaseActivity.ROOT_MDZ_API).create(APIInterface.class);

        // create basic authentication
        String auth = BasicAuth();

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


                            if (request.getType() == 1) {
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
                                if (request.getOffers().size() == 0) {
                                    req.setOffers(null);
                                } else {
                                    req.setOffers(request.getOffers());
                                }

                                // adding request to requests array
                                requestList.add(req);

                            }


                        }

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        _requestAdapter.notifyDataSetChanged();
                    }

                    // stopping swipe refresh
                    _swipeRefreshSearchItem.setRefreshing(false);

                    // next step : load all category list
                    fetchAllCategory();
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
                                hideLoadingView();
                                fetchAllRequests();
                            }
                        })
                        .show();
            }
        });
    }*/

    /**
     * Fetches mail messages by making HTTP request
     * url: https://api.androidhive.info/json/inbox.json
     */
    private void getInbox() {
        swipeRefreshLayout.setRefreshing(true);

        // show spinner
        showLoadingView(getResources().getString(R.string.app_spinner));

        // set retrofit api
        APIInterface api = APIClient.getClient(BaseActivity.ROOT_MDZ_API).create(APIInterface.class);

        // create basic authentication
        String auth = BasicAuth();

        // send query
        Call<JsonObject> call = api.getAllRequest(auth);

        // request
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    // clear the inbox
                    messages.clear();

                    // sort using result(s)
                    JsonArray filter = response.body().get("_embedded").getAsJsonObject().get("requests").getAsJsonArray();

                    // count data
                    if (filter.size() > 0) {

                        // the loop was performed to add colors to each message
                        for (int i = 0; i < filter.size(); i++) {
                            // class model to mapping gson
                            Request request = new Gson().fromJson(filter.get(i), Request.class);

                            // if request type == 1 (BUY)
                            if (request.getType() == 1) {
                                // new class model to set all values
                                Request req = new Request();

                                request.setColor(getRandomMaterialColor("400"));
                                messages.add(request);

                                mAdapter.notifyDataSetChanged();
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }
                    }
                    // next step : load all category list
                    // TODO: fetchAllCategory();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
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
                                hideLoadingView();
                                getInbox();
                            }
                        })
                        .show();
            }
        });
    }

    /**
     * chooses a random color from array xml
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

    private void enableActionMode(int position) {
        if (actionMode == null) {
            actionMode = getActivity().startActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        mAdapter.toggleSelection(position);
        int count = mAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
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
                        _categoryList = productTemplates;

                        // verif in LOG
                        Log.d("REQUESTS", "" + productTemplates);

                        // enable layout
                        swipeRefreshLayout.setVisibility(View.VISIBLE);

                        // hide spinner
                        hideLoadingView();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
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
                                fetchAllCategory();
                            }
                        })
                        .show();
            }
        });
    }

    /**
     * Display popup post new item
     */
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
        final MaterialBetterSpinner spinnerUnitType = (MaterialBetterSpinner) mView.findViewById(R.id.fragment_post_item_design_spinner_unit_type);
        final EditText editTextQuantity = (EditText) mView.findViewById(R.id.fragment_post_item_editText_quantity);
        final EditText editTextPrice = (EditText) mView.findViewById(R.id.fragment_post_item_editText_price);
        final EditText editTextProduct = (EditText) mView.findViewById(R.id.fragment_post_item_editText_product);

        // drop down element
        List<String> itemsName = new ArrayList<>();
        final List<String> itemsId = new ArrayList<>();
        for (int i = 0; i < _categoryList.size(); i++) {
            itemsName.add(_categoryList.get(i).getName());
            itemsId.add(_categoryList.get(i).getId());
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
                        String unitType = spinnerUnitType.getText().toString();
                        String price = editTextPrice.getText().toString();
                        String product = editTextProduct.getText().toString();
                        // product
                        if (product.isEmpty() || TextUtils.isEmpty(product)) {
                            editTextProduct.setError("Add item product");
                            editTextProduct.requestFocus();
                            return;
                        }
                        // category
                        if (category.isEmpty() || TextUtils.isEmpty(category) || category.contains("Choose")) {
                            spinnerCategory.setError("Select category");
                            spinnerCategory.requestFocus();
                            return;
                        }
                        // quantity
                        if (quantity.isEmpty() || TextUtils.isEmpty(quantity)) {
                            editTextQuantity.setError("Add item quantity");
                            editTextQuantity.requestFocus();
                            return;
                        }
                        // unitType
                        if (unitType.isEmpty() || TextUtils.isEmpty(unitType) || unitType.contains("Choose")) {
                            spinnerUnitType.setError("Select category");
                            spinnerUnitType.requestFocus();
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

                        //
                        APIInterface api = APIClient.getClient(BaseActivity.ROOT_MDZ_API).create(APIInterface.class);

                        // create basic authentication
                        String auth = BasicAuth();

                        //
                        BaseActivity baseActivity = (BaseActivity) getActivity();

                        //
                        RequestSend request = new RequestSend(baseActivity.getCurrentUser(getContext()).getId(), product, Request.UnitType.valueOf(unitType), Float.parseFloat(price), Request.Type.BUY, itemIdSelected,true);

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
                                    messages.clear();

                                    // refresh list
                                    onRefresh();
                                }

                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                // stopping swipe refresh
                                swipeRefreshLayout.setRefreshing(false);
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

    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_main_activity, menu);

            // disable swipe refresh if action mode is enabled
            swipeRefreshLayout.setEnabled(false);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_information:
                    // delete all the selected messages
                    deleteMessages();
                    mode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdapter.clearSelections();
            swipeRefreshLayout.setEnabled(true);
            actionMode = null;
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    mAdapter.resetAnimationIndex();
                    // mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    // deleting the messages from recycler view
    private void deleteMessages() {
        mAdapter.resetAnimationIndex();
        List<Integer> selectedItemPositions =
                mAdapter.getSelectedItems();
        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
            mAdapter.removeData(selectedItemPositions.get(i));
        }
        mAdapter.notifyDataSetChanged();
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}
