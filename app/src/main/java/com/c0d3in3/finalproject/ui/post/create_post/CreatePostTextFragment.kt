package com.c0d3in3.finalproject.ui.post.create_post

import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_create_post_text.*
import kotlinx.android.synthetic.main.fragment_create_post_text.view.*

class CreatePostTextFragment : BaseFragment() {


    override fun init() {

        rootView!!.btnCreatePost.setOnClickListener {

            (activity as CreatePostActivity).addPost(etAddPostTitle.text.toString(), etAddPostDescription.text.toString())
        }




    }

    override fun setUpFragment() {

    }

    override fun getLayout() = R.layout.fragment_create_post_text


}