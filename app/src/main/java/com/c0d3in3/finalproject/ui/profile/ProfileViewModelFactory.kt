package com.c0d3in3.finalproject.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.c0d3in3.finalproject.network.PostsRepository
import com.c0d3in3.finalproject.network.UsersRepository

class ProfileViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(PostsRepository::class.java, UsersRepository::class.java)
            .newInstance(PostsRepository(), UsersRepository())
    }
}