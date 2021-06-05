package com.uvg.singlesignon

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val TAG: String = "Sign In"
    private val RC_SIGN_IN: Int = 1
    var ver:Boolean=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("AIzaSyBGWZApmRscTK9fN3w8BCsGmC1JuyPWdGo")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        var btnIniciar = findViewById<Button>(R.id.btnIniciarSesion)
        btnIniciar.setOnClickListener {
            signIn()
        }

        var siguiente = findViewById<Button>(R.id.sig)
        siguiente.setOnClickListener(){
            maps()
        }
    }
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)

    }

    private fun maps(){
        val intent: Intent = Intent(this, Maps::class.java)
        startActivity(intent)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN)
        {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try
            {// Google Sign In was successful, authenticate with Firebase

                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)



            }
            catch (e: ApiException) {// Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }


        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {// Sign in success, update UI with the signed-in user's information
                     Log.d(TAG, "signInWithCredential:success")
                     val user = auth.currentUser//updateUI(user)}

                    //luego del login, empezar la actividad de maps
                    maps()
                }
                else {// If sign in fails, display a message to the user.
                     Log.w(TAG, "signInWithCredential:failure", task.exception)//updateUI(null)}}}
                }
            }
    }
}