package com.team.lezomadetana.api;

import android.net.wifi.hotspot2.pps.Credential;

import com.google.gson.JsonObject;
import com.team.lezomadetana.model.receive.UserCredentialResponse;
import com.team.lezomadetana.model.send.RequestSend;
import com.team.lezomadetana.model.send.UserCheckCredential;
import com.team.lezomadetana.model.send.UserRegisterSend;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Created by team on 29/08/2018.
 **/

public interface APIInterface {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods from SuperClass
    // ===========================================================

    // ===========================================================
    // Methods for Interfaces
    // ===========================================================

    /*@GET("api/login")
    Call<Player> signInPlayer(@Query("phone") String ph, @Query("password") String pw);

    @FormUrlEncoded
    @POST("api/signup")
    Call<Player> createPlayer(@Field("firstName") String fn, @Field("lastName")
            String ln, @Field("phone") String ph, @Field("password") String pw);

    @GET("api/leaderboard")
    Call<List<Rank>> getRank();

    @GET("api/getQuestions")
    Call<QuestionsResponse> getQuestionsInformations(@Query("idUser") String idUser);

    @POST("api/insertSeenQuestions")
    Call<Void> postRightAnswer(@Query("idQuestion") int idQuestion, @Query("idUser") int idUser, @Query("response") int response);

    @POST("api/insertSeenQuestions")
    Call<Void> postWrongAnswer(@Query("idQuestion") int idQuestion, @Query("idUser") int idUser, @Query("response") int response, @Query("idPubGM") String idPubGM);

    @POST("api/postGame")
    Call<Void> postScore(@Query("idUser") int idUser, @Query("duration") String duration);*/

    // user: login
    @POST("checkCredentials/")
    Call<UserCredentialResponse> checkCredential(@Header("Authorization") String auth, @Body UserCheckCredential user);

    // user: register
    @POST("rest/users/")
    Call<ResponseBody> userRegisterJSON(@Header("Authorization") String auth, @Body UserRegisterSend user);

    // search item: list request
    @GET("rest/requests/")
    Call<JsonObject> getAllRequest(@Header("Authorization") String auth);


    @GET("rest/requests/search/advancedSearch")
    Call<JsonObject> searchRequest(@Header("Authorization") String auth, @QueryMap Map<String,String> map);

    @POST("rest/requests/")
    Call<Void> sendRequest(@Header("Authorization") String auth,@Body RequestSend req);



    @GET("rest/productTemplates")
    Call<JsonObject> getAllProductTemplate(@Header("Authorization") String auth);


    @GET("/rest/userWallets")
    Call<JsonObject> getAllWallet(@Header("Authorization") String auth);



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
