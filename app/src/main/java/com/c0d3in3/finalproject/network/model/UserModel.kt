package com.c0d3in3.finalproject.network.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserModel(
    var userFollowers: ArrayList<String>? = null,
    var userFollowing : ArrayList<String>?  = null,
    var userFullName: String = "",
    var userId : String = "",
    var userProfileCover : String = "",
    var userProfileImage : String = "",
    var userRegisterDate : Long = 0,
    var username : String = ""): Parcelable{}

/*
    var userFollowers: ArrayList<UserModel> = arrayListOf()
    var userFollowing : ArrayList<UserModel> = arrayListOf()
    var userFullName = ""
    var userId = ""
    var userProfileCover = ""
    var userProfileImage = ""
    var userRegisterDate : Long = 0
    var username = ""
 */