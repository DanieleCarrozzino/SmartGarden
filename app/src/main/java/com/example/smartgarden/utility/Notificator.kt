package com.example.smartgarden.utility

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.smartgarden.R

class Notificator {

    companion object{

        const val CHANNEL_ID = "basic_notification"
        fun showNotification(title: String, message: String, id : Int, context: Context) {

            // Create a notification channel (for Android 8+)
            createNotificationChannel(context)

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(context)) {
                // notificationId is a unique int for each notification that you must define.
                try{
                    notify(id, builder.build())
                }
                catch(ex :  SecurityException){
                    Log.d("Notification", "Unable to show the notification")
                }
            }
        }

        private fun createNotificationChannel(context : Context) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is not in the Support Library.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "channel_name"
                val descriptionText = "Description_channel"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
                // Register the channel with the system.
                val notificationManager: NotificationManager =
                    getSystemService(context, NotificationManager::class.java) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

}