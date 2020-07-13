package com.c0d3in3.finalproject.ui.profile

import android.content.Intent
import androidx.databinding.DataBindingUtil
import com.c0d3in3.finalproject.App
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.base.BaseActivity
import com.c0d3in3.finalproject.bean.UserModel
import com.c0d3in3.finalproject.databinding.ActivityProfileBinding

class ProfileActivity : BaseActivity() {


    private lateinit var binding: ActivityProfileBinding

    override fun getLayout() = R.layout.activity_profile

    override fun init() {
        binding = DataBindingUtil.setContentView(this, getLayout())

        val userModel = intent.getParcelableExtra<UserModel>("model")

        binding.userModel = userModel
        initToolbar("Profile")


        if (App.getCurrentUser().userId == userModel?.userId) {
            binding.btnProfile.text = getString(R.string.update_profile)

            binding.btnProfile.setOnClickListener {
                val intent = Intent(this, EditProfileActivity::class.java)
                intent.putExtra("model", userModel)
                startActivityForResult(intent, 0)
            }

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 0 && resultCode ==  RESULT_OK) {
            binding.userModel = (App.getCurrentUser())
            binding.notifyChange()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


}