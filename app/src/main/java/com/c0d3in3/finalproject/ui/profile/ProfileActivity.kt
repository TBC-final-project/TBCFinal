package com.c0d3in3.finalproject.ui.profile

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log.d
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.c0d3in3.finalproject.App
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.base.BaseActivity
import com.c0d3in3.finalproject.bean.UserModel
import com.c0d3in3.finalproject.databinding.ActivityImagePostDetailedBinding
import com.c0d3in3.finalproject.databinding.ActivityProfileBinding
import com.c0d3in3.finalproject.network.FirebaseHandler
import com.c0d3in3.finalproject.network.FirebaseHandler.USERS_REF
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_profile.*
import java.util.*
import kotlin.collections.HashMap

class ProfileActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)


    }

    override fun getLayout() = R.layout.activity_profile

    override fun init() {
        val binding: ActivityProfileBinding = DataBindingUtil.setContentView(this, getLayout())
        val userModel = intent.getParcelableExtra<UserModel>("model")

        binding.userModel = userModel

        if (App.getCurrentUser().userId == userModel?.userId) {
            binding.btnProfile.text = getString(R.string.update_profile)

            btnProfile.setOnClickListener {
                val intent = Intent(this, EditProfileActivity::class.java)
                intent.putExtra("model", userModel)
                startActivity(intent)
            }
        }

        d("useruser", userModel.userId)


    }

}

