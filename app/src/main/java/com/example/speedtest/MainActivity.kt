package com.example.speedtest

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import java.util.*

class MainActivity : AppCompatActivity(), TitleSettable, OnCompleteListener<Void> {

    /**
     * Provides access to the Geofencing API.
     */
    private var mGeofencingClient: GeofencingClient? = null

    /**
     * The list of geofences used in this sample.
     */
    private var mGeofenceList: ArrayList<Geofence>? = null

    /**
     * Used when requesting to add or remove geofences.
     */
    private var mGeofencePendingIntent: PendingIntent? = null

    // Buttons for kicking off the process of adding or removing geofences.
    private var mAddGeofencesButton: Button? = null
    private var mRemoveGeofencesButton: Button? = null

    private var mPendingGeofenceTask = PendingGeofenceTask.ADD

    /**
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     */
    private// The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
    // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
    // is already inside that geofence.
    // Add the geofences to be monitored by geofencing service.
    // Return a GeofencingRequest.
    val geofencingRequest: GeofencingRequest
        get() {
            val builder = GeofencingRequest.Builder()
            builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            builder.addGeofences(mGeofenceList)
            return builder.build()
        }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private// Reuse the PendingIntent if we already have it.
    // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
    // addGeofences() and removeGeofences().
    val geofencePendingIntent: PendingIntent?
        get() {
            if (mGeofencePendingIntent != null) {
                return mGeofencePendingIntent
            }
            val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
            mGeofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            return mGeofencePendingIntent
        }

    /**
     * Returns true if geofences were added, otherwise false.
     */
    private val geofencesAdded: Boolean
        get() = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
            Constants.GEOFENCES_ADDED_KEY, false
        )

    /**
     * Tracks whether the user requested to add or remove geofences, or to do neither.
     */
    private enum class PendingGeofenceTask {
        ADD, REMOVE, NONE
    }

    override fun setActionBarTitle(title: String) {
        supportActionBar?.title = title
    }

    private var navHostFragment: NavHostFragment? = null
    private var navController: NavController? = null

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                navController?.navigate(R.id.homeFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_timed -> {
                navController?.navigate(R.id.timedTestFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_geofences -> {
                navController?.navigate(R.id.geofenceFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_logs -> {
                navController?.navigate(R.id.logsFragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment?
        navController = navHostFragment?.navController

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            1
        )

        // Empty list for storing geofences.
        mGeofenceList = ArrayList()

        // Initially set the PendingIntent used in addGeofences() and removeGeofences() to null.
        mGeofencePendingIntent = null

        // Get the geofences used. Geofence data is hard coded in this sample.
        populateGeofenceList()
        mGeofencingClient = LocationServices.getGeofencingClient(this)
    }

    public override fun onStart() {
        super.onStart()
        if (!checkPermissions()) {
            requestPermissions()
        } else {
            performPendingGeofenceTask()
        }

        addGeofences()
    }

    private fun addGeofences() {
        if (!checkPermissions()) {
            showSnackbar(getString(R.string.insufficient_permissions))
            return
        }

        try {
            mGeofencingClient!!.addGeofences(geofencingRequest, geofencePendingIntent)
                .addOnCompleteListener(this)
        } catch (e: SecurityException) {
            Log.v("addGeofences", e.toString())
        }
    }

    /**
     * Removes geofences. This method should be called after the user has granted the location
     * permission.
     */
    private fun removeGeofences() {
        if (!checkPermissions()) {
            showSnackbar(getString(R.string.insufficient_permissions))
            return
        }

        mGeofencingClient!!.removeGeofences(geofencePendingIntent).addOnCompleteListener(this)
    }

    /**
     * Runs when the result of calling [.addGeofences] and/or [.removeGeofences]
     * is available.
     * @param task the resulting Task, containing either a result or error.
     */
    override fun onComplete(task: Task<Void>) {
        mPendingGeofenceTask = PendingGeofenceTask.NONE
        if (task.isSuccessful) {
            updateGeofencesAdded(!geofencesAdded)

            val messageId = if (geofencesAdded)
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

    /**
     * This sample hard codes geofence data. A real app might dynamically create geofences based on
     * the user's location.
     */
    private fun populateGeofenceList() {
        for ((key, value) in Constants.HKLOCATIONS) {

            mGeofenceList!!.add(
                Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(key)

                    // Set the circular region of this geofence.
                    .setCircularRegion(
                        value.latitude,
                        value.longitude,
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


    /**
     * Shows a [Snackbar] using `text`.
     *
     * @param text The Snackbar text.
     */
    private fun showSnackbar(text: String) {
        val container = findViewById<View>(R.id.content)
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show()
        }
    }

    /**
     * Shows a [Snackbar].
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private fun showSnackbar(
        mainTextStringId: Int, actionStringId: Int,
        listener: View.OnClickListener
    ) {
        Snackbar.make(
            findViewById(R.id.content),
            getString(mainTextStringId),
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction(getString(actionStringId), listener).show()
    }

    /**
     * Stores whether geofences were added ore removed in [SharedPreferences];
     *
     * @param added Whether geofences were added or removed.
     */
    private fun updateGeofencesAdded(added: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(this)
            .edit()
            .putBoolean(Constants.GEOFENCES_ADDED_KEY, added)
            .apply()
    }

    /**
     * Performs the geofencing task that was pending until location permission was granted.
     */
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
            showSnackbar(
                R.string.permission_rationale, 0,
                View.OnClickListener {
                    // Request permission
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_PERMISSIONS_REQUEST_CODE
                    )
                })
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

    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.i(TAG, "onRequestPermissionResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.size <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.")
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission granted.")
                performPendingGeofenceTask()
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(
                    R.string.permission_denied_explanation, R.string.settings,
                    View.OnClickListener {
                        // Build intent that displays the App settings screen.
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts(
                            "package",
                            BuildConfig.APPLICATION_ID, null
                        )
                        intent.data = uri
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    })
                mPendingGeofenceTask = PendingGeofenceTask.NONE
            }
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    }
}
