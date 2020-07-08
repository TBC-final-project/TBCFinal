package com.c0d3in3.finalproject.ui.auth

import android.content.Intent
import android.util.Log.d
import com.c0d3in3.finalproject.base.BaseActivity
import com.c0d3in3.finalproject.Constants.RC_SIGN_IN
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.extensions.setBold
import com.c0d3in3.finalproject.extensions.setUnderline
import com.c0d3in3.finalproject.network.FirebaseHandler
import com.c0d3in3.finalproject.network.FirebaseHandler.USERS_REF
import com.c0d3in3.finalproject.network.model.UserModel
import com.c0d3in3.finalproject.tools.Utils
import com.c0d3in3.finalproject.ui.dashboard.DashboardActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_pre_auth.*

class PreAuthActivity : BaseActivity() {

    private lateinit var gso : GoogleSignInOptions

    private lateinit var auth : FirebaseAuth

    private lateinit var googleSignInClient : GoogleSignInClient

    override fun getLayout() = R.layout.activity_pre_auth

    override fun init(){

        signInGoogleButton.setOnClickListener {
            gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            googleSignInClient =  GoogleSignIn.getClient(this, gso)
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        btnCreateAccount.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        auth = Firebase.auth

        welcomeTV.setBold(" ${getString(R.string.app_name)}")
        logInTextView.text = getString(R.string.have_an_account_already)
        logInTextView.setUnderline(" ${getString(R.string.login)}")

        logInTextView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }


    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    FirebaseHandler.getDatabase().collection(USERS_REF).document(auth.uid!!).get().addOnSuccessListener {
                        if(it.exists()){
                            UserInfo.userInfo = it.toObject<UserModel>()!!
                            val intent = Intent(this, DashboardActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                        else{
                            val intent = Intent(this, RegisterActivity::class.java)
                            intent.putExtra("googleAuth", true)
                            startActivity(intent)
                        }
                    }
                    //uploadUser(auth.currentUser!!.uid)
                } else {
                    d("GoogleSignIn", "signInWithCredential:failure", task.exception)
                    Utils.createDialog(this, "Error", "Google sign in failed")
                }

            }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                d("GoogleSignIn", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Utils.createDialog(this, "Error", "Google sign in failed")
                d("GoogleSignIn", "Google sign in failed", e)
                // ...
            }
        }
    }
}