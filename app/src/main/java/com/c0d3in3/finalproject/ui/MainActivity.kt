package com.c0d3in3.finalproject.ui

import android.os.Bundle
import android.util.Log.d
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.network.model.PostModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    private var posts = listOf<PostModel>()
    private var lastId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this,
            MainViewModelFactory()
        )
            .get(MainViewModel::class.java)
        init()


    }



    private fun init() {
        viewModel.posts.observe(this, Observer { list ->
            posts = list
            posts.forEach {
                d("author", it.postAuthor)
            }
            if(list.isNotEmpty()) lastId = posts[posts.size-1].postId
        })

        refresh.setOnClickListener {
            viewModel.loadMorePosts(lastId)
        }
        addPost.setOnClickListener {
            addPosts()
        }
    }

    private fun addPosts(){
        val post = PostModel()
        post.postId = "${(1..1000).random()}"
        post.postAuthor = "tedo ${(1..100).random()}"
        viewModel.addPosts(post)
    }
}















//        viewModel.getPosts().observe(this, Observer { list ->
//            list.forEach {
//                println("author : ${it.postAuthor}")
//            }
//            // update UI
//        })
//        FirebaseHandler.getPostByID("YS914q5K0sbjiKa35Ajq", object : FutureCallback {
//            override fun <T> onResponse(data: T) {
//                if(data != null){
//                    val post = data as PostModel
//                    textView.text = post.postAuthor
//                }
//            }
//
//            override fun onFail(response: String) {
//                textView.text = response
//            }
//
//        })