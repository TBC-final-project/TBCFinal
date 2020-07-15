package com.c0d3in3.finalproject.ui.post.create_post


import android.content.Intent
import android.content.pm.PackageManager
import com.bumptech.glide.Glide
import com.c0d3in3.finalproject.App
import com.c0d3in3.finalproject.Constants
import com.c0d3in3.finalproject.Constants.POST_TYPE_IMAGE
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
import kotlinx.android.synthetic.main.fragment_create_post_image.*
import kotlinx.android.synthetic.main.fragment_create_post_text.*


class CreatePostActivity : BaseActivity() {

    private var imageFile: MediaFile? = null
    private val postModel = PostModel()
    lateinit var adapter: BasePagerAdapter

    override fun getLayout() = R.layout.activity_create_post

    override fun init() {

        adapter =
            BasePagerAdapter(supportFragmentManager)
        adapter.addFragment(CreatePostImageFragment())
        adapter.addFragment(CreatePostTextFragment())


        createPostViewPager.adapter = adapter

    }

    fun addPost(title: String, description: String, postImage: String? = null){
        postModel.postAuthor = App.getCurrentUser().userId
        postModel.postTitle = title
        postModel.postDescription = description
        postModel.postTimestamp = System.currentTimeMillis()
        postModel.postLikes = arrayListOf()
        postModel.postComments = arrayListOf()

        if(postImage != null){
            postModel.postImage = postImage
            postModel.postType = POST_TYPE_IMAGE.toLong()
        } else {
            postModel.postType = POST_TYPE_IMAGE.toLong()
        }

        FirebaseHandler.getDatabase().collection("posts").add(postModel).addOnCompleteListener {
            FirebaseHandler.getDatabase().collection("posts").document(it.result!!.id).update("postId", it.result!!.id)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ImageChooserUtils.PERMISSIONS_REQUEST) {
            if (grantResults.size > 2) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    ImageChooserUtils.chooseResource(this)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ImageChooserUtils.easyImage.handleActivityResult(
            requestCode,
            resultCode,
            data,
            this,
            object : EasyImage.Callbacks {
                override fun onImagePickerError(error: Throwable, source: MediaSource) {

                }

                override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                    if (imageFiles.isNotEmpty()) {
                        imageFile = imageFiles[0]
                        Glide.with(applicationContext).load(imageFile!!.uri).into(imagePost)

                        val pictureRef = FirebaseHandler.getStorage().reference.child("posts/${postModel.postId}")
                        val uploadTask = pictureRef.putFile(imageFile!!.uri)
                        uploadTask.continueWithTask { task ->
                            if (!task.isSuccessful) {
                                task.exception?.let {
                                    throw it
                                }
                            }
                            pictureRef.downloadUrl
                        }.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val downloadUri = task.result
                                FirebaseHandler.getDatabase().collection("posts").document(postModel.postId).update("postImage", downloadUri.toString())
                            }
                        }
                    }
                }

                override fun onCanceled(source: MediaSource) {

                }
            })
    }


}
