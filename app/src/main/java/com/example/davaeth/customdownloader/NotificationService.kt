package com.example.davaeth.customdownloader

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat

@SuppressLint("Registered")
class NotificationService : Application() {

    fun notifyUser(content: String) {
        GlobalVariables.NotificationID += 1

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Your download result!"
            val descriptionText = "Result information of your recently downloaded file"

            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel("Download result", name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val builder = NotificationCompat.Builder(this, "Download result")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Your result!")
            .setContentText(content)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(content)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)

        with(NotificationManagerCompat.from(this)) {
            notify(GlobalVariables.NotificationID, builder.build())
        }
    }

}