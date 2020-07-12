package com.c0d3in3.finalproject.ui.profile

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.image_chooser.EasyImage
import com.c0d3in3.finalproject.image_chooser.ImageChooserUtils
import com.c0d3in3.finalproject.image_chooser.MediaFile
import com.c0d3in3.finalproject.image_chooser.MediaSource
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.choose_resource_file.*

class EditProfileActivity : AppCompatActivity() {

    private var imageFile: MediaFile? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        choosePhoto.setOnClickListener{
            ImageChooserUtils.choosePhoto(this)
        }

        btnUpdateProfile.setOnClickListener {
            val intent = Intent()
            intent.putExtra("fullName", etEditProfileFullName.text.toString())
            intent.putExtra("image", imageFile?.uri.toString())
            intent.putExtra("userName", etEditProfileUserName.text.toString())
            intent.putExtra("email", etEditProfileEmail.text.toString())

            setResult(Activity.RESULT_OK, intent)
            finish()
        }

    }



    //fun choosePhotoOnClick(view: View) {
    //    ImageChooserUtils.choosePhoto(this)
    //}

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
                        Glide.with(applicationContext).load(imageFile!!.uri).into(editProfileImage)
                    }
                }

                override fun onCanceled(source: MediaSource) {

                }
            })
    }
}
