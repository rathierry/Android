package com.team.lezomadetana.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.team.lezomadetana.R;
import com.team.lezomadetana.api.Client;
import com.team.lezomadetana.api.Service;
import com.team.lezomadetana.model.receive.Request;
import com.team.lezomadetana.model.receive.User;
import com.team.lezomadetana.model.receive.UserCredentialResponse;
import com.team.lezomadetana.view.UrlImageView;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.List;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserUpdateInfoActivity extends BaseActivity {

    RelativeLayout _layout;
    UrlImageView _avatarImage;
    EditText _nameText;
    EditText _firstNameText;
    EditText _phoneText;
    MaterialBetterSpinner _regionSpinner;
    EditText _addressText;
    EditText _passwordText;
    EditText _rePasswordText;
    Button _btnSignUp;

    // input values
    private String name;
    private String firstName;
    private String phone;
    private String region;
    private String address;
    private String password;
    private String rePassword;
    private Bitmap _bitmapImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_user_update_info);
        _layout = findViewById(R.id.user_update_relativeLayout);
        _avatarImage = findViewById(R.id.user_update_imageView_logo);
        _nameText = findViewById(R.id.user_update_input_name);
        _firstNameText = findViewById(R.id.user_update_input_firstname);
        _phoneText = findViewById(R.id.user_update_input_phone);
        _regionSpinner = findViewById(R.id.user_update_material_design_spinner_region);
        _addressText = findViewById(R.id.user_update_input_address);
        _passwordText = findViewById(R.id.user_update_input_password);
        _rePasswordText = findViewById(R.id.user_update_input_re_password);
        _btnSignUp = findViewById(R.id.user_update_btn_validate);

        showDefaultValue();


    }



    private void showDefaultValue()
    {
        showLoadingView("Miandry ...");

        Service api = Client.getClient(BaseActivity.ROOT_MDZ_USER_API).create(Service.class);

        // create basic authentication
        String auth = BasicAuth();




        // send query

        final UserCredentialResponse cUser = getCurrentUser(this);
        Call<JsonObject> call = api.getUserById(auth,cUser.getId());
        // request
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response)
            {
                if(response.code() == 200)
                {
                    JsonObject jUser = response.body().getAsJsonObject();
                    User user = new Gson().fromJson(jUser,User.class);
                    Log.d("pouaaa",user.toString());

                    _nameText.setText(user.getName());
                    //_firstNameText;
                    _phoneText.setText(user.getUsername());
                    _regionSpinner.setText(user.getRegion());

                    _passwordText.setText(user.getPassword());
                    _rePasswordText.setText(user.getPassword());
                    hideLoadingView();

                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }
}
