package com.example.smartgarden.viewmodels

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.Analyzer
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.PathNode
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.smartgarden.firebase.authentication.FirebaseAuthenticator
import com.example.smartgarden.firebase.storage.FirebaseFirestoreImpl
import com.example.smartgarden.firebase.storage.FirebaseRealTimeDatabase
import com.example.smartgarden.manager.RaspberryConnectionManager
import com.example.smartgarden.navigation.Screen
import com.example.smartgarden.objects.CHART_TYPE
import com.example.smartgarden.objects.ChartObject
import com.example.smartgarden.objects.GardenKeys
import com.example.smartgarden.repository.DataInternalRepository
import com.example.smartgarden.utility.Utility.Companion.convertJsonToHashMap
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.values
import com.google.firebase.messaging.FirebaseMessaging
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.random.Random

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext context : Context,
    private val auth : FirebaseAuthenticator,
    private val firestore : FirebaseFirestoreImpl,
    private val database : FirebaseRealTimeDatabase,
    private val dataInternalRepository: DataInternalRepository,
    private val raspberryConnection : RaspberryConnectionManager
) : ViewModel() {

    // Sensor
    val vibrator : Vibrator? = getSystemService(context, Vibrator::class.java)

    private lateinit var garden : HashMap<String, Any>

    val name = mutableStateOf("")
    val date = mutableStateOf("")

    private val filesDirInternal : String = context.filesDir.absolutePath
    val doingConfiguration  = mutableStateOf(false)
    val statusConfiguration = MutableLiveData<RaspberryConnectionManager.RaspberryStatus>()
    val connected           = mutableStateOf(false)

    // Visible Chart
    private var animationChartIsRunning = false
    val chart = MutableLiveData<ChartObject>()
    var lastListPathNode = listOf<PathNode>()
    val typeChart = MutableLiveData<CHART_TYPE>()
    var changeScreenClick = false
    var pressingScreen = false

    // 3 Seek bar values
    val temperatureValue    = MutableLiveData<Int>()
    val hydrationValue      = MutableLiveData<Int>()
    val wellnessValue       = MutableLiveData<Int>()

    // Gesture values
    var maxDistanceGesture = 500 // TODO change this value looking for the width screen size
    private var offsetPress     = mutableStateOf(Offset(0f, 0f))
    private var fingerMovement  = mutableStateOf(Offset(0f, 0f))
    private var vibrateYet      = false

    /* 0 - 1 define a perc value*/
    var transitionGestureY = mutableFloatStateOf(0f)
    var transitionGestureX = mutableFloatStateOf(0f)

    enum class POSITION_TYPE{
        LEFT,
        RIGHT,
        UP,
        DOWN,
        DEFAULT,
    }

    fun init(){

        // To know if I connect myself already to
        // the raspberry pi
        connected.value = /*dataInternalRepository.getConnected()*/true

        // Start real time database listener
        // to keep watch every changes
        startGardenListener()

        // Start charts animation from the values
        // get from the garden
        animateCharts()

        // Update firebase token
        // TODO update firestore and real time database
        retrieveToken()
    }

    /**
     * Listening the changes of the garden
     * Sensors or other user
     * */
    private fun startGardenListener(){
        garden = dataInternalRepository.getGarden()
        database.getNodeReference("gardens", listOf<String>(garden["id"].toString()))
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    try {
                        val key     = dataSnapshot.key
                        val value   = dataSnapshot.getValue(object : GenericTypeIndicator<HashMap<String, Any>>() {})

                        garden = value ?: hashMapOf()
                        garden["id"] = key.toString()

                        Log.d("ViewModel", garden["temperatures"].toString())
                        Log.d("ViewModel", garden["humidity"].toString())
                        Log.d("ViewModel", garden["soil_moistures"].toString())
                        Log.d("ViewModel", garden["avarage_temperature"].toString())
                        Log.d("ViewModel", garden["avarage_humidity"].toString())
                        Log.d("ViewModel", garden["avarage_soil_moisture"].toString())

                        // Update layout
                        name.value = garden["name"].toString()
                        date.value = garden["date_creation"].toString()

                        // Unlock the home screen
                        // Now I got the real data
                        // update and see
                        // TODO unlock the screen
                    }
                    catch(ex : Exception){
                        Log.w("ViewModel", ex.message.toString())
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
    }

    private fun animateCharts(){

        // Starting the animation only once
        if(animationChartIsRunning) return
        animationChartIsRunning = true

        viewModelScope.launch(Dispatchers.IO) {
            while(true){

                /*
                * Get random values
                * to create a moving chart into
                * the main screen
                * */
                //TODO get real list of values
                var list = mutableListOf<Float>()
                for (i in 0 until 30){
                    list.add(Random.nextFloat() * 50f)
                }

                // Define the type of the cart
                val type = CHART_TYPE
                    .entries
                    .toTypedArray()[((chart.value?.type?.ordinal ?: 0) + 1) % CHART_TYPE.entries.size]

                /*
                * Modify the list depending
                * on what is the visualized type
                * of chart
                * */
                list = when(type){
                    /*
                    * Get the entries of the temperature,
                    * if the visualizing type is the TEMP
                    * */
                    CHART_TYPE.TEMPERATURE -> {
                        if(garden.containsKey(GardenKeys.Temperatures.key)){
                            // inside of this list there are
                            // the last 30 entries of the last
                            // measures
                            garden[GardenKeys.Temperatures.key] as MutableList<Float>
                        }
                        else
                            list
                    }
                    CHART_TYPE.SOIL_MOISTURE -> {
                        if(garden.containsKey(GardenKeys.Moisture.key)){
                            // inside of this list there are
                            // the last 30 entries of the last
                            // measures
                            (garden[GardenKeys.Moisture.key] as ArrayList<ArrayList<Float>>).map {
                                it.last().toFloat()
                            }.toMutableList()
                        }
                        else
                            list
                    }
                    else -> {
                        list
                    }
                }

                launch(Dispatchers.Main) {
                    //Set the title of the chart and the type
                    chart.value = ChartObject(
                        list,
                        type,
                        type.toString()
                    )

                    // Define type and color
                    typeChart.value = type

                    // Define 3 main values
                    // return 0 if the garden is disconnected
                    when(type){
                        CHART_TYPE.TEMPERATURE -> {
                            temperatureValue.value  = if(!connected.value) 0 else list.last().toInt().absoluteValue
                        }
                        CHART_TYPE.SOIL_MOISTURE -> {
                            wellnessValue.value     = if(!connected.value) 0 else list.last().toInt()
                        }
                        CHART_TYPE.HUMIDITY -> {
                            hydrationValue.value    = if(!connected.value) 0 else Random.nextInt(1, 100)
                        }
                    }
                }

                /*
                * Change main screen for a user click
                * or for timeout
                * */
                var count = 0
                while((count < 700 && !changeScreenClick) || pressingScreen || !connected.value){
                    count ++
                    delay(10)
                }
                changeScreenClick   = false
            }
        }
    }


    var positionType = mutableStateOf(POSITION_TYPE.DEFAULT)
    val thresholdGesture = 0.12f
    fun managePointerEvent(event : PointerEvent, navController: NavController){
        // handle pointer event
        if (event.type == PointerEventType.Press) {
            Log.d("HomeScreen", "Press")

            // Save the offset
            offsetPress.value = event.changes.first().position

            // Pressing state, to block the animation
            pressingScreen = true

        } else if (event.type == PointerEventType.Move) {

            // Calculate new position
            fingerMovement.value = event.changes.first().position
            moveTransitionsGesture()

            Log.d("Home", "Move Y ${transitionGestureY.floatValue}")
            Log.d("Home", "Move X ${transitionGestureX.floatValue}")

            if(transitionGestureY.floatValue > thresholdGesture){
                positionType.value = POSITION_TYPE.UP
                if(!vibrateYet){
                    vibrate(50)
                    vibrateYet = true
                }
            }
            else if(transitionGestureY.floatValue < -thresholdGesture){
                positionType.value = POSITION_TYPE.DOWN
                if(!vibrateYet){
                    vibrate(50)
                    vibrateYet = true
                }
            }
            else if(transitionGestureX.floatValue > thresholdGesture){
                positionType.value = POSITION_TYPE.LEFT
                if(!vibrateYet){
                    vibrate(50)
                    vibrateYet = true
                }
            }
            else if(transitionGestureX.floatValue < -thresholdGesture){
                positionType.value = POSITION_TYPE.RIGHT
                if(!vibrateYet){
                    vibrate(50)
                    vibrateYet = true
                }
            }
            else{
                positionType.value = POSITION_TYPE.DEFAULT
                if(vibrateYet){
                    vibrate(50)
                    vibrateYet = false
                }
            }

        } else if (event.type == PointerEventType.Release) {

            when(positionType.value){
                POSITION_TYPE.LEFT -> {
                    Log.d("Home", "LEFT")
                    navController.navigate(Screen.InstantCamera.route)
                }
                POSITION_TYPE.RIGHT -> {
                    Log.d("Home", "RIGHT")
                    navController.navigate(Screen.Switch.route)
                }
                POSITION_TYPE.UP -> {
                    Log.d("Home", "UP")
                    navController.navigate(Screen.Settings.route)
                }
                POSITION_TYPE.DOWN -> {
                    Log.d("Home", "DOWN")
                    navController.navigate(Screen.Camera.route)
                }
                POSITION_TYPE.DEFAULT -> {
                    if (connected.value)
                        changeScreenClick = true
                }
            }

            // Set all the movement to 0
            transitionGestureX.floatValue = 0f
            transitionGestureY.floatValue = 0f
            vibrateYet          = false
            pressingScreen      = false
            positionType.value  = POSITION_TYPE.DEFAULT

        } else if (event.type == PointerEventType.Exit) {
            pressingScreen = false
        }
    }

    fun moveTransitionsGesture(){
        // perc of X movement
        transitionGestureX.floatValue = ((
                (fingerMovement.value.x - offsetPress.value.x)
                ) / maxDistanceGesture).toDouble()
            .div(4.0).toFloat()
            .coerceAtMost(1f)
            .coerceAtLeast(-1f)

        // perc of Y movement
        transitionGestureY.floatValue = ((
                (fingerMovement.value.y - offsetPress.value.y)
                ) / maxDistanceGesture).toDouble()
            .div(4.0).toFloat()
            .coerceAtMost(1f)
            .coerceAtLeast(-1f)
    }

    /**
     * Vibrate the phone to create a
     * simile back pressed button
     * */
    fun vibrate(duration: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // For Android O and above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            }
            else{
                vibrator?.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
            }
        } else {
            // For devices below Android O
            @Suppress("DEPRECATION")
            vibrator?.vibrate(duration)
        }
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


    /**
     * Connect the camera preview to the screen and
     * start the qr code scanner
     * */
    fun bindCameraPreview(cameraProvider: ProcessCameraProvider, previewView: PreviewView, lifecycleOwner: LifecycleOwner) {
        // Init preview
        val preview = Preview.Builder().build()

        // Init qr scanner
        raspberryConnection.initializeScanner(::raspberryCallback, ::startProvisioning)

        // Define the camera
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        preview.setSurfaceProvider(previewView.surfaceProvider)

        // Start analyzing
        val imageAnalyzer = ImageAnalysis.Builder()
            .build()
            .also {
                it.setAnalyzer(Dispatchers.IO.asExecutor(), raspberryConnection)
            }

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalyzer
            )
        } catch (exc: Exception) {
            // Handle camera binding exception
        }
    }

    /**
     * Receive the callback from the RaspberryConnectionManager,
     * and update the layout
     * */
    fun raspberryCallback(
        result : RaspberryConnectionManager.RaspberryStatus
    ){
        updateConfigStatus(result)
    }

    /**
     * Provisioning
     * get the code from the qr,
     * create the configurator file to send,
     * send the file to the raspberry,
     * sending the finish statement
     * */
    fun startProvisioning(code : String){

        // Starting the layout
        doingConfiguration.value = true
        updateConfigStatus(RaspberryConnectionManager.RaspberryStatus.INIT)

        viewModelScope.launch(Dispatchers.IO) {
            // To update the layout in case of error
            try {
                // Get info from the qr code
                updateConfigStatus(RaspberryConnectionManager.RaspberryStatus.GET_CODE)
                val raspberry = handleQrCode(code)

                // Create configurator file to send
                updateConfigStatus(RaspberryConnectionManager.RaspberryStatus.CREATE_CONFIGURATOR_FILE)
                val path = createConfiguratorFile()

                if (path.isNotEmpty()) {

                    // Set rasp info
                    updateConfigStatus(RaspberryConnectionManager.RaspberryStatus.SETTING_RASP_INFO)
                    raspberryConnection.sourceFilePath = path
                    raspberryConnection.raspberryIp = raspberry["ip"].toString()
                    raspberryConnection.raspberryUsername = raspberry["username"].toString()

                    updateConfigStatus(RaspberryConnectionManager.RaspberryStatus.SENDING_FILE)
                    raspberryConnection.sendConfigFile()

                    // Everything is done correctly
                    dataInternalRepository.setConnected()
                } else {
                    updateConfigStatus(RaspberryConnectionManager.RaspberryStatus.ERROR)
                }
            }
            catch (ex : Exception){
                updateConfigStatus(RaspberryConnectionManager.RaspberryStatus.ERROR)
            }
            updateConfigStatus(RaspberryConnectionManager.RaspberryStatus.FINISHED)
        }
    }

    /**
     * Update Config status
     * I need to update the status of the provisioning
     * of the raspberry pi on the Main Thread because
     * statusConfiguration is a MutableLiveData
     * and they need to be update on the main thread
     * */
    private fun updateConfigStatus(state : RaspberryConnectionManager.RaspberryStatus){
        viewModelScope.launch(Dispatchers.Main) {
            statusConfiguration.value = state
        }
    }

    /**
     * Get raspberry info
     * */
    private suspend fun handleQrCode(qrCode : String) : HashMap<String, Any>{

        // Save the code internally
        // to be able to modify the settings of this
        // raspberry
        dataInternalRepository.saveRaspberryCode(qrCode)

        // Get the ref from the real time database
        val ref = database.getNodeReference("raspberry", listOf(qrCode))

        try {
            // Get rasp info
            val dataSnapshot = try {
                ref.get().await()
            } catch (e: Exception) {
                null
            }

            // Use GenericTypeIndicator to capture the generic type information
            val indicator = object : GenericTypeIndicator<HashMap<String, Any>>() {}

            // Retrieve the value using the GenericTypeIndicator
            return dataSnapshot?.getValue(indicator) ?: hashMapOf<String, Any>()
        }
        catch(ex : Exception){
            throw Exception("Throw error getting the raspberry info")
        }
    }

    private fun createConfiguratorFile() : String{
        val fileName    = "configuratorFile${auth.currentUser?.uid}.txt"
        garden          = dataInternalRepository.getGarden()
        val fileContent =
            "{\"user_uid\": \"${auth.currentUser?.uid}\",\"garden_id\": \"${garden["id"]}\",  \"user_token\": \"${dataInternalRepository.getToken()}\"}"

        val file = File(filesDirInternal, fileName)

        try {
            val writer = BufferedWriter(FileWriter(file))
            writer.write(fileContent)
            writer.close()

            println("File created successfully at: ${file.absolutePath}")
        } catch (e: Exception) {
            throw Exception("Throw exception creating the configurator file")
        }
        return file.absolutePath
    }
}