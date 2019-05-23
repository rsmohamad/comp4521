package com.example.speedtest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.time.format.DateTimeFormatter


class TimedTestAdapter(var tests: List<TimedTestFragment.TimedTest>?) :
    RecyclerView.Adapter<TimedTestAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context).inflate(R.layout.alarm_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if (tests == null) 0 else tests!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        tests?.let {
            holder.bindData(it[position])
        }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("KK:mm a")
        fun bindData(data: TimedTestFragment.TimedTest) {
            view.findViewById<TextView>(R.id.timeLabel).text = data.time.format(dateFormatter)
            view.findViewById<Switch>(R.id.enabled_box).isChecked = data.enabled
        }
    }
}