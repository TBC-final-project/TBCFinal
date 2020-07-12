package com.c0d3in3.finalproject.ui.profile

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.c0d3in3.finalproject.App
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.network.FirebaseHandler
import com.c0d3in3.finalproject.network.FirebaseHandler.USERS_REF
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_profile.*
import java.util.*
import kotlin.collections.HashMap

class ProfileActivity : AppCompatActivity() {

    private val REQUEST_CODE = 10


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val user = FirebaseAuth.getInstance().currentUser
        tvProfileFullName.text = App.getCurrentUser().userFullName


        followerNumber.text = App.getCurrentUser().userFollowers?.size.toString()
        followingNumber.text = App.getCurrentUser().userFollowing?.size.toString()



        if (App.getCurrentUser().userId == user?.uid) {
            btnProfile.text = "Update Profile"

            btnProfile.setOnClickListener {
                val intent = Intent(this, EditProfileActivity::class.java)
                startActivityForResult(intent, REQUEST_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            val fullName = data?.getStringExtra("fullName")
            val imageUri = data?.getStringExtra("image")
            //val email = data?.getStringExtra("email")
            val userName = data?.getStringExtra("userName")

            tvProfileFullName.text = fullName.toString()

            val params: HashMap<String, Any> = HashMap() // დავამუღამო :დ
            params["userFullName"] = fullName.toString()
            params["userProfileCover"] = imageUri.toString()
            params["username"] = userName.toString()
            params["userFullNameToLowerCase"] = fullName.toString().toLowerCase(Locale.ROOT)
            //params["email"] = email.toString()

            // setting image to profile imageView
            Glide.with(applicationContext).load(imageUri).into(profileImageView)


            FirebaseHandler.getDatabase().collection(USERS_REF).document(
                FirebaseAuth.getInstance().currentUser!!.uid
            ).update(params)

        }
    }

}

