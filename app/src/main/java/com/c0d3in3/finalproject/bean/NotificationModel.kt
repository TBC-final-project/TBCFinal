package com.c0d3in3.finalproject.bean

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NotificationModel(
    var notificationAuthorId: String = "",
    var notificationText: String = "",
    var notificationSenderId: String = "",
    var notificationType : Long = 0,
    var notificationId: String = "",
    var notificationTimestamp: Long = 0,
    @get:Exclude var notificationSenderModel: UserModel? = null) : Parcelable