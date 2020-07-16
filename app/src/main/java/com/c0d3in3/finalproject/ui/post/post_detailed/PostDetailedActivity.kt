package com.c0d3in3.finalproject.ui.post.post_detailed

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.c0d3in3.finalproject.App
import com.c0d3in3.finalproject.Constants
import com.c0d3in3.finalproject.Constants.POST_TYPE_TEXT
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.base.BaseActivity
import com.c0d3in3.finalproject.bean.PostModel
import com.c0d3in3.finalproject.databinding.ImagePostDetailedLayoutBinding
import com.c0d3in3.finalproject.databinding.TextPostDetailedLayoutBinding
import com.c0d3in3.finalproject.tools.DialogCallback
import com.c0d3in3.finalproject.tools.Utils
import com.c0d3in3.finalproject.ui.post.EditPostActivity
import com.c0d3in3.finalproject.ui.post.comment.CommentsActivity
import kotlinx.android.synthetic.main.image_post_detailed_layout.*


class PostDetailedActivity : BaseActivity() {


    private var post: PostModel? = null
    private var position : Int? = null
    private lateinit var postViewModel: PostViewModel

    override fun getLayout() : Int{
        getModel()

        return if(post!!.postType.toInt()== POST_TYPE_TEXT) R.layout.text_post_detailed_layout
        else R.layout.image_post_detailed_layout
    }

    override fun init() {


        postViewModel =
            ViewModelProvider(this).get(PostViewModel::class.java)

        post?.let { postViewModel.setPostModel(it) }

        if(post!!.postType.toInt()== POST_TYPE_TEXT){
            val binding : TextPostDetailedLayoutBinding = DataBindingUtil.setContentView(this, getLayout())
            postViewModel.getPostModel().observe(this, Observer {
                post = it
                if(it == null) onBackPressed()
                else binding.postModel = post
            })
        }
        else{
            val binding : ImagePostDetailedLayoutBinding = DataBindingUtil.setContentView(this, getLayout())
            postViewModel.getPostModel().observe(this, Observer {
                post = it
                if(it == null) onBackPressed()
                else binding.imagePostModel = post
            })

            postImageView.setOnClickListener {
                hideBottomView()
            }
        }



        initToolbar("${post!!.postAuthorModel?.userFullName}'s post")

        setListeners()

    }

    private fun getModel(){
        post = intent.extras?.getParcelable("model")!!
        if(intent.hasExtra("position")) position = intent.extras?.getInt("position")

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

        if(post!!.postAuthor == App.getCurrentUser().userId){
            spinnerButton.visibility= View.VISIBLE
            spinnerButton.setOnClickListener {
                openOptionsDialog()
            }
        }

    }

    private fun hideBottomView() {
        if(bottomView.visibility == View.VISIBLE) bottomView.visibility = View.GONE
        else bottomView.visibility = View.VISIBLE
    }

    private fun likePost() {
        if(post!!.postLikes?.contains(App.getCurrentUser().userId)!!) likeButton.setImageResource(R.mipmap.ic_unfavorite)
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
        if(requestCode == Constants.EDIT_POST_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val model = data?.extras!!.getParcelable<PostModel>("model")
            if (model != null)  postViewModel.setPostModel(model)
        }
    }

    private fun openOptionsDialog() {
        Utils.createPostOptionsDialog(this, object:
            DialogCallback {
            override fun onEditPost() {
                val intent = Intent(this@PostDetailedActivity, EditPostActivity::class.java)
                intent.putExtra("model", post)
                startActivityForResult(intent, Constants.EDIT_POST_REQUEST_CODE)
            }

            override fun onDeletePost() {
                postViewModel.deletePost()
            }
        })
    }
}