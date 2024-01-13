package com.example.smartgarden.viewmodels

import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.smartgarden.firebase.storage.FirebaseRealTimeDatabase
import com.example.smartgarden.objects.DatabaseEntry
import com.example.smartgarden.repository.DataInternalRepository
import com.example.smartgarden.utility.Utility
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ThresholdViewModel @Inject constructor(
    private val database : FirebaseRealTimeDatabase,
    private val dataInternalRepository: DataInternalRepository
): ViewModel() {

    private val raspberryCode = dataInternalRepository.getRaspberryCode()

    // hydration perc
    var percMin = mutableIntStateOf(34)
    var percMax = mutableIntStateOf(62)
    var enabled = mutableStateOf(false)
    var alpha   = mutableFloatStateOf(0.3f)

    var percMinTemperature  = mutableIntStateOf(12)
    var percMaxTemperature  = mutableIntStateOf(36)
    var enabledTemperature  = mutableStateOf(false)
    var alphaTemperature    = mutableFloatStateOf(0.3f)

    fun changeLimits(){
        val rules = hashMapOf<String, Any>(
            "min_watering"          to percMin.intValue,
            "max_watering"          to percMax.intValue,
            "min_temperature"       to percMinTemperature.intValue,
            "max_temperature"       to percMaxTemperature.intValue,
            "notify_temperature"    to enabledTemperature.value
        )

        // Save user inside the realtime database
        database.updateNode(
            DatabaseEntry.Raspberry.key,
            listOf<String>(raspberryCode),
            rules)
    }

}