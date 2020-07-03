package com.c0d3in3.finalproject.network.model

import android.os.Parcelable
import com.google.firebase.firestore.auth.User
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CommentModel(
    var commentTimestamp: Long = 0,
    var commentAuthor : UserModel? = null,
    var comment : String = "",
    var commentLikes : ArrayList<UserModel>? = null,
    var replies : ArrayList<CommentModel>? = null) : Parcelable{}


/*
    var commentTimestamp : Long = 0
    var commentAuthor = UserModel()
    var comment = ""
    var commentLikes = ""
    var replies = arrayListOf<CommentModel>()
 */