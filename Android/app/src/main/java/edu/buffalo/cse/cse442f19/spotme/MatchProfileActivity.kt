package edu.buffalo.cse.cse442f19.spotme

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import edu.buffalo.cse.cse442f19.spotme.Globals.Companion.otherUser1
import edu.buffalo.cse.cse442f19.spotme.utils.Date
import edu.buffalo.cse.cse442f19.spotme.utils.Date.Companion.getAge
import kotlinx.android.synthetic.main.activity_match_profile.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class MatchProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_profile)

        var task = GetPotentialMatchesAsyncTask(this)
        task.userId = Globals.currentUser!!.id
        task.execute()

        /*val textView = findViewById(R.id.matchName) as TextView
        textView.text = Globals.otherUser1?.name*/

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_match_profile, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.rate -> {
                startActivity(Intent(this, RatingActivity::class.java))
                true
            }
            R.id.report -> {
                startActivity(Intent(this, ReportActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    fun displayProf(){
        if(Globals.selectedMatch == 0){
            matchName1.text = Globals.otherUser1?.name
        }else if(Globals.selectedMatch == 1){
            matchName1.text = Globals.otherUser2?.name
        }else if(Globals.selectedMatch == 2){
            matchName1.text = Globals.otherUser3?.name
        }else if(Globals.selectedMatch == 3){
            matchName1.text = Globals.otherUser4?.name
        }else if(Globals.selectedMatch == 4){
            matchName1.text = Globals.otherUser5?.name
        }else if(Globals.selectedMatch == 5){
            matchName1.text = Globals.otherUser6?.name
        }
    }

    fun addUser(u: JSONObject) {

        Globals.otherUser1 = User.fromJson(u)

        var dob  = Date(otherUser1!!.dob)
        var age = getAge(dob)



       // var matchName = TextView(this)
        Log.d("PRINTING LOL", Globals.otherUser1!!.name)
        matchName1.text = Globals.otherUser1!!.name
        fitnessLevel.text = Globals.otherUser1!!.level.toString()
        match_age.text = age.toString()
        if(Globals.otherUser1!!.gender == 0){
            match_gender.text = "Male"
        }else{
            match_gender.text = "Female"
        }

        acceptMatchButton.setOnClickListener {

            //Globals.currentAcceptedUsers.add(Globals.otherUser1!!)

            //TODO set put request to API

            var task = PutAcceptedMatchesAsyncTask()
            task.userId1 = Globals.currentUser!!.id
            task.userId2 = Globals.otherUser1!!.id
            task.execute();

            var task2 = PutAcceptedMatchesAsyncTask()
            task2.userId2 = Globals.currentUser!!.id
            task2.userId1 = Globals.otherUser1!!.id
            task2.execute();
        }
       // linlayout.addView(matchName)

    }

    class PutAcceptedMatchesAsyncTask : AsyncTask<String, String, String>() {
        var userId1: Int = 1
        var userId2: Int = 2

        override fun doInBackground(vararg p0: String?): String {
            var result = ""

            try {

                val url = URL("https://api.spot-me.xyz/accepted-matches?user1=$userId1&user2=$userId2")
                val conn = url.openConnection() as HttpsURLConnection


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
                /**You have JSONObject from response
                This object contains a JSON Array of JSON Objects
                To get the array you need to .getJSonArray("matches")
                Loop over each JSON object and create a User from that json
                Display those users**/

                var matchArray = responseObj.getJSONArray("matches")
                var user1Json = matchArray[0] as JSONObject

                for (i in 0..(matchArray.length())) {
                    //var user1Json = matchArray[0] as JSONObject
                    if(i == Globals.selectedMatch){
                        user1Json = matchArray[i] as JSONObject
                    }
                    /*Globals.otherUser1 = User.fromJson(user1Json)*/

                    //activity.addUser(user1Json)
                }
                activity.addUser(user1Json)
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

    override fun onBackPressed() {

        var intent = Intent(this, MatchListActivity::class.java)
        startActivity(intent)
    }
}
