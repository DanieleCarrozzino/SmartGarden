package com.example.smartgarden.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartgarden.firebase.authentication.FirebaseAuthenticator
import com.example.smartgarden.firebase.storage.FirebaseFirestoreInterface
import com.example.smartgarden.firebase.storage.FirebaseRealTimeDatabase
import com.example.smartgarden.navigation.Screen
import com.example.smartgarden.repository.DataInternalRepository
import com.example.smartgarden.utility.Utility
import com.example.smartgarden.utility.Utility.Companion.getCurrentDateTime
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth : FirebaseAuthenticator,
    private val firestore : FirebaseFirestoreInterface,
    private val database : FirebaseRealTimeDatabase,
    private val dataInternalRepository: DataInternalRepository
) : ViewModel(){

    val email       = mutableStateOf("")
    val password    = mutableStateOf("")
    val gardenName  = mutableStateOf("")

    private var signInResult = -1
    val signedIn        = mutableStateOf(false)
    val passwordError   = mutableStateOf(false)

    // Init garden
    val loadedGarden    = mutableStateOf(false)
    var listGardens     = MutableLiveData<List<DocumentSnapshot>>()
    private var garden          = hashMapOf<String, Any>()
    val creationGardenFinished  = mutableStateOf(false)

    // is the garden linked to a raspberry garden?
    private var linked = false

    init {
        auth.setCallback(::resultSignIn)
        firestore.setCallback(::resultCallbackFirestore)
        firestore.setCallbackGardens(::resultCallbackGardenFirestore)
    }

    fun signIn(){
        if(email.value.isNotEmpty() && password.value.isNotEmpty())
            auth.createUser(email.value, password.value)
    }

    fun signInWithGoogle(){
        auth.signInWithGoogle()
    }

    fun resultSignIn(result : Int){
        signInResult = result
        when(signInResult){
            200 -> {
                signedIn.value = true

                val user = hashMapOf<String, Any>(
                    "id" to (auth.currentUser?.uid ?: ""),
                    "email" to (auth.currentUser?.email ?: ""),
                    "date_creation" to getCurrentDateTime()
                )

                // Save user inside the realtime database
                database.insertForceNode(
                    "users",
                    listOf<String>(auth.currentUser?.uid ?: ""),
                    user)
            }
            400 -> passwordError.value  = true
            401 -> {} //TODO manage other type of errors
        }
    }

    /**
     * List of gardens request of this uid user,
     * the result will be back with resultCallbackGardenFirestore
     * */
    fun getFirestoreData(){
        firestore.getGardens(auth.currentUser?.uid ?: "")
    }

    /**
     * Firestore callback after a garden creation
     * */
    fun resultCallbackFirestore(data : String){
        if(data.contains("Success")){
            saveGardenAndGoOn(garden)
        }
    }

    /**
     * Save the new garden or an old one
     * and go on
     * */
    fun saveGardenAndGoOn(hashGarden : HashMap<String, Any>){
        // Save the new garden
        dataInternalRepository.saveGarden(hashGarden)
        if(hashGarden.containsKey("connected")){
            linked = hashGarden["connected"] == "true"
            if(hashGarden["connected"] == "true") {
                dataInternalRepository.setConnected()
                dataInternalRepository.saveRaspberryCode(hashGarden["raspberry_code"].toString())
            }
        }
        creationGardenFinished.value = true
    }

    fun getRoute() : String{
        return if(linked){
            Screen.Home.route
        } else Screen.InitRaspGarden.route
    }

    /**
     * getFirestoreData callback
     * */
    private fun resultCallbackGardenFirestore(list : List<DocumentSnapshot>){
        viewModelScope.launch(Dispatchers.Main) {
            listGardens.value = list.toMutableList()
        }
        loadedGarden.value = true
    }

    fun createGarden(){
        garden = hashMapOf(
            "name"              to gardenName.value,
            "date_creation"     to getCurrentDateTime(),
            "connected"         to false
        )

        if(auth.currentUser?.uid?.isNotEmpty() == true) {
            //Save inside the realtime database
            val key = database.insertNode(
                "gardens",
                listOf<String>(),
                garden)

            // Add unique id
            garden["id"] = key

            // Save inside the firestore to connect the user to the garden
            firestore.setGardens(auth.currentUser?.uid ?: "", key, garden)
        }
    }
}