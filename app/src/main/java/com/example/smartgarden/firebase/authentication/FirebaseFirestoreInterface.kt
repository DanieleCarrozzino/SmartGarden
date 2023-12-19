package com.example.smartgarden.firebase.authentication

import com.google.firebase.firestore.DocumentSnapshot

interface FirebaseFirestoreInterface {

    /**
     * set the callback result
     * */
    fun setCallback(callback : (String) -> Unit)
    fun setCallbackGardens(callback : (List<DocumentSnapshot>) -> Unit)

    /**
     * Get gardens of a specific uid user
     * */
    fun getGardens(uid : String)

    /**
     * Set gardens of a specific uid user
     * */
    fun setGardens(uid : String, key : String, garden : HashMap<String, String>)

}