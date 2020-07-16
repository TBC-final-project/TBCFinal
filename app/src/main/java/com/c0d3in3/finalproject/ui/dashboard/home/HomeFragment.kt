package com.c0d3in3.finalproject.ui.dashboard.home

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.c0d3in3.finalproject.Constants
import com.c0d3in3.finalproject.Constants.EDIT_POST_REQUEST_CODE
import com.c0d3in3.finalproject.CustomLinearLayoutManager
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.base.BaseFragment
import com.c0d3in3.finalproject.bean.PostModel
import com.c0d3in3.finalproject.bean.StoryModel
import com.c0d3in3.finalproject.bean.DialogCallback
import com.c0d3in3.finalproject.tools.Utils
import com.c0d3in3.finalproject.ui.dashboard.DashboardActivity
import com.c0d3in3.finalproject.ui.dashboard.stories.StoryAdapter
import com.c0d3in3.finalproject.ui.dashboard.stories.story_view.StoryViewActivity
import com.c0d3in3.finalproject.ui.post.EditPostActivity
import com.c0d3in3.finalproject.ui.post.PostsAdapter
import com.c0d3in3.finalproject.ui.post.comment.CommentsActivity
import com.c0d3in3.finalproject.ui.post.post_detailed.PostDetailedActivity
import com.c0d3in3.finalproject.ui.profile.ProfileActivity
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
                CustomLinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            storyAdapter = StoryAdapter(rootView!!.storyRecyclerView, this, false)
            rootView!!.storyRecyclerView.adapter = storyAdapter
            homeViewModel.loadStories(false)

        }

        homeViewModel.posts.observe(this, Observer { list ->
            if (rootView!!.homeSwipeLayout.isRefreshing) rootView!!.homeSwipeLayout.isRefreshing =
                false
            if (list != null) {
                if(list.isEmpty()) rootView!!.noPostsTextView.visibility = View.VISIBLE
                else if(rootView!!.noPostsTextView.visibility == View.VISIBLE) rootView!!.noPostsTextView.visibility  = View.GONE
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
                (activity as DashboardActivity).sendStoryList(stories)
            }
        })

        rootView!!.homeSwipeLayout.setOnRefreshListener {
            if (rootView!!.homeSwipeLayout.isRefreshing) {
                posts.clear()
                adapter!!.setPostsList(posts)
                stories.clear()
                storyAdapter!!.setList(stories)

                homeViewModel.loadStories(true)
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


    override fun setUpFragment() {

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
        startPostActionActivity(PostDetailedActivity(), posts[position], position)
    }

    override fun openProfile(position: Int) {
        val intent = Intent(activity, ProfileActivity::class.java)
        intent.putExtra("model", posts[position].postAuthorModel)
        startActivity(intent)
    }

    override fun openOptionsDialog(position: Int) {
        Utils.createPostOptionsDialog((activity as DashboardActivity), object:
            DialogCallback {
            override fun onEditPost() {
                val intent = Intent(activity, EditPostActivity::class.java)
                intent.putExtra("model", posts[position])
                intent.putExtra("position", position)
                startActivityForResult(intent, EDIT_POST_REQUEST_CODE)
            }

            override fun onDeletePost() {
                homeViewModel.deletePost(position)
            }
        })
    }

    private fun onLoadMore() {
        if (posts.isNotEmpty()) homeViewModel.loadPosts(posts[posts.size - 1])
        else homeViewModel.loadPosts(null)
        rootView!!.loaderProgressBar.visibility = View.VISIBLE
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.OPEN_DETAILED_POST && resultCode == RESULT_OK) {
            val position = data?.extras?.getInt("position")
            val model = data?.extras!!.getParcelable<PostModel>("model")
            if (position != null && model != null) {
                homeViewModel.posts.value?.set(position, model)
                posts[position] = model
                adapter!!.updateSingleItem(model, position)
            }
            if(model == null) homeViewModel.deletePost(position!!)
        }
        if(requestCode == Constants.EDIT_POST_REQUEST_CODE && resultCode == RESULT_OK){
            val position = data?.extras?.getInt("position")
            val model = data?.extras!!.getParcelable<PostModel>("model")
            if (position != null && model != null) {
                homeViewModel.posts.value?.set(position, model)
                posts[position] = model
                adapter!!.updateSingleItem(model, position)
            }
            if(model == null) homeViewModel.deletePost(position!!)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStoryClick(position: Int) {
        if(position != 0){
            val intent = Intent(activity, StoryViewActivity::class.java)
            val listJson = Gson().toJson(stories)
            intent.putExtra("storiesList", listJson)
            intent.putExtra("position", position)
            startActivity(intent)
        }
        else (activity as DashboardActivity).addStory()

    }

    override fun onLoadMoreStories() {
        if (stories.isNotEmpty())
            homeViewModel.loadStories(false)
    }

    override fun scrollToPosition(position: Int) {
        rootView!!.storyRecyclerView.scrollToPosition(position)
    }


}