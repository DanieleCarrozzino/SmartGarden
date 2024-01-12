package com.example.smartgarden.firebase.storage

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

class FirebaseFirestoreImpl @Inject constructor() : FirebaseFirestoreInterface {

    /* Firestore, user data */
    val db = Firebase.firestore
    /* callback */
    private lateinit var callback : (String) -> Unit
    private lateinit var callbackGetGardens : (List<DocumentSnapshot>) -> Unit

    override fun setCallback(callback : (String) -> Unit){
        this.callback = callback
    }

    override fun setCallbackGardens(callback : (List<DocumentSnapshot>) -> Unit){
        this.callbackGetGardens = callback
    }

    override fun getGardens(uid: String) {
        db.collection(uid)
            .get()
            .addOnSuccessListener { result ->
                callbackGetGardens(result.documents)
            }
            .addOnFailureListener { exception ->
                callbackGetGardens(listOf())
            }
    }

    override fun setGardens(uid: String, key : String, garden : HashMap<String, Any>) {
        db.collection(uid).document(key).set(garden)
            .addOnSuccessListener { data ->
                callback("Success!")
            }
            .addOnFailureListener { exception ->
                callback("Error!")
            }
    }

    override fun saveToken(uid: String, token : String){
        val hash = hashMapOf<String, String>(
            "token" to token
        )
        db.collection(uid).document("firebase_token").set(hash)
    }


}