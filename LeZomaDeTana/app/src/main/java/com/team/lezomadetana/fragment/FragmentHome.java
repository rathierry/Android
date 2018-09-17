package com.team.lezomadetana.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.team.lezomadetana.R;
import com.team.lezomadetana.activity.BaseActivity;
import com.team.lezomadetana.adapter.TabsPagerAdapter;

/**
 * Created by RaThierry on 01/09/2018.
 **/

public class FragmentHome extends BaseFragment {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private BaseActivity baseActivity;
    private View rootView;
    private AppBarLayout appBarLayout;
    private TabLayout tabLayout;
    private ShimmerFrameLayout mShimmerViewContainer;
    private ViewPager viewPager;
    private TabsPagerAdapter viewPagerAdapter;

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
        // inflate the layout for this fragment or reuse the existing one
        rootView = getView() != null ? getView() :
                inflater.inflate(R.layout.fragment_home, container, false);

        // current activity
        baseActivity = ((BaseActivity) getActivity());

        // toolBar title
        baseActivity.getSupportActionBar().setTitle(getResources().getString(R.string.fragment_buy_toolBar_title));
        //baseActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // init view
        appBarLayout = (AppBarLayout) rootView.findViewById(R.id.appbar);
        tabLayout = (TabLayout) rootView.findViewById(R.id.result_tabs);
        mShimmerViewContainer = (ShimmerFrameLayout) rootView.findViewById(R.id.shimmer_view_container);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        viewPagerAdapter = new TabsPagerAdapter(getChildFragmentManager());

        // start placeholder animation
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();

        // wait few second
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // set tab
                viewPagerAdapter.addFragment(new FragmentBuyItem(), getResources().getString(R.string.fragment_buy_tab_layout_search_item));
                viewPagerAdapter.addFragment(new FragmentSellItem(), getResources().getString(R.string.fragment_buy_tab_layout_available_item));

                // set adapter
                viewPager.setAdapter(viewPagerAdapter);
                tabLayout.setupWithViewPager(viewPager);

                // equal all tab rows width
                EqualWidthTabRows();

                // turn appBarLayout to visible
                appBarLayout.setVisibility(View.VISIBLE);

                // hide placeholder animation
                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.INVISIBLE);
            }
        }, 1000);

        // return current view
        return rootView;
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // here your action
        }
        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public void onResume() {
        super.onResume();
        if (!getUserVisibleHint()) {
            return;
        }
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                appBarLayout.setVisibility(View.VISIBLE);
            }
        }, 1000);
    }

    @Override
    public void onPause() {
        appBarLayout.setVisibility(View.INVISIBLE);
        mShimmerViewContainer.stopShimmerAnimation();
        mShimmerViewContainer.setVisibility(View.INVISIBLE);
        super.onPause();
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
     * To allow equal width for each tab, while (TabLayout.MODE_SCROLLABLE)
     */
    private void EqualWidthTabRows() {
        ViewGroup slidingTabStrip = (ViewGroup) tabLayout.getChildAt(0);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            View tab = slidingTabStrip.getChildAt(i);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) tab.getLayoutParams();
            layoutParams.weight = 1;
            tab.setLayoutParams(layoutParams);
        }
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}