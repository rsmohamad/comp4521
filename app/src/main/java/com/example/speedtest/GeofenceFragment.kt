package com.example.speedtest


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


class GeofenceFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_geofence, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        (context as TitleSettable).setActionBarTitle("Geofences")
    }


}
