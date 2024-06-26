package com.example.smartgarden.firebase.storage

import android.graphics.Bitmap

interface FirebaseStorageInterface {

    /**
     * Get the url of the last image taken
     * */
    suspend fun getLastImageUrl(code : String) : String

    /**
     * Returns the url and the name
     * of the last image taken
     * */
    suspend fun getLastTakenImageUrl(code : String) : Pair<String, String>

    /**
     * Get the url of the time-laps
     * */
    suspend fun getTimeLapsUrl(code : String) : Pair<String, String>

}