package com.c0d3in3.finalproject.ui.dashboard.home

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.c0d3in3.finalproject.Constants
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.base.BaseFragment
import com.c0d3in3.finalproject.network.model.PostModel
import com.c0d3in3.finalproject.ui.post.PostsAdapter
import com.c0d3in3.finalproject.ui.post.comment.CommentsActivity
import com.c0d3in3.finalproject.ui.post.post_detailed.ImagePostDetailedActivity
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : BaseFragment(), PostsAdapter.CustomPostCallback {

    private lateinit var homeViewModel: HomeViewModel
    private var posts = arrayListOf<PostModel>()
    private var adapter: PostsAdapter? = null
    private var lastPost : PostModel? = null

    override fun init() {

//        adapter = PostsAdapter(this)
//        rootView!!.postsRecyclerView.adapter = adapter
//
//        rootView!!.homeSwipeLayout.setOnRefreshListener {
//            if (rootView!!.homeSwipeLayout.isRefreshing) {
//                posts.clear()
//                adapter.setPostsList(posts)
//                homeViewModel.loadPosts(null)
//            }
//        }
    }


    override fun setUpFragment() {


        homeViewModel =
            ViewModelProvider(this, HomeViewModelFactory()).get(HomeViewModel::class.java)

        if(adapter == null){
            adapter = PostsAdapter(rootView!!.postsRecyclerView, this)
            rootView!!.postsRecyclerView.adapter = adapter
        }

        homeViewModel.posts.observe(this, Observer { list ->
            if (rootView!!.homeSwipeLayout.isRefreshing) rootView!!.homeSwipeLayout.isRefreshing = false
            if(list != null && list.isNotEmpty()){
                posts = list
                adapter!!.setPostsList(posts)
                lastPost = posts[posts.size - 1]
            }
        })

        rootView!!.homeSwipeLayout.setOnRefreshListener {
            if (rootView!!.homeSwipeLayout.isRefreshing) {
                posts.clear()
                adapter?.setPostsList(posts)
                adapter?.notifyDataSetChanged()
                homeViewModel.loadPosts(null)
            }
        }
    }


    override fun getLayout() = R.layout.fragment_home

    override fun onLikeButtonClick(position: Int) {
        homeViewModel.likePost(position)
        adapter?.notifyItemChanged(position)
    }
    private fun startPostActionActivity(act : Activity, model: PostModel, position: Int){
        val intent = Intent(activity, act::class.java)
        intent.putExtra("model", model)
        intent.putExtra("position", position)
        startActivityForResult(intent, Constants.OPEN_DETAILED_POST)
    }

    override fun onCommentButtonClick(position: Int) {
        startPostActionActivity(CommentsActivity(), posts[position], position)
    }

    override fun openDetailedPost(position: Int) {
        startPostActionActivity(ImagePostDetailedActivity(), posts[position], position)
    }

    override fun onLoadMore() {
        homeViewModel.loadPosts(lastPost)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == Constants.OPEN_DETAILED_POST && resultCode == RESULT_OK){
            val position = data?.extras?.getInt("position")
            if(position != null){
                val model = data.extras!!.getParcelable<PostModel>("model")
                posts[position] = model!!
                adapter?.notifyItemChanged(position)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}