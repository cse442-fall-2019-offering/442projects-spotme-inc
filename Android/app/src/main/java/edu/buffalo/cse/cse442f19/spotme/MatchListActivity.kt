package edu.buffalo.cse.cse442f19.spotme

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import kotlinx.android.synthetic.main.activity_match_list.*
import java.net.HttpURLConnection
import java.net.URL

class MatchListActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_list)
       // setSupportActionBar(toolbar)

//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }

        potentialMatch1.setOnClickListener {
            val intent = Intent(this, MatchProfileActivity :: class.java)
            startActivity(intent)
        }
        potentialMatch2.setOnClickListener {

            val url = URL("api.spot-me.xyz/accepted-matches")
            val urlConnection = url.openConnection() as HttpURLConnection

            try {

                urlConnection.requestMethod = "DELETE"
                urlConnection.addRequestProperty("user1", "THIS USER SOMEHOW KNOW WHO U ARE")
                urlConnection.addRequestProperty("user2", "POTENTIAL MATCH 2 USER ID")
//                val inputStream = BufferedInputStream(urlConnection.getInputStream())
//                readStream(inputStream);
            } finally {

                urlConnection.disconnect()
            }

//            val intent = Intent(this, MatchProfileActivity :: class.java)
//            startActivity(intent)
        }
        potentialMatch3.setOnClickListener {
            val intent = Intent(this, MatchProfileActivity :: class.java)
            startActivity(intent)
        }
        potentialMatch4.setOnClickListener {
            val intent = Intent(this, MatchProfileActivity :: class.java)
            startActivity(intent)
        }
        potentialMatch5.setOnClickListener {
            val intent = Intent(this, MatchProfileActivity :: class.java)
            startActivity(intent)
        }
        potentialMatch6.setOnClickListener {
            val intent = Intent(this, MatchProfileActivity :: class.java)
            startActivity(intent)
        }
        chosenMatch1Button.setOnClickListener {
            val intent = Intent(this, ChatActivity :: class.java)
            startActivity(intent)
        }
        chosenMatch2Button.setOnClickListener {
            val intent = Intent(this, ChatActivity :: class.java)
            startActivity(intent)
        }
        chosenMatch3Button.setOnClickListener {
            val intent = Intent(this, ChatActivity :: class.java)
            startActivity(intent)
        }
        chosenMatch4Button.setOnClickListener {
            val intent = Intent(this, ChatActivity :: class.java)
            startActivity(intent)
        }
        chosenMatch5Button.setOnClickListener {
            val intent = Intent(this, ChatActivity :: class.java)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_my_profile, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.MyProfileB -> {
                startActivity(Intent(this, MyProfile::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
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
                    Globals.currentUser?.lat = location.latitude
                    Globals.currentUser?.lon = location.longitude
                    UpdateUserAsyncTask().execute()
                }
            }
    }

    private fun requestPermissions() {
        val requiredPerms = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
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
