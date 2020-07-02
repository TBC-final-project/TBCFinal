package com.c0d3in3.finalproject.ui.dashboard.home

import android.util.Log.d
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.c0d3in3.BaseFragment
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.network.PostsRepository
import com.c0d3in3.finalproject.network.model.PostModel
import com.c0d3in3.finalproject.ui.dashboard.DashboardActivity
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : BaseFragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var posts = listOf<PostModel>()
    private var lastId = ""

    override fun init() {
        //(activity as DashboardActivity).setToolbarTitle(getString(R.string.news_feed))
    }

    override fun setUpFragment() {


        homeViewModel = ViewModelProvider(this, HomeViewModelFactory()).get(HomeViewModel::class.java)
        homeViewModel.posts.observe(viewLifecycleOwner, Observer {
            posts = it
        })
        homeViewModel.posts.observe(this, Observer { list ->
            posts = list
            posts.forEach {
                d("author", it.postAuthor)
            }
            if(list.isNotEmpty()) lastId = posts[posts.size-1].postId
        })

        rootView!!.refresh.setOnClickListener {
            homeViewModel.loadMorePosts(lastId)
        }
        rootView!!.addPost.setOnClickListener {
            addPosts()
        }


    }

    private fun addPosts(){
        val post = PostModel()
        post.postId = "${(1..1000).random()}"
        post.postAuthor = "tedo ${(1..100).random()}"
        homeViewModel.addPosts(post)
    }

    override fun getLayout() = R.layout.fragment_home
}
