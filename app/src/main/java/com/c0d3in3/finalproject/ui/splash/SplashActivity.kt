package com.c0d3in3.finalproject.ui.splash

import android.content.Intent
import android.os.Handler
import android.util.Log.d
import com.c0d3in3.finalproject.App
import com.c0d3in3.finalproject.base.BaseActivity
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.network.FirebaseHandler
import com.c0d3in3.finalproject.network.FirebaseHandler.USERS_REF
import com.c0d3in3.finalproject.bean.UserModel
import com.c0d3in3.finalproject.ui.auth.PreAuthActivity
import com.c0d3in3.finalproject.ui.dashboard.DashboardActivity
import com.c0d3in3.finalproject.ui.profile.ProfileActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : BaseActivity() {

    private var dataLoaded = false
    private val handler by lazy {
        Handler()
    }
    private val auth = FirebaseAuth.getInstance()

    private val runnable = Runnable {
        stopSplash()
    }

    override fun getLayout() = R.layout.activity_splash

    override fun init() {
        if (auth.currentUser != null) {
            getUserData()
        }
    }



    private fun getUserData() {
        FirebaseHandler.getDatabase().collection(USERS_REF).document(auth.currentUser!!.uid).get()
            .addOnSuccessListener {
                val userInfo = it.toObject(UserModel::class.java)
                if (userInfo != null) {
                    App.setCurrentUser(userInfo)
                    dataLoaded = true
                    stopSplash()
                } else stopSplash()
            }
    }

    override fun onResume() {
        if (dataLoaded) stopSplash()
        if (auth.currentUser == null) handler.postDelayed(runnable, 3000)
        super.onResume()
    }

    override fun onPause() {
        handler.removeCallbacks(runnable)
        super.onPause()
    }


    private fun stopSplash() {
        if (dataLoaded) {
            val intent = Intent(this, ProfileActivity::class.java) // dashboard
            intent.putExtra("model", App.getCurrentUser())
            //d("useruser", App.getCurrentUser().userId)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        } else {
            val intent = Intent(this, PreAuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }
}