package com.example.smartgarden.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.smartgarden.firebase.authentication.FirebaseAuthenticator
import com.example.smartgarden.firebase.authentication.FirebaseFirestoreImpl
import com.example.smartgarden.firebase.authentication.FirebaseRealTimeDatabase
import com.example.smartgarden.repository.DataInternalRepository
import com.example.smartgarden.utility.Utility.Companion.convertJsonToHashMap
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
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

    fun startGardenListener(){
        garden = dataInternalRepository.getGarden()
        database.getNodeReference("gardens", listOf<String>(auth.currentUser?.uid ?: "", garden["id"].toString()))
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val key     = dataSnapshot.key
                    val value   = dataSnapshot.getValue(String::class.java)

                    garden = convertJsonToHashMap(value.toString())

                    // Update layout
                    // garden.value...
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
    }
}