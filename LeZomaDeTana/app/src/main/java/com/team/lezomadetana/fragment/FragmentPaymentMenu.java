package com.team.lezomadetana.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.team.lezomadetana.R;
import com.team.lezomadetana.activity.BaseActivity;

/**
 * Created by RaThierry on 15/09/2018.
 **/

public class FragmentPaymentMenu extends BaseFragment implements View.OnClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private View rootView;
    private BaseActivity activity;

    private RelativeLayout layout_payment_charge;
    private RelativeLayout layout_payment_give_money;
    private RelativeLayout layout_payment_send_money;
    private RelativeLayout layout_payment_get_money;

    // ===========================================================
    // Constructors
    // ===========================================================

    public FragmentPaymentMenu() {
        // Required empty public constructor
    }

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
        // inflate the layout for this fragment or reuse the existing one
        rootView = getView() != null ? getView() :
                inflater.inflate(R.layout.fragment_payment_menu, container, false);

        // current activity
        activity = ((BaseActivity) getActivity());

        // set view
        layout_payment_charge = (RelativeLayout) rootView.findViewById(R.id.fragment_payment_charge);
        layout_payment_give_money = (RelativeLayout) rootView.findViewById(R.id.fragment_payment_give_money);
        layout_payment_send_money = (RelativeLayout) rootView.findViewById(R.id.fragment_payment_send_money);
        layout_payment_get_money = (RelativeLayout) rootView.findViewById(R.id.fragment_payment_get_money);

        // event onClick
        layout_payment_charge.setOnClickListener(this);
        layout_payment_give_money.setOnClickListener(this);
        layout_payment_send_money.setOnClickListener(this);
        layout_payment_get_money.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_payment_charge:
                switchToMenuPaymentFragment(new FragmentPaymentCharge());
                break;
            case R.id.fragment_payment_give_money:
                switchToMenuPaymentFragment(new FragmentPaymentGiveMoney());
                break;
            case R.id.fragment_payment_send_money:
                switchToMenuPaymentFragment(new FragmentPaymentSendMoney());
                break;
            case R.id.fragment_payment_get_money:
                switchToMenuPaymentFragment(new FragmentPaymentGetMoney());
                break;
            default:
                break;
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

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}
