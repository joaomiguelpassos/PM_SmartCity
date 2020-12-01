package com.example.pm_22689.api

import retrofit2.Call
import retrofit2.http.*


interface EndPoints {
    /**
     * Gets all markers from WS REST
     */
    @GET("/api/markers")
    fun getMarkers(): Call<Marker>

    /**
     * Sends email and password to DB in order to check if they match a record
     */
    @FormUrlEncoded
    @POST("/myslim/api/userlogin")
    fun login(@Field("email") email: String?, @Field("password") password: String?): Call<OutputPost>
}
