package com.example.smartgarden.firebase.authentication

import android.content.Context
import android.content.IntentSender
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult.Companion.EXTRA_SEND_INTENT_EXCEPTION
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class FirebaseAuthenticationImpl @Inject constructor(
    @ApplicationContext context : Context
) : FirebaseAuthenticator {

    /**
     * Lo UID è persistente nel tempo
     * anche dopo una nuova installazione,
     * importante per poter recuperare tutte
     * le informazioni di un utente solamente dal suo uid
     * */

    /* Firebase authenticator object */
    private var auth : FirebaseAuth = FirebaseAuth.getInstance()
    /* Google authentication */
    private var oneTapClient        : SignInClient = Identity.getSignInClient(context)
    private lateinit var launcher   : ActivityResultLauncher<IntentSenderRequest>
    /* Callbacks */
    private lateinit var callbackResult : (Int) -> Unit

    override fun setCallback(callback: (Int) -> Unit){
        callbackResult = callback
    }

    override fun createUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    currentUser = auth.currentUser
                    callbackResult(200)
                } else {
                    val exception = task.exception
                    if (exception is FirebaseAuthUserCollisionException) {
                        // Email is already in use, handle this case
                        signIn(email, password)
                    }
                }
            }
    }

    override fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    currentUser = auth.currentUser
                    callbackResult(200)
                } else {
                    val exception = task.exception
                    when (exception) {
                        is FirebaseAuthInvalidUserException -> {
                            // Invalid user (user doesn't exist)
                            // Handle accordingly
                            callbackResult(401)
                        }

                        is FirebaseAuthInvalidCredentialsException -> {
                            // Invalid credentials (wrong password)
                            // Handle accordingly
                            callbackResult(400)
                        }

                        else -> {
                            // Other authentication failures
                            // Handle other exceptions or errors
                            callbackResult(401)
                        }
                    }
                }
            }
    }

    override fun signInWithCredential(credential: AuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    currentUser = auth.currentUser
                    callbackResult(200)
                } else {
                    // If sign in fails, display a message to the user.
                    callbackResult(401)
                }
            }
    }

    override var currentUser : FirebaseUser? = auth.currentUser

    override fun signOut(){
        auth.signOut()
    }

    /* ***************************
     *
     *  GOOGLE AUTHENTICATION
     *
     * ***************************/

    override fun responseSignInWithGoogle(result: ActivityResult) {
        // Handle the result in onActivityResult method
        if (result.resultCode == ComponentActivity.RESULT_OK) {
            val googleCredential = oneTapClient.getSignInCredentialFromIntent(result.data)
            val idToken = googleCredential.googleIdToken
            when {
                idToken != null -> {
                    // Got an ID token from Google. Use it to authenticate
                    // with Firebase.
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    signInWithCredential(firebaseCredential)
                }
                else -> {
                    // Shouldn't happen.
                    Log.d("TAG", "No ID token!")
                }
            }
        } else {
            // Handle other cases, like errors or canceled actions
        }
    }

    override fun signInWithGoogle(){
        val signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId("880067905026-gvake2jkks5gjko0nlsdftb1o6tqa373.apps.googleusercontent.com")
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            .setAutoSelectEnabled(true)
            .build()


        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener() { result ->
                try {
                    val intentSenderRequest = IntentSenderRequest
                        .Builder(result.pendingIntent.intentSender).build()
                    launcher.launch(intentSenderRequest)
                } catch (e: IntentSender.SendIntentException) {
                    Log.e("TAG", "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener() { e ->
                Log.d(
                    "googleLoginButton",
                    "oneTapClient.beginSignIn:onError - " + e.message
                )
            }
    }

    override fun setActivityResultLanucher(launcher : ActivityResultLauncher<IntentSenderRequest>){
        this.launcher = launcher
    }
}