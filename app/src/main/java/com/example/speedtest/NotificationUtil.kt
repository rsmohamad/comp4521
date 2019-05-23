package com.example.speedtest

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationUtil {
    companion object {
        fun sendNotification(ctx: Context, title: String, message: String) {
            val mNotificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val name = ctx.getString(R.string.app_name)
            val mChannel = NotificationChannel("channel_id1", name, NotificationManager.IMPORTANCE_DEFAULT)
            mNotificationManager.createNotificationChannel(mChannel)

            val noti = NotificationCompat.Builder(ctx, "channel_id1")
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()
            NotificationManagerCompat.from(ctx).notify(0, noti)
        }
    }
}