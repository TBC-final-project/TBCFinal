package com.c0d3in3.finalproject.network.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PostModel(
    var postAuthor: UserModel? = null,
    val postComments: ArrayList<CommentModel>? = null,
    val postLikes: ArrayList<UserModel>? = null,
    val postDescription: String = "",
    var postId: String = "",
    val postLocation: String = "",
    val postTimestamp: Long = 0,
    val postTitle: String = "",
    val postType: Long = 0) :Parcelable

/*
    var postAuthor = ""
    var postComments : ArrayList<CommentModel> = arrayListOf()
    var postDescription = ""
    var postId = ""
    //var postLikes : ArrayList<UserModel> = arrayListOf()
    var postLocation = ""
    var postTimestamp : Long = 0
    var postTitle = ""
    var postType = 0
 */