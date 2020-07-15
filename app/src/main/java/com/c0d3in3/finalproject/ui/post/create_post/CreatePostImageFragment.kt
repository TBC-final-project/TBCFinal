package com.c0d3in3.finalproject.ui.post.create_post

import android.widget.Toast
import com.bumptech.glide.Glide
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.base.BaseFragment
import com.c0d3in3.finalproject.bean.PostModel
import com.c0d3in3.finalproject.image_chooser.ImageChooserUtils
import kotlinx.android.synthetic.main.fragment_create_post_image.*
import kotlinx.android.synthetic.main.fragment_create_post_image.view.*

class CreatePostImageFragment: BaseFragment(){

    val model = PostModel()
    override fun init() {


        rootView!!.choosePhoto.setOnClickListener {
            ImageChooserUtils.choosePhoto(activity as CreatePostActivity)
        }

        rootView!!.btnCreatePost.setOnClickListener {
            if(etAddPostTitle.text.isEmpty() || etAddPostDescription.text.isEmpty()){
                Toast.makeText(activity , "You need to fill all fields", Toast.LENGTH_LONG).show()
            }

            (activity as CreatePostActivity).addPost(etAddPostTitle.text.toString(), etAddPostDescription.text.toString())
        }

    }

    override fun setUpFragment() {
    }

    override fun getLayout() = R.layout.fragment_create_post_image



}