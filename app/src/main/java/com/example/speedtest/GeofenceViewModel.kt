//  # COMP 4521    #  MOHAMAD, Randitya Setyawan    20316273    rsmohamad@ust.hk
//  # COMP 4521    #  IVANOV, Metodi Dimitrov       20314512    mdivanov@connect.ust.hk

package com.example.speedtest

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class GeofenceViewModel(app: Application) : AndroidViewModel(app) {
    private val locationTests: MutableLiveData<List<GeofenceTest>> = MutableLiveData()

    fun addLocation(test: GeofenceTest) {
        locationTests.value?.let {
            if (it.contains(test))
                it
            else
                ArrayList<GeofenceTest>(it).apply {
                    run {
                        add(test)
                        sortBy { t -> t.lat + t.long }
                    }
                }

        }?.let { setLocations(it) }
    }

    init {
        loadTests()
    }

    fun getLocations(): LiveData<List<GeofenceTest>> {
        return locationTests
    }

    fun setLocations(list: List<GeofenceTest>) {
        locationTests.value = (list)
        saveTests()
    }

    fun clearLocations() {
        locationTests.value = (ArrayList())
        saveTests()
    }

    fun updateLocation(test: GeofenceTest, position: Int) {
        locationTests.value?.let {
            if (it.contains(test))
                it.toMutableList().apply { this[position] = test }
            else
                it

        }?.let { setLocations(it) }
    }

    private fun loadTests() {
        getApplication<Application>().applicationContext.let {
            val sharedPref = it.getSharedPreferences("speed_test_app", Context.MODE_PRIVATE)
            val serializedArr =
                sharedPref.getString("locations", Gson().toJson(ArrayList<GeofenceTest>()))
            val arrListType = object : TypeToken<ArrayList<GeofenceTest>>() {}.type
            val arr = Gson().fromJson<List<GeofenceTest>>(serializedArr, arrListType)
            locationTests.postValue(arr)
        }
    }

    private fun saveTests() {
        getApplication<Application>().applicationContext.let {
            val sharedPref = it.getSharedPreferences("speed_test_app", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString("locations", Gson().toJson(getLocations().value))
            editor.apply()
        }
    }
}