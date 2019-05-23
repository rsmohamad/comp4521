package com.example.speedtest

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceTransitionsIntentService : IntentService("GeofenceTransitionsIntentService"){

    override fun onCreate(){
        super.onCreate()
        Log.e("geocreateservice", "geofencetransitionsintentservice")
    }
    override fun onHandleIntent(intent: Intent?) {
        Log.e("inOnhandleIntent", "entered onhandleintent")
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if(geofencingEvent.hasError()){
            Log.e("error", geofencingEvent.errorCode.toString())
            return
        }

        val geofenceTransition = geofencingEvent.geofenceTransition

        if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL){
            val triggeringGeofences = geofencingEvent.triggeringGeofences

            //val geofenceTransitionDetails = getGeofenceTransitionDetails(this, geofenceTransition, triggeringGeofences)

            //sendNotification(geofenceTransitionDetails)
            Log.e("triggeredgeofence", "caught geofence transition")
        }
        else{
            Log.e("someError", "i dont know what error this is")
        }
    }

    private fun getGeofenceTransitionDetails(geofenceTransitionsIntentService: GeofenceTransitionsIntentService, geofenceTransition: Int, triggeringGeofences: List<Geofence>): String? {
     return null
    }


}