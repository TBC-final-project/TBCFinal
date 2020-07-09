package com.c0d3in3.finalproject.ui.dashboard.home

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c0d3in3.finalproject.App
import com.c0d3in3.finalproject.network.PostsRepository
import com.c0d3in3.finalproject.network.State
import com.c0d3in3.finalproject.network.model.PostModel
import com.c0d3in3.finalproject.tools.Utils
import com.c0d3in3.finalproject.ui.auth.UserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(private val repository: PostsRepository) : ViewModel() {

    val posts by lazy{
        MutableLiveData<ArrayList<PostModel>>().also {
            viewModelScope.launch {
                getPosts(null)
            }
        }
    }

    fun loadPosts(lastId : String?){
        viewModelScope.launch {
            getPosts(lastId)
        }
    }

    fun likePost(position: Int){
        if (posts.value!![position].postLikes?.contains(UserInfo.userInfo.userId)!!)
            posts.value!![position].postLikes!!.remove(UserInfo.userInfo.userId)
         else
            posts.value!![position].postLikes!!.add(UserInfo.userInfo.userId)
        Utils.likePost(posts.value!![position])
    }

    private suspend fun getPosts(lastId: String? = null) {
        repository.getAllPosts(10, lastId).collect { state ->
            when (state) {
                is State.Loading -> {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(App.getInstance().applicationContext, "Loading", Toast.LENGTH_SHORT).show()
                    }
                }

                is State.Success -> {
                    posts.value = state.data
                }

                is State.Failed -> withContext(Dispatchers.Main) {
                    Toast.makeText(
                        App.getInstance().applicationContext,
                        "Failed! ${state.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } // END when
        } // END collect
    }
}