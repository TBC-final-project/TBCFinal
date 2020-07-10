package com.c0d3in3.finalproject.tools

import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.util.TypedValue
import android.view.Window
import android.view.WindowManager
import com.c0d3in3.finalproject.App
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.network.FirebaseHandler
import com.c0d3in3.finalproject.bean.PostModel
import kotlinx.android.synthetic.main.dialog_error_layout.*
import kotlinx.android.synthetic.main.dialog_two_option_layout.*

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

//
//    fun checkLike(postArray: ArrayList<String>) : Int{
//        var likePos = -1
//        if(postArray.isNotEmpty()){
//            for(idx in 0 until postArray.size){
//                if(postArray[idx] == UserInfo.userInfo.userId){
//                    likePos = idx
//                    break
//                }
//            }
//        }
//        return likePos
//    }

    fun likePost(post: PostModel){
        val postRef =  FirebaseHandler.getDatabase().collection(FirebaseHandler.POSTS_REF).document(post.postId)
        FirebaseHandler.getDatabase().runTransaction { transaction ->
            transaction.update(postRef, "postLikes", post.postLikes)
            null
        }.addOnSuccessListener { Log.d("postLikes", "Transaction success!") }
            .addOnFailureListener { e -> Log.d("postLikes", "Transaction failure.", e) }
    }

    fun createOptionalDialog(ctx: Context, title: String, description: String, callback: DialogCallback){
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