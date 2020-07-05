package com.c0d3in3.finalproject.ui.post.post_detailed

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.network.FirebaseHandler
import com.c0d3in3.finalproject.network.FirebaseHandler.POSTS_REF
import com.c0d3in3.finalproject.network.model.CommentModel
import com.c0d3in3.finalproject.network.model.PostModel
import com.c0d3in3.finalproject.tools.Utils
import com.c0d3in3.finalproject.tools.Utils.checkLike
import com.c0d3in3.finalproject.tools.Utils.getTimeDiff
import com.c0d3in3.finalproject.tools.Utils.likePost
import com.c0d3in3.finalproject.ui.auth.UserInfo
import com.c0d3in3.finalproject.ui.post.comment.CommentsActivity
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.synthetic.main.activity_image_post_detailed.*
import kotlinx.android.synthetic.main.app_bar_layout.view.*

class ImagePostDetailedActivity : AppCompatActivity() {


    private lateinit var post : PostModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_post_detailed)
        init()
    }

    private fun init(){
        commentButton.setOnClickListener {
            val intent = Intent(this, CommentsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("post", post)
            startActivity(intent)
        }

        favoriteButton.setOnClickListener {
            likePost()
        }

        FirebaseHandler.getDatabase().collection(POSTS_REF).document("0X2oV8kQYB3LcJx35mfJ").get().addOnSuccessListener {
            post = it.toObject<PostModel>()!!
            println("got post")
            updatePostUI()
        }
    }

    private fun likePost(){
        val likePos = post.postLikes?.let { checkLike(it) }
        likePost(post)
        if(likePos != -1)
            favoriteButton.setImageResource(R.mipmap.ic_unfavorite)
        else
            favoriteButton.setImageResource(R.mipmap.ic_favorited)
        likesTextView.text = "${post.postLikes!!.size} likes"
//        val likePos = post.postLikes?.let { checkLike(it) }
//        if(likePos != -1){
//            if (likePos != null) {
//                post.postLikes!!.removeAt(likePos)
//            }
//            favoriteButton.setImageResource(R.mipmap.ic_unfavorite)
//        }
//
//        else{
//            post.postLikes!!.add(UserInfo.userInfo)
//            favoriteButton.setImageResource(R.mipmap.ic_favorited)
//        }
//        likesTextView.text = "${post.postLikes!!.size} likes"
//        val postRef =  FirebaseHandler.getDatabase().collection(POSTS_REF).document(post.postId)
//        FirebaseHandler.getDatabase().runTransaction { transaction ->
//            transaction.update(postRef, "postLikes", post.postLikes)
//            null
//        }.addOnSuccessListener { Log.d("postLikes", "Transaction success!") }
//            .addOnFailureListener { e -> Log.d("postLikes", "Transaction failure.", e) }
    }

    private fun updatePostUI(){
        likesTextView.text = "${post.postLikes!!.size} likes"
        commentsTextView.text = "${post.postComments!!.size} comments"
        timestampTextView.text = getTimeDiff(post.postTimestamp)
        descriptionTextView.text = post.postDescription
        //imagePostToolbar.titleTV.text = "${post.postAuthor?.userFullName} post"
    }
}
