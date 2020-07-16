package com.c0d3in3.finalproject.ui.post.comment

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c0d3in3.finalproject.App
import com.c0d3in3.finalproject.Constants
import com.c0d3in3.finalproject.Constants.LIKE_COMMENT
import com.c0d3in3.finalproject.Constants.REMOVE_COMMENT
import com.c0d3in3.finalproject.network.FirebaseHandler
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

    private fun removeComment(comment:CommentModel) {

        var index = -1
        _post.value?.postComments?.forEachIndexed { idx, it->
            if(it.commentTimestamp == comment.commentTimestamp) index = idx
        }
        if(index != -1){
            _post.value?.postComments!!.removeAt(index)
            FirebaseHandler.getDatabase().collection(POSTS_REF).document(_post.value!!.postId).update(
                "postComments", _post.value?.postComments!!
            )
            setPostModel(_post.value!!)
        }
    }

    fun addComment(comment: CommentModel) {
        comment.commentAuthorModel = App.getCurrentUser()
        _post.value!!.postComments?.add(comment)
        setPostModel(_post.value!!)

        FirebaseHandler.getDatabase().collection(POSTS_REF).document(_post.value!!.postId)
            .update("postComments", FieldValue.arrayUnion(comment))

        if (_post.value!!.postAuthor != App.getCurrentUser().userId) {
            Utils.addNotification(
                App.getCurrentUser().userId,
                _post.value!!.postAuthor.toString(),
                Constants.NOTIFICATION_COMMENT,
                _post.value!!.postId,
                comment.comment
            )
        }
    }

    private fun likeComment(comment: CommentModel) {
        var index = -1
        _post.value?.postComments?.forEachIndexed { idx, it->
            if(it.commentTimestamp == comment.commentTimestamp) index = idx
        }
        if(index != -1){
            val postComment = _post.value!!.postComments?.get(index)
            if (postComment?.commentLikes?.contains(App.getCurrentUser().userId)!!)
                postComment.commentLikes?.remove(App.getCurrentUser().userId)
            else {
                postComment.commentLikes?.add(App.getCurrentUser().userId)

                val receiver = postComment.commentAuthor
                if (postComment.commentAuthor != App.getCurrentUser().userId) {
                    if (receiver != null) {
                        Utils.addNotification(
                            App.getCurrentUser().userId,
                            receiver,
                            Constants.NOTIFICATION_LIKE_COMMENT,
                            _post.value!!.postId,
                            postComment.comment
                        )
                    }
                }
            }
            FirebaseHandler.getDatabase().collection(POSTS_REF).document(_post.value!!.postId).update(
                "postComments", _post.value!!.postComments
            )
            setPostModel(_post.value!!)
        }
        else setPostModel(_post.value!!)
    }

    fun updatePost(comment: CommentModel? = null, operation: Int? = null) {
        viewModelScope.launch {
            repository.getPost(_post.value!!.postId).collect { state ->
                when (state) {

                    is State.Success -> {
                        if (state.data != null) {
                            state.data.postAuthorModel = getPost().value!!.postAuthorModel
                            if(comment != null && operation != null){
                                _post.value!!.postComments = state.data.postComments
                                if(operation == REMOVE_COMMENT) removeComment(comment)
                                else likeComment(comment)
                            }
                            else setPostModel(state.data)
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