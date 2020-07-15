package com.c0d3in3.finalproject.ui.post.create_post

import android.widget.Toast
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


    override fun init() {

        rootView!!.btnCreatePost.setOnClickListener {
            if(etAddPostTitle.text.isEmpty() || etAddPostDescription.text.isEmpty())
                return@setOnClickListener Toast.makeText(activity , "You need to fill all fields", Toast.LENGTH_LONG).show()
            (activity as CreatePostActivity).addPost(
                etAddPostTitle.text.toString(),
                etAddPostDescription.text.toString()
            )

        }


    }

    override fun setUpFragment() {

    }

    override fun getLayout() = R.layout.fragment_create_post_text


}