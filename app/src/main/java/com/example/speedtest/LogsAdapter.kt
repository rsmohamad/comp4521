package com.example.speedtest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class LogsAdapter(var logs: List<TestResult>?) :
    RecyclerView.Adapter<LogsAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        logs?.get(position)?.let { holder.bindData(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context).inflate(R.layout.log_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return logs?.size ?: 0
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {


        private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM uu HH:mm")

        fun bindData(data: TestResult) {
            val millis = data.timestamp ?: 0
            val timezone = ZoneOffset.ofHours(TimeZone.getDefault().rawOffset / 3600000)
            val date =
                LocalDateTime.ofEpochSecond(millis / 1000, 0, timezone)
            view.findViewById<TextView>(R.id.downloadLabel).text = toMbpsString(data.downloadRate)
            view.findViewById<TextView>(R.id.uploadLabel).text = toMbpsString(data.uploadRate)
            view.findViewById<TextView>(R.id.timeLabel).text = date.format(dateFormatter)
        }

        private fun toMbpsString(rate: Double?): String {
            return "%.3f".format((rate!!) / 1e6)
        }
    }


}