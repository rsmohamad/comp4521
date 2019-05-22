package com.example.speedtest

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fr.bmartel.speedtest.SpeedTestReport
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*


data class TimedTest(val time: LocalTime, val enabled: Boolean)


val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("KK:mm a")
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


class ScheduledSpeedTestTask(ctx: Context) : SpeedTestTask(ctx) {
    override fun onProgressUpdate(vararg values: SpeedTestReport?) {
        super.onProgressUpdate(*values)
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
    }

    override fun onPreExecute() {
        super.onPreExecute()
        Log.v("alarm triggered", "time triggered")
    }
}


class ScheduledTestBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.v("alarm triggered", "time triggered")
        System.out.println("fuck")
        ScheduledSpeedTestTask(context!!).execute()
    }
}


class TimedTestFragment : Fragment(), TimePickerDialog.OnTimeSetListener {


    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val timedTests = ArrayList<TimedTest>()

    private var hour: Int = 0
    private var minute: Int = 0
    private lateinit var timeDialog: TimePickerDialog

    private val INTENT_ALARM = "intent_alarm_haha"

    private fun toRequestCode(hour: Int, minute: Int): Int {
        return hour * 100 + minute
    }


    private fun scheduleAlarm(hour: Int, minute: Int) {

        val intent = Intent(context, ScheduledSpeedTestTask::class.java).apply { action = INTENT_ALARM }
        val alarmIntent =
            PendingIntent.getBroadcast(context, toRequestCode(hour, minute), intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }


        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, alarmIntent)

        val alarmNotCreated = PendingIntent.getBroadcast(
            context,
            toRequestCode(hour, minute),
            intent,
            PendingIntent.FLAG_NO_CREATE
        ) == null

        if (alarmNotCreated)
            Log.v("scheduled", "not created")

        Log.v("next alarm", alarmManager.nextAlarmClock.triggerTime.toString())
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
