package com.example.speedtest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.time.format.DateTimeFormatter


class TimedTestAdapter(var vm: TimedTestViewModel) :
    RecyclerView.Adapter<TimedTestAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context).inflate(R.layout.alarm_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        val tests = vm.getSchedules().value
        return tests?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tests = vm.getSchedules().value
        tests?.let {
            holder.bindData(it[position], position)
        }
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        fun bindData(data: TimedTestFragment.TimedTest, position: Int) {
            view.findViewById<TextView>(R.id.timeLabel).text = data.time.format(dateFormatter)

            with(view.findViewById<Switch>(R.id.enabled_box)) {
                isChecked = data.enabled
                setOnClickListener {run{
                    vm.updateTest(TimedTestFragment.TimedTest(data.time, isChecked), position)
                }}
            }

        }
    }
}