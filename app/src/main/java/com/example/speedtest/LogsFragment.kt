package com.example.speedtest


import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore


class LogsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: LogsAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var logs: ArrayList<TestResult> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_logs, container, false)

        viewAdapter = LogsAdapter(logs)
        activity?.let {
            viewManager = LinearLayoutManager(it)
            recyclerView = view.findViewById<RecyclerView>(R.id.logs_list).apply {
                layoutManager = viewManager
                adapter = viewAdapter
            }
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as TitleSettable).setActionBarTitle("Logs")

        var imei = ""
        try {
            val tm = (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager)
            imei = tm.imei

        } catch (e: SecurityException) {

        }

        val db = FirebaseFirestore.getInstance()
        db.collection("testResults").whereEqualTo("imei", imei)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    logs.clear()
                    task.result?.forEach { d -> logs.add(TestResult.fromMap(d.data)) }
                    logs.sortBy { d -> d.timestamp }
                    viewAdapter.notifyDataSetChanged()
                } else {
                    Log.w(ContentValues.TAG, "Error getting documents.", task.exception)
                }
            }
    }

}
