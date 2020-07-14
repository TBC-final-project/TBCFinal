package com.c0d3in3.finalproject.ui.profile

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c0d3in3.finalproject.App
import com.c0d3in3.finalproject.Constants
import com.c0d3in3.finalproject.network.PostsRepository
import com.c0d3in3.finalproject.network.State
import com.c0d3in3.finalproject.bean.PostModel
import com.c0d3in3.finalproject.bean.StoryModel
import com.c0d3in3.finalproject.network.StoriesRepository
import com.c0d3in3.finalproject.tools.Utils
import com.c0d3in3.finalproject.bean.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel(private val repository: PostsRepository) : ViewModel() {

    private val _posts by lazy{
        MutableLiveData<ArrayList<PostModel>>()
    }

    private var userId = ""

    fun getPosts()  = _posts


    fun setUserId(userId: String){
        this.userId = userId
    }

    fun loadPosts(lastPost : PostModel?){
        viewModelScope.launch {
            if(lastPost == null && _posts.value != null) _posts.value = null
            getUserPosts(lastPost)
        }
    }


    fun likePost(position: Int){
        if (_posts.value!![position].postLikes?.contains(App.getCurrentUser().userId)!!)
            _posts.value!![position].postLikes!!.remove(App.getCurrentUser().userId)
        else{
            _posts.value!![position].postLikes!!.add(App.getCurrentUser().userId)
            if(_posts.value!![position].postAuthor != App.getCurrentUser().userId){
                Utils.addNotification(
                    App.getCurrentUser().userId,
                    _posts.value!![position].postAuthor!!,
                    Constants.NOTIFICATION_LIKE_POST
                )
            }
        }
        Utils.likePost(_posts.value!![position])
        _posts.value = _posts.value
    }

    private suspend fun getUserPosts(lastPost: PostModel? = null) {
        repository.getUserPosts(10, lastPost, userId).collect { state ->
            when (state) {

                is State.Success -> {
                    if(_posts.value != null){
                        _posts.value?.addAll(state.data)
                        _posts.value = _posts.value
                    }
                    else _posts.value = state.data
                }

                is State.Failed -> withContext(Dispatchers.Main) {
                    Toast.makeText(
                        App.getInstance().applicationContext,
                        "Error while loading posts",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } // END when
        } // END collect
    }
}