package com.example.pm_22689.api

import retrofit2.Call
import retrofit2.http.*


interface EndPoints {
    @GET("/api/users/{id}")
    fun getUserById(@Path("id") id: Int): Call<User>

    @FormUrlEncoded
    @POST("/myslim/api/userlogin")
    fun login(@Field("email") email: String?, @Field("password") password: String?): Call<OutputPost>
}
