package com.example.smartly.Util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.smartly.R
import com.example.smartly.view.activities.MainActivity

class ShowNotificationClass {
    companion object{

        fun showNotification(context: Context, message: String) {
            val intent = Intent(context, MainActivity::class.java).apply {
                putExtra("fragment_to_open", "ResultFragment")
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(context, "MY_CHANNEL_ID")
                .setSmallIcon(R.drawable.quiz_app_logo)
                .setContentTitle("Your Quiz Result")
                .setContentText("$message")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    "MY_CHANNEL_ID",
                    "My Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Channel for My Notifications"
                }
                val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }

            with(NotificationManagerCompat.from(context)) {
                notify(1, builder.build())
            }
        }
    }
}