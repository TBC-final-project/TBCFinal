package com.c0d3in3.finalproject.ui.post.post_detailed

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.c0d3in3.finalproject.network.model.PostModel
import com.c0d3in3.finalproject.tools.Utils
import com.c0d3in3.finalproject.ui.auth.UserInfo

class PostViewModel: ViewModel() {

    private val post: MutableLiveData<PostModel> by lazy {
        MutableLiveData<PostModel>()
    }

    fun setPostModel(postModel: PostModel){
        post.value = postModel
    }

    fun getPostModel() = post

    fun likePost(){
        if (post.value?.postLikes?.contains(UserInfo.userInfo.userId)!!) {
            post.value?.postLikes!!.remove(UserInfo.userInfo.userId)
        } else
            post.value!!.postLikes?.add(UserInfo.userInfo.userId)
        post.value?.let { Utils.likePost(it) }
        setPostModel(post.value!!)
    }

}