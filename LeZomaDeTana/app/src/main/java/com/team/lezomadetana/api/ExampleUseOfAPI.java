package com.team.lezomadetana.api;

import android.os.Debug;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.team.lezomadetana.activity.BaseActivity;
import com.team.lezomadetana.model.receive.ProductTemplate;
import com.team.lezomadetana.model.receive.Request;
import com.team.lezomadetana.model.receive.UserCredentialResponse;


import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExampleUseOfAPI
{
    public static void getAllRequest(){

        APIInterface api = APIClient.getClient(BaseActivity.ROOT_MDZ_API).create(APIInterface.class);

        // create basic authentication
        String auth = BasicAuth();

        // send query
        Call<JsonObject> call = api.getAllRequest(auth);

        // request
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response)
            {
                if(response.code() == 200)
                {
                    JsonArray filter = response.body().get("_embedded").getAsJsonObject().get("requests").getAsJsonArray();
                    List<Request> requests = null;

                    if(filter.size()>0){
                        requests = new ArrayList<Request>();
                        for(int i=0;i<filter.size();i++){
                            Request request = new Gson().fromJson(filter.get(i),Request.class);
                            requests.add(request);

                        }
                        Log.d("REQUESTS",""+requests);

                    }


                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }


    public static void getAllProductTemplate(){

        APIInterface api = APIClient.getClient(BaseActivity.ROOT_MDZ_API).create(APIInterface.class);

        // create basic authentication
        String auth = BasicAuth();

        // send query
        Call<JsonObject> call = api.getAllProductTemplate(auth);

        // request
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response)
            {
                if(response.code() == 200)
                {
                    JsonArray filter = response.body().get("_embedded").getAsJsonObject().get("productTemplates").getAsJsonArray();
                    List<ProductTemplate> productTemplates = null;

                    if(filter.size()>0){
                        productTemplates = new ArrayList<ProductTemplate>();
                        for(int i=0;i<filter.size();i++){
                            ProductTemplate productTemplate = new Gson().fromJson(filter.get(i),ProductTemplate.class);
                            productTemplates.add(productTemplate);

                        }
                        Log.d("REQUESTS",""+productTemplates);

                    }


                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });




    }


        public static String BasicAuth() {
            return "Basic " + Base64.encodeToString((BaseActivity.APP_USER_NAME + ":" + BaseActivity.APP_PASSWORD).getBytes(), Base64.NO_WRAP);
        }



}
