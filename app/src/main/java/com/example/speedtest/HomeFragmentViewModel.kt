package com.example.speedtest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeFragmentViewModel : ViewModel() {
    private val percent: MutableLiveData<Double> = MutableLiveData()
    private val buttonEnabled: MutableLiveData<Boolean> = MutableLiveData()
    private val downloadRate: MutableLiveData<Double> = MutableLiveData()
    private val uploadRate: MutableLiveData<Double> = MutableLiveData()
    private val downloading: MutableLiveData<Boolean> = MutableLiveData()
    private val isTesting: MutableLiveData<Boolean> = MutableLiveData()

    fun getProgress(): LiveData<Double> {
        return percent
    }

    fun getDownloadRate(): LiveData<Double> {
        return downloadRate
    }

    fun getUploadRate(): LiveData<Double> {
        return uploadRate
    }

    fun isButtonEnabled(): LiveData<Boolean> {
        return buttonEnabled
    }

    fun isDownloading(): LiveData<Boolean> {
        return downloading
    }

    fun isTesting(): LiveData<Boolean> {
        return isTesting
    }

    fun setProgress(value: Double) {
        percent.postValue(value)
    }

    fun setButtonEnabled(value: Boolean) {
        buttonEnabled.postValue(value)
    }

    fun setDowloadRate(value: Double) {
        downloadRate.postValue(value)
    }

    fun setUploadRate(value: Double) {
        uploadRate.postValue(value)
    }

    fun setDownloading(value: Boolean) {
        downloading.postValue(value)
    }

    fun setIsTesting(value: Boolean) {
        isTesting.postValue(value)
    }
}