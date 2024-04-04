package com.example.smartgarden.viewmodels

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val storage : FirebaseStorageInterface,
    private val database : FirebaseRealTimeDatabase,
    private val dataInternalRepository: DataInternalRepository,
    val player: Player
) : ViewModel(), Player.Listener {

    var cameraUrl   = mutableStateOf("")
    var timelapsUrl = mutableStateOf("")

    // video layout
    var videoVisibility = mutableStateOf(false)

    fun getImageAndVideoUrl(){
        viewModelScope.launch(Dispatchers.IO) {
            cameraUrl.value     = storage.getLastImageUrl(dataInternalRepository.getRaspberryCode())
            timelapsUrl.value   = storage.getTimeLapsUrl(dataInternalRepository.getRaspberryCode())

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
    }
}