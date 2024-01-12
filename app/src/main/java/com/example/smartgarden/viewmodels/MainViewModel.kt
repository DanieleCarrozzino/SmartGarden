package com.example.smartgarden.viewmodels

import android.content.Context
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
import androidx.compose.ui.graphics.vector.PathNode
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartgarden.firebase.authentication.FirebaseAuthenticator
import com.example.smartgarden.firebase.storage.FirebaseFirestoreImpl
import com.example.smartgarden.firebase.storage.FirebaseRealTimeDatabase
import com.example.smartgarden.manager.RaspberryConnectionManager
import com.example.smartgarden.objects.CHART_TYPE
import com.example.smartgarden.objects.ChartObject
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

    private lateinit var garden : HashMap<String, Any>

    val name = mutableStateOf("")
    val date = mutableStateOf("")

    private val filesDirInternal : String = context.filesDir.absolutePath
    val doingConfiguration  = mutableStateOf(false)
    val statusConfiguration = MutableLiveData<RaspberryConnectionManager.RaspberryStatus>()
    val connected           = mutableStateOf(false)

    // Visible Chart
    var animationChartIsRunning = false
    val chart = MutableLiveData<ChartObject>()
    var lastListPathNode = listOf<PathNode>()
    val typeChart = MutableLiveData<CHART_TYPE>()
    var changeScreenClick = false

    // 3 Seek bar values
    val temperatureValue    = MutableLiveData<Int>()
    val hydrationValue      = MutableLiveData<Int>()
    val wellnessValue       = MutableLiveData<Int>()

    // THRESHOLD
    // hydration perc
    var percMin = mutableIntStateOf(34)
    var percMax = mutableIntStateOf(62)
    var enabled = mutableStateOf(false)
    var alpha = mutableFloatStateOf(0.3f)

    fun init(){

        // To know if I connect myself already to
        // the raspberry pi
        connected.value = dataInternalRepository.getConnected()

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
                        val key = dataSnapshot.key
                        val value = dataSnapshot.getValue(object : GenericTypeIndicator<HashMap<String, Any>>() {})

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
        animationChartIsRunning = true
        viewModelScope.launch(Dispatchers.IO) {
            while(true){
                //TODO get real list of values
                val list = mutableListOf<Float>()
                for (i in 0 until 10){
                    list.add(Random.nextFloat() * 50f)
                }

                val type = CHART_TYPE
                    .entries
                    .toTypedArray()[((chart.value?.type?.ordinal ?: 0) + 1) % CHART_TYPE.entries.size]
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
                    temperatureValue.value  = Random.nextInt(1, 100)
                    hydrationValue.value    = Random.nextInt(1, 100)
                    wellnessValue.value     = Random.nextInt(1, 100)

                    // TODO remove
                    connected.value = true
                }

                /*
                * Change main screen for a user click
                * or for timeout
                * */
                var count = 0
                while(count < 700 && !changeScreenClick){
                    count ++
                    delay(10)
                }
                changeScreenClick = false
            }
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
        raspberryConnection.initializeScanner(::raspberryCallback)

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
     * SCANNED -> get the raspberry code from the qr
     * and get the ip and other info of the raspberry from
     * the real_database on firebase and save the code of the raspberry
     * inside my personal data on the real_database,
     * create a configurator file
     * with my uid and the code of the garden and send all this
     * stuff to the raspberry manager
     *
     * START_CONFIGURED -> Start to send the configurator file
     * to the raspberry
     *
     * END_CONFIGURED -> Check if everything goes well,
     * otherwise restart the process with a limit number of retry
     *
     * FINISHED -> check my data and the data of the raspberry
     * and close the configurator screen
     * */
    fun raspberryCallback(
        result : RaspberryConnectionManager.RaspberryStatus,
        qrCode : String? = ""
    ){
        updateConfigStatus(result)
        when(result){
            RaspberryConnectionManager.RaspberryStatus.SCANNED -> {
                doingConfiguration.value = true
                viewModelScope.launch(Dispatchers.IO) {

                    // Get info
                    val raspberry = handleQrCode(qrCode ?: "")
                    // Create configurator
                    val path = createConfiguratorFile()

                    if(path.isNotEmpty()){
                        // Set rasp info
                        raspberryConnection.sourceFilePath      = path
                        raspberryConnection.raspberryIp         = raspberry["ip"].toString()
                        raspberryConnection.raspberryUsername   = raspberry["username"].toString()
                        raspberryConnection.sendConfigFile()
                    }
                }
            }
            RaspberryConnectionManager.RaspberryStatus.START_CONFIGURED -> {
                //TODO start animation
            }
            RaspberryConnectionManager.RaspberryStatus.END_CONFIGURED -> {
                //TODO end animation
            }
            RaspberryConnectionManager.RaspberryStatus.FINISHED -> {
                dataInternalRepository.setConnected()
                viewModelScope.launch(Dispatchers.IO)  {
                    delay(3000)
                    raspberryCallback(RaspberryConnectionManager.RaspberryStatus.CLOSE, "")
                }
            }
            RaspberryConnectionManager.RaspberryStatus.CLOSE -> {

            }
        }
    }

    private fun updateConfigStatus(state : RaspberryConnectionManager.RaspberryStatus){
        viewModelScope.launch(Dispatchers.Main) {
            statusConfiguration.value = state
        }
    }

    /**
     * Get raspberry info
     * */
    private suspend fun handleQrCode(qrCode : String) : HashMap<String, Any>{
        val ref = database.getNodeReference("raspberry", listOf(qrCode))

        try {
            // Get rasp info
            val dataSnapshot = try {
                ref.get().await()
            } catch (e: Exception) {
                null
            }

            val value           = dataSnapshot?.getValue(String::class.java)
            val raspberry       = convertJsonToHashMap(value.toString())

            Log.d("ViewModel", "raspberry ip: ${raspberry["ip"].toString()}")
            return raspberry
        }
        catch(ex : Exception){
            Log.e("ViewModel", ex.message.toString())
        }
        return hashMapOf()
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
            println("Error creating the file: ${e.message}")
            return ""
        }
        return file.absolutePath
    }
}