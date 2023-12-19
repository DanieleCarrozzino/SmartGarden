package com.example.smartgarden.firebase.authentication

import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser

interface FirebaseAuthenticator {

    /**
     * Set the callback for every sign in method
     * */
    fun setCallback(callback: (Int) -> Unit)

    /**
     * Create a new user
     * */
    fun createUser(email : String , password : String)

    /**
     * Sign in with standard credentials
     * */
    fun signIn(email: String, password: String)

    /**
     * Sign in with google, to use this method
     * register the ActivityResultLauncher inside this class
     * with the method setActivityResultLauncher
     * */
    fun signInWithGoogle()

    /**
     * Manadatory to use signInWithGoogle
     * */
    fun setActivityResultLanucher(launcher : ActivityResultLauncher<IntentSenderRequest>)

    /**
     * Activity result
     * */
    fun responseSignInWithGoogle(result: ActivityResult)

    /**
     * Sign in with credential (used by google sign in)
     * */
    fun signInWithCredential(credential: AuthCredential)

    /**
     * Get current user
     * return null if there is not
     * */
    var currentUser : FirebaseUser?

    fun signOut()
}