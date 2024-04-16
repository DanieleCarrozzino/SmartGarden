package com.example.smartgarden.manager

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SharedPreferenceManager @Inject constructor(
    @ApplicationContext context : Context
){
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("GardenPrefs", Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()

    fun putString(value : String, key : String){
        editor.putString(key, value).apply()
    }

    fun getString(key : String) : String{
        return sharedPreferences.getString(key, "") ?: ""
    }

    fun putBoolean(key : String, value : Boolean){
        editor.putBoolean(key, value).apply()
    }

    fun getBoolean(key : String) : Boolean{
        return sharedPreferences.getBoolean(key, false) ?: false
    }
}