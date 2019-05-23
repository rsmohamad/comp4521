package com.example.speedtest

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TimedTestViewModel(app: Application) : AndroidViewModel(app) {
    private val timedTests: MutableLiveData<List<TimedTestFragment.TimedTest>> = MutableLiveData()

    fun addTest(test: TimedTestFragment.TimedTest) {
        timedTests.value?.let {
            if (it.contains(test))
                it
            else
                ArrayList<TimedTestFragment.TimedTest>(it).apply {
                    run {
                        add(test)
                        sortBy { t -> t.time }
                    }
                }

        }?.let { setSchedules(it) }
    }

    init {
        loadTests()
    }

    fun getSchedules(): LiveData<List<TimedTestFragment.TimedTest>> {
        return timedTests
    }

    fun setSchedules(list: List<TimedTestFragment.TimedTest>) {
        timedTests.postValue(list)
        saveTests()
    }

    fun clearSchedules() {
        timedTests.postValue(ArrayList())
        saveTests()
    }

    private fun loadTests() {
        getApplication<Application>().applicationContext.let {
            val sharedPref = it.getSharedPreferences("speed_test_app", Context.MODE_PRIVATE)
            val serializedArr =
                sharedPref.getString("schedules", Gson().toJson(java.util.ArrayList<TimedTestFragment.TimedTest>()))
            val arrListType = object : TypeToken<java.util.ArrayList<TimedTestFragment.TimedTest>>() {}.type
            val arr = Gson().fromJson<List<TimedTestFragment.TimedTest>>(serializedArr, arrListType)
            timedTests.postValue(arr)
        }
    }

    private fun saveTests() {
        getApplication<Application>().applicationContext.let {
            val sharedPref = it.getSharedPreferences("speed_test_app", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString("schedules", Gson().toJson(getSchedules().value))
            editor.apply()
        }
    }
}