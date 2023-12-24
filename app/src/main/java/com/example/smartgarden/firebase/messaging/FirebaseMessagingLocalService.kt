package com.example.smartgarden.firebase.messaging

import android.util.Log
import com.example.smartgarden.utility.Notificator.Companion.showNotification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessagingLocalService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.let { notification ->
            val title = notification.title // Get notification title
            val message = notification.body // Get notification message/body

            showNotification(title ?: "", message ?: "", 1, applicationContext)
        }
    }

    override fun onNewToken(token: String) {
        Log.d("FirebaseMessaging", token)
        //TODO update firebase
    }

}