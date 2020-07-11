package com.c0d3in3.finalproject.ui.dashboard.home

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c0d3in3.finalproject.App
import com.c0d3in3.finalproject.network.PostsRepository
import com.c0d3in3.finalproject.network.State
import com.c0d3in3.finalproject.bean.PostModel
import com.c0d3in3.finalproject.bean.StoryModel
import com.c0d3in3.finalproject.network.StoriesRepository
import com.c0d3in3.finalproject.tools.Utils
import com.c0d3in3.finalproject.UserInfo
import com.c0d3in3.finalproject.bean.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(private val repository: PostsRepository, private val storyRepository: StoriesRepository) : ViewModel() {

    val posts by lazy{
        MutableLiveData<ArrayList<PostModel>>()
    }

    private val stories by lazy{
        MutableLiveData<ArrayList<ArrayList<StoryModel>>>()
    }

    private var lastStoryUserId: String? = null

    fun getStoriesList()  = stories

    fun loadPosts(lastPost : PostModel?){
        viewModelScope.launch {
            if(lastPost == null && posts.value != null) posts.value = null
            getPosts(lastPost)
        }
    }

    fun loadStories(isRefreshing : Boolean){
        viewModelScope.launch {
            if(isRefreshing){
                lastStoryUserId = null
                stories.value = null
            }
            getStories()

        }
    }

//    fun loadStories(lastStory: StoryModel?){
//        viewModelScope.launch {
//            if(lastStory == null && stories.value != null) stories.value = null
//            getStories(lastStory)
//        }
//    }

    fun likePost(position: Int){
        if (posts.value!![position].postLikes?.contains(UserInfo.userInfo.userId)!!)
            posts.value!![position].postLikes!!.remove(UserInfo.userInfo.userId)
         else
            posts.value!![position].postLikes!!.add(UserInfo.userInfo.userId)
        Utils.likePost(posts.value!![position])
        posts.value = posts.value
    }

    private suspend fun getPosts(lastPost: PostModel? = null) {
        repository.getAllPosts(10, lastPost).collect { state ->
            when (state) {

                is State.Success -> {
                    if(posts.value != null){
                        posts.value?.addAll(state.data)
                        posts.value = posts.value
                    }
                    else posts.value = state.data
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
    private suspend fun getStories() {
        storyRepository.getAllStories(10, lastStoryUserId).collect { state ->
            when (state) {

                is State.Loading ->{

                }

                is State.Success -> {


                    if(stories.value != null){
                        stories.value?.addAll(state.data)
                        stories.value = stories.value
                    }
                    else{
                        val storyModel = StoryModel()
                        storyModel.storyAuthorId = "self"
                        val user = UserModel()
                        user.userProfileImage = UserInfo.userInfo.userProfileImage
                        user.userFullName = "Add a Story"
                        storyModel.storyAuthorModel = user
                        state.data.add(0, arrayListOf(storyModel))
                        stories.value = state.data
                    }
                    lastStoryUserId = stories.value!![stories.value!!.size-1][0].storyAuthorId
                }

                is State.Failed -> withContext(Dispatchers.Main) {
                    println(state.message)
                    Toast.makeText(
                        App.getInstance().applicationContext,
                        "Error while loading stories",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } // END when
        } // END collect
    }
//    private suspend fun getStories(lastStory:  StoryModel? = null) {
//        storyRepository.getAllStories(10, lastStory).collect { state ->
//            when (state) {
//
//                is State.Success -> {
//                    if(stories.value != null){
//                        stories.value?.addAll(state.data)
//                        stories.value = stories.value
//                    }
//                    else stories.value = state.data
//                }
//
//                is State.Failed -> withContext(Dispatchers.Main) {
//                    //println(state.message)
//                    Toast.makeText(
//                        App.getInstance().applicationContext,
//                        "Failed! ${state.message}",
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
//            } // END when
//        } // END collect
//    }
}