package com.c0d3in3.finalproject.network

import android.util.Log.d
import com.c0d3in3.finalproject.App
import com.c0d3in3.finalproject.bean.NotificationModel
import com.google.firebase.auth.FirebaseAuth.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

object FirebaseHandler {

    const val USERS_REF = "users"
    const val POSTS_REF = "posts"
    const val STORIES_REF = "stories"

    private val database = Firebase.firestore
    private val storage = Firebase.storage
    private val auth = getInstance()

    fun getDatabase() = database

    fun getAuth() = auth

    fun getStorage() = storage

}