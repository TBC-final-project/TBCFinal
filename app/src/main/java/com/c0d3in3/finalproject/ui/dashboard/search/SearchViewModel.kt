package com.c0d3in3.finalproject.ui.dashboard.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.network.State
import com.c0d3in3.finalproject.network.UsersRepository
import com.c0d3in3.finalproject.network.model.UserModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel(private val usersRepository: UsersRepository) : ViewModel() {
    private val _searchList by lazy {
        MutableLiveData<ArrayList<UserModel>>()
    }

    private fun setSearchList(list: ArrayList<UserModel>) {
        _searchList.value = list
    }

    fun clearList(){
        _searchList.value?.clear()
        _searchList.value = _searchList.value
    }
    fun getSearchList() = _searchList

    fun searchUser(user: String) {
        if(user.isBlank()) return
        CoroutineScope(Dispatchers.IO).launch {
            usersRepository.searchUser(user).collect { state ->
                when (state) {
                    is State.Success -> {
                        withContext(Dispatchers.Main){setSearchList(state.data)}
                    }

                    is State.Failed ->{

                    }
                } // END when
            } // END collect
        }
    }
}
