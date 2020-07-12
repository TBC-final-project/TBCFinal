package com.c0d3in3.finalproject.ui.dashboard.stories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.c0d3in3.finalproject.network.PostsRepository
import com.c0d3in3.finalproject.network.StoriesRepository

class StoriesViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(StoriesRepository::class.java)
            .newInstance(StoriesRepository())
    }
}