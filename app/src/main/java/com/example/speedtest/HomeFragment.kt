package com.example.speedtest

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import fr.bmartel.speedtest.SpeedTestReport
import fr.bmartel.speedtest.inter.ISpeedTestListener
import fr.bmartel.speedtest.model.SpeedTestError
import java.lang.ClassCastException


class HomeFragment : Fragment() {

    private var progressBar: ProgressBar? = null
    private var button: Button? = null

    inner class TestListener : ISpeedTestListener {
        override fun onCompletion(report: SpeedTestReport?) {
            System.out.println("Complete")
        }

        override fun onProgress(percent: Float, report: SpeedTestReport?) {
            progressBar?.progress = percent.toInt()
            Log.v("speedTestProg", "Progress: $percent")
        }

        override fun onError(speedTestError: SpeedTestError?, errorMessage: String?) {
            Log.v("speedTestErr", errorMessage)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        button = view.findViewById(R.id.button2)
        progressBar = view.findViewById(R.id.progressBar)
        button?.setOnClickListener { this.onButtonPressed() }
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        try {
            (context as TitleSettable).setActionBarTitle("Home")
        } catch (e: ClassCastException) {

        }
    }

    private fun onButtonPressed() {
        Log.v("buttonPress", "Button pressed")
        SpeedTestTask(TestListener()).execute()
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            HomeFragment().apply { arguments = Bundle().apply { } }
    }
}
