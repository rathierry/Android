package com.team.lezomadetana.api;

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
