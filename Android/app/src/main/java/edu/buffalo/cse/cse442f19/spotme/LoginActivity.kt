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
import java.io.InputStream
import java.io.InputStreamReader
import javax.net.ssl.HttpsURLConnection


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        button.setOnClickListener {
            val intent = Intent(this, MainActivity :: class.java)
            startActivity(intent)
        }

        val selections = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, selections)

        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = aa;

        var loginAct: LoginActivity = this;

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                var task: SetUserAsyncTask = SetUserAsyncTask(loginAct)
                task.userId = position + 1;
                Log.d("Fetching on", "" + task.userId);
                task.execute()
            }
        }
    }
    class SetUserAsyncTask(private var activity: LoginActivity?) : AsyncTask<String, String, String>() {

        var userId: Int = 1;

        override fun onPreExecute() {

            super.onPreExecute()
        }

        override fun doInBackground(vararg p0: String?): String {

            var result = ""

            try {

                val url = URL("https://api.spot-me.xyz/user?id=$userId")
                val httpURLConnection = url.openConnection() as HttpsURLConnection//HTTPS

                httpURLConnection.requestMethod = "GET";
                httpURLConnection.connect()

                val responseCode: Int = httpURLConnection.responseCode
                Log.d(activity?.localClassName, "responseCode - $responseCode")

                val inStream: InputStream;

                if (responseCode >= 400) {

                    inStream = httpURLConnection.errorStream;
                } else {

                    inStream = httpURLConnection.inputStream
                }
                val isReader = InputStreamReader(inStream)
                val bReader = BufferedReader(isReader)
                var tempStr: String?

                try {
                   while (true) {
                        tempStr = bReader.readLine()
                        if (tempStr == null) {
                            break
                        }
                        result += tempStr
                    }
                } catch (Ex: Exception) {

                    Log.e(activity?.localClassName, "Error in convertToString " + Ex.printStackTrace())
                }
            } catch (ex: Exception) {
                Log.d("", "Error in doInBackground " + ex.message)
            }
            return result
        }

        override fun onPostExecute(result: String?) {

            super.onPostExecute(result)

            if (result == "") {

                Log.d("Result", "EMPTY")
            } else {

                Log.d("Result", result)

                var parsedResult = ""
                var jsonObject: JSONObject = JSONObject(result) ?: return //Returns if null

                var user = User()

                user.dob = jsonObject.getString("dob")
                user.gender = jsonObject.optInt("gender")
                user.id = jsonObject.optInt("id")
                user.lat = jsonObject.optDouble("lat")
                user.level = jsonObject.optInt("level")
                user.lon = jsonObject.optDouble("lon")
                user.name = jsonObject.optString("name")
                user.partner_gender = jsonObject.optInt("partner_gender")
                user.partner_level = jsonObject.optInt("partner_level")
                user.radius = jsonObject.optInt("radius")
                user.username = jsonObject.optString("username")
                user.weight = jsonObject.optDouble("weight")

                Globals.currentUser = user; //Set global currentUser.

//              Advance to next screen.

            }
        }
    }
}
