package com.team.lezomadetana.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.team.lezomadetana.R;
import com.team.lezomadetana.adapter.TabsPagerAdapter;

public class FragmentBusiness extends Fragment {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private TabLayout tabLayout;

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
        View view = inflater.inflate(R.layout.fragment_business, container, false);

        // setting ViewPager for each Tabs
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        // set Tabs inside Toolbar
        tabLayout = (TabLayout) view.findViewById(R.id.result_tabs);
        tabLayout.setupWithViewPager(viewPager);

        // equal all tab rows widt
        EqualWidthTabRows();

        // return current view
        return view;
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

    // set view pager
    private void setupViewPager(ViewPager viewPager) {
        /**
         * Add Fragments to Tabs
         */
        TabsPagerAdapter adapter = new TabsPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new FragmentSearchItem(), getResources().getString(R.string.fragment_business_tab_layout_search_item));
        adapter.addFragment(new FragmentAvailableItem(), getResources().getString(R.string.fragment_business_tab_layout_available_item));
        viewPager.setAdapter(adapter);
    }

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
