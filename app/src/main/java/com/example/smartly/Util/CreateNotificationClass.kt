package com.example.smartly.Util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class CreateNotificationClass {
    companion object{
        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "My Channel"
                val descriptionText = "Channel for My Notifications"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel("MY_CHANNEL_ID", name, importance).apply {
                    description = descriptionText
                }
                val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
}