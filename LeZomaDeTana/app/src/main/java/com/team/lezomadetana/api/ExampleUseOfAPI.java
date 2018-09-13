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
import com.team.lezomadetana.model.receive.Transaction;
import com.team.lezomadetana.model.receive.UserCredentialResponse;
import com.team.lezomadetana.model.send.RequestSend;
import com.team.lezomadetana.model.send.TransactionAriaryJeton;
import com.team.lezomadetana.model.send.TransactionSend;


import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExampleUseOfAPI
{
    // FETCH
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

    // SEARCH

    public static void getSearchRequest(){

        APIInterface api = APIClient.getClient(BaseActivity.ROOT_MDZ_API).create(APIInterface.class);

        // create basic authentication
        String auth = BasicAuth();

        Map<String,String> map = new HashMap<String,String>();

        map.put("product","ovy");
        // send query
        Call<JsonObject> call = api.searchRequest(auth,map);

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


    // TEMPLATE
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


    // POST item
    public static void sendRequest(){

        APIInterface api = APIClient.getClient(BaseActivity.ROOT_MDZ_API).create(APIInterface.class);

        // create basic authentication
        String auth = BasicAuth();
        JsonObject req = new JsonObject();
        req.addProperty("userId","a");
        Gson json = new Gson();

        //String req = "";
        RequestSend request = new RequestSend( "819837c3-5ca5-4987-ba34-9855288a00f6","ovy", Request.UnitType.UNIT,5f,Request.Type.BUY,"64774d43-e303-4604-bd28-900a9bb0695a",true);

        // send query
        Call<Void> call = api.sendRequest(auth,request);

        // request
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response)
            {
                if(response.code() == 201)
                {
                    Log.d("aaa",""+response.body());

                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });




    }


    // Transation Jeton2Jegon
    // ecran Handefa vola
    public static void sendTransaction(){

        APIInterface api = APIClient.getClient(BaseActivity.ROOT_MDZ_USER_API).create(APIInterface.class);

        // create basic authentication
        String auth = BasicAuth();

        TransactionSend transactionSend = new TransactionSend();
        transactionSend.setSenderId("70d4b023-f790-4060-8cdd-e171deb5f017");
        transactionSend.setRecipientId("ce91ea8a-e18b-467f-82a1-7fc041d7535b");
        transactionSend.setAmount(1f);
        transactionSend.setDescription("Ito ny volan ilay ovy");
        transactionSend.setStatus(TransactionSend.Status.PENDING);


        // send query
        Call<Void> call = api.commitTransaction(auth,transactionSend);

        // request
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response)
            {
                if(response.code() == 201)
                {

                    Log.d("Payment","METY lesy dada");
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });




    }


    // Transation Ariary2Jegon
    // ecran Hampiditra vola
    public static void sendTransactionAr2Jt(){

        APIInterface api = APIClient.getClient(BaseActivity.ROOT_MDZ_USER_API).create(APIInterface.class);

        // create basic authentication
        String auth = BasicAuth();

        TransactionAriaryJeton transactionSend = new TransactionAriaryJeton();
        transactionSend.setUserId("70d4b023-f790-4060-8cdd-e171deb5f017");
        transactionSend.setPhone("0346655762");
        transactionSend.setAmount(1f);
        transactionSend.setOperator(TransactionAriaryJeton.Operator.TELMA);

        transactionSend.setType(TransactionAriaryJeton.Type.DEPOSIT);
        // send query
        Call<Void> call = api.commitTransactionAriary2Jeton(auth,transactionSend);

        // request
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response)
            {
                if(response.code() == 201)
                {

                    Log.d("Payment","METY lesy dada");
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });




    }


    // Transation Jegon2Ariary
    // Hamoaka vola
    public static void sendTransactionJt2Ar(){

        APIInterface api = APIClient.getClient(BaseActivity.ROOT_MDZ_USER_API).create(APIInterface.class);

        // create basic authentication
        String auth = BasicAuth();

        TransactionAriaryJeton transactionSend = new TransactionAriaryJeton();
        transactionSend.setUserId("70d4b023-f790-4060-8cdd-e171deb5f017");
        transactionSend.setPhone("0346655762");
        transactionSend.setAmount(1f);
        transactionSend.setOperator(TransactionAriaryJeton.Operator.TELMA);

        transactionSend.setType(TransactionAriaryJeton.Type.WITHDRAWAL);
        // send query
        Call<Void> call = api.commitTransactionAriary2Jeton(auth,transactionSend);

        // request
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response)
            {
                if(response.code() == 201)
                {

                    Log.d("Payment","METY lesy dada");
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });




    }


    public void ExplicationEnum()
    {
        //variable du type enum UnitType
        Request.UnitType unitType;

        //affecter une valeur à la variable
        unitType = Request.UnitType.KG;

        //si on veut avoir toutes les valeurs possible de UnitType dans un tableu de string
        // ito ilay ho affichena amin ilay select
        String [] unityTypeValues = BaseActivity.getNames(Request.UnitType.class);

        //affecter un enum par un string dans sa valeur possible
        unitType = Request.UnitType.valueOf("KG"); // mitovy amin oe unitType = UnitType.KG;

        //affecter un enum par un entier correspondant à l'index dans sa valeur pssble
        unitType = Request.UnitType.values()[0]; // mitovy amin oe unitType = UnitType.KG satria ilay KG=0;





    }




        public static String BasicAuth() {
            return "Basic " + Base64.encodeToString((BaseActivity.APP_USER_NAME + ":" + BaseActivity.APP_PASSWORD).getBytes(), Base64.NO_WRAP);
        }



}
