package com.example.smartgarden.firebase.storage

import com.google.firebase.firestore.DocumentSnapshot

interface FirebaseFirestoreInterface {

    /**
     * set the callback result
     * */
    fun setCallback(callback : (String) -> Unit)
    fun setCallbackGardens(callback : (List<DocumentSnapshot>) -> Unit)

    /**************************
    *
    *         GARDENS
    *
    * **************************/

    /**
     * Get gardens of a specific uid user
     * */
    fun getGardens(uid : String)

    /**
     * Set gardens of a specific uid user
     * */
    fun setGardens(uid : String, key : String, garden : HashMap<String, Any>)

    /**
     * Save firebase messaging token
     * */
    fun saveToken(uid: String, token : String)

}