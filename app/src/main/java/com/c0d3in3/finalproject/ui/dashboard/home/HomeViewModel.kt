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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(private val repository: PostsRepository) : ViewModel() {

    val posts by lazy{
        MutableLiveData<ArrayList<PostModel>>().also {
            getAllPosts()
        }
    }

    private fun getAllPosts() {
        viewModelScope.launch {
            loadPosts(null)
        }
    }

    fun loadMorePosts(lastId : String){
        viewModelScope.launch {
            loadPosts(lastId)
        }
    }

    fun addPosts(post: PostModel){
        viewModelScope.launch {
            addPost(post)
        }
    }

    private suspend fun addPost(post: PostModel){
        repository.addPost(post).collect{state->
            when(state){
                is State.Success ->{
                    posts.value?.add(post)
                }
            }
        }
    }

    private suspend fun loadPosts(lastId: String? = null) {
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