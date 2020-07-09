package com.c0d3in3.finalproject.network.model

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PostModel(
    var postAuthor: String? = "",
    var postComments: ArrayList<CommentModel>? = null,
    var postLikes: ArrayList<String>? = null,
    var postImage : String = "",
    val postDescription: String = "",
    var postId: String = "",
    val postLocation: String = "",
    var postTimestamp: Long = 0,
    val postTitle: String = "",
    val postType: Long = 0,
    @get:Exclude var postAuthorModel: UserModel? = null,
    @get:Exclude var isLast: Boolean = false) :Parcelable