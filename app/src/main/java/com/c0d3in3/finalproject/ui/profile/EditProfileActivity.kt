package com.c0d3in3.finalproject.ui.profile

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import com.bumptech.glide.Glide
import com.c0d3in3.finalproject.App
import com.c0d3in3.finalproject.Constants.USER_PROFILE_PICTURES_REF
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.base.BaseActivity
import com.c0d3in3.finalproject.image_chooser.EasyImage
import com.c0d3in3.finalproject.image_chooser.ImageChooserUtils
import com.c0d3in3.finalproject.image_chooser.MediaFile
import com.c0d3in3.finalproject.image_chooser.MediaSource
import com.c0d3in3.finalproject.network.FirebaseHandler
import com.c0d3in3.finalproject.network.FirebaseHandler.USERS_REF
import com.c0d3in3.finalproject.network.State
import com.c0d3in3.finalproject.network.UsersRepository
import com.c0d3in3.finalproject.bean.ImageUploadCallback
import com.c0d3in3.finalproject.tools.Utils
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class EditProfileActivity : BaseActivity() {

    private var imageFile: MediaFile? = null
    private val model = App.getCurrentUser()

    override fun getLayout() = R.layout.activity_edit_profile

    override fun init() {

        initToolbar("Edit profile")

        val name = model.userFullName.split(" ")
        etEditProfileFirstName.setText(name[0])
        etEditProfileLastName.setText(name[1])
        etEditProfileUserName.setText(model.username)
        Glide.with(this).load(model.userProfileImage).into(editProfileImage)

        choosePhotoLayout.setOnClickListener{
            ImageChooserUtils.choosePhoto(this)
        }

        btnUpdateProfile.setOnClickListener {
            val firstName = etEditProfileFirstName.text.toString()
            val lastName = etEditProfileLastName.text.toString()
            val username = etEditProfileUserName.text.toString()
            val bio = etEditProfileBio.text.toString()

            if(lastName.isBlank() || firstName.isBlank() || username.isBlank()) return@setOnClickListener Utils.createDialog(this, "Error", getString(R.string.fields_are_empty))
            if(username != App.getCurrentUser().username)
                checkUsername(firstName, lastName, username,bio)
            else updateProfile(firstName, lastName, username, bio)
        }
    }

    private fun updateProfile(firstName: String, lastName: String, username: String, bio: String){
        App.getCurrentUser().userFullName = "$firstName $lastName"

        App.getCurrentUser().userFullNameToLowerCase = App.getCurrentUser().userFullName.toLowerCase(
            Locale.ROOT)
        App.getCurrentUser().username = username

        App.getCurrentUser().userProfileDescription = bio

        FirebaseHandler.getDatabase().collection(USERS_REF).document(
            App.getCurrentUser().userId
        ).set(App.getCurrentUser())
        if(imageFile != null){
            Utils.uploadImage("$USER_PROFILE_PICTURES_REF/${model.userId}", imageFile!!.uri, object:
                ImageUploadCallback {
                override fun onFinish(downloadUrl: String) {
                    FirebaseHandler.getDatabase().collection(USERS_REF).document(App.getCurrentUser().userId).update("userProfileImage", downloadUrl)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }

            })
        }
        else{
            setResult(Activity.RESULT_OK, intent)
            finish()
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
                        Glide.with(applicationContext).load(imageFile!!.uri).into(editProfileImage)
                    }
                }

                override fun onCanceled(source: MediaSource) {

                }
            })
    }

    private fun checkUsername(firstName: String, lastName: String, username: String, bio: String) {
        CoroutineScope(Dispatchers.IO).launch {
            UsersRepository().checkUser(username).collect { state ->
                when (state) {
                    is State.Success -> {
                        val model = state.data
                        if (model != null)
                            withContext(Dispatchers.Main) {
                                Utils.createDialog(
                                    this@EditProfileActivity,
                                    "Error",
                                    getString(R.string.username_is_taken)
                                )
                            }
                        else withContext(Dispatchers.Main) {
                            updateProfile(firstName,lastName,username,bio)
                        }
                    }

                    is State.Failed -> withContext(Dispatchers.Main) {
                        Utils.createDialog(
                            this@EditProfileActivity,
                            "Error",
                            state.message
                        )
                    }
                } // END when
            } // END collect
        }
    }
}
