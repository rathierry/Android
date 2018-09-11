package com.team.lezomadetana.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.team.lezomadetana.R;
import com.team.lezomadetana.adapter.TabsPagerAdapter;

public class FragmentHome extends BaseFragment {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private View rootView;
    private TabLayout tabLayout;
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
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // toolBar title
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Entana");

        // init view
        tabLayout = (TabLayout) rootView.findViewById(R.id.result_tabs);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        viewPagerAdapter = new TabsPagerAdapter(getChildFragmentManager());

        // set tab
        viewPagerAdapter.addFragment(new FragmentBuyItem(), getResources().getString(R.string.fragment_buy_tab_layout_search_item));
        viewPagerAdapter.addFragment(new FragmentSellItem(), getResources().getString(R.string.fragment_buy_tab_layout_available_item));

        // set adapter
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        // equal all tab rows width
        EqualWidthTabRows();

        // return current view
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!getUserVisibleHint()) {
            return;
        }
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