package com.c0d3in3.finalproject.ui.profile

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.c0d3in3.finalproject.App
import com.c0d3in3.finalproject.Constants
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.base.BaseActivity
import com.c0d3in3.finalproject.bean.PostModel
import com.c0d3in3.finalproject.bean.UserModel
import com.c0d3in3.finalproject.databinding.ActivityProfileBinding
import com.c0d3in3.finalproject.ui.dashboard.home.HomeViewModel
import com.c0d3in3.finalproject.ui.dashboard.home.HomeViewModelFactory
import com.c0d3in3.finalproject.ui.post.PostsAdapter
import com.c0d3in3.finalproject.ui.post.comment.CommentsActivity
import com.c0d3in3.finalproject.ui.post.post_detailed.ImagePostDetailedActivity
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.fragment_home.view.*

class ProfileActivity : BaseActivity(), PostsAdapter.CustomPostCallback {


    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var binding: ActivityProfileBinding
    private var adapter : PostsAdapter? = null
    private var posts = arrayListOf<PostModel>()
    private var isLoading = false

    override fun getLayout() = R.layout.activity_profile

    override fun init() {

        profileViewModel =
            ViewModelProvider(this, ProfileViewModelFactory()).get(ProfileViewModel::class.java)

        binding = DataBindingUtil.setContentView(this, getLayout())

        val userModel = intent.getParcelableExtra<UserModel>("model")

        binding.userModel = userModel
        initToolbar("Profile")

        profileViewModel.setUserId(userModel.userId)

        if (adapter == null) {
            adapter = PostsAdapter(this)
            profilePostsRecyclerView.adapter = adapter
            profilePostsRecyclerView.isNestedScrollingEnabled = false
            onLoadMore()
        }

        profileViewModel.getPosts().observe(this, Observer { list ->
            if (profileSwipeRefreshLayout.isRefreshing) profileSwipeRefreshLayout.isRefreshing =
                false
            if (list != null && list.isNotEmpty()) {
                posts = list
                adapter!!.setPostsList(posts)
                isLoading = false
            }
        })

        profileSwipeRefreshLayout.setOnRefreshListener {
            if (profileSwipeRefreshLayout.isRefreshing) {
                posts.clear()
                adapter!!.setPostsList(posts)

                profileViewModel.loadPosts(null)

            }
        }

//        rootView!!.homeScrollView.viewTreeObserver
//            .addOnScrollChangedListener {
//                val layout = rootView!!.homeScrollView
//                val view: View = layout.getChildAt(layout.childCount - 1) as View
//                val diff: Int = view.bottom - (layout.height + layout.scrollY)
//                if (diff == 0 && posts.size >= 6) {
//                    if (!isLoading) {
//                        onLoadMore()
//                        isLoading = true
//                    }
//                }
//            }

        if (App.getCurrentUser().userId == userModel?.userId) {
            binding.btnProfile.text = getString(R.string.update_profile)

            binding.btnProfile.setOnClickListener {
                val intent = Intent(this, EditProfileActivity::class.java)
                intent.putExtra("model", userModel)
                startActivityForResult(intent, 0)
            }

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 0 && resultCode ==  RESULT_OK) {
            binding.userModel = (App.getCurrentUser())
            binding.notifyChange()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun onLoadMore() {
        if (posts.isNotEmpty()) profileViewModel.loadPosts(posts[posts.size - 1])
        else profileViewModel.loadPosts(null)
    }

    override fun onLikeButtonClick(position: Int) {
        profileViewModel.likePost(position)
        adapter?.notifyItemChanged(position)
    }

    private fun startPostActionActivity(act: Activity, model: PostModel, position: Int) {
        val intent = Intent(this, act::class.java)
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

    override fun openProfile(position: Int) {
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra("model", posts[position].postAuthorModel)
        startActivity(intent)
    }
}

