package com.team.lezomadetana.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.team.lezomadetana.R;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.Arrays;
import java.util.List;

/**
 * Created by RaThierry on 04/09/2018.
 **/

public class FragmentSearchItem extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private EditText _editTextPost;
    private MaterialBetterSpinner _itemSpinner;
    private Button _buttonPost;
    private EditText _editTextSearch;
    private String itemNameSelected;
    private SwipeRefreshLayout _swipeRefreshSearchItem;
    private ListView _listViewSearchItem;

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
        View rootView = inflater.inflate(R.layout.fragment_search_item, container, false);

        // init view
        _editTextPost = (EditText) rootView.findViewById(R.id.fragment_search_item_text_view_post);
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
                        Toast.makeText(getContext(), "icon search clicked", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
                return false;
            }
        });
        _swipeRefreshSearchItem = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_search_item_swipe_refresh_layout_post);
        _swipeRefreshSearchItem.setColorSchemeColors(ContextCompat.getColor(getContext(), android.R.color.holo_red_dark));
        _swipeRefreshSearchItem.setOnRefreshListener(this);
        _listViewSearchItem = (ListView) rootView.findViewById(R.id.fragment_search_item_list_view_item);

        // initialize item data in spinner
        initSpinnerForItem();

        // return current view
        return rootView;
    }

    /**
     * View method
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_search_item_button_post:
                Toast.makeText(getContext(), "Your post: \n" + _editTextPost.getText().toString() + "\nItem selected is " + itemNameSelected, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * SwipeRefresh method
     */
    @Override
    public void onRefresh() {
        Toast.makeText(getContext(), "onRefresh", Toast.LENGTH_SHORT).show();
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
     * Initialize item'spinner (drop down list)
     */
    private void initSpinnerForItem() {
        // drop down element
        List<String> items = Arrays.asList(getResources().getStringArray(R.array.array_items));

        // set adapter for spinner
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, items);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        _itemSpinner.setAdapter(arrayAdapter);

        // event onClick
        _itemSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // item'clicked name
                itemNameSelected = parent.getItemAtPosition(position).toString();

                // showing clicked spinner item name and position
                Toast.makeText(parent.getContext(), "Item selected : " + itemNameSelected + "\n(at position n° " + position + ")", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}
