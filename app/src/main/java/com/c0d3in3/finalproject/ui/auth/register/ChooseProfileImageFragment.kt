package com.c0d3in3.finalproject.ui.auth.register

import android.content.Intent
import com.bumptech.glide.Glide
import com.c0d3in3.finalproject.App
import com.c0d3in3.finalproject.base.BaseFragment
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.image_chooser.EasyImage
import com.c0d3in3.finalproject.image_chooser.ImageChooserUtils
import com.c0d3in3.finalproject.image_chooser.MediaFile
import com.c0d3in3.finalproject.image_chooser.MediaSource
import com.c0d3in3.finalproject.network.State
import com.c0d3in3.finalproject.network.UsersRepository
import com.c0d3in3.finalproject.tools.Utils
import com.c0d3in3.finalproject.ui.auth.RegisterActivity
import com.c0d3in3.finalproject.ui.post.create_post.CreatePostActivity
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.choose_email_layout.view.*
import kotlinx.android.synthetic.main.choose_email_layout.view.btnBack
import kotlinx.android.synthetic.main.choose_email_layout.view.btnNext
import kotlinx.android.synthetic.main.fragment_choose_profile_image.view.*
import kotlinx.android.synthetic.main.fragment_create_post_image.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChooseProfileImageFragment : BaseFragment() {

    private val usersRepository = UsersRepository()
    private var imageFile: MediaFile? = null

    override fun init() {
        rootView!!.btnNext.setOnClickListener {
            if(imageFile != null) (activity as RegisterActivity).getImage(imageFile!!.uri.toString())
        }

        rootView!!.btnSkip.setOnClickListener {
            (activity as RegisterActivity).registerViewPager.currentItem =
                (activity as RegisterActivity).registerViewPager.currentItem + 1
        }

        rootView!!.btnBack.setOnClickListener {
            (activity as RegisterActivity).registerViewPager.currentItem =
                (activity as RegisterActivity).registerViewPager.currentItem - 1
        }

        rootView!!.profileImageChooser.setOnClickListener {
            ImageChooserUtils.choosePhoto(activity as RegisterActivity, this)
        }
    }

    override fun setUpFragment() {

    }

    override fun getLayout() = R.layout.fragment_choose_profile_image

    private fun checkEmail(email: String) {
        CoroutineScope(Dispatchers.IO).launch {
            usersRepository.checkEmail(email).collect { state ->
                when (state) {
                    is State.Success -> {
                        val newUser = state.data
                        if (!newUser)
                            withContext(Dispatchers.Main) {
                                Utils.createDialog(
                                    activity as RegisterActivity,
                                    "Error",
                                    getString(R.string.email_is_taken)
                                )
                            }
                        else withContext(Dispatchers.Main) {
                            (activity as RegisterActivity).getEmail(
                                email
                            )
                        }
                    }

                    is State.Failed -> withContext(Dispatchers.Main) {
                        Utils.createDialog(
                            activity as RegisterActivity,
                            "Error",
                            state.message
                        )
                    }
                } // END when
            } // END collect

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        ImageChooserUtils.easyImage.handleActivityResult(
            requestCode,
            resultCode,
            data,
            activity as RegisterActivity,
            object : EasyImage.Callbacks {
                override fun onImagePickerError(error: Throwable, source: MediaSource) {

                }

                override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                    if (imageFiles.isNotEmpty()) {
                        imageFile = imageFiles[0]
                        Glide.with(App.getContext()).load(imageFile!!.uri)
                            .into(rootView!!.profileImageChooser)
                    }
                }

                override fun onCanceled(source: MediaSource) {

                }
            })
        super.onActivityResult(requestCode, resultCode, data)
    }


}