package com.c0d3in3.finalproject.ui.post.post_detailed

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.databinding.DataBindingUtil
import com.c0d3in3.finalproject.base.BaseActivity
import com.c0d3in3.finalproject.Constants
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.databinding.ActivityImagePostDetailedBinding
import com.c0d3in3.finalproject.network.model.PostModel
import com.c0d3in3.finalproject.tools.Utils.checkLike
import com.c0d3in3.finalproject.tools.Utils.likePost
import com.c0d3in3.finalproject.ui.auth.UserInfo
import com.c0d3in3.finalproject.ui.post.comment.CommentsActivity
import kotlinx.android.synthetic.main.activity_image_post_detailed.*
import kotlinx.android.synthetic.main.app_bar_layout.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.properties.Delegates


class ImagePostDetailedActivity : BaseActivity() {


    private lateinit var post: PostModel
    private var position by Delegates.notNull<Int>()

    override fun getLayout() = R.layout.activity_image_post_detailed

    override fun init() {
        post = intent.extras!!.getParcelable("model")!!
        position = intent.extras!!.get("position")!! as Int

        val binding : ActivityImagePostDetailedBinding = DataBindingUtil.setContentView(this, getLayout())
        binding.imagePostModel = post

        initToolbar("${post.postAuthorModel?.userFullName}'s post")

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
    //override fun getToolbarTitle() = "${post.postAuthor?.userFullName}'s post"

    private fun likePost() {
        CoroutineScope(Dispatchers.Main).launch {
            val likePos = post.postLikes?.let { checkLike(it) }
            if (likePos != null) {
                if (likePos >= 0) {
                    post.postLikes!!.removeAt(likePos)
                    likeButton.setImageResource(R.mipmap.ic_unfavorite)
                } else {
                    post.postLikes?.add(UserInfo.userInfo.userId)
                    likeButton.setImageResource(R.mipmap.ic_favorited)
                }
            }

            likePost(post)
            likesTextView.text = "${post.postLikes?.size ?: 0} likes"
        }
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
            if (model != null) {
                post = model
                commentsTextView.text = "${post.postComments?.size ?: 0} comments"
            }
        }
    }
}
