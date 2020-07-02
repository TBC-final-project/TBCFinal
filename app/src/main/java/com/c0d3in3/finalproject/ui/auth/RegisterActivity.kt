package com.c0d3in3.finalproject.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log.d
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.c0d3in3.finalproject.BasePagerAdapter
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.network.FirebaseHandler
import com.c0d3in3.finalproject.network.model.UserModel
import com.c0d3in3.finalproject.ui.auth.register.ChooseEmailFragment
import com.c0d3in3.finalproject.ui.auth.register.ChooseNameFragment
import com.c0d3in3.finalproject.ui.auth.register.ChoosePasswordFragment
import com.c0d3in3.finalproject.ui.dashboard.DashboardActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : FragmentActivity() {

    private val userModel = UserModel()
    private lateinit var password: String
    private lateinit var email: String

    lateinit var adapter: BasePagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        adapter = BasePagerAdapter(supportFragmentManager)
        adapter.addFragment(ChooseNameFragment())
        adapter.addFragment(ChooseEmailFragment())
        adapter.addFragment(ChoosePasswordFragment())
        registerViewPager.adapter = adapter

    }

    fun getEmail(email: String){
        this.email = email
    }

    fun getName(firstName: String, lastName: String){
        userModel.userFullName = "$firstName $lastName"
    }

    fun getPassword(password: String){
        this.password = password
        registerUser()
    }

    private fun uploadUser(uid: String){
        FirebaseHandler.getDatabase().collection("users").document(uid).set(userModel).addOnSuccessListener {
            UserInfo.userInfo = userModel
            val intent = Intent(this, DashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    private fun registerUser() {
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
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

}
