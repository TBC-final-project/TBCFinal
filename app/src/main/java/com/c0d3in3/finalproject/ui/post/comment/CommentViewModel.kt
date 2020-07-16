package com.c0d3in3.finalproject.ui.post.comment

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c0d3in3.finalproject.App
import com.c0d3in3.finalproject.Constants
import com.c0d3in3.finalproject.network.FirebaseHandler
import com.c0d3in3.finalproject.network.UsersRepository
import com.c0d3in3.finalproject.bean.CommentModel
import com.c0d3in3.finalproject.bean.PostModel
import com.c0d3in3.finalproject.network.FirebaseHandler.POSTS_REF
import com.c0d3in3.finalproject.network.PostsRepository
import com.c0d3in3.finalproject.network.State
import com.c0d3in3.finalproject.tools.Utils
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommentViewModel(private val repository: PostsRepository) : ViewModel() {

    private val _post by lazy {
        MutableLiveData<PostModel>()
    }

    fun getPost() = _post

    fun setPostModel(postModel: PostModel) {
        _post.value = postModel
    }


    fun removeComment(position: Int) {
        _post.value?.postComments!!.removeAt(position)
        FirebaseHandler.getDatabase().collection(POSTS_REF).document(_post.value!!.postId).update("postComments", FieldValue.arrayRemove(
            _post.value?.postComments!![position]
        ))
        setPostModel(_post.value!!)
    }

    fun addComment(comment: CommentModel) {
        comment.commentAuthorModel = App.getCurrentUser()
        _post.value!!.postComments?.add(comment)
        setPostModel(_post.value!!)

        FirebaseHandler.getDatabase().collection(POSTS_REF).document(_post.value!!.postId).update("postComments", FieldValue.arrayUnion(comment))

        if(_post.value!!.postAuthor != App.getCurrentUser().userId){
            Utils.addNotification(
                App.getCurrentUser().userId,
                _post.value!!.postAuthor.toString(),
                Constants.NOTIFICATION_COMMENT,
                _post.value!!.postId,
                comment.comment
            )
        }
    }

    fun likeComment(position: Int){

        val postComments = _post.value!!.postComments?.get(position)
        if (postComments?.commentLikes?.contains(App.getCurrentUser().userId)!!)
            postComments.commentLikes?.remove(App.getCurrentUser().userId)
        else{
            postComments.commentLikes?.add(App.getCurrentUser().userId)

            val receiver = postComments.commentAuthor
            if(postComments.commentAuthor != App.getCurrentUser().userId){
                if (receiver != null) {
                    Utils.addNotification(
                        App.getCurrentUser().userId,
                        receiver,
                        Constants.NOTIFICATION_LIKE_COMMENT,
                        _post.value!!.postId,
                        _post.value!!.postComments?.get(position)?.comment
                    )
                }
            }
        }

        val postRef =  FirebaseHandler.getDatabase().collection(POSTS_REF).document(
            _post.value!!.postId)
        FirebaseHandler.getDatabase().runTransaction { transaction ->
            transaction.update(postRef, "postComments", _post.value!!.postComments)
            null
        }.addOnSuccessListener { Log.d("postComments", "Transaction success!") }
            .addOnFailureListener { e -> Log.d("postComments", "Transaction failure.", e) }

        setPostModel(_post.value!!)
    }

    fun loadPost(){
        viewModelScope.launch {
            repository.getPost(_post.value!!.postId).collect { state ->
                when (state) {

                    is State.Success -> {
                        if(state.data != null) {
                            state.data.postAuthorModel = getPost().value!!.postAuthorModel
                            setPostModel(state.data)
                        }
                    }

                    is State.Failed -> withContext(Dispatchers.Main) {
                        Toast.makeText(
                            App.getInstance().applicationContext,
                            "Error while loading post",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } // END when
            } // END collect
        }
    }
}