package com.c0d3in3.finalproject.tools

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.util.TypedValue
import android.view.Window
import android.view.WindowManager
import com.c0d3in3.finalproject.App
import com.c0d3in3.finalproject.Constants
import com.c0d3in3.finalproject.Constants.NOTIFICATION_COMMENT
import com.c0d3in3.finalproject.Constants.NOTIFICATION_LIKE_COMMENT
import com.c0d3in3.finalproject.Constants.NOTIFICATION_LIKE_POST
import com.c0d3in3.finalproject.Constants.NOTIFICATION_START_FOLLOW
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.bean.NotificationModel
import com.c0d3in3.finalproject.network.FirebaseHandler
import com.c0d3in3.finalproject.bean.PostModel
import com.c0d3in3.finalproject.network.State
import kotlinx.android.synthetic.main.dialog_error_layout.*
import kotlinx.android.synthetic.main.dialog_two_option_layout.*
import kotlinx.android.synthetic.main.post_options_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

object Utils {
    fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target!!)
            .matches()
    }

    fun createDialog(ctx: Context, title: String, description: String) {
        val dialog = Dialog(ctx)

        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.setContentView(R.layout.dialog_error_layout)

        val params = dialog.window!!.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes = params

        dialog.dialogDescriptionTV.text = description
        dialog.dialogTitleTV.text = title

        dialog.dialogOkButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    fun likePost(post: PostModel) {
        val postRef = FirebaseHandler.getDatabase().collection(FirebaseHandler.POSTS_REF)
            .document(post.postId)
        FirebaseHandler.getDatabase().runTransaction { transaction ->
            transaction.update(postRef, "postLikes", post.postLikes)
            null
        }.addOnSuccessListener { Log.d("postLikes", "Transaction success!") }
            .addOnFailureListener { e -> Log.d("postLikes", "Transaction failure.", e) }

    }

    fun addNotification(
        senderId: String,
        receiverId: String,
        notificationType: Int,
        text: String? = null
    ) {
        val notificationModel = NotificationModel()
        notificationModel.notificationReceiverId = receiverId
        notificationModel.notificationSenderId = senderId
        notificationModel.notificationTimestamp = System.currentTimeMillis()
        when (notificationType) {
            NOTIFICATION_START_FOLLOW -> notificationModel.notificationText =
                App.getContext().getString(R.string.started_following_you)
            NOTIFICATION_LIKE_POST -> notificationModel.notificationText =
                App.getContext().getString(R.string.liked_your_post)
            NOTIFICATION_COMMENT -> notificationModel.notificationText = "Commented: $text"
            NOTIFICATION_LIKE_COMMENT -> notificationModel.notificationText =
                "Liked your comment: $text"
        }

        CoroutineScope(Dispatchers.IO).launch {
            checkNotification(notificationModel).collect { state ->
                when (state) {

                    is State.Success -> {
                        if (state.data)
                            FirebaseHandler.getDatabase().collection(FirebaseHandler.USERS_REF)
                                .document(receiverId).collection("notifications")
                                .add(notificationModel).addOnSuccessListener {
                                    FirebaseHandler.getDatabase().collection(FirebaseHandler.USERS_REF)
                                        .document(receiverId).collection("notifications").document(it.id).update("notificationId", it.id)
                                }
                    }
                }
            }
        }
    }

    private fun checkNotification(notificationModel: NotificationModel) = flow<State<Boolean>> {
        val mNotificationsCollection =
            FirebaseHandler.getDatabase().collection(FirebaseHandler.USERS_REF)
                .document(notificationModel.notificationReceiverId).collection("notifications")
        emit(State.loading())

        val snapshot = mNotificationsCollection.get().await()

        var foundSameNotification = false

        if (snapshot != null) {
            val list = snapshot.toObjects(NotificationModel::class.java)
            list.forEach {
                if (it.notificationReceiverId == notificationModel.notificationReceiverId && it.notificationSenderId == notificationModel.notificationSenderId &&
                    it.notificationText == notificationModel.notificationText
                ) {
                    mNotificationsCollection.document(it.notificationId).delete()
                    foundSameNotification = true
                    emit(State.success(true))
                    return@forEach
                }
            }
            if(!foundSameNotification)emit(State.success(true))
        }

    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    fun createOptionalDialog(
        ctx: Context,
        title: String,
        description: String,
        callback: DialogCallback
    ) {
        val dialog = Dialog(ctx)

        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.setContentView(R.layout.dialog_two_option_layout)

        val params = dialog.window!!.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes = params

        dialog.dialogDescription.text = description
        dialog.dialogTitle.text = title

        dialog.noDialogButton.setOnClickListener {
            dialog.dismiss()
            callback.onCancel()
        }

        dialog.yesDialogButton.setOnClickListener {
            callback.onResponse(dialog)
        }

        dialog.show()
    }

    fun createPostOptionsDialog(
        ctx: Context,
        callback: DialogCallback
    ) {
        val dialog = Dialog(ctx)

        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.setContentView(R.layout.post_options_layout)

        val params = dialog.window!!.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes = params

        dialog.editPostDialogButton.setOnClickListener {
            dialog.dismiss()
            callback.onEditPost()
        }

        dialog.deletePostDialogButton.setOnClickListener {
            dialog.dismiss()
            callback.onDeletePost()
        }

        dialog.closeDialogButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun getScreenWidth(): Int {
        return Resources.getSystem().displayMetrics.widthPixels
    }

    fun getScreenHeight(): Int {
        return Resources.getSystem().displayMetrics.heightPixels
    }

    fun convertDp(dp: Float): Int {
        val r: Resources = App.getInstance().resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            r.displayMetrics
        ).toInt()
    }
}