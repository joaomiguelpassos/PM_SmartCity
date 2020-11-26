package com.example.pm_22689.api

import retrofit2.Call
import retrofit2.http.*


interface EndPoints {
    @GET("/api/users/{id}")
    fun getUserById(@Path("id") id: Int): Call<User>

    @FormUrlEncoded
    @POST("/myslim/api/adduser")
    fun addUser(@Field("id") id: Int?, @Field("name") name: String?, @Field("email") email: String?, @Field("password") password: String?, @Field("address") address: String?): Call<OutputPost>
}
