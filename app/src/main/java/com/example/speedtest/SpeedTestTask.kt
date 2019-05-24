//  # COMP 4521    #  MOHAMAD, Randitya Setyawan    20316273    rsmohamad@ust.hk
//  # COMP 4521    #  IVANOV, Metodi Dimitrov       20314512    mdivanov@connect.ust.hk

package com.example.speedtest

import android.content.Context
import android.location.Location
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.telephony.TelephonyManager
import android.util.Log
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import fr.bmartel.speedtest.SpeedTestReport
import fr.bmartel.speedtest.SpeedTestSocket
import fr.bmartel.speedtest.inter.ISpeedTestListener
import fr.bmartel.speedtest.model.SpeedTestError
import fr.bmartel.speedtest.model.SpeedTestMode
import java.lang.ref.WeakReference
import java.util.concurrent.locks.ReentrantLock


abstract class SpeedTestTask(context: Context) : AsyncTask<Void, SpeedTestReport, Void?>() {

    protected val contextRef: WeakReference<Context> = WeakReference(context)

    private val speedTestUrl = "http://ipv4.ikoula.testdebit.info/1M.iso"
    private val uploadSize = 1000000
    private val testDuration = 8000
    private val reportInterval = 100
    private val uploadChunkSize = 65565

    private val testSocket = SpeedTestSocket()
    private val lock = ReentrantLock()
    private val condition = lock.newCondition()

    protected var uploadReport: SpeedTestReport? = null
    protected var downloadReport: SpeedTestReport? = null
    protected var currentLocation: Location? = null
    protected var carrierName: String? = null
    protected var imei: String? = null

    protected var isWifi: Boolean = true

    private val listener: ISpeedTestListener = object : ISpeedTestListener {
        override fun onCompletion(report: SpeedTestReport?) {
            if (report?.speedTestMode == SpeedTestMode.UPLOAD) {
                lock.lock()
                Log.v("speedTestComplete", "Upload Complete")
                uploadReport = report
                condition.signalAll()
                lock.unlock()
            } else {
                Log.v("speedTestComplete", "Download Complete")
                downloadReport = report
                testSocket.startFixedUpload(speedTestUrl, uploadSize, testDuration, reportInterval)
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

    override fun doInBackground(vararg params: Void?): Void? {

        contextRef.get()?.let {
            try {
                Log.v("location", "getting location")
                currentLocation = Tasks.await(LocationServices.getFusedLocationProviderClient(it).lastLocation)

                val tm = (it.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager)
                carrierName = tm.networkOperatorName
                imei = tm.imei

                val cm = (it.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
                val active = cm.activeNetworkInfo ?: return null
                isWifi = (active.type == ConnectivityManager.TYPE_WIFI)

            } catch (e: SecurityException) {

            }
        }

        lock.lock()
        testSocket.uploadChunkSize = uploadChunkSize
        testSocket.addSpeedTestListener(listener)
        testSocket.startFixedDownload(speedTestUrl, testDuration, reportInterval)
        condition.await()
        lock.unlock()

        return null
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        Log.v("asyncTask", "postExecute")
        storeData()
    }

    override fun onPreExecute() {
        super.onPreExecute()
        Log.v("asyncTask", "preExecute")

        contextRef.get()?.let {
        }
    }

    private fun storeData() {
        val db = FirebaseFirestore.getInstance()

        val testResult = TestResult(
            downloadReport?.transferRateBit?.toDouble(),
            uploadReport?.transferRateBit?.toDouble(),
            currentLocation?.longitude,
            currentLocation?.latitude,
            currentLocation?.altitude,
            System.currentTimeMillis(),
            carrierName,
            "",
            imei,
            isWifi
        )

        Log.v("firestore", "storing data")
        db.collection("testResults")
            .add(testResult.toMap())
            .addOnSuccessListener { Log.v("firestore", "test stored") }
            .addOnFailureListener { Log.v("firestore", "test store failed") }
    }
}