package edu.buffalo.cse.cse442f19.spotme

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*
import java.net.URL
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.net.ssl.HttpsURLConnection


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        button.setOnClickListener {
            val intent = Intent(this, MatchListActivity :: class.java)
            startActivity(intent)
        }

        val selections = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, selections)

        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = aa

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val task = GetUserAsyncTask()
                task.userId = position + 1
                Log.d("Fetching on", "" + task.userId)
                task.execute()
            }
        }
    }
    class GetUserAsyncTask : AsyncTask<String, String, String>() {
        var userId: Int = 1

        override fun doInBackground(vararg p0: String?): String {
            var result = ""

            try {

                val url = URL("https://api.spot-me.xyz/user?id=$userId")
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

                Globals.currentUser = User.fromJson(JSONObject(result))
            }
        }
    }
}
