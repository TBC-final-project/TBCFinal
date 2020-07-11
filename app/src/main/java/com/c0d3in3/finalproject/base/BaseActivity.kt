package com.c0d3in3.finalproject.base

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.c0d3in3.finalproject.UserInfo
import kotlinx.android.synthetic.main.app_bar_layout.*
import kotlinx.android.synthetic.main.app_bar_layout.view.*

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayout())

        init()
    }

    fun initToolbar(title: String?){
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        if(UserInfo.userInfo.userProfileImage.isNotEmpty()) Glide.with(applicationContext).load(
            UserInfo.userInfo.userProfileImage).into(toolbarFrameLayout.profileImageButton)
        else toolbarFrameLayout.profileImageButton.setCircleBackgroundColorResource(android.R.color.black)
        if(title != null) toolbarFrameLayout.titleTV.text = title
    }

    fun initMainToolbar(title: String?){
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        if(title != null) toolbarFrameLayout.titleTV.text = title
        if(UserInfo.userInfo.userProfileImage.isNotEmpty()) Glide.with(applicationContext).load(
            UserInfo.userInfo.userProfileImage).into(toolbarFrameLayout.profileImageButton)
        else toolbarFrameLayout.profileImageButton.setCircleBackgroundColorResource(android.R.color.black)
        toolbarFrameLayout.messengerImageView.visibility = View.VISIBLE
        toolbarFrameLayout.messengerImageView.setOnClickListener{
            // TODO openMessenger
        }
    }

    fun setToolbarTitle(title: String){
        toolbarFrameLayout.titleTV.text = title
    }

    abstract fun getLayout() : Int

    abstract fun init()

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //abstract fun getToolbarTitle() : String?
}