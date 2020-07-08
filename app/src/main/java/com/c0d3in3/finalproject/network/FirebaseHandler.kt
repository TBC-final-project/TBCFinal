package com.c0d3in3.finalproject.network

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