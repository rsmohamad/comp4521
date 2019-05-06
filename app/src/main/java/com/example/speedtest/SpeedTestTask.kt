package com.example.speedtest

import android.os.AsyncTask
import fr.bmartel.speedtest.SpeedTestSocket
import fr.bmartel.speedtest.inter.ISpeedTestListener

class SpeedTestTask(l: ISpeedTestListener) : AsyncTask<Void, Void, String?>() {

    private val listener: ISpeedTestListener = l
    private val testSocket: SpeedTestSocket = SpeedTestSocket()

    override fun doInBackground(vararg params: Void?): String? {
        testSocket.addSpeedTestListener(listener)
        testSocket.startDownload("http://ipv4.ikoula.testdebit.info/1M.iso");
        return null
    }

}