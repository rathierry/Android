package com.team.lezomadetana.fragment;

import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.team.lezomadetana.R;
import com.team.lezomadetana.activity.BaseActivity;
import com.team.lezomadetana.adapter.BUYAdapter;
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

public class FragmentBuyItem extends BaseFragment implements
        View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, BUYAdapter.RequestAdapterListener {

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
    private List<ProductTemplate> _categoryList = new ArrayList<ProductTemplate>();
    private ListView listViewItem;
    private BUYAdapter buyAdapter;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflate the layout for this fragment or reuse the existing one
        rootView = getView() != null ? getView() :
                inflater.inflate(R.layout.fragment_search_item, container, false);

        // floating btn to add new item
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fragment_search_item_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowPostItemPopup();
            }
        });

        // refresh
        swipeRefreshItem = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_search_item_swipe_refresh_layout_post);
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
        listViewItem = (ListView) rootView.findViewById(R.id.fragment_search_item_list_view_item);
        buyAdapter = new BUYAdapter(getActivity(), requestList, this);
        listViewItem.setAdapter(buyAdapter);

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

    @Override
    public void onClick(View v) {
        //
    }

    @Override
    public void onRefresh() {
        isRefresh = true;
        fetchAllRequests();
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
        fetchAllRequests();
        showShortToast(getContext(), "onResume: MITADY ENTANA");
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
        buyAdapter.notifyDataSetChanged();

        Toast.makeText(getContext(), "Read: " + request.getUserId(), Toast.LENGTH_SHORT).show();
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
        swipeRefreshItem.setRefreshing(true);

        if (isRefresh) {
            hideLoadingView();
        } else {
            showLoadingView(getResources().getString(R.string.app_spinner));
        }

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

                                // offer
                                if (request.getOffers().size() == 0) {
                                    req.setOffers(null);
                                } else {
                                    req.setOffers(request.getOffers());
                                }

                                // generate a random color
                                req.setColor(getRandomMaterialColor("400"));

                                // adding request to requests array
                                requestList.add(req);
                            }
                        }

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        buyAdapter.notifyDataSetChanged();
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
                                hideLoadingView();
                                fetchAllRequests();
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
                        _categoryList = productTemplates;

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