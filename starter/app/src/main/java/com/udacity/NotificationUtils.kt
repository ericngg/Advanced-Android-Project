package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

private val NOTIFICATION_ID = 1

fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context, channelId: String, file: String, success: Boolean) {
    val intent = Intent(applicationContext, DetailActivity::class.java)
    intent.putExtra("file", file)
    intent.putExtra("success", success)

    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(applicationContext, channelId)
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)
        .setAutoCancel(true)
        .addAction(
            R.drawable.cloud_icon,
            applicationContext.getString(R.string.action_status),
            contentPendingIntent
        )

    notify(NOTIFICATION_ID, builder.build())
}