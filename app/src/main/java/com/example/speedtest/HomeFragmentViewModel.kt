package com.example.speedtest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeFragmentViewModel : ViewModel() {
    private val percent: MutableLiveData<Float> = MutableLiveData()
    private val buttonEnabled: MutableLiveData<Boolean> = MutableLiveData()

    fun getProgress(): LiveData<Float> {
        return percent
    }

    fun isButtonEnabled(): LiveData<Boolean> {
        return buttonEnabled
    }

    fun setProgress(value: Float) {
        percent.postValue(value)
    }

    fun setButtonEnabled(value: Boolean) {
        buttonEnabled.postValue(value)
    }
}