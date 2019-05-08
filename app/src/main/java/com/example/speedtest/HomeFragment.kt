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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import fr.bmartel.speedtest.SpeedTestReport


class HomeFragment : Fragment() {

    private var progressBar: ProgressBar? = null
    private var button: Button? = null

    private class HomeSpeedTest(private val model: HomeFragmentViewModel) : SpeedTestTask() {

        override fun onPreExecute() {
            super.onPreExecute()
            model.setButtonEnabled(false)
        }

        override fun onProgressUpdate(vararg values: SpeedTestReport?) {
            super.onProgressUpdate(*values)
            val percent = values[0]?.progressPercent
            model.setProgress(percent!!)
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            model.setButtonEnabled(true)
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

        val model = getViewModel()
        model.getProgress().observe(this, Observer<Float> { progress -> progressBar?.progress = progress.toInt() })
        model.isButtonEnabled().observe(this, Observer<Boolean> { enabled -> button?.isEnabled = enabled })

        try {
            (context as TitleSettable).setActionBarTitle("Home")
        } catch (e: ClassCastException) {

        }
    }

    private fun getViewModel(): HomeFragmentViewModel {
        return ViewModelProviders.of(activity!!).get(HomeFragmentViewModel::class.java)
    }

    private fun onButtonPressed() {
        Log.v("buttonPress", "Button pressed")
        HomeSpeedTest(getViewModel()).execute()
    }

}
