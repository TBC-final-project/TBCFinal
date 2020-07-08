package com.c0d3in3.finalproject.ui.auth

import android.content.Intent
import android.util.Log.d
import androidx.core.content.ContextCompat
import com.c0d3in3.finalproject.base.BaseActivity
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.extensions.isEmailValid
import com.c0d3in3.finalproject.extensions.setColor
import com.c0d3in3.finalproject.network.FirebaseHandler
import com.c0d3in3.finalproject.network.FirebaseHandler.USERS_REF
import com.c0d3in3.finalproject.network.model.UserModel
import com.c0d3in3.finalproject.tools.Utils
import com.c0d3in3.finalproject.tools.Utils.isValidEmail
import com.c0d3in3.finalproject.ui.dashboard.DashboardActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {

    override fun getLayout() = R.layout.activity_login

    override fun init(){
        emailET.isEmailValid()

        signUpTV.setColor(" ${getString(R.string.sign_up)}", ContextCompat.getColor(this,R.color.colorBlue))

        loginButton.setOnClickListener {
            logIn()
        }

        backButton.setOnClickListener {
            onBackPressed()
        }

        signUpTV.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }


    private fun logIn(){
        if(!isValidEmail(emailET.text.toString())) return Utils.createDialog(this, "Error", "Email is not valid!")
        if(passwordET.text.toString().isBlank()) return Utils.createDialog(this, "Error", "Password is empty")
        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(emailET.text.toString(), passwordET.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    FirebaseHandler.getDatabase().collection(USERS_REF).document(auth.currentUser!!.uid).get().addOnSuccessListener {
                        UserInfo.userInfo = it.toObject<UserModel>()!!

                        val intent = Intent(this, DashboardActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }
                } else {
                    d("SignIn", "signInWithEmail:failure", task.exception)
                    Utils.createDialog(this, "Error", "Authentication failed")
                }
            }

    }
}
