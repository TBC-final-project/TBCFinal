package com.c0d3in3.finalproject.base

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.c0d3in3.finalproject.App
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.bean.UserModel
import com.c0d3in3.finalproject.network.FirebaseHandler
import com.c0d3in3.finalproject.tools.DialogCallback
import com.c0d3in3.finalproject.tools.Utils
import com.c0d3in3.finalproject.ui.auth.PreAuthActivity
import com.c0d3in3.finalproject.ui.profile.ProfileActivity
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
        if(App.getCurrentUser().userProfileImage.isNotEmpty()) Glide.with(applicationContext).load(
            App.getCurrentUser().userProfileImage).into(toolbarFrameLayout.profileImageButton)
        else toolbarFrameLayout.profileImageButton.setCircleBackgroundColorResource(android.R.color.black)
        if(title != null) toolbarFrameLayout.titleTV.text = title
        toolbarFrameLayout.profileImageButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("model", App.getCurrentUser())
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    fun initMainToolbar(title: String?){
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        if(title != null) toolbarFrameLayout.titleTV.text = title
        if(App.getCurrentUser().userProfileImage.isNotEmpty()) Glide.with(applicationContext).load(
            App.getCurrentUser().userProfileImage).into(toolbarFrameLayout.profileImageButton)
        else toolbarFrameLayout.profileImageButton.setCircleBackgroundColorResource(android.R.color.black)
        toolbarFrameLayout.messengerImageView.visibility = View.VISIBLE
        toolbarFrameLayout.messengerImageView.setOnClickListener{
            // TODO openMessenger
        }
        toolbarFrameLayout.profileImageButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("model", App.getCurrentUser())
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }
    fun initProfileToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        toolbarFrameLayout.profileImageButton.setImageResource(R.mipmap.ic_more)
        if(title != null) toolbarFrameLayout.titleTV.text = "Your profile"
        toolbarFrameLayout.profileImageButton.setOnClickListener {
            Utils.createOptionalDialog(this, "Log out", "Do you really want to log out?", object: DialogCallback{
                override fun onResponse(dialog: Dialog) {
                    dialog.dismiss()
                    FirebaseHandler.getAuth().signOut()
                    val intent = Intent(this@BaseActivity, PreAuthActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }

                override fun onCancel() {
                }

            })
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