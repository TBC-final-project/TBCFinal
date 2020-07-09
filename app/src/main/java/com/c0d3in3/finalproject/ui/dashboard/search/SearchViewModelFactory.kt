package com.c0d3in3.finalproject.ui.dashboard.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.c0d3in3.finalproject.network.UsersRepository

class SearchViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(UsersRepository::class.java)
            .newInstance(UsersRepository())
    }
}