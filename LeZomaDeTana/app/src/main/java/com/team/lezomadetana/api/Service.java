package com.team.lezomadetana.api;

import com.google.gson.JsonObject;
import com.team.lezomadetana.model.receive.Message;

import com.team.lezomadetana.model.receive.Transaction;
import com.team.lezomadetana.model.receive.User;
import com.team.lezomadetana.model.receive.UserCredentialResponse;
import com.team.lezomadetana.model.send.OfferSend;
import com.team.lezomadetana.model.send.RequestSend;
import com.team.lezomadetana.model.send.TransactionAriaryJeton;
import com.team.lezomadetana.model.send.TransactionSend;
import com.team.lezomadetana.model.send.UserCheckCredential;
import com.team.lezomadetana.model.send.UserRegisterSend;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Created by team on 29/08/2018.
 **/

public interface Service {

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

    // user: profile
    @GET("/rest/users/{userId}")
    Call<JsonObject> getUserById(@Header("Authorization") String auth, @Path(value = "userId", encoded = true) String userId);

    // user: register
    @POST("rest/users/")
    Call<ResponseBody> userRegisterJSON(@Header("Authorization") String auth, @Body UserRegisterSend user);

    @POST("rest/users/")
    Call<ResponseBody> userUpdateJSON(@Header("Authorization") String auth, @Body User user);

    // list request
    @GET("rest/requests")
    Call<JsonObject> getAllRequest(@Header("Authorization") String auth);


    @GET("rest/requests")
    Call<JsonObject> getAllRequest(@Header("Authorization") String auth,@QueryMap Map<String, Integer> map);

    // list offer
    @GET("rest/requests")
    Call<JsonObject> getListOfferInRequest(@Header("Authorization") String auth, @QueryMap Map<String, String> map);

    // search advanced
    @GET("rest/requests/search/advancedSearch")
    Call<JsonObject> searchRequest(@Header("Authorization") String auth, @QueryMap Map<String, String> map);

    // new post
    @POST("rest/requests/")
    Call<Void> sendRequest(@Header("Authorization") String auth, @Body RequestSend req);

    // list template (category)
    @GET("rest/productTemplates")
    Call<JsonObject> getAllProductTemplate(@Header("Authorization") String auth);

    // list offer
    @POST("rest/offers")
    Call<Void> sendOffer(@Header("Authorization") String auth, @Body OfferSend offer);

    // user: ???
    @GET("/rest/userWallets")
    Call<JsonObject> getAllWallet(@Header("Authorization") String auth);

    //transaction jeton -> jeton
    @POST("/rest/transactionRequests")
    Call<Void> commitTransaction(@Header("Authorization") String auth, @Body TransactionSend transactionSend);


    //transaction ariary -> jeton
    @POST("/rest/mobileBankingRequests")
    Call<Void> commitTransactionAriary2Jeton(@Header("Authorization") String auth, @Body TransactionAriaryJeton transactionSend);

    @GET("inbox.json")
    Call<List<Message>> getInbox();

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
