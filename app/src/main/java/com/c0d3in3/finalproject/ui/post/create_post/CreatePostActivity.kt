package com.c0d3in3.finalproject.ui.post.create_post


import com.c0d3in3.finalproject.App
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.base.BaseActivity
import com.c0d3in3.finalproject.base.BasePagerAdapter
import com.c0d3in3.finalproject.bean.PostModel
import com.c0d3in3.finalproject.network.FirebaseHandler
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.android.synthetic.main.fragment_create_post_text.*


class CreatePostActivity : BaseActivity() {


    lateinit var adapter: BasePagerAdapter

    override fun getLayout() = R.layout.activity_create_post

    override fun init() {

        adapter =
            BasePagerAdapter(supportFragmentManager)
        adapter.addFragment(CreatePostImageFragment())
        adapter.addFragment(CreatePostTextFragment())


        createPostViewPager.adapter = adapter


    }



}
