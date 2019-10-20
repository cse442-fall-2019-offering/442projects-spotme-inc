package edu.buffalo.cse.cse442f19.spotme

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

import kotlinx.android.synthetic.main.activity_match_list.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class MatchListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_list)


        val task = GetAcceptedMatchesAsyncTask()
        task.userId = Globals.currentUser!!.id
        Log.d("Fetching on", "" + task.userId)
        task.execute()

        potentialMatch1.setOnClickListener {
            val intent = Intent(this, MatchProfileActivity :: class.java)
            startActivity(intent)
        }
        potentialMatch2.setOnClickListener {

            /*val url = URL("api.spot-me.xyz/accepted-matches")
            val urlConnection = url.openConnection() as HttpURLConnection

            try {

                urlConnection.requestMethod = "GET"
                urlConnection.addRequestProperty("user1", "THIS USER SOMEHOW KNOW WHO U ARE")
                urlConnection.addRequestProperty("user2", "POTENTIAL MATCH 2 USER ID")
                //val inputStream = new BufferedInputStream(urlConnection.getInputStream())
                //readStream(inputStream);

            } finally {

                urlConnection.disconnect();
            }*/
            val intent = Intent(this, MatchProfileActivity :: class.java)
            startActivity(intent)
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
    }

    // Creates a Menu with MyProfile button
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_my_profile, menu)
        return true
    }

    // Lets MyProfile button take you to the MyProfile page
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.MyProfileB -> {
                startActivity(Intent(this, MyProfile::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Getting the accepted matches list
    class GetAcceptedMatchesAsyncTask : AsyncTask<String, String, String>() {
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
            } catch (ex: Exception) {
                Log.d("GetAcceptedMatches", "Error in doInBackground " + ex.message)
            }

            return result
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)

            if (result == "") {
                Log.d("Accepted Result", "EMPTY")
            } else {
                Log.d("Accepted Result", result)

                var resultObj: JSONObject = JSONObject(result)
                var listMatches: JSONArray = resultObj.getJSONArray("matches")
                Globals.currentAcceptedUsers

                for (obj in 0 until listMatches.length()) {
                    Globals.currentAcceptedUsers.add(User.fromJson(listMatches.getJSONObject(obj)))
                }

                //Output
                //for (obj in Globals.currentAcceptedUsers) {
                //    Log.d("Accepted-Match User", obj.toJson().toString())
                //}
            }
        }
    }
}
