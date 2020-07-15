package com.c0d3in3.finalproject.ui.post.create_post

import android.widget.Toast
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_create_post_image.*
import kotlinx.android.synthetic.main.fragment_create_post_image.view.*

class CreatePostImageFragment: BaseFragment(){

    override fun init() {

        rootView!!.btnCreatePost.setOnClickListener {
            if(etAddPostTitle.text.isEmpty() || etAddPostDescription.text.isEmpty()){
                Toast.makeText(activity , "You need to fill all fields", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun setUpFragment() {
    }

    override fun getLayout() = R.layout.fragment_create_post_image
}