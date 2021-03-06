//  # COMP 4521    #  MOHAMAD, Randitya Setyawan    20316273    rsmohamad@ust.hk
//  # COMP 4521    #  IVANOV, Metodi Dimitrov       20314512    mdivanov@connect.ust.hk

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
    private val locationText: MutableLiveData<String> = MutableLiveData()
    private val carrierText: MutableLiveData<String> = MutableLiveData()

    fun getProgress(): LiveData<Double> {
        return percent
    }

    fun getLocationText(): LiveData<String> {
        return locationText
    }

    fun getCarrierText(): LiveData<String> {
        return carrierText
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

    fun setLocationText(value: String) {
        locationText.postValue(value)
    }

    fun setCarrierText(value: String) {
        carrierText.postValue(value)
    }
}