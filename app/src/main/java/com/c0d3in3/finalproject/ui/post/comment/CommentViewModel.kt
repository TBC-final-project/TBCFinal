package com.c0d3in3.finalproject.ui.post.comment

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.c0d3in3.finalproject.network.FirebaseHandler
import com.c0d3in3.finalproject.network.UsersRepository
import com.c0d3in3.finalproject.bean.CommentModel
import com.c0d3in3.finalproject.bean.PostModel
import com.c0d3in3.finalproject.ui.auth.UserInfo

class CommentViewModel(private val repository: UsersRepository) : ViewModel() {

    private lateinit var post: PostModel

    private val commentsList by lazy {
        MutableLiveData<ArrayList<CommentModel>>()
    }

    fun getComments() = commentsList

    fun setPostModel(postModel: PostModel) {
        post = postModel
        //commentsList.value = post.postComments
    }


    @SuppressLint("SetTextI18n")
    fun removeComment(position: Int) {
        post.postComments!!.removeAt(position)
        FirebaseHandler.getDatabase().collection(FirebaseHandler.POSTS_REF)
            .document(post.postId)
            .update("postComments", post.postComments)
        commentsList.value = post.postComments
    }

    fun addComment(comment: CommentModel) {
        comment.commentAuthorModel = UserInfo.userInfo
        post.postComments?.add(comment)
        commentsList.value = post.postComments
        val postRef = FirebaseHandler.getDatabase().collection(FirebaseHandler.POSTS_REF)
            .document(post.postId)
        FirebaseHandler.getDatabase().runTransaction { transaction ->
            transaction.update(postRef, "postComments", post.postComments)
            null
        }.addOnSuccessListener {
            Log.d("AddComment", "Transaction success!")

        }.addOnFailureListener { e -> Log.d("AddComment", "Transaction failure.", e) }
    }
}