package com.c0d3in3.finalproject.network.model

class UserModel {
    var userFollowers: ArrayList<UserModel> = arrayListOf()
    var userFollowing : ArrayList<UserModel> = arrayListOf()
    var userFullName = ""
    var userId = ""
    var userProfileCover = ""
    var userProfileImage = ""
    var userRegisterDate : Long = 0
    var username = ""
}