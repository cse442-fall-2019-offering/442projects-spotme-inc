package edu.buffalo.cse.cse442f19.spotme

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import kotlinx.android.synthetic.main.activity_match_list.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

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

        val task = GetAcceptedMatchesAsyncTask(this)
        task.userId = Globals.currentUser!!.id
        task.execute()

        requestPermissions()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            updateLocation()
        }
    }


    fun taskComplete() {
        for (i in Globals.currentAcceptedUsers) {
            displayUser(i)
        }

        val potentialTask = GetPotentialMatchesAsyncTask(this)
        potentialTask.userId = Globals.currentUser!!.id
        potentialTask.execute()
    }

    fun clearPotentialMatches() {

        potentialMatchLinLayout.removeAllViews()
    }

//  Display Potential Match
    fun addPotentialMatch(user: User) {

        for (u: User in Globals.currentAcceptedUsers) {

//          Already accepted this user.
            if (u.id == user.id) {

                return;
            }
        }
        var profileButton: ImageButton = ImageButton(this)

        profileButton.setImageResource(R.drawable.match_avatar)
        profileButton.scaleType = ImageView.ScaleType.CENTER_INSIDE

        profileButton.setOnClickListener {
            Globals.selectedMatch = user.id
            var intent = Intent(this, MatchProfileActivity::class.java)
            startActivity(intent)
        }

        potentialMatchLinLayout.addView(profileButton)
        profileButton.layoutParams.width = 250;
        profileButton.layoutParams.height = 250;
    }

//  Display Accepted Match
    fun displayUser(user: User) {
        var acceptedMatchesLayout = LinearLayout(this)
        var acceptedMatchImage = ImageButton(this)
        var acceptedMatchName = TextView(this)
        var unmatchButton = Button(this)

        acceptedMatchName.setText(user.name)
        acceptedMatchName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)

        acceptedMatchesLayout.addView(acceptedMatchImage)

        acceptedMatchesLayout.orientation = LinearLayout.HORIZONTAL

        acceptedMatchImage.layoutParams.width = 250
        acceptedMatchImage.layoutParams.height = 250
        acceptedMatchImage.setImageResource(R.drawable.match_avatar)
        acceptedMatchImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
        acceptedMatchImage.setOnClickListener {

            var intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("match_id", user.id)
            startActivity(intent)
        }

//        Globals.currentUser.id
//            user.id

        unmatchButton.text = "Unmatch"
        unmatchButton.setOnClickListener() {
            val task = DeleteAcceptedMatchesAsyncTask()
            task.userId1 = Globals.currentUser!!.id
            task.userId2 = user.id
            task.execute()

            val task2 = DeleteAcceptedMatchesAsyncTask()
            task2.userId2 = Globals.currentUser!!.id
            task2.userId1 = user.id
            task2.execute()

            acceptedMatchesView.removeView(acceptedMatchesLayout)
        }

        acceptedMatchesLayout.addView(acceptedMatchName)
        acceptedMatchesLayout.addView(unmatchButton)
//        acceptedMatchesView.addView(acceptedMatchName)
        acceptedMatchesView.addView(acceptedMatchesLayout)

    }

    class GetPotentialMatchesAsyncTask(private var activity: MatchListActivity) : AsyncTask<String, String, String>() {

        var userId: Int = 1



        override fun doInBackground(vararg p0: String?): String {
            var result = ""

            try {

                val url = URL("https://api.spot-me.xyz/matches?id=$userId")
                val conn = url.openConnection() as HttpsURLConnection

                conn.requestMethod = "GET"
                conn.connect()

                val responseCode: Int = conn.responseCode
                Log.d("GetUser", "responseCode - $responseCode")

                val inStream = if (responseCode >= 400) {
                    conn.errorStream
                } else {
                    conn.inputStream
                }
                val isReader = InputStreamReader(inStream)
                val bReader = BufferedReader(isReader)

                result = bReader.readText()
            } catch (ex: Exception) {
                Log.d("GetUser", "Error in doInBackground " + ex.message)
            }


            return result
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)

            if (result == "") {
                Log.d("Result", "EMPTY")
            } else {
                Log.d("Result", result)

                var responseObj: JSONObject = JSONObject(result)

                Log.d("Show User ", responseObj.toString())

                var matchArray: JSONArray = responseObj.getJSONArray("matches")
                Log.d("Match array size", ""+matchArray.length())
                activity.clearPotentialMatches()

                for (i in 0 until matchArray.length()) {

                    var userJsonObj: JSONObject = matchArray[i] as JSONObject
                    var userObj = User.fromJson(userJsonObj)
                    Log.d("USER OBJ", userObj.toString())
                    activity.addPotentialMatch(userObj)
                }
                /*var user1Json = matchArray[0] as JSONObject
                Globals.otherUser1 = User.fromJson(user1Json)

                var user2Json = matchArray[1] as JSONObject
                Globals.otherUser2 = User.fromJson(user2Json)

                var user3Json = matchArray[2] as JSONObject
                Globals.otherUser3 = User.fromJson(user3Json)

                var user4Json = matchArray[3] as JSONObject
                Globals.otherUser4 = User.fromJson(user4Json)

                var user5Json = matchArray[4] as JSONObject
                Globals.otherUser5 = User.fromJson(user5Json)

                var user6Json = matchArray[5] as JSONObject
                Globals.otherUser6 = User.fromJson(user6Json)

                var user7Json = matchArray[6] as JSONObject
                Globals.otherUser7 = User.fromJson(user7Json)

                var user8Json = matchArray[7] as JSONObject
                Globals.otherUser8 = User.fromJson(user8Json)

                var user9Json = matchArray[8] as JSONObject
                Globals.otherUser9 = User.fromJson(user9Json)*/

                //activity.displayProf()
            }
        }
    }

    // Getting the accepted matches list
    class GetAcceptedMatchesAsyncTask(private var matchListActivity: MatchListActivity) : AsyncTask<String, String, String>() {
        var userId: Int = 1

        override fun doInBackground(vararg p0: String?): String {
            var result = ""

            try {

                val url = URL("https://api.spot-me.xyz/accepted-matches?id=$userId")
                val conn = url.openConnection() as HttpsURLConnection

                conn.requestMethod = "GET"
                conn.connect()

                val responseCode: Int = conn.responseCode
                Log.d("GetAcceptedMatches", "responseCode - $responseCode")

                val inStream = if (responseCode >= 400) {
                    conn.errorStream
                } else {
                    conn.inputStream
                }
                val isReader = InputStreamReader(inStream)
                val bReader = BufferedReader(isReader)

                result = bReader.readText()

                var obj: JSONObject = JSONObject(result)
                var array: JSONArray = obj.getJSONArray("matches")

                Globals.currentAcceptedUsers.clear()

                for (i in 0 until array.length()) {

                    var userObj: JSONObject = array[i] as JSONObject;
                    var user = User.fromJson(userObj)
                    Globals.currentAcceptedUsers.add(user);
                }

            } catch (ex: Exception) {
                Log.d("GetAcceptedMatches", "Error in doInBackground " + ex.message)

            }

            return result;
        }


        override fun onPostExecute(result: String) {
            super.onPostExecute(result)

            matchListActivity.taskComplete()
        }
    }

    // Deleting Accepted matches
    class DeleteAcceptedMatchesAsyncTask : AsyncTask<String, String, String>() {
        var userId1: Int = 1
        var userId2: Int = 2

        override fun doInBackground(vararg p0: String?): String {
            var result = ""

            try {

                val url = URL("https://api.spot-me.xyz/accepted-matches?user1=$userId1&user2=$userId2")
                val conn = url.openConnection() as HttpsURLConnection


                conn.requestMethod = "DELETE"
                conn.doOutput = true
                conn.setRequestProperty("Content-Type", "application/json; utf-8")
                conn.setRequestProperty("Accept", "application/json")
                conn.connect()

                val responseCode: Int = conn.responseCode
                Log.d("DeletedAcceptedMatches", "responseCode - $responseCode")

                val inStream = if (responseCode >= 400) {
                    conn.errorStream
                } else {
                    conn.inputStream
                }
                val isReader = InputStreamReader(inStream)
                val bReader = BufferedReader(isReader)

                result = bReader.readText()
            } catch (ex: Exception) {
                Log.d("DeletedAcceptedMatches", "Error in doInBackground " + ex.message)
            }

            return result
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)

            if (result == "") {
                Log.d("Deleted Match", "EMPTY")
            } else {
                Log.d("Deleted Match", result)
            }
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

    override fun onBackPressed() {

        var intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}
