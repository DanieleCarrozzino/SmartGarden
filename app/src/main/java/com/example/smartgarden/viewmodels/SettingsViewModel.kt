package com.example.smartgarden.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.smartgarden.firebase.authentication.FirebaseAuthenticator
import com.example.smartgarden.firebase.storage.FirebaseRealTimeDatabase
import com.example.smartgarden.objects.DatabaseEntry
import com.example.smartgarden.repository.DataInternalRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val database : FirebaseRealTimeDatabase,
    private val dataInternalRepository: DataInternalRepository,
    private val auth : FirebaseAuthenticator
): ViewModel() {

    private val raspberryCode = dataInternalRepository.getRaspberryCode()

    init {
        database.getNodeReference(DatabaseEntry.Raspberry.key, listOf<String>(raspberryCode))
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    try {
                        val key     = dataSnapshot.key
                        val value   = dataSnapshot.getValue(object : GenericTypeIndicator<HashMap<String, Any>>() {})

                        Log.d("ThresholdViewModel", value.toString())

                        val settings = value ?: hashMapOf()

                        Log.d("ViewModel", settings["max_temperature"].toString())
                        Log.d("ViewModel", settings["max_watering"].toString())
                        Log.d("ViewModel", settings["min_watering"].toString())
                        Log.d("ViewModel", settings["notify_temperature"].toString())
                        Log.d("ViewModel", settings["min_temperature"].toString())

                        percMin.intValue = settings["min_watering"].toString().toInt()
                        percMax.intValue = settings["max_watering"].toString().toInt()
                        percMinTemperature.intValue = settings["min_temperature"].toString().toInt()
                        percMaxTemperature.intValue = settings["max_temperature"].toString().toInt()
                        enabledTemperature.value = settings["notify_temperature"].toString().toBoolean()
                    }
                    catch(ex : Exception){
                        Log.w("ViewModel", ex.message.toString())
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
    }

    // Main Settings
    var activated = mutableStateOf(true)

    // hydration perc
    var percMin = mutableIntStateOf(34)
    var percMax = mutableIntStateOf(62)
    var enabled = mutableStateOf(false)
    var alpha   = mutableFloatStateOf(0.3f)

    var percMinTemperature  = mutableIntStateOf(12)
    var percMaxTemperature  = mutableIntStateOf(36)
    var alphaTemperature    = mutableFloatStateOf(0.3f)

    // Notifications
    var enabledTemperature      = mutableStateOf(false)
    var enabledNotifications    = mutableStateOf(false)
    var enabledIrrigation       = mutableStateOf(false)

    fun changeLimits(){
        val rules = hashMapOf<String, Any>(
            "min_watering"          to percMin.intValue,
            "max_watering"          to percMax.intValue,
            "min_temperature"       to percMinTemperature.intValue,
            "max_temperature"       to percMaxTemperature.intValue,
            "notify"                to enabledNotifications.value,
            "notify_temperature"    to enabledTemperature.value,
            "notify_irrigation"     to enabledIrrigation.value
        )

        // Save user inside the realtime database
        database.updateNode(
            DatabaseEntry.Raspberry.key,
            listOf<String>(raspberryCode),
            rules)
    }

    fun setActivation(value : Boolean){
        val rules = hashMapOf<String, Any>(
            "activated"          to value
        )

        // Save user inside the realtime database
        database.updateNode(
            DatabaseEntry.Raspberry.key,
            listOf<String>(raspberryCode),
            rules)
    }

    fun setParam(value : Any, key : String){
        val rules = hashMapOf<String, Any>(
            key to value
        )

        // Save user inside the realtime database
        database.updateNode(
            DatabaseEntry.Raspberry.key,
            listOf<String>(raspberryCode),
            rules)
    }

    fun signout(){
        dataInternalRepository.setDisconnected()
        auth.signOut()
    }

}