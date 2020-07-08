package com.c0d3in3.finalproject.ui.post.comment

import android.annotation.SuppressLint
import android.app.Dialog
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.c0d3in3.finalproject.App
import com.c0d3in3.finalproject.ListLiveData
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.network.FirebaseHandler
import com.c0d3in3.finalproject.network.State
import com.c0d3in3.finalproject.network.UsersRepository
import com.c0d3in3.finalproject.network.model.CommentModel
import com.c0d3in3.finalproject.network.model.PostModel
import kotlinx.android.synthetic.main.comment_item_layout.view.*
import kotlinx.android.synthetic.main.dialog_error_layout.*
import kotlinx.android.synthetic.main.dialog_two_option_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommentViewModel(private val repository: UsersRepository) : ViewModel() {

    private lateinit var post: PostModel

//    private val list by lazy{
//        ListLiveData<ArrayList<CommentModel>>()
//    }

    private val commentsList by lazy {
        MutableLiveData<ArrayList<CommentModel>>()
    }

    fun getComments() = commentsList

    fun setPostModel(postModel: PostModel) {
        post = postModel
        commentsList.value = post.postComments
        getUsers()
    }

    private fun getUsers() {
        CoroutineScope(Dispatchers.IO).launch {
            commentsList.value?.forEach {
                if (it.commentAuthorModel != null) {
                    repository.getUser(it.commentAuthor!!).collect { state ->
                        when (state) {
                            is State.Success -> {
                                val model = commentsList.value!!.indexOf(it)
                                commentsList.value!![model].commentAuthorModel = state.data
                            }
                        }
                    }
                }
            }
        }
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
        post.postComments?.add(comment)
        commentsList.value = post.postComments
        val postRef = FirebaseHandler.getDatabase().collection(FirebaseHandler.POSTS_REF)
            .document(post.postId)
        FirebaseHandler.getDatabase().runTransaction { transaction ->
            transaction.update(postRef, "postComments", post.postComments)
            null
        }.addOnSuccessListener {
            Log.d("AddComment", "Transaction success!")

        }
            .addOnFailureListener { e -> Log.d("AddComment", "Transaction failure.", e) }
    }
}