package com.example.speedtest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import fr.bmartel.speedtest.SpeedTestReport

class ScheduledTestBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.v("alarm triggered", "time triggered")
        System.out.println("fuck")
        ScheduledSpeedTestTask(context!!).execute()
    }

    class ScheduledSpeedTestTask(ctx: Context) : SpeedTestTask(ctx) {
        override fun onProgressUpdate(vararg values: SpeedTestReport?) {
            super.onProgressUpdate(*values)
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
        }

        override fun onPreExecute() {
            super.onPreExecute()
            Log.v("alarm triggered", "time triggered")
        }
    }
}