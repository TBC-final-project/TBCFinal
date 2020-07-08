package com.c0d3in3.finalproject.ui.post.post_detailed

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.c0d3in3.finalproject.Constants
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.network.model.PostModel
import com.c0d3in3.finalproject.tools.Utils.checkLike
import com.c0d3in3.finalproject.tools.Utils.getTimeDiff
import com.c0d3in3.finalproject.tools.Utils.likePost
import com.c0d3in3.finalproject.ui.auth.UserInfo
import com.c0d3in3.finalproject.ui.post.comment.CommentsActivity
import kotlinx.android.synthetic.main.activity_image_post_detailed.*
import kotlinx.android.synthetic.main.activity_image_post_detailed.commentButton
import kotlinx.android.synthetic.main.app_bar_layout.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class ImagePostDetailedActivity : AppCompatActivity() {


    private lateinit var post: PostModel
    private var position by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_post_detailed)
        init()
    }

    private fun init() {
        post = intent.extras!!.getParcelable("model")!!
        position = intent.extras!!.get("position")!! as Int
        updatePostUI()
        commentButton.setOnClickListener {
            val intent = Intent(this, CommentsActivity::class.java)
            intent.putExtra("model", post)
            intent.putExtra("position", position)
            startActivityForResult(intent, Constants.OPEN_DETAILED_POST)
        }

        likeButton.setOnClickListener {
            likePost()
        }


    }

    private fun likePost() {
        CoroutineScope(Dispatchers.Main).launch {
            val likePos = post.postLikes?.let { checkLike(it) }
            if (likePos != null) {
                if (likePos >= 0) {
                    post.postLikes!!.removeAt(likePos)
                    likeButton.setImageResource(R.mipmap.ic_unfavorite)
                } else {
                    post.postLikes?.add(UserInfo.userInfo)
                    likeButton.setImageResource(R.mipmap.ic_favorited)
                }
            }

            likePost(post)
            likesTextView.text = "${post.postLikes?.size ?: 0} likes"
        }
    }

    private fun updatePostUI() {
        val checkLike = post.postLikes?.let { checkLike(it) }
        if(checkLike != null && checkLike >= 0) likeButton.setImageResource(R.mipmap.ic_favorited)
        else likeButton.setImageResource(R.mipmap.ic_unfavorite)
        likesTextView.text = "${post.postLikes?.size ?: 0} likes"
        commentsTextView.text = "${post.postComments?.size ?: 0} comments"
        timestampTextView.text = getTimeDiff(post.postTimestamp)
        descriptionTextView.text = post.postDescription
        imagePostToolbar.titleTV.text = "${post.postAuthor?.userFullName}'s post"
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
