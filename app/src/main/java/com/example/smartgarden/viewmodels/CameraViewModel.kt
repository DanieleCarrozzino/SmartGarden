package com.example.smartgarden.viewmodels

import android.app.DownloadManager
import android.content.Context.DOWNLOAD_SERVICE
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.STATE_ENDED
import com.example.smartgarden.firebase.storage.FirebaseRealTimeDatabase
import com.example.smartgarden.firebase.storage.FirebaseStorageInterface
import com.example.smartgarden.objects.DatabaseEntry
import com.example.smartgarden.repository.DataInternalRepository
import com.example.smartgarden.utility.Utility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CameraViewModel @Inject constructor(
    private val storage : FirebaseStorageInterface,
    private val database : FirebaseRealTimeDatabase,
    private val dataInternalRepository: DataInternalRepository,
    val player: Player
) : ViewModel(), Player.Listener {

    companion object{
        const val TAG : String = "CameraViewModel"
    }

    var cameraUrl           = mutableStateOf("")
    var instantCameraUrl    = mutableStateOf("")
    var instantCameraName   = mutableStateOf("")
    var timelapsUrl         = mutableStateOf("")
    var timelapseLastDate   = mutableStateOf("")

    var buttonEnable    = mutableStateOf(true)
    var canDownload     = mutableStateOf(true)

    // video layout
    var videoVisibility = mutableStateOf(false)

    // Context methods
    var downloadFromUrl     : (String) -> Unit = {}
    var shareFromText       : (String) -> Unit = {}

    init {
        getImageAndVideoUrl()
    }

    fun getImageAndVideoUrl(){
        viewModelScope.launch(Dispatchers.IO) {
            cameraUrl.value         = storage.getLastImageUrl(dataInternalRepository.getRaspberryCode())
            var pair = storage.getTimeLapsUrl(dataInternalRepository.getRaspberryCode())
            timelapsUrl.value       = pair.first
            timelapseLastDate.value = pair.second

            pair = storage.getLastTakenImageUrl(dataInternalRepository.getRaspberryCode())
            instantCameraUrl.value  = pair.second
            instantCameraName.value = pair.first

            launch(Dispatchers.Main){
                prepareVideo()
            }
        }
    }

    private fun prepareVideo(){
        player.setMediaItem(
            MediaItem.fromUri(Uri.parse(timelapsUrl.value))
        )
        player.prepare()
        player.addListener(this)
    }

    fun startVideo(){
        videoVisibility.value = true
        player.play()
    }

    fun releaseVideo(){
        player.release()
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        Log.d("CameraViewModel", "playback $playbackState")
        if(STATE_ENDED == playbackState){
            Log.d("CameraViewModel", "STOP")
            videoVisibility.value = false
        }
    }

    fun takePicture(){
        database.updateNode(
            DatabaseEntry.Raspberry.key,
            listOf<String>(dataInternalRepository.getRaspberryCode()),
            hashMapOf("camera" to Utility.getCurrentDateTime())
        )
        refreshInstantImage()
    }

    fun download(){
        Log.d(TAG, "Starting download")
        canDownload.value = false
        downloadFromUrl(timelapsUrl.value)
    }

    fun downloadInstant(){
        Log.d(TAG, "Starting download")
        canDownload.value = false
        downloadFromUrl(instantCameraUrl.value)
    }

    fun share(){
        Log.d(TAG, "Starting share")
        shareFromText(timelapsUrl.value)
    }

    fun shareInstantImage(){
        shareFromText(instantCameraUrl.value)
    }

    private fun refreshInstantImage(){
        viewModelScope.launch(Dispatchers.IO) {
            buttonEnable.value = false
            delay(8000)
            val pair = storage.getLastTakenImageUrl(dataInternalRepository.getRaspberryCode())
            instantCameraUrl.value  = pair.second
            instantCameraName.value = pair.first
            buttonEnable.value = true
        }
    }

    /**
     * The timelapse is been downloaded,
     * modify the download button
     * */
    fun downloaded(){
        canDownload.value = true
    }
}