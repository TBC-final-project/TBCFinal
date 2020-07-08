package com.c0d3in3.finalproject.tools

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import androidx.core.app.ActivityCompat.startActivityForResult
import com.c0d3in3.finalproject.BaseFragment
import com.c0d3in3.finalproject.Constants
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.network.FirebaseHandler
import com.c0d3in3.finalproject.network.model.PostModel
import com.c0d3in3.finalproject.network.model.UserModel
import com.c0d3in3.finalproject.ui.auth.UserInfo
import com.c0d3in3.finalproject.ui.post.comment.CommentsActivity
import kotlinx.android.synthetic.main.dialog_error_layout.*

object Utils {
    fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target!!)
            .matches()
    }

    fun createDialog(ctx: Context, title: String, description : String){
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

        dialog.dialogOkButton.setOnClickListener{
            dialog.dismiss()
        }

        dialog.show()
    }

    fun getTimeDiff(timestamp : Long): String {
        val currentStamp = System.currentTimeMillis()
        val diff = currentStamp - timestamp
        return when {
            diff < 60000 -> "${diff / 1000} second ago"
            diff in 60001 until 3600000 -> "${diff / 60000} minutes ago"
            diff in 3600001 until 86400000 -> "${diff / 3600000} hours ago"
            diff in 86400001 until 2678400000 -> "${diff / 86400000} days ago"
            diff in 2678400001 until 32140800000 -> "${diff / 2678400000} months ago"
            else -> "more than years ago"
        }
    }

    fun getTimeDiffMinimal(timestamp : Long): String {
        val currentStamp = System.currentTimeMillis()
        val diff = currentStamp - timestamp
        return when {
            diff < 60000 -> "${diff / 1000}s"
            diff in 60001 until 3600000 -> "${diff / 60000}m"
            diff in 3600001 until 86400000 -> "${diff / 3600000}h"
            diff in 86400001 until 2678400000 -> "${diff / 86400000}d"
            diff in 2678400001 until 32140800000 -> "${diff / 2678400000}m"
            else -> "1y"
        }
    }

    fun checkLike(postArray: ArrayList<UserModel>) : Int{
        var likePos = -1
        if(postArray.isNotEmpty()){
            for(idx in 0 until postArray.size){
                if(postArray[idx].userId == UserInfo.userInfo.userId){
                    likePos = idx
                    break
                }
            }
        }
        return likePos
    }

    fun likePost(post: PostModel){
        val postRef =  FirebaseHandler.getDatabase().collection(FirebaseHandler.POSTS_REF).document(post.postId)
        FirebaseHandler.getDatabase().runTransaction { transaction ->
            transaction.update(postRef, "postLikes", post.postLikes)
            null
        }.addOnSuccessListener { Log.d("postLikes", "Transaction success!") }
            .addOnFailureListener { e -> Log.d("postLikes", "Transaction failure.", e) }
    }
}