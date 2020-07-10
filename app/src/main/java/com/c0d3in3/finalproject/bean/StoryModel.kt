package com.c0d3in3.finalproject.bean

import android.os.Parcelable
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Exclude
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StoryModel(
    var storyId : String = "",
    var storyAuthorId : String = "",
    var storyType : Long = 0,
    var storyImage : String = "",
    var storyText : String = "",
    var storyPeopleSeenList: ArrayList<String> = arrayListOf(),
    var storySeenUpdate : Boolean = false,
    var storyCreatedAt : Long = 0,
    var storyValidUntil : Long = 0,
    @get:Exclude var storyAuthorModel: UserModel? = null) : Parcelable, CustomInterface