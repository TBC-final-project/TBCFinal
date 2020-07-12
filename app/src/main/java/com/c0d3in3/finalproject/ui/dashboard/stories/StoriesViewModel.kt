package com.c0d3in3.finalproject.ui.dashboard.stories

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c0d3in3.finalproject.App
import com.c0d3in3.finalproject.bean.StoryModel
import com.c0d3in3.finalproject.bean.UserModel
import com.c0d3in3.finalproject.network.State
import com.c0d3in3.finalproject.network.StoriesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StoriesViewModel(private val storyRepository: StoriesRepository) : ViewModel() {

    private val stories by lazy{
        MutableLiveData<ArrayList<ArrayList<StoryModel>>>()
    }

    private var lastStoryUserId: String? = null

    fun getStoriesList()  = stories

    fun loadStories(isRefreshing : Boolean){
        viewModelScope.launch {
            if(isRefreshing){
                lastStoryUserId = null
                stories.value = null
            }
            getStories()

        }
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
                        user.userProfileImage = App.getCurrentUser().userProfileImage
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
}