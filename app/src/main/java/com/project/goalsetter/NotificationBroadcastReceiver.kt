package com.project.goalsetter


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build


class NotificationBroadcastReceiver(): BroadcastReceiver() {

    override fun onReceive(p0: Context, p1: Intent?) {

        val title = p1?.getStringExtra("title")
        val CHANNEL_ID = "GOAL_ALERT"

        val notificationManager: NotificationManager = p0.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        val notification:Notification;

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            notification = Notification.Builder(p0,CHANNEL_ID)
                .setContentText("Reminder: $title")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setSubText("Your Goal is Approaching.")
                .setChannelId(CHANNEL_ID)
                .build()
            notificationManager.createNotificationChannels(
                listOf(NotificationChannel(CHANNEL_ID,"AlertUser",NotificationManager.IMPORTANCE_DEFAULT)))

        }
        else{
            notification = Notification.Builder(p0)
                .setContentTitle("Goal Reminder")
                .setSubText("Reminder: $title")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build()
        }

        notificationManager.notify(1,notification)
    }
}