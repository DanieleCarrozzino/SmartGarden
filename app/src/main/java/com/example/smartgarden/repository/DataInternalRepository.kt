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

    private val GARDEN_KEY = "garden"

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

}