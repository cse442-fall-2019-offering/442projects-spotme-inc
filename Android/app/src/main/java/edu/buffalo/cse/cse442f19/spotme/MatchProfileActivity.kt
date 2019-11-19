package edu.buffalo.cse.cse442f19.spotme

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import edu.buffalo.cse.cse442f19.spotme.utils.Date
import edu.buffalo.cse.cse442f19.spotme.utils.Date.Companion.getAge
import kotlinx.android.synthetic.main.activity_match_profile.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MatchProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_profile)

        var task = GetPotentialMatchesAsyncTask(this)
        task.userId = Globals.currentUser!!.id
        task.execute()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_match_profile, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.rate -> {
                //val intent2 = activity.intent
                val matchID: Int = intent.getIntExtra("match_id", 1) //user2
                val intent = Intent(this, RatingActivity::class.java)
                //startActivity(Intent(this, RatingActivity::class.java))
                intent.putExtra("match_id", matchID)
                startActivity(intent)
                true
            }
            R.id.report -> {
                startActivity(Intent(this, ReportActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun addUser(u: JSONObject) {
        val user: User.ScoredUser = User.ScoredUser.fromJson(u)

        val dob  = Date(user.dob)
        val age = getAge(dob)

        // var matchName = TextView(this)
        Log.d("PRINTING LOL", user.name)
        matchName1.text = user.name
        val levels = resources.getStringArray(R.array.select_level)
        fitnessLevel.text = levels[user.level]
        match_age.text = age.toString()

        val genders = resources.getStringArray(R.array.select_gender)
        match_gender.text = genders[user.gender]

        val bitmap = BitmapFactory.decodeByteArray(user.picture, 0, user.picture.size)
        imageView.setImageBitmap(bitmap)

        val task = LoadRateAsyncTask(this)

        task.execute()

        var factors = ""

        if (user.fitness_level_desired) {

            factors += user.name + " matches your desired fitness level!\n"
        }

        factors += "Distance: " + user.distance + "\n"
        factors += "Match Score: " + user.match_score

        match_factors.text = factors

        acceptMatchButton.setOnClickListener {
            val task = PutAcceptedMatchesAsyncTask()
            task.userId1 = Globals.currentUser!!.id
            task.userId2 = user.id
            task.execute()

            val intent = Intent(this, MatchListActivity::class.java)
            startActivity(intent)
        }
    }

    fun setStars(stars: String) {
        average_rating.text = (stars + " stars")

    }

    class LoadRateAsyncTask(private var activity: MatchProfileActivity) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {

            var result = ""

            val userId: Int = Globals.currentUser!!.id

            val intent = activity.intent

            val matchID: Int = intent.getIntExtra("match_id", 1) //user1

            try {

                val url = URL("${Globals.ENDPOINT_BASE}/ratings?id=$userId&other_id=$matchID")
                val conn = url.openConnection() as HttpURLConnection

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
            //handler.post(runnableCode)

            if (result == "") {
                Log.d("Result", "EMPTY")
            } else {
                Log.d("Result", result)

//                Globals.currentUser = User.fromJson(JSONObject(result))
                //Use result to get values for chat
                val jsonObject = JSONObject(result)

                val str_num = jsonObject.getJSONObject("rating")

                val rate = str_num.getString("message")

                activity.setStars(rate)

            }
        }
    }

    class PutAcceptedMatchesAsyncTask : AsyncTask<String, String, String>() {
        var userId1: Int = 1
        var userId2: Int = 2

        override fun doInBackground(vararg p0: String?): String {
            var result = ""

            try {

                val url = URL("${Globals.ENDPOINT_BASE}/accepted-matches?user1=$userId1&user2=$userId2")
                val conn = url.openConnection() as HttpURLConnection


                conn.requestMethod = "PUT"
                conn.doOutput = true
                conn.setRequestProperty("Content-Type", "application/json; utf-8")
                conn.setRequestProperty("Accept", "application/json")
                conn.connect()

                val responseCode: Int = conn.responseCode
                Log.d("PutAcceptedMatches", "responseCode - $responseCode")

                val inStream = if (responseCode >= 400) {
                    conn.errorStream
                } else {
                    conn.inputStream
                }
                val isReader = InputStreamReader(inStream)
                val bReader = BufferedReader(isReader)

                result = bReader.readText()
            } catch (ex: Exception) {
                Log.d("PutAcceptedMatches", "Error in doInBackground " + ex.message)
            }

            return result
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)

            if (result == "") {
                Log.d("PutAcceptedMatches Response", "EMPTY")
            } else {
                Log.d("PutAcceptedMatches Response", result)
            }
        }
    }

    class GetPotentialMatchesAsyncTask(private var activity: MatchProfileActivity) : AsyncTask<String, String, String>() {

        var userId: Int = 1

        override fun doInBackground(vararg p0: String?): String {
            var result = ""

            try {

                val url = URL("${Globals.ENDPOINT_BASE}/matches?id=$userId")
                val conn = url.openConnection() as HttpURLConnection

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
                /**You have JSONObject from response
                This object contains a JSON Array of JSON Objects
                To get the array you need to .getJSonArray("matches")
                Loop over each JSON object and create a User from that json
                Display those users**/

                var matchArray: JSONArray = responseObj.getJSONArray("matches")

                var selectedUser = matchArray[0] as JSONObject

                for (i in 0 until matchArray.length()) {
                    var obj: JSONObject = matchArray[i] as JSONObject
                    var userObj = User.fromJson(obj)

                    if (userObj.id == Globals.selectedMatch) {
                        selectedUser = obj
                        break
                    }
                }
                activity.addUser(selectedUser)
            }
        }
    }
}
