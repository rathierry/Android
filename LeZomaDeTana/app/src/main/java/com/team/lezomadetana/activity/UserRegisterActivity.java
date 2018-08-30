package com.team.lezomadetana.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.team.lezomadetana.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by team on 28/08/2018.
 **/

public class UserRegisterActivity extends BaseActivity implements OnItemSelectedListener {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    @BindView(R.id.user_register_spinner_occupation)
    Spinner _spinnerOccupation;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        ButterKnife.bind(this);
        initSpinnerOccupation();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransitionExit();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // on selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // showing selected spinner item
        Toast.makeText(parent.getContext(), "Asa atao: " + item, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(parent.getContext(), "Inona no asa ataonao ?", Toast.LENGTH_LONG).show();
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

    private void initSpinnerOccupation() {
        // click listener
        _spinnerOccupation.setOnItemSelectedListener(this);

        // drop down element
        List<String> occupation = new ArrayList<String>();
        occupation.add("Mpiompy");
        occupation.add("Mpivaro-mandeha");
        occupation.add("Mpamboly");
        occupation.add("Mpihaza");
        occupation.add("Mpitrandraka volamena/vato soa");
        occupation.add("Mpandrafitra");

        // creating adapter for spinner
        // (1) android.R.layout.simple_spinner_item
        // (1.a) android.R.layout.simple_spinner_dropdown_item
        // (2) android.R.layout.simple_dropdown_item_1line
        // (2.a) android.R.layout.
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, occupation);

        // drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        _spinnerOccupation.setAdapter(dataAdapter);
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}
