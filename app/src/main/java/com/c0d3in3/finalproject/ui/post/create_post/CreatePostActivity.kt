package com.c0d3in3.finalproject.ui.post.create_post


import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import com.bumptech.glide.Glide
import com.c0d3in3.finalproject.App
import com.c0d3in3.finalproject.Constants.POST_TYPE_IMAGE
import com.c0d3in3.finalproject.Constants.POST_TYPE_TEXT
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.base.BaseActivity
import com.c0d3in3.finalproject.base.BasePagerAdapter
import com.c0d3in3.finalproject.bean.PostModel
import com.c0d3in3.finalproject.image_chooser.EasyImage
import com.c0d3in3.finalproject.image_chooser.ImageChooserUtils
import com.c0d3in3.finalproject.image_chooser.MediaFile
import com.c0d3in3.finalproject.image_chooser.MediaSource
import com.c0d3in3.finalproject.network.FirebaseHandler
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.fragment_create_post_text.*


class CreatePostActivity : BaseActivity() {


    lateinit var adapter: BasePagerAdapter


    override fun getLayout() = R.layout.activity_create_post

    override fun init() {

        adapter =
            BasePagerAdapter(supportFragmentManager)
        adapter.addFragment(CreatePostImageFragment())
        adapter.addFragment(CreatePostTextFragment())

        initToolbar("Add a post")

        createPostViewPager.adapter = adapter


    }




    fun addPost(title: String, description: String, imageUrl: String? = null){
        val postModel = PostModel()
        postModel.postAuthor = App.getCurrentUser().userId
        postModel.postTitle = title
        postModel.postDescription = description
        postModel.postTimestamp = System.currentTimeMillis()
        postModel.postLikes = arrayListOf()
        postModel.postComments = arrayListOf()

        if(imageUrl != null){
            postModel.postImage = imageUrl
            postModel.postType = POST_TYPE_IMAGE.toLong()
        }
        else postModel.postType = POST_TYPE_TEXT.toLong()

        FirebaseHandler.getDatabase().collection("posts").add(postModel).addOnCompleteListener {
            FirebaseHandler.getDatabase().collection("posts").document(it.result!!.id).update("postId", it.result!!.id)
            Toast.makeText(this, "Post added", Toast.LENGTH_SHORT).show()
            finish()
        }
    }


}
