//  # COMP 4521    #  MOHAMAD, Randitya Setyawan    20316273    rsmohamad@ust.hk
//  # COMP 4521    #  IVANOV, Metodi Dimitrov       20314512    mdivanov@connect.ust.hk

package com.example.speedtest

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Address
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.schibstedspain.leku.*


class GeofenceFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: GeofenceAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_timed_test, container, false)
        viewAdapter = GeofenceAdapter(getViewModel())

        view.findViewById<FloatingActionButton>(R.id.addScheduleButton)
            .apply { setOnClickListener { startPickerActivity() } }

        view.findViewById<FloatingActionButton>(R.id.deleteButton)
            .apply { setOnClickListener { getViewModel().clearLocations() } }

        activity?.let {
            viewManager = LinearLayoutManager(it)
            recyclerView = view.findViewById<RecyclerView>(R.id.recycler_list).apply {
                layoutManager = viewManager
                adapter = viewAdapter
            }
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as TitleSettable).setActionBarTitle("Geofences")

        getViewModel().getLocations().observe(this, Observer { data ->
            run {
                viewAdapter.vm = getViewModel()
                viewAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun startPickerActivity() {
        context?.let {
            val locationPickerIntent = LocationPickerActivity.Builder()
                .withLocation(22.3375457, 114.2656919)
                .withGeolocApiKey(getString(R.string.geoloc_key))
                .withSearchZone("hk_EN")
                .withDefaultLocaleSearchZone()
                .shouldReturnOkOnBackPressed()
                .withSatelliteViewHidden()
                .withGooglePlacesEnabled()
                .withGoogleTimeZoneEnabled()
                .withVoiceSearchHidden()
                .build(it)

            startActivityForResult(locationPickerIntent, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            Log.d("RESULT****", "OK")
            if (requestCode == 1) {
                val latitude = data.getDoubleExtra(LATITUDE, 0.0)
                Log.d("LATITUDE****", latitude.toString())
                val longitude = data.getDoubleExtra(LONGITUDE, 0.0)
                Log.d("LONGITUDE****", longitude.toString())
                val address = data.getStringExtra(LOCATION_ADDRESS)
                Log.d("ADDRESS****", address.toString())
                val postalcode = data.getStringExtra(ZIPCODE)
                Log.d("POSTALCODE****", postalcode.toString())
                val fullAddress = data.getParcelableExtra<Address>(ADDRESS)
                if (fullAddress != null) {
                    Log.d("FULL ADDRESS****", fullAddress.toString())
                }

                getViewModel().addLocation(GeofenceTest(address, latitude, longitude, true))
            }
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            Log.d("RESULT****", "CANCELLED")
        }
    }

    private fun getViewModel(): GeofenceViewModel {
        return ViewModelProviders.of(activity!!).get(GeofenceViewModel::class.java)
    }


}
