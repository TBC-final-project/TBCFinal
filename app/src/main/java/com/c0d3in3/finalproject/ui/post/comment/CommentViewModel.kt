package com.c0d3in3.finalproject.ui.post.comment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.c0d3in3.finalproject.network.FirebaseHandler
import com.c0d3in3.finalproject.network.model.CommentModel
import com.c0d3in3.finalproject.network.model.PostModel
import kotlinx.android.synthetic.main.activity_comments.*

class CommentViewModel : ViewModel() {

    private lateinit var post: PostModel

    init{

    }

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

            adapter.notifyItemInserted(post.postComments!!.size - 1)
            commentsRecyclerView.smoothScrollToPosition(adapter.itemCount - 1)
        }
        .addOnFailureListener { e -> Log.d("AddComment", "Transaction failure.", e) }
    }
}