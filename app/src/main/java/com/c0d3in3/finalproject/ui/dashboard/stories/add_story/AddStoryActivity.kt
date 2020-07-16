package com.c0d3in3.finalproject.ui.dashboard.stories.add_story

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.c0d3in3.finalproject.App
import com.c0d3in3.finalproject.Constants.USER_STORIES_REF
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.base.BaseActivity
import com.c0d3in3.finalproject.bean.StoryModel
import com.c0d3in3.finalproject.bean.UserModel
import com.c0d3in3.finalproject.databinding.ActivityAddStoryBinding
import com.c0d3in3.finalproject.network.FirebaseHandler
import com.c0d3in3.finalproject.tools.ImageUploadCallback
import com.c0d3in3.finalproject.tools.Utils
import com.c0d3in3.finalproject.ui.post.create_post.CreatePostActivity
import kotlinx.android.synthetic.main.fragment_create_post_image.*

class AddStoryActivity : BaseActivity() {


    private lateinit var imageUri: Uri

    override fun getLayout() = R.layout.activity_add_story


    override fun init() {
        imageUri = Uri.parse(intent.getStringExtra("imageUri"))

        val binding: ActivityAddStoryBinding = DataBindingUtil.setContentView(this, getLayout())
        binding.userModel = App.getCurrentUser()

        Glide.with(this).load(imageUri).into(binding.storyImageView)

        binding.addStoryButton.setOnClickListener{
            binding.addStoryButton.isClickable = false
            uploadImage()
        }

        binding.storyCloseButton.setOnClickListener {
            finish()
        }
    }

    private fun uploadImage(){
        Utils.uploadImage("$USER_STORIES_REF/${App.getCurrentUser().userId}_${(0..10000).random()}", imageUri, object: ImageUploadCallback{
            override fun onFinish(downloadUrl: String) {
                addStory(downloadUrl.toString())
            }
        })
    }

    private fun addStory(imageUrl: String){
        val user = App.getCurrentUser()
        val storyModel = StoryModel()
        storyModel.storyAuthorId = user.userId
        storyModel.storyCreatedAt = System.currentTimeMillis()
        storyModel.storyValidUntil = storyModel.storyCreatedAt+10000000
        storyModel.storyImage = imageUrl.toString()
        val mStoriesCollection = FirebaseHandler.getDatabase().collection("${FirebaseHandler.USERS_REF}/${user.userId}/${FirebaseHandler.STORIES_REF}")
        mStoriesCollection.add(storyModel)
        Toast.makeText(this, "Your story was added", Toast.LENGTH_SHORT).show()
        finish()
    }
}
