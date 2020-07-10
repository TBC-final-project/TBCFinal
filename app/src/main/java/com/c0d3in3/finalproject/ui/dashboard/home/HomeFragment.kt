package com.c0d3in3.finalproject.ui.dashboard.home

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.c0d3in3.finalproject.Constants
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.base.BaseFragment
import com.c0d3in3.finalproject.bean.PostModel
import com.c0d3in3.finalproject.bean.StoryModel
import com.c0d3in3.finalproject.ui.dashboard.stories.StoryAdapter
import com.c0d3in3.finalproject.ui.dashboard.stories.StoryViewActivity
import com.c0d3in3.finalproject.ui.post.PostsAdapter
import com.c0d3in3.finalproject.ui.post.comment.CommentsActivity
import com.c0d3in3.finalproject.ui.post.post_detailed.ImagePostDetailedActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_home.view.*


class HomeFragment : BaseFragment(), PostsAdapter.CustomPostCallback,
    StoryAdapter.CustomStoryCallback {

    private lateinit var homeViewModel: HomeViewModel
    private var posts = arrayListOf<PostModel>()
    private var stories = arrayListOf<ArrayList<StoryModel>>()
    private var adapter: PostsAdapter? = null
    private var storyAdapter: StoryAdapter? = null
    private var isLoading = false

    override fun init() {

    }


    override fun setUpFragment() {


        homeViewModel =
            ViewModelProvider(this, HomeViewModelFactory()).get(HomeViewModel::class.java)

        if (adapter == null) {
            adapter = PostsAdapter(this)
            rootView!!.postsRecyclerView.adapter = adapter
            rootView!!.postsRecyclerView.isNestedScrollingEnabled = false
            onLoadMore()
        }

        if (storyAdapter == null) {
            rootView!!.storyRecyclerView.layoutManager =
                LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            storyAdapter = StoryAdapter(rootView!!.storyRecyclerView, this)
            rootView!!.storyRecyclerView.adapter = storyAdapter
            homeViewModel.loadStories(null)

        }

        homeViewModel.posts.observe(this, Observer { list ->
            if (rootView!!.homeSwipeLayout.isRefreshing) rootView!!.homeSwipeLayout.isRefreshing =
                false
            if (list != null && list.isNotEmpty()) {
                posts = list
                adapter!!.setPostsList(posts)
                isLoading = false
                rootView!!.loaderProgressBar.visibility = View.GONE
            }
        })

        homeViewModel.getStoriesList().observe(this, Observer {
            if (rootView!!.homeSwipeLayout.isRefreshing) rootView!!.homeSwipeLayout.isRefreshing =
                false
            if (it != null && it.isNotEmpty()) {
                stories = it
                storyAdapter!!.setList(stories)
            }
        })

        rootView!!.homeSwipeLayout.setOnRefreshListener {
            if (rootView!!.homeSwipeLayout.isRefreshing) {
                posts.clear()
                adapter!!.setPostsList(posts)
                stories.clear()
                storyAdapter!!.setList(stories)

                homeViewModel.loadStories(null)
                homeViewModel.loadPosts(null)

            }
        }

        rootView!!.homeScrollView.viewTreeObserver
            .addOnScrollChangedListener {
                val layout = rootView!!.homeScrollView
                val view: View = layout.getChildAt(layout.childCount - 1) as View
                val diff: Int = view.bottom - (layout.height + layout.scrollY)
                if (diff == 0 && posts.size >= 6) {
                    if (!isLoading) {
                        onLoadMore()
                        isLoading = true
                    }
                }
            }
    }


    override fun getLayout() = R.layout.fragment_home

    override fun onLikeButtonClick(position: Int) {
        homeViewModel.likePost(position)
        adapter?.notifyItemChanged(position)
    }

    private fun startPostActionActivity(act: Activity, model: PostModel, position: Int) {
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

    private fun onLoadMore() {
        if (posts.isNotEmpty()) homeViewModel.loadPosts(posts[posts.size - 1])
        else homeViewModel.loadPosts(null)
        rootView!!.loaderProgressBar.visibility = View.VISIBLE
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.OPEN_DETAILED_POST && resultCode == RESULT_OK) {
            val position = data?.extras?.getInt("position")
            if (position != null) {
                val model = data.extras!!.getParcelable<PostModel>("model")
                posts[position] = model!!
                adapter?.notifyItemChanged(position)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStoryClick(position: Int) {
        val intent = Intent(activity, StoryViewActivity::class.java)
        val listJson = Gson().toJson(stories)
        intent.putExtra("storiesList", listJson)
        intent.putExtra("position", position)
        startActivity(intent)
    }

    override fun onLoadMoreStories() {
        if (stories.isNotEmpty()) homeViewModel.loadStories(stories.size)
    }
}