//  # COMP 4521    #  MOHAMAD, Randitya Setyawan    20316273    rsmohamad@ust.hk
//  # COMP 4521    #  IVANOV, Metodi Dimitrov       20314512    mdivanov@connect.ust.hk

package com.example.speedtest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class ScheduledTestBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.v("alarm triggered", "time triggered")
        ScheduledSpeedTestTask(context!!).execute()
    }

    class ScheduledSpeedTestTask(ctx: Context) : SpeedTestTask(ctx) {

        override fun onPreExecute() {
            super.onPreExecute()
            Log.v("alarm triggered", "time triggered")

            contextRef.get()?.let {
                NotificationUtil.sendNotification(
                    it,
                    it.getString(R.string.notification_title),
                    it.getString(R.string.notification_message_time)
                )
            }
        }
    }
}