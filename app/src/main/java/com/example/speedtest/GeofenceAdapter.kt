package com.example.speedtest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GeofenceAdapter(var vm: GeofenceViewModel) : RecyclerView.Adapter<GeofenceAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context).inflate(R.layout.geofence_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        val tests = vm.getLocations().value
        return tests?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tests = vm.getLocations().value
        tests?.let {
            holder.bindData(it[position], position)
        }
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bindData(data: GeofenceTest, position: Int) {
            view.findViewById<TextView>(R.id.locationLabel).text = data.name
            view.findViewById<TextView>(R.id.longLabel).text = "%.3f".format(data.long)
            view.findViewById<TextView>(R.id.latLabel).text = "%.3f".format(data.lat)

            with(view.findViewById<Switch>(R.id.geofenceEnabled)) {
                isChecked = data.enabled
                setOnClickListener {
                    vm.updateLocation(GeofenceTest(data.name, data.lat, data.long, isChecked), position)
                }
            }

        }
    }


}