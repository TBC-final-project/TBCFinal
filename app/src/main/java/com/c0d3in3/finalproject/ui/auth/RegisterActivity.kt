package com.c0d3in3.finalproject.ui.auth

import android.content.Intent
import android.net.Uri
import android.util.Log.d
import android.widget.Toast
import com.c0d3in3.finalproject.App
import com.c0d3in3.finalproject.Constants.USER_PROFILE_PICTURES_REF
import com.c0d3in3.finalproject.base.BaseActivity
import com.c0d3in3.finalproject.base.BasePagerAdapter
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.network.FirebaseHandler
import com.c0d3in3.finalproject.bean.UserModel
import com.c0d3in3.finalproject.bean.ImageUploadCallback
import com.c0d3in3.finalproject.tools.Utils
import com.c0d3in3.finalproject.ui.auth.register.*
import com.c0d3in3.finalproject.ui.dashboard.DashboardActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*
import kotlin.properties.Delegates

class RegisterActivity : BaseActivity() {

    private val userModel = UserModel()
    private lateinit var password: String
    private lateinit var email: String
    private var googleAuth by Delegates.notNull<Boolean>()
    private val auth = FirebaseAuth.getInstance()
    private var imageUri : Uri? = null
    lateinit var adapter: BasePagerAdapter


    override fun getLayout() = R.layout.activity_register

    override fun init(){
        userModel.userProfileImage = "https://firebasestorage.googleapis.com/v0/b/postit-tbc.appspot.com/o/profile.png?alt=media&token=cd2c28cf-43cc-44d6-ad41-123c1f212fc8"
        googleAuth = intent.getBooleanExtra("googleAuth", false)
        adapter =
            BasePagerAdapter(supportFragmentManager)
        adapter.addFragment(ChooseNameFragment())
        adapter.addFragment(ChooseProfileImageFragment())
        adapter.addFragment(ChooseUsernameFragment())
        if(googleAuth) {
            email = auth.currentUser?.email.toString()
            userModel.userId = auth.currentUser?.uid.toString()
            userModel.userRegisterDate = System.currentTimeMillis()
        }
        else{
            adapter.addFragment(ChooseEmailFragment())
            adapter.addFragment(ChoosePasswordFragment())
        }
        registerViewPager.adapter = adapter
    }


    fun getEmail(email: String){
        this.email = email
        registerViewPager.currentItem = registerViewPager.currentItem + 1
    }

    fun getImage(imageUri: String){
        this.imageUri = Uri.parse(imageUri)
        registerViewPager.currentItem = registerViewPager.currentItem + 1
    }

    fun getName(firstName: String, lastName: String){
        userModel.userFullName = "$firstName $lastName"
        userModel.userFullNameToLowerCase = userModel.userFullName.toLowerCase(Locale.ROOT)
        registerViewPager.currentItem = registerViewPager.currentItem + 1
    }

    fun getPassword(password: String){
        this.password = password
        registerUser()
    }

    fun getUsername(username: String){
        userModel.username = username
        registerViewPager.currentItem = registerViewPager.currentItem + 1
        if(googleAuth) uploadUser(auth.currentUser!!.uid)
    }

    private fun uploadUser(uid: String){
        userModel.userFollowers = arrayListOf()
        userModel.userFollowing = arrayListOf()
        FirebaseHandler.getDatabase().collection("users").document(uid).set(userModel).addOnSuccessListener {
            if(imageUri != null) uploadImage()
            else{
                App.setCurrentUser(userModel)
                val intent = Intent(this, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }
    }

    private fun registerUser() {
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    userModel.userId = auth.currentUser?.uid.toString()
                    userModel.userFollowers = arrayListOf()
                    userModel.userFollowing = arrayListOf()
                    userModel.userRegisterDate = System.currentTimeMillis()
                    uploadUser(auth.currentUser!!.uid)
                } else {
                    // If sign in fails, display a message to the user.
                    d("SignUp", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        this, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this, PreAuthActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }

                // ...
            }
    }

    private fun uploadImage() {
        Utils.uploadImage("$USER_PROFILE_PICTURES_REF/${userModel.userId}", imageUri!!, object :
            ImageUploadCallback {
            override fun onFinish(downloadUrl: String) {
                App.setCurrentUser(userModel)
                App.getCurrentUser().userProfileImage = downloadUrl
                FirebaseHandler.getDatabase().collection(FirebaseHandler.USERS_REF)
                    .document(App.getCurrentUser().userId)
                    .update("userProfileImage", downloadUrl)
                val intent = Intent(this@RegisterActivity, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }

        })
    }

}