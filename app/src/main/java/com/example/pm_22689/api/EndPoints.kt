package com.example.pm_22689.api

import retrofit2.Call
import retrofit2.http.*


interface EndPoints {
    /**
     * Gets all markers from WS REST
     */
    @GET("/myslim/api/markers")
    fun getMarkers(): Call<List<Marker>>

    /**
     * Sends email and password to DB in order to check if they match a record
     */
    @FormUrlEncoded
    @POST("/myslim/api/userlogin")
    fun login(@Field("email") email: String?, @Field("password") password: String?): Call<OutputPost>

    /**
     * Sends coords to insert on DB
     */
    @FormUrlEncoded
    @POST("/myslim/api/addMarker")
    fun saveMarker(
        @Field("idUser") idUser: Int,
        @Field("latitude") lat: String,
        @Field("longitude") lon: String,
        @Field("tipo") tipo: String,
        @Field("descr")descr: String?
    ): Call<Marker>

    /**
     * Deletes a marker with a giver ID
     */
    @GET("/myslim/api/deletemarker/{id}")
    fun deleteMarker(@Path("id") id: Int): Call<ResponseDelete>
}
