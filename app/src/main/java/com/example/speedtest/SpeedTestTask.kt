package com.example.speedtest

import android.os.AsyncTask
import android.util.Log
import fr.bmartel.speedtest.SpeedTestReport
import fr.bmartel.speedtest.SpeedTestSocket
import fr.bmartel.speedtest.inter.ISpeedTestListener
import fr.bmartel.speedtest.model.SpeedTestError
import fr.bmartel.speedtest.model.SpeedTestMode
import java.util.concurrent.locks.ReentrantLock

abstract class SpeedTestTask : AsyncTask<Void, SpeedTestReport, String?>() {

    private val speedTestUrl = "http://ipv4.ikoula.testdebit.info/1M.iso"
    private val uploadSize = 1000000

    private val testSocket = SpeedTestSocket()
    private val lock = ReentrantLock()
    private val condition = lock.newCondition()

    private val listener: ISpeedTestListener = object : ISpeedTestListener {
        override fun onCompletion(report: SpeedTestReport?) {
            if (report?.speedTestMode == SpeedTestMode.UPLOAD) {
                lock.lock()
                Log.v("speedTestComplete", "Upload Complete")
                condition.signalAll()
                lock.unlock()
            } else {
                Log.v("speedTestComplete", "Download Complete")
                testSocket.startUpload(speedTestUrl, uploadSize)
            }

        }

        override fun onProgress(percent: Float, report: SpeedTestReport?) {
            publishProgress(report)
        }

        override fun onError(speedTestError: SpeedTestError?, errorMessage: String?) {
            lock.lock()
            Log.v("speedTestErr", errorMessage)
            condition.signalAll()
            lock.unlock()
        }

    }

    override fun doInBackground(vararg params: Void?): String? {

        lock.lock()

        testSocket.addSpeedTestListener(listener)
        testSocket.startDownload(speedTestUrl)
        Log.v("ff", "locked")
        condition.await()

        lock.unlock()
        return null
    }

    override fun onProgressUpdate(vararg values: SpeedTestReport?) {
        super.onProgressUpdate(*values)
        Log.v(
            "asyncTask",
            "progressUpdate: " + values[0]?.speedTestMode.toString() + " " + values[0]?.progressPercent.toString()
        )
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        Log.v("asyncTask", "postExecute")
    }

    override fun onPreExecute() {
        super.onPreExecute()
        Log.v("asyncTask", "preExecute")
    }

}