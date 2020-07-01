package com.c0d3in3.finalproject.network

import com.c0d3in3.finalproject.network.model.PostModel
import com.c0d3in3.finalproject.network.model.UserModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

object FirebaseHandler {

    const val USERS_REF = "users"
    const val POSTS_REF = "posts"

    private val database = Firebase.firestore
    private val storage = Firebase.storage

    fun getDatabase() = database
}