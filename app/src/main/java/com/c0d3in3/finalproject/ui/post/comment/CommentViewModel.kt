package com.c0d3in3.finalproject.ui.post.comment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.c0d3in3.finalproject.network.FirebaseHandler
import com.c0d3in3.finalproject.network.UsersRepository
import com.c0d3in3.finalproject.network.model.CommentModel
import com.c0d3in3.finalproject.network.model.PostModel

class CommentViewModel(private val repository: UsersRepository) : ViewModel() {

    private lateinit var post: PostModel

    private val commentsList by lazy{
        MutableLiveData<ArrayList<CommentModel>>()
    }

    fun getComments() = commentsList

    fun getPostModel(postModel: PostModel){
        post = postModel
    }

    fun addComment(comment: CommentModel) {
        val postRef = FirebaseHandler.getDatabase().collection(FirebaseHandler.POSTS_REF).document(post.postId)
        FirebaseHandler.getDatabase().runTransaction { transaction ->
            transaction.update(postRef, "postComments", post.postComments)
            null
        }.addOnSuccessListener {
            Log.d("AddComment", "Transaction success!")

        }
            .addOnFailureListener { e -> Log.d("AddComment", "Transaction failure.", e) }
    }
}