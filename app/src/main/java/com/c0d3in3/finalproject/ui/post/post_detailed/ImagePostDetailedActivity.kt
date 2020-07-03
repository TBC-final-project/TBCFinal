package com.c0d3in3.finalproject.ui.post.post_detailed

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.network.FirebaseHandler
import com.c0d3in3.finalproject.network.FirebaseHandler.POSTS_REF
import com.c0d3in3.finalproject.network.model.PostModel
import com.c0d3in3.finalproject.ui.post.comment.CommentsActivity
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.synthetic.main.activity_image_post_detailed.*

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

        FirebaseHandler.getDatabase().collection(POSTS_REF).document("0X2oV8kQYB3LcJx35mfJ").get().addOnSuccessListener {
            post = it.toObject<PostModel>()!!
            println("got post")
        }
    }
}
