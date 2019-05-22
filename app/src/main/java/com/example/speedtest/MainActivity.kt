package com.example.speedtest

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), TitleSettable {

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
    }
}
