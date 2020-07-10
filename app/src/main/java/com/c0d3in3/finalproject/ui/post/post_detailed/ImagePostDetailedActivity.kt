package com.c0d3in3.finalproject.ui.post.post_detailed

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.c0d3in3.finalproject.Constants
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.base.BaseActivity
import com.c0d3in3.finalproject.databinding.ActivityImagePostDetailedBinding
import com.c0d3in3.finalproject.bean.PostModel
import com.c0d3in3.finalproject.ui.auth.UserInfo
import com.c0d3in3.finalproject.ui.post.comment.CommentsActivity
import kotlinx.android.synthetic.main.activity_image_post_detailed.*
import kotlin.properties.Delegates


class ImagePostDetailedActivity : BaseActivity() {


    private lateinit var post: PostModel
    private var position by Delegates.notNull<Int>()
    private lateinit var postViewModel: PostViewModel

    override fun getLayout() = R.layout.activity_image_post_detailed

    override fun init() {

        postViewModel =
            ViewModelProvider(this).get(PostViewModel::class.java)

        getModel()


        val binding : ActivityImagePostDetailedBinding = DataBindingUtil.setContentView(this, getLayout())

        initToolbar("${post.postAuthorModel?.userFullName}'s post")

        setListeners()

        postViewModel.getPostModel().observe(this, Observer {
            post = it
            binding.imagePostModel = post
        })

    }

    private fun getModel(){
        post = intent.extras!!.getParcelable("model")!!
        position = intent.extras!!.get("position")!! as Int

        postViewModel.setPostModel(post)

    }

    private fun setListeners(){
        commentButton.setOnClickListener {
            val intent = Intent(this, CommentsActivity::class.java)
            intent.putExtra("model", post)
            intent.putExtra("position", position)
            startActivityForResult(intent, Constants.OPEN_DETAILED_POST)
        }

        likeButton.setOnClickListener {
            likePost()
        }

        postImageView.setOnClickListener {
            hideBottomView()
        }
    }

    private fun hideBottomView() {
        if(bottomView.visibility == View.VISIBLE) bottomView.visibility = View.GONE
        else bottomView.visibility = View.VISIBLE
    }

    private fun likePost() {
        if(post.postLikes?.contains(UserInfo.userInfo.userId)!!) likeButton.setImageResource(R.mipmap.ic_unfavorite)
        else likeButton.setImageResource(R.mipmap.ic_favorited)
        postViewModel.likePost()
    }

    override fun onBackPressed() {
        val mIntent = Intent()
        mIntent.putExtra("model", post)
        mIntent.putExtra("position", position)
        setResult(Activity.RESULT_OK, mIntent)
        super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == Constants.OPEN_DETAILED_POST && resultCode == Activity.RESULT_OK){
            val model = data?.extras!!.getParcelable<PostModel>("model")
            if (model != null) postViewModel.setPostModel(model)
        }
    }
}