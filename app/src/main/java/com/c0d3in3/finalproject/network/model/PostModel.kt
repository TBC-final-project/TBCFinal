package com.c0d3in3.finalproject.network.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PostModel(
    var postAuthor: String? = null,
    var postComments: ArrayList<CommentModel>? = null,
    var postLikes: ArrayList<String>? = null,
    val postDescription: String = "",
    var postId: String = "",
    val postLocation: String = "",
    var postTimestamp: Long = 0,
    val postTitle: String = "",
    val postType: Long = 0,
    var postAuthorModel: UserModel? = null) :Parcelable