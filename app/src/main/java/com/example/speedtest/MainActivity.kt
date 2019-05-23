package com.example.speedtest

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import androidx.annotation.NonNull
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import android.content.pm.PackageManager
import android.Manifest.permission
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.tasks.Task


class MainActivity : AppCompatActivity(), TitleSettable {

    override fun setActionBarTitle(title: String) {
        supportActionBar?.title = title
    }

    private val TAG = MainActivity::class.java.simpleName


    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34

    private var navHostFragment: NavHostFragment? = null
    private var navController: NavController? = null


    private enum class PendingGeofenceTask {
        ADD, REMOVE, NONE
    }

    lateinit var mGeofencingClient: GeofencingClient
    private var mGeofenceList: java.util.ArrayList<Geofence> = java.util.ArrayList<Geofence>()
    private var mGeofencePendingIntent: PendingIntent? = null


    private var mPendingGeofenceTask = PendingGeofenceTask.NONE


    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                navController?.navigate(R.id.homeFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                navController?.navigate(R.id.settingFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }


    //lateinit var geofencingClient: GeofencingClient
    //protected var geofenceList: ArrayList<Geofence> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {

        /*checkLocationPermission()
        Log.i("canaccess", canAccessLocation().toString())*/
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        mGeofenceList = java.util.ArrayList()
        mGeofencePendingIntent = null

        populateGeofenceList()
        mGeofencingClient = LocationServices.getGeofencingClient(this)


        navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment?
        navController = navHostFragment?.navController

/*
        val GEOFENCE_RADIUS_IN_METERS = 300
        val GEOFENCE_EXPIRATION_IN_MILLISECONDS = Geofence.NEVER_EXPIRE
        val Longitude = 114.117
        val Latitude = 22.315

        val geofence = Geofence.Builder()
            .setRequestId("Google HQ")
            .setCircularRegion(Latitude, Longitude, GEOFENCE_RADIUS_IN_METERS.toFloat())
            .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)
            .setTransitionTypes(
                Geofence.GEOFENCE_TRANSITION_ENTER or
                        Geofence.GEOFENCE_TRANSITION_EXIT or
                        Geofence.GEOFENCE_TRANSITION_DWELL
            )
            .setLoiteringDelay(1)
            .build()

        geofenceList.add(Geofence.Builder()
            .setRequestId("Google HQ")
            .setCircularRegion(Latitude, Longitude, GEOFENCE_RADIUS_IN_METERS.toFloat())
            .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)
            .setTransitionTypes(
                Geofence.GEOFENCE_TRANSITION_ENTER or
                        Geofence.GEOFENCE_TRANSITION_DWELL
            )
            .setLoiteringDelay(1)
            .build())

        for(i in geofenceList){
            Log.e("geo", "geofence " + i.requestId.toString())
        }

        //startService(geofencePendingIntent)

        geofencingClient = LocationServices.getGeofencingClient(this)
        if(geofencingClient != null){
            Log.e("geo", "success getting geofencingClient")
        }
        //val geofencePendingIntent = createIntent()
        geofencingClient.addGeofences(getGeofencingRequest(), geofencePendingIntent).run {
            addOnSuccessListener {
                Log.e("geo", "success adding a geofence")
            }
            addOnFailureListener {
                Log.e("geo", "fail adding a geofence", exception)
            }
        }

*/

    }
    public override fun onStart() {
        super.onStart()

        if (!checkPermissions()) {
            requestPermissions()
        } else {
            performPendingGeofenceTask()
        }
    }

    private fun getGeofencingRequest(): GeofencingRequest {
        val builder = GeofencingRequest.Builder()

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList)

        // Return a GeofencingRequest.
        return builder.build()
    }

    private fun addGeofences() {
        if (!checkPermissions()) {
            //showSnackbar(getString(R.string.insufficient_permissions))
            return
        }

        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
            .addOnCompleteListener(this)
    }

    private fun removeGeofences() {
        if (!checkPermissions()) {
            //showSnackbar(getString(R.string.insufficient_permissions))
            return
        }

        mGeofencingClient.removeGeofences(getGeofencePendingIntent()).addOnCompleteListener(this)
    }

    override fun onComplete(task: Task<Void>) {
        mPendingGeofenceTask = PendingGeofenceTask.NONE
        if (task.isSuccessful) {
            updateGeofencesAdded(!getGeofencesAdded())
            setButtonsEnabledState()

            val messageId = if (getGeofencesAdded())
                R.string.geofences_added
            else
                R.string.geofences_removed
            Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show()
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            val errorMessage = GeofenceErrorMessages.getErrorString(this, task.exception)
            Log.w(TAG, errorMessage)
        }
    }

    private fun getGeofencePendingIntent(): PendingIntent {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent
        }
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        return mGeofencePendingIntent
    }

    private fun performPendingGeofenceTask() {
        if (mPendingGeofenceTask == PendingGeofenceTask.ADD) {
            addGeofences()
        } else if (mPendingGeofenceTask == PendingGeofenceTask.REMOVE) {
            removeGeofences()
        }
    }

    /**
     * Return the current state of the permissions needed.
     */
    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.")
            /*showSnackbar(R.string.permission_rationale, android.R.string.ok,
                View.OnClickListener {
                    // Request permission
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_PERMISSIONS_REQUEST_CODE
                    )
                })*/
        } else {
            Log.i(TAG, "Requesting permission")
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }


    private fun populateGeofenceList() {
        for (entry in Constants.BAY_AREA_LANDMARKS) {

            mGeofenceList.add(
                Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(entry.key)

                    // Set the circular region of this geofence.
                    .setCircularRegion(
                        entry.value.latitude,
                        entry.value.longitude,
                        Constants.GEOFENCE_RADIUS_IN_METERS
                    )

                    // Set the expiration duration of the geofence. This geofence gets automatically
                    // removed after this period of time.
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                    // Set the transition types of interest. Alerts are only generated for these
                    // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)

                    // Create the geofence.
                    .build()
            )
        }
    }
    /*private createIntent(){
        Intent intent = new Intent(MainActivity, GeofenceTransitionsIntentService::class.java)
        Log.e(TAG,
            return PendingIntent.getService(mActivity, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }*/
/*
    private val geofencePendingIntent: PendingIntent by lazy{

        val intent = Intent(this, GeofenceTransitionsIntentService::class.java)
        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        //original: PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
*/

/*
    private fun getGeofencingRequest(): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(geofenceList)
        }.build()
    }*/
/*
    fun checkLocationPermission(): Boolean {
        val permission = "android.permission.ACCESS_FINE_LOCATION"
        val res = this.checkCallingOrSelfPermission(permission)
        Log.i("res", res.toString())
        return res == PackageManager.PERMISSION_GRANTED
    }

    private fun canAccessLocation(): Boolean {
        return hasPermission("android.permission.ACCESS_FINE_LOCATION")
    }

    private fun hasPermission(perm: String): Boolean {
        return PackageManager.PERMISSION_GRANTED == checkSelfPermission(perm)
    }
*/
}
