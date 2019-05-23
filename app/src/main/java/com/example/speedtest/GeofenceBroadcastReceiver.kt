package com.example.speedtest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    internal class LocationSpeedTestTask(context: Context) : SpeedTestTask(context) {
        override fun onPreExecute() {
            super.onPreExecute()
            contextRef.get()?.let {
                NotificationUtil.sendNotification(
                    it,
                    it.getString(R.string.notification_title),
                    it.getString(R.string.notification_message_location)
                )
            }
        }
    }


    override fun onReceive(context: Context, intent: Intent) {
        // Enqueues a JobIntentService passing the context and intent as parameters
        Log.v("geofences", "yay")
        LocationSpeedTestTask(context).execute()
    }
}