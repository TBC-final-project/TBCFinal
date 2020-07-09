package com.c0d3in3.finalproject.network.model

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.auth.User
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CommentModel(
    var commentTimestamp: Long = 0,
    var commentAuthor : String? = null,
    var comment : String = "",
    var commentLikes : ArrayList<String>? = null,
    var replies : ArrayList<CommentModel>? = null,
    @get:Exclude var commentAuthorModel: UserModel? = null) : Parcelable