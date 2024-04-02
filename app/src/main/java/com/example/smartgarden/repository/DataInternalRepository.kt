package com.example.smartgarden.repository

import com.example.smartgarden.manager.SharedPreferenceManager
import com.example.smartgarden.utility.Utility.Companion.convertHashMapToJsonString
import com.example.smartgarden.utility.Utility.Companion.convertJsonToHashMap
import javax.inject.Inject

class DataInternalRepository @Inject constructor(
    private val shared : SharedPreferenceManager
) {

    private val GARDEN_KEY      = "garden"
    private val FIREBASE_TOKEN  = "firebase_token"
    private val RASPBERRY_CODE  = "raspberry_code"
    private val CONNECTED_RASP  = "connected_raspberry"

    /**
     * Save the garden while
     * completing the sign-in process.
     */
    fun saveGarden(garden : HashMap<String, Any>){
        shared.putString(convertHashMapToJsonString(garden), GARDEN_KEY)
    }

    /**
     * Retrieve the garden currently associated
     * with this user from local storage.
     */
    fun getGarden() : HashMap<String, Any>{
        val serializedHashMap = shared.getString(GARDEN_KEY)
        return try {
            convertJsonToHashMap(serializedHashMap)
        } catch(ex : Exception) {
            hashMapOf<String, Any>()
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

    fun setDisconnected() {
        shared.putBoolean(CONNECTED_RASP, false)
    }

    fun getConnected() : Boolean{
        return shared.getBoolean(CONNECTED_RASP)
    }

    /**
     * Raspberry code got from the
     * qr code, provisioning phase
     * */
    fun saveRaspberryCode(code : String){
        shared.putString(code, RASPBERRY_CODE)
    }

    fun getRaspberryCode() : String{
        return shared.getString(RASPBERRY_CODE)
    }

}