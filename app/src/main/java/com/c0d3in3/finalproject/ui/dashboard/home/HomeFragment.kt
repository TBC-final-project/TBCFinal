package com.c0d3in3.finalproject.ui.dashboard.home

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.c0d3in3.finalproject.BaseFragment
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.network.model.PostModel
import com.c0d3in3.finalproject.ui.auth.UserInfo
import com.c0d3in3.finalproject.ui.post.PostsAdapter
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : BaseFragment(), PostsAdapter.CustomPostCallback{

    private lateinit var homeViewModel: HomeViewModel
    private var posts = arrayListOf<PostModel>()
    private lateinit var adapter : PostsAdapter
    private var lastId = ""

    override fun init() {
        //(activity as DashboardActivity).setToolbarTitle(getString(R.string.news_feed))

        adapter = PostsAdapter(posts,this )
        rootView!!.postsRecyclerView.adapter = adapter
    }

    override fun setUpFragment() {


        homeViewModel = ViewModelProvider(this, HomeViewModelFactory()).get(HomeViewModel::class.java)
        homeViewModel.posts.observe(viewLifecycleOwner, Observer {
            posts = it
        })
        homeViewModel.posts.observe(this, Observer { list ->
            posts = list
            adapter = PostsAdapter(posts,this )
            rootView!!.postsRecyclerView.adapter = adapter
            if(list.isNotEmpty()) lastId = posts[posts.size-1].postId
        })

//        rootView!!.refresh.setOnClickListener {
//            homeViewModel.loadMorePosts(lastId)
//        }

    }



    override fun getLayout() = R.layout.fragment_home

    override fun onLikeButtonClick() {

    }

    override fun onCommentButtonClick() {
    }
}
