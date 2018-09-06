package com.team.lezomadetana.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.team.lezomadetana.R;
import com.team.lezomadetana.activity.BaseActivity;
import com.team.lezomadetana.model.receive.UserCredentialResponse;

public class FragmentChat extends Fragment
{
    TextView madCoin;
    TextView madCointText;
    ImageView telma;
    ImageView orange;
    ImageView airtel;

    SharedPreferences playerPref;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        playerPref = getActivity().getPreferences(Context.MODE_PRIVATE);

        UserCredentialResponse user = new Gson().fromJson( playerPref.getString(BaseActivity.PREFS_KEY_USER,"NULL"),UserCredentialResponse.class);

        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        madCoin = view.findViewById(R.id.madcoin_text);
        madCointText = view.findViewById(R.id.madcoin_text);
        telma = view.findViewById(R.id.m_vola_item);
       orange = view.findViewById(R.id.orange_money_item);
        airtel = view.findViewById(R.id.airtel_money_item);

        return inflater.inflate(R.layout.fragment_chat, container, false);
    }


    public void refreshMadcoin()
    {

    }


}
