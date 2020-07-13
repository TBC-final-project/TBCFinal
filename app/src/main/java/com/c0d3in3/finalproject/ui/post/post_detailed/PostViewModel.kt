package com.c0d3in3.finalproject.ui.post.post_detailed

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.c0d3in3.finalproject.App
import com.c0d3in3.finalproject.Constants
import com.c0d3in3.finalproject.bean.PostModel
import com.c0d3in3.finalproject.tools.Utils

class PostViewModel: ViewModel() {

    private val post: MutableLiveData<PostModel> by lazy {
        MutableLiveData<PostModel>()
    }

    fun setPostModel(postModel: PostModel){
        post.value = postModel
    }

    fun getPostModel() = post

    fun likePost(){
        if (post.value?.postLikes?.contains(App.getCurrentUser().userId)!!) {
            post.value?.postLikes!!.remove(App.getCurrentUser().userId)
        } else{
            post.value!!.postLikes?.add(App.getCurrentUser().userId)
            Utils.addNotification(
                App.getCurrentUser().userId,
                post.value!!.postAuthor!!,
                Constants.NOTIFICATION_LIKE_POST
            )
        }
        post.value?.let { Utils.likePost(it) }
        setPostModel(post.value!!)
    }

}