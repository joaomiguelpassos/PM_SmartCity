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