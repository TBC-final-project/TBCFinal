package com.c0d3in3.finalproject.ui.post.create_post

import com.c0d3in3.finalproject.App
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.base.BaseFragment
import com.c0d3in3.finalproject.bean.PostModel
import com.c0d3in3.finalproject.network.FirebaseHandler
import kotlinx.android.synthetic.main.fragment_create_post_text.*
import kotlinx.android.synthetic.main.fragment_create_post_text.view.*
import kotlinx.android.synthetic.main.fragment_create_post_text.view.etAddPostTitle
import java.util.ArrayList

class CreatePostTextFragment : BaseFragment() {


    private val postModel = PostModel()
    override fun init() {

        rootView!!.btnCreatePost.setOnClickListener {
//            postModel.postAuthor = App.getCurrentUser().userId
//            postModel.postTitle = etAddPostTitle.text.toString()
//            postModel.postDescription = etAddPostDescription.text.toString()
//
//            FirebaseHandler.getDatabase().collection("posts").add(postModel)
            addPost(etAddPostTitle.text.toString(), etAddPostDescription.text.toString(), postModel.postId)

        }




    }

    override fun setUpFragment() {

    }

    override fun getLayout() = R.layout.fragment_create_post_text

    private fun addPost(title: String, description: String, postId: String){
        postModel.postAuthor = App.getCurrentUser().userId
        postModel.postTitle = title
        postModel.postDescription = description
        postModel.postId = postId
        postModel.postTimestamp = System.currentTimeMillis()
        postModel.postLikes = arrayListOf()
        postModel.postComments = arrayListOf()

        FirebaseHandler.getDatabase().collection("posts").add(postModel)
    }
}