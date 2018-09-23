package com.team.lezomadetana.fragment;

import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.team.lezomadetana.R;
import com.team.lezomadetana.activity.BaseActivity;
import com.team.lezomadetana.adapter.ListOfferAdapter;
import com.team.lezomadetana.api.Client;
import com.team.lezomadetana.api.Service;
import com.team.lezomadetana.model.receive.Offer;
import com.team.lezomadetana.model.receive.Request;
import com.team.lezomadetana.model.send.OfferSend;
import com.team.lezomadetana.utils.CircleTransform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.team.lezomadetana.activity.BaseActivity.BasicAuth;

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
    private String selectedItemOfUnitType;

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
        //((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.fragment_list_offer_toolbar_title));

        // initialize view
        ImageView imageView = (ImageView) rootView.findViewById(R.id.icon_profile);
        TextView from = (TextView) rootView.findViewById(R.id.from);
        TextView txt_primary = (TextView) rootView.findViewById(R.id.txt_primary);
        TextView txt_secondary = (TextView) rootView.findViewById(R.id.txt_secondary);
        TextView iconText = (TextView) rootView.findViewById(R.id.icon_text);
        TextView txt_footer = (TextView) rootView.findViewById(R.id.txt_footer);
        txt_no_list = (TextView) rootView.findViewById(R.id.no_list_offer);
        Button buttonAnswer = (Button) rootView.findViewById(R.id.answer);

        // loading
        showLoadingView(getResources().getString(R.string.app_spinner));

        // fetch data in serializable
        request = (Request) getArguments().getSerializable("request");

        // initialize request's data
        String totalOffer = "";
        if (request.getOffers() != null) {
            totalOffer = String.valueOf(request.getOffers().size());
        }
        String userName = "";
        String userImageUrl = "";
        if (request.getUser() != null) {
            userName = request.getUser().getName();
            userImageUrl = request.getUser().getProfileImageUrl();
            // verification
            if (!TextUtils.isEmpty(userImageUrl)) {
                Glide.with(getActivity())
                        .load(userImageUrl)
                        .thumbnail(0.5f)
                        .crossFade()
                        .transform(new CircleTransform(getActivity()))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.mipmap.ic_launcher_round)
                        .into(imageView);
                imageView.setColorFilter(null);
                iconText.setVisibility(View.GONE);
            } else {
                imageView.setImageResource(R.drawable.bg_circle);
                imageView.setColorFilter(getRandomMaterialColor("400"));
                iconText.setText(request.getUser().getName().substring(0, 1));
                // displaying the first letter of From in icon text
                iconText.setVisibility(View.VISIBLE);
            }
        }
        String itemImageUrl = "";
        if (request.getAssetUrls().size() != 0) {
            itemImageUrl = request.getAssetUrls().get(0);
        }
        String itemUnitType = request.getUnitType().name();
        String itemPrice = String.valueOf(request.getPrice());
        String itemProduct = request.getProduct();
        String itemQuantity = request.getQuantity().toString();
        String itemType = request.getType().name();

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
                showAnswerOfferPopup(request);
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
        listOfferAdapter = new ListOfferAdapter(getActivity(), offerList, this);
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
        Toast.makeText(getContext(), "onResume", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRefresh() {
        super.onResume();
        swipeRefreshItem.setRefreshing(true);
        fetchAllOfferData();
        Toast.makeText(getContext(), "onRefresh", Toast.LENGTH_SHORT).show();
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
    // Private Methods
    // ===========================================================

    /**
     * Fetch all offers
     */
    private void fetchAllOfferData() {
        // set retrofit api
        Service api = Client.getClient(BaseActivity.ROOT_MDZ_API).create(Service.class);

        // create basic authentication
        String auth = BasicAuth();

        Map<String, String> map = new HashMap<String, String>();
        map.put("id", request.getId());

        // send query
        Call<JsonObject> call = api.getListOfferInRequest(auth, map);

        // request
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                // verification
                if (response.body() == null) {
                    Toast.makeText(getContext(), getResources().getString(R.string.app_response_body_null), Toast.LENGTH_LONG).show();
                } else if (response.code() == 200) {
                    // array filter
                    JsonObject filter = response.body().get("_embedded").getAsJsonObject().get("requests").getAsJsonArray().get(0).getAsJsonObject();

                    // clear list request
                    offerList.clear();

                    // class model to mapping gson
                    Request request = new Gson().fromJson(filter, Request.class);

                    // verification
                    if (request.getOffers().size() != 0) {
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

                        // hide alert text
                        txt_no_list.setVisibility(View.INVISIBLE);
                    } else {
                        txt_no_list.setVisibility(View.VISIBLE);
                    }

                    // hide swipe refresh
                    swipeRefreshItem.setRefreshing(false);
                    hideLoadingView();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                // stopping swipe refresh
                swipeRefreshItem.setRefreshing(false);
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
                                showLoadingView(getResources().getString(R.string.app_spinner));
                                fetchAllOfferData();
                            }
                        })
                        .show();
            }
        });
    }

    /**
     * Display popup post new offer
     */
    private void showAnswerOfferPopup(final Request request) {
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

                        //
                        Service api = Client.getClient(BaseActivity.ROOT_MDZ_API).create(Service.class);

                        // create basic authentication
                        String auth = BasicAuth();

                        //
                        BaseActivity baseActivity = (BaseActivity) getActivity();

                        showShortToast(baseActivity, "requestId : " + request.getId());

                        OfferSend offerSend = new OfferSend(request.getId(), baseActivity.getCurrentUser(getContext()).getId(), Integer.parseInt(quantity), Request.UnitType.valueOf(unitType), Float.parseFloat(price), true);

                        // send query
                        Call<Void> call = api.sendOffer(auth, offerSend);

                        // request
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.code() == 201) {
                                    dialog.dismiss();
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
                                // stopping swipe refresh
                                swipeRefreshItem.setRefreshing(false);
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
