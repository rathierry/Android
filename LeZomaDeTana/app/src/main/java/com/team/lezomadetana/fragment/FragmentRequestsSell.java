package com.team.lezomadetana.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.team.lezomadetana.R;
import com.team.lezomadetana.activity.BaseActivity;
import com.team.lezomadetana.activity.MainActivity;
import com.team.lezomadetana.adapter.RequestsAdapter;
import com.team.lezomadetana.model.receive.ProductTemplate;
import com.team.lezomadetana.model.receive.Request;
import com.team.lezomadetana.utils.PaginationScrollListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RaThierry on 21/09/2018.
 */

public class FragmentRequestsSell extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,
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

    private String itemNameSelected;
    private String itemIdSelected;
    private String itemUnitTypeSelected;

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
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onIconClicked(int position, Request request) {
        // TODO
    }

    @Override
    public void onMessageRowClicked(int position, Request request) {
        // TODO
    }

    @Override
    public void onButtonAnswerClicked(int position, Request request) {
        // TODO
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
        // TODO
    }

    /**
     * Display popup post new item
     */
    private void addNewRequest() {
        // TODO
    }

    /**
     * Display popup search request
     */
    private void searchRequest() {
        // TODO
        showLongToast(getContext(), "searchRequest");
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}
