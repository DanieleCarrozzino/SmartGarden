package com.example.smartgarden.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.smartgarden.firebase.authentication.FirebaseAuthenticator
import com.example.smartgarden.firebase.authentication.FirebaseFirestoreImpl
import com.example.smartgarden.firebase.authentication.FirebaseRealTimeDatabase
import com.example.smartgarden.repository.DataInternalRepository
import com.example.smartgarden.utility.Utility.Companion.convertJsonToHashMap
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val auth : FirebaseAuthenticator,
    private val firestore : FirebaseFirestoreImpl,
    private val database : FirebaseRealTimeDatabase,
    private val dataInternalRepository: DataInternalRepository
) : ViewModel() {

    private lateinit var garden : HashMap<String, String>

    val name = mutableStateOf("")
    val date = mutableStateOf("")

    fun init(){
        startGardenListener()
        retrieveToken()
    }

    /**
     * Listening the changes of the garden
     * Sensors or other user
     * */
    private fun startGardenListener(){
        garden = dataInternalRepository.getGarden()
        database.getNodeReference("gardens", listOf<String>(auth.currentUser?.uid ?: "", garden["id"].toString()))
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val key     = dataSnapshot.key
                    val value   = dataSnapshot.getValue(String::class.java)

                    garden = convertJsonToHashMap(value.toString())

                    // Update layout
                    name.value = garden["name"].toString()
                    date.value = garden["dateCreation"].toString()
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
    }

    /**
     * Retrieve the token to get notification
     * */
    private fun retrieveToken(){

        val token = dataInternalRepository.getToken()
        if(token.isNotEmpty()) return

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val newToken = task.result
                Log.d("FCM Token", newToken)

                dataInternalRepository.saveToken(newToken)
                firestore.saveToken(auth.currentUser?.uid ?: "", newToken)
            } else {
                Log.e("FCM Token", "Fetching FCM token failed: ${task.exception}")
            }
        }
    }
}