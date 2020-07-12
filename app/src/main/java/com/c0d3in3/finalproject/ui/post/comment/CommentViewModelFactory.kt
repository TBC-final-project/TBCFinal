package com.c0d3in3.finalproject.ui.post.comment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.c0d3in3.finalproject.network.PostsRepository
import com.c0d3in3.finalproject.network.UsersRepository

class CommentViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(PostsRepository::class.java)
            .newInstance(PostsRepository())
    }
}