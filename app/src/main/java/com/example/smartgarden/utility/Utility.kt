package com.example.smartgarden.utility

import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.util.Calendar
import java.util.Locale

class Utility {

    companion object{
        fun convertHashMapToJson(hash : HashMap<String, String>) : String{
            return Gson().toJson(hash).toString()
        }

        fun convertJsonToHashMap(json : String) : HashMap<String, String>{
            val hashMap: HashMap<String, String> = run {
                val mapType = object : TypeToken<HashMap<String, String>>() {}.type
                Gson().fromJson(json, mapType)
            }
            return hashMap
        }

        fun generateKey(){

        }

        fun getCurrentDateTime(): String {
            val currentDateTime = Calendar.getInstance().time
            val formatter = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ITALY) // Define your desired date-time format
            return formatter.format(currentDateTime)
        }
    }

}