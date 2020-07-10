package com.c0d3in3.finalproject.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserModel(
    var userFollowers: ArrayList<String>? = null,
    var userFollowing : ArrayList<String>?  = null,
    var userFullName: String = "",
    var userFullNameToLowerCase : String = "",
    var userId : String = "",
    var userProfileCover : String = "",
    var userProfileImage : String = "",
    var userRegisterDate : Long = 0,
    var username : String = ""): Parcelable{}