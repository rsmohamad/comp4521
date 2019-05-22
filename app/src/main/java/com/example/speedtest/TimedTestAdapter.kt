package com.example.speedtest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.time.format.DateTimeFormatter
import java.util.*

private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("KK:mm a")

class TimedTestAdapter(val tests: ArrayList<TimedTest>) : RecyclerView.Adapter<TimedTestAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context).inflate(R.layout.alarm_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tests.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(tests[position])
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bindData(data: TimedTest) {
            view.findViewById<TextView>(R.id.timeLabel).text = data.time.format(dateFormatter)
            view.findViewById<CheckBox>(R.id.enabled_box).isChecked = data.enabled
        }
    }
}