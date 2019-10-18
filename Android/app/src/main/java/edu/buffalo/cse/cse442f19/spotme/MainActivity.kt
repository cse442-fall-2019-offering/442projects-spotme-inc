package edu.buffalo.cse.cse442f19.spotme

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var lastLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("", Globals.currentUser!!.name)

        button.setOnClickListener {
            val intent = Intent(this, MatchListActivity :: class.java)
            if (lastLocation != null) {
                val lastLoc = this.lastLocation!!
                intent.putExtra("lat", lastLoc.latitude)
                intent.putExtra("lon", lastLoc.longitude)
            }
            startActivity(intent)
        }

        requestPermissions()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            updateLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        for ((i, result) in grantResults.withIndex()) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                continue
            }
            when (permissions[i]) {
                Manifest.permission.ACCESS_COARSE_LOCATION -> updateLocation()
            }
        }
    }

    private fun updateLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    this.lastLocation = location
                }
            }
    }

    private fun requestPermissions() {
        val requiredPerms = listOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.INTERNET)

        // Generate list of all missing permissions
        val missingPerms = arrayListOf<String>()
        for (permission in requiredPerms) {
            val granted = ContextCompat.checkSelfPermission(this, permission)

            if (granted != PackageManager.PERMISSION_GRANTED) {
                missingPerms.add(permission)
            }
        }

        // Request any missing permissions
        if (missingPerms.isNotEmpty()) {
            val requested = arrayOfNulls<String>(missingPerms.size)
            missingPerms.toArray(requested)
            ActivityCompat.requestPermissions(this, requested, 0)
        }
    }
}
