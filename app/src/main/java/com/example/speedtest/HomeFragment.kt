package com.example.speedtest

import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import fr.bmartel.speedtest.SpeedTestReport
import fr.bmartel.speedtest.model.SpeedTestMode


class HomeFragment : Fragment() {

    private var circularProgress: ProgressBar? = null
    private var progressBar: ProgressBar? = null
    private var button: Button? = null
    private var downloadLabel: TextView? = null
    private var uploadLabel: TextView? = null
    private var statusLabel: TextView? = null
    private var locationLabel: TextView? = null
    private var carrierLabel: TextView? = null

    private class HomeSpeedTest(private val model: HomeFragmentViewModel, context: Context) : SpeedTestTask(context) {

        override fun onPreExecute() {
            super.onPreExecute()
            model.setButtonEnabled(false)
            model.setUploadRate(0.0)
            model.setDowloadRate(0.0)
            model.setIsTesting(true)
        }

        override fun onProgressUpdate(vararg values: SpeedTestReport?) {
            super.onProgressUpdate(*values)
            val report = values[0]!!
            val percent = report.progressPercent.toDouble()
            model.setProgress(percent)
            model.setDownloading(report.speedTestMode == SpeedTestMode.DOWNLOAD)

            when (report.speedTestMode) {
                SpeedTestMode.DOWNLOAD -> model.setDowloadRate(report.transferRateBit.toDouble())
                SpeedTestMode.UPLOAD -> model.setUploadRate(report.transferRateBit.toDouble())
                else -> {
                }
            }
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            model.setButtonEnabled(true)
            model.setDowloadRate(downloadReport?.transferRateBit!!.toDouble())
            model.setUploadRate(uploadReport?.transferRateBit!!.toDouble())
            model.setIsTesting(false)

            carrierName?.let {
                model.setCarrierText(it)
            }

            currentLocation?.let {
                Log.v("postExecute", it.toString())
                val addr = Geocoder(contextRef.get()).getFromLocation(it.latitude, it.longitude, 1)
                addr[0]?.let {
                    model.setLocationText(it.adminArea)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        button = view.findViewById(R.id.button2)
        circularProgress = view.findViewById(R.id.circular_progress)
        uploadLabel = view.findViewById(R.id.upload_rate_label)
        downloadLabel = view.findViewById(R.id.download_rate_label)
        statusLabel = view.findViewById(R.id.status_label)
        locationLabel = view.findViewById(R.id.location_label)
        carrierLabel = view.findViewById(R.id.carrier_label)
        button?.setOnClickListener { this.onButtonPressed() }
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        (context as TitleSettable).setActionBarTitle("Home")
        attachUiToViewModel()
    }

    private fun attachUiToViewModel() {
        val model = getViewModel()
        model.getProgress().observe(this, Observer { progress -> progressBar?.progress = progress.toInt() })
        model.isButtonEnabled().observe(this, Observer { enabled -> button?.isEnabled = enabled })
        model.getDownloadRate().observe(this, Observer { rate -> downloadLabel?.text = toMbpsString(rate) })
        model.getUploadRate().observe(this, Observer { rate -> uploadLabel?.text = toMbpsString(rate) })
        model.getLocationText().observe(this, Observer { location -> locationLabel?.text = location })
        model.getCarrierText().observe(this, Observer { carrier -> carrierLabel?.text = carrier })

        model.isDownloading().observe(this, Observer { isDownloading ->
            run {
                if (isDownloading)
                    statusLabel?.text = getString(R.string.status_download)
                else
                    statusLabel?.text = getString(R.string.status_upload)
            }
        })

        model.isTesting().observe(this, Observer { isTesting ->
            run {
                if (isTesting) {
                    circularProgress?.visibility = View.VISIBLE
                    statusLabel?.visibility = View.VISIBLE
                } else {
                    circularProgress?.visibility = View.INVISIBLE
                    statusLabel?.visibility = View.INVISIBLE
                }
            }
        })
    }

    private fun toMbpsString(rate: Double): String {
        return "%.3f".format(rate / 1e6)
    }

    private fun getViewModel(): HomeFragmentViewModel {
        return ViewModelProviders.of(activity!!).get(HomeFragmentViewModel::class.java)
    }

    private fun onButtonPressed() {
        Log.v("buttonPress", "Button pressed")
        HomeSpeedTest(getViewModel(), activity!!).execute()
    }

}
