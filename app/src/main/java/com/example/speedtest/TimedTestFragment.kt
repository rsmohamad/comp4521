package com.example.speedtest

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalTime
import java.util.*


class TimedTestFragment : Fragment(), TimePickerDialog.OnTimeSetListener {

    data class TimedTest(val time: LocalTime, val enabled: Boolean) {
        override fun equals(other: Any?): Boolean {
            val rhs: TimedTest
            try {
                rhs = (other as TimedTest)
            } catch (e: ClassCastException) {
                return false
            }
            return rhs.time.hour == time.hour && rhs.time.minute == time.minute
        }
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: TimedTestAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var timeDialog: TimePickerDialog
    private lateinit var INTENT_ALARM: String

    private fun toRequestCode(hour: Int, minute: Int): Int {
        return hour * 100 + minute
    }

    private fun cancelAlarm(hour: Int, minute: Int) {
        val alarmIntent = Intent(context, ScheduledTestBroadcastReceiver::class.java)
            .apply { action = INTENT_ALARM }
            .let {
                PendingIntent.getBroadcast(context, toRequestCode(hour, minute), it, PendingIntent.FLAG_UPDATE_CURRENT)
            }

        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(alarmIntent)
        Log.v("cancelled", toRequestCode(hour, minute).toString())
    }

    private fun scheduleAlarm(hour: Int, minute: Int) {

        val alarmIntent = Intent(context, ScheduledTestBroadcastReceiver::class.java)
            .apply { action = INTENT_ALARM }
            .let {
                PendingIntent.getBroadcast(context, toRequestCode(hour, minute), it, PendingIntent.FLAG_UPDATE_CURRENT)
            }

        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            alarmIntent
        )
        Log.v("scheduled", toRequestCode(hour, minute).toString())
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        getViewModel().addTest(TimedTest(LocalTime.of(hourOfDay, minute), true))
        scheduleAlarm(hourOfDay, minute)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        INTENT_ALARM = getString(R.string.alarm_intent)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_timed_test, container, false)
        viewAdapter = TimedTestAdapter(getViewModel().getSchedules().value)
        timeDialog = TimePickerDialog(context, this, 0, 0, true)
        timeDialog.setTitle("Schedule a time to run test")

        view.findViewById<FloatingActionButton>(R.id.addScheduleButton)
            .apply { setOnClickListener { timeDialog.show() } }

        view.findViewById<FloatingActionButton>(R.id.deleteButton)
            .apply {
                setOnClickListener {
                    run {
                        getViewModel().getSchedules().value?.forEach { f -> cancelAlarm(f.time.hour, f.time.minute) }
                        getViewModel().clearSchedules()
                    }
                }
            }

        val br = ScheduledTestBroadcastReceiver()
        val filter = IntentFilter().apply { addAction(INTENT_ALARM) }
        context?.registerReceiver(br, filter)
        activity?.let {
            viewManager = LinearLayoutManager(it)
            recyclerView = view.findViewById<RecyclerView>(R.id.recycler_list).apply {
                layoutManager = viewManager
                adapter = viewAdapter
            }
        }

        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        (context as TitleSettable).setActionBarTitle("Timed Test")
        getViewModel().getSchedules().observe(this, Observer { data ->
            run {
                viewAdapter.tests = data
                viewAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun getViewModel(): TimedTestViewModel {
        return ViewModelProviders.of(activity!!).get(TimedTestViewModel::class.java)
    }
}
