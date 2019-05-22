package com.example.speedtest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TimedTestViewModel : ViewModel() {
    private val timedTests: MutableLiveData<List<TimedTestFragment.TimedTest>> = MutableLiveData()

    fun addTest(test: TimedTestFragment.TimedTest) {
        timedTests.value?.let {
            if (it.contains(test))
                it
            else
                ArrayList<TimedTestFragment.TimedTest>(it).apply { add(test) }

        }.let { timedTests.postValue(it) }
    }

    fun getSchedules(): LiveData<List<TimedTestFragment.TimedTest>> {
        return timedTests
    }

    fun setSchedules(list: List<TimedTestFragment.TimedTest>) {
        timedTests.postValue(list)
    }
}