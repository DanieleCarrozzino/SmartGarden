package com.example.smartgarden.broadcast

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.smartgarden.viewmodels.CameraViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DownloadBroadcastReceiver : BroadcastReceiver() {

    @Inject lateinit var cameraViewModel: CameraViewModel

    companion object{
        const val TAG : String = "DownloadBroadcastReceiver"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == intent?.action) {
            Log.d(TAG, "Downloaded")
            cameraViewModel.downloaded()
        }
    }
}