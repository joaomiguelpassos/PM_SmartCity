package com.example.pm_22689.api

data class OutputPost(
    val status: Boolean,
    val MSG: String,
    val data: User
)

data class User(
    val id: Int,
    val email: String,
    val password: String
)

data class Marker(
    val idUser: Int,
    val latitude: String,
    val longitude: String,
    val tipo: String,
    val id: Int
)

data class ResponseDelete(
    val status: Boolean,
    val MSG: String
)