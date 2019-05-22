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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalTime
import java.util.*


data class TimedTest(val time: LocalTime, val enabled: Boolean)

class TimedTestFragment : Fragment(), TimePickerDialog.OnTimeSetListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val timedTests = ArrayList<TimedTest>()
    private var hour: Int = 0
    private var minute: Int = 0
    private lateinit var timeDialog: TimePickerDialog
    private val INTENT_ALARM = getString(R.string.alarm_intent)

    private fun toRequestCode(hour: Int, minute: Int): Int {
        return hour * 100 + minute
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
        Log.v("scheduled", calendar.timeInMillis.toString())
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        timedTests.add(TimedTest(LocalTime.of(hourOfDay, minute), true))
        viewAdapter.notifyDataSetChanged()
        scheduleAlarm(hourOfDay, minute)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_timed_test, container, false)
        viewAdapter = TimedTestAdapter(timedTests)
        timeDialog = TimePickerDialog(context, this, hour, minute, true)
        timeDialog.setTitle("Schedule a time to run test")

        val addButton = view.findViewById<FloatingActionButton>(R.id.addScheduleButton)
        addButton.setOnClickListener { timeDialog.show() }

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
    }
}
