package com.c0d3in3.finalproject.ui.profile

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c0d3in3.finalproject.App
import com.c0d3in3.finalproject.Constants
import com.c0d3in3.finalproject.Constants.NOTIFICATION_START_FOLLOW
import com.c0d3in3.finalproject.bean.PostModel
import com.c0d3in3.finalproject.bean.StoryModel
import com.c0d3in3.finalproject.tools.Utils
import com.c0d3in3.finalproject.bean.UserModel
import com.c0d3in3.finalproject.network.*
import com.c0d3in3.finalproject.network.FirebaseHandler.USERS_REF
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel(private val repository: PostsRepository, private val usersRepository: UsersRepository) : ViewModel() {

    private val _posts by lazy{
        MutableLiveData<ArrayList<PostModel>>()
    }

    private val _user by lazy{
        MutableLiveData<UserModel>()
    }

    fun getPosts()  = _posts


    fun getUser() = _user

    fun setUser(user: UserModel){
        _user.value = user
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

    fun updateUser(){
        viewModelScope.launch {
            updateUserInternal()
        }
    }

    fun follow(){
        if(App.getCurrentUser().userFollowing!!.contains(_user.value?.userId)){
            _user.value?.userFollowers!!.add(App.getCurrentUser().userId)
            Utils.addNotification(App.getCurrentUser().userId, _user.value!!.userId, NOTIFICATION_START_FOLLOW)
        }
        else
            _user.value?.userFollowers!!.remove(App.getCurrentUser().userId)
        FirebaseHandler.getDatabase().collection(USERS_REF).document(App.getCurrentUser().userId).update("userFollowing", App.getCurrentUser().userFollowing)
        FirebaseHandler.getDatabase().collection(USERS_REF).document(_user.value!!.userId).update("userFollowers", _user.value?.userFollowers!!)
        setUser(_user.value!!)
    }


    private suspend fun updateUserInternal() {
        _user.value?.userId?.let {
            usersRepository.getUser(it).collect { state ->
                when (state) {

                    is State.Success -> {
                        if(state.data != null) setUser(state.data)
                    }

                    is State.Failed -> withContext(Dispatchers.Main) {
                        Toast.makeText(
                            App.getInstance().applicationContext,
                            "Error while loading user profile",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } // END when
            }
        } // END collect
    }

    private suspend fun getUserPosts(lastPost: PostModel? = null) {
        _user.value?.userId?.let {
            repository.getUserPosts(10, lastPost, it).collect { state ->
                when (state) {

                    is State.Success -> {
                        if(_posts.value != null){
                            _posts.value?.addAll(state.data)
                            _posts.value = _posts.value
                        } else _posts.value = state.data
                    }

                    is State.Failed -> withContext(Dispatchers.Main) {
                        Toast.makeText(
                            App.getInstance().applicationContext,
                            "Error while loading posts",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } // END when
            }
        } // END collect
    }
}