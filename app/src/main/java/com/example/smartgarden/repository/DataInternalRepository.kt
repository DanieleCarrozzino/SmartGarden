package com.example.smartgarden.repository

import com.example.smartgarden.manager.SharedPreferenceManager
import com.example.smartgarden.utility.Utility.Companion.convertHashMapToJson
import com.example.smartgarden.utility.Utility.Companion.convertJsonToHashMap
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import javax.inject.Inject

class DataInternalRepository @Inject constructor(
    private val shared : SharedPreferenceManager
) {

    private val GARDEN_KEY      = "garden"
    private val FIREBASE_TOKEN  = "firebase_token"
    private val CONNECTED_RASP  = "connected_raspberry"

    /**
     * Save the garden while
     * completing the sign-in process.
     */
    fun saveGarden(garden : HashMap<String, String>){
        shared.putString(convertHashMapToJson(garden), GARDEN_KEY)
    }

    /**
     * Retrieve the garden currently associated
     * with this user from local storage.
     */
    fun getGarden() : HashMap<String, String>{
        val serializedHashMap = shared.getString(GARDEN_KEY)
        return try {
            convertJsonToHashMap(serializedHashMap)
        } catch(ex : Exception) {
            hashMapOf<String, String>()
        }
    }

    fun saveToken(token : String){
        shared.putString(token, FIREBASE_TOKEN)
    }

    fun getToken() : String{
        return shared.getString(FIREBASE_TOKEN)
    }

    fun setConnected() {
        shared.putBoolean(CONNECTED_RASP, true)
    }

    fun getConnected() : Boolean{
        return shared.getBoolean(CONNECTED_RASP)
    }

}