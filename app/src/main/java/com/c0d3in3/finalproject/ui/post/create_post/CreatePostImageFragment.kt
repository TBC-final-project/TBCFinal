package com.c0d3in3.finalproject.ui.post.create_post

import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import com.bumptech.glide.Glide
import com.c0d3in3.finalproject.App
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.base.BaseFragment
import com.c0d3in3.finalproject.image_chooser.EasyImage
import com.c0d3in3.finalproject.image_chooser.ImageChooserUtils
import com.c0d3in3.finalproject.image_chooser.MediaFile
import com.c0d3in3.finalproject.image_chooser.MediaSource
import com.c0d3in3.finalproject.network.FirebaseHandler
import com.c0d3in3.finalproject.network.FirebaseHandler.POSTS_REF
import com.c0d3in3.finalproject.tools.ImageUploadCallback
import com.c0d3in3.finalproject.tools.Utils
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.fragment_create_post_image.*
import kotlinx.android.synthetic.main.fragment_create_post_image.view.*

class CreatePostImageFragment: BaseFragment(){

    private var imageFile: MediaFile? = null

    override fun init() {

        rootView!!.btnCreatePost.setOnClickListener {
            if(etAddPostTitle.text.isEmpty() || etAddPostDescription.text.isEmpty())
                return@setOnClickListener Toast.makeText(activity , "You need to fill all fields", Toast.LENGTH_LONG).show()
            if(imageFile == null) return@setOnClickListener Utils.createDialog(activity as CreatePostActivity, "Error", "You must select image")
            uploadImage()
        }

        rootView!!.createPostImage.setOnClickListener{
            ImageChooserUtils.choosePhoto(activity as CreatePostActivity, this)
        }
    }

    override fun setUpFragment() {
    }

    private fun uploadImage() {
        Utils.uploadImage("$POSTS_REF/${App.getCurrentUser().userId}_${(0..10000).random()}", imageFile!!.uri, object : ImageUploadCallback {
            override fun onFinish(downloadUrl: String) {
                (activity as CreatePostActivity).addPost(
                    etAddPostTitle.text.toString(),
                    etAddPostDescription.text.toString(),
                    downloadUrl
                )
            }

        })
    }

    override fun getLayout() = R.layout.fragment_create_post_image

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ImageChooserUtils.PERMISSIONS_REQUEST) {
            if (grantResults.size > 2) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    ImageChooserUtils.chooseResource(activity as CreatePostActivity)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ImageChooserUtils.easyImage.handleActivityResult(
            requestCode,
            resultCode,
            data,
            activity as CreatePostActivity,
            object : EasyImage.Callbacks {
                override fun onImagePickerError(error: Throwable, source: MediaSource) {

                }

                override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                    if (imageFiles.isNotEmpty()) {
                        imageFile = imageFiles[0]
                        Glide.with(App.getContext()).load(imageFile!!.uri).into(rootView!!.createPostImage)
                    }
                }

                override fun onCanceled(source: MediaSource) {

                }
            })
    }
}