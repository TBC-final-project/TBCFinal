package com.c0d3in3.finalproject.ui.profile

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.c0d3in3.finalproject.App
import com.c0d3in3.finalproject.Constants
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.base.BaseActivity
import com.c0d3in3.finalproject.bean.PostModel
import com.c0d3in3.finalproject.bean.UserModel
import com.c0d3in3.finalproject.databinding.ActivityProfileBinding
import com.c0d3in3.finalproject.tools.DialogCallback
import com.c0d3in3.finalproject.tools.Utils
import com.c0d3in3.finalproject.ui.post.EditPostActivity
import com.c0d3in3.finalproject.ui.post.PostsAdapter
import com.c0d3in3.finalproject.ui.post.comment.CommentsActivity
import com.c0d3in3.finalproject.ui.post.post_detailed.PostDetailedActivity
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : BaseActivity(), PostsAdapter.CustomPostCallback {

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var binding: ActivityProfileBinding
    private lateinit var userModel: UserModel
    private var adapter: PostsAdapter? = null
    private var posts = arrayListOf<PostModel>()
    private var isLoading = false
    override fun getLayout() = R.layout.activity_profile

    override fun init() {

        profileViewModel =
            ViewModelProvider(this, ProfileViewModelFactory()).get(ProfileViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, getLayout())

        userModel = intent.getParcelableExtra("model")

        binding.userModel = userModel
        if (userModel.userId == App.getCurrentUser().userId)
            initProfileToolbar()
        else initToolbar("${userModel.userFullName}'s profile")
        profileViewModel.setUser(userModel)

        if (adapter == null) {
            adapter = PostsAdapter(this)
            profilePostsRecyclerView.adapter = adapter
            profilePostsRecyclerView.isNestedScrollingEnabled = false
            onLoadMore()
        }

        profileViewModel.getUser().observe(this, Observer {
            if (it != null) {
                if (App.getCurrentUser().userId == it.userId) App.setCurrentUser(it)
                binding.userModel = it
                Glide.with(this).load(it.userProfileImage).into(profileImageView)
                binding.notifyChange()
            }
        })
        profileViewModel.getPosts().observe(this, Observer { list ->
            if (profileSwipeRefreshLayout.isRefreshing) profileSwipeRefreshLayout.isRefreshing =
                false
            if (list != null) {
                posts = list
                adapter!!.setPostsList(posts)
                isLoading = false
            }
        })

        profileSwipeRefreshLayout.setOnRefreshListener {
            if (profileSwipeRefreshLayout.isRefreshing) {
                posts.clear()
                adapter!!.setPostsList(posts)
                profileViewModel.updateUser()
                profileViewModel.loadPosts(null)

            }
        }

        profileScrollView.viewTreeObserver
            .addOnScrollChangedListener {
                val layout = profileScrollView
                val view: View = layout.getChildAt(layout.childCount - 1) as View
                val diff: Int = view.bottom - (layout.height + layout.scrollY)
                if (diff == 0 && posts.size >= 6) {
                    if (!isLoading) {
                        onLoadMore()
                        isLoading = true
                    }
                }
            }

        if (App.getCurrentUser().userId == userModel?.userId) {
            btnProfile.text = getString(R.string.update_profile)

            btnProfile.setOnClickListener {
                val intent = Intent(this, EditProfileActivity::class.java)
                intent.putExtra("model", userModel)
                startActivityForResult(intent, 0)
            }
        } else {
            btnProfile.setOnClickListener {
                if (App.getCurrentUser().userFollowing!!.contains(userModel.userId))
                    App.getCurrentUser().userFollowing!!.remove(userModel.userId)
                else
                    App.getCurrentUser().userFollowing!!.add(userModel.userId)
                profileViewModel.follow()
                checkFollowStatus()
            }
            checkFollowStatus()
        }

    }

    private fun checkFollowStatus() {
        if (App.getCurrentUser().userFollowing?.contains(userModel.userId)!!)
            btnProfile.text = getString(R.string.unfollow)
        else btnProfile.text = getString(R.string.follow)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 0 && resultCode == RESULT_OK)
            profileViewModel.updateUser()
        if (requestCode == Constants.OPEN_DETAILED_POST && resultCode == Activity.RESULT_OK) {
            val position = data?.extras?.getInt("position")
            val model = data?.extras!!.getParcelable<PostModel>("model")
            if (position != null && model != null) {
                profileViewModel.getPosts().value?.set(position, model)
                posts[position] = model
                adapter!!.updateSingleItem(model, position)
            }
            if(model == null) profileViewModel.deletePost(position!!)
        }
        if(requestCode == Constants.EDIT_POST_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val position = data?.extras?.getInt("position")
            val model = data?.extras!!.getParcelable<PostModel>("model")
            if (position != null && model != null) {
                profileViewModel.getPosts().value?.set(position, model)
                posts[position] = model
                adapter!!.updateSingleItem(model, position)
            }
            if(model == null) profileViewModel.deletePost(position!!)
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
        startPostActionActivity(PostDetailedActivity(), posts[position], position)
    }

    override fun openProfile(position: Int) {
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra("model", posts[position].postAuthorModel)
        startActivity(intent)
    }

    override fun openOptionsDialog(position: Int) {
        Utils.createPostOptionsDialog(this, object:
            DialogCallback {
            override fun onEditPost() {
                val intent = Intent(this@ProfileActivity, EditPostActivity::class.java)
                intent.putExtra("model", posts[position])
                intent.putExtra("position", position)
                startActivityForResult(intent, Constants.EDIT_POST_REQUEST_CODE)
            }

            override fun onDeletePost() {
                profileViewModel.deletePost(position)
            }
        })
    }

}