package edu.buffalo.cse.cse442f19.spotme

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import kotlinx.android.synthetic.main.activity_preference.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.URL
import java.text.SimpleDateFormat
import javax.net.ssl.HttpsURLConnection
import java.util.*


class PreferenceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preference)

        if (Globals.currentUser == null) {
            val intent = Intent(this, LoginActivity :: class.java)
            startActivity(intent)
        }
        val user = Globals.currentUser!!

        prefName.setText(user.name)
        prefPartnerGender.setSelection(user.partner_gender, false)
        prefDistance.setSelection(user.radius, false)
        prefPartnerLevel.setSelection(user.partner_level, false)
        prefLevel.setSelection(user.level, false)

        prefName.setOnFocusChangeListener { _, gainFocus ->
            if (!gainFocus) {
                updatePreferences()
            }
        }

        val spinnerUpdateListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updatePreferences()
            }
        }

        prefPartnerGender.onItemSelectedListener = spinnerUpdateListener
        prefDistance.onItemSelectedListener = spinnerUpdateListener
        prefPartnerLevel.onItemSelectedListener = spinnerUpdateListener
        prefLevel.onItemSelectedListener = spinnerUpdateListener
    }

    fun updatePreferences() {
        val user = Globals.currentUser!!
        user.name = prefName.text.toString()
        user.partner_gender = prefPartnerGender.selectedItemPosition
        user.radius = prefDistance.selectedItemPosition
        user.partner_level = prefPartnerLevel.selectedItemPosition
        user.level = prefLevel.selectedItemPosition

        val task = PrefsSetAsyncTask()
        task.execute()
    }

    class PrefsSetAsyncTask : AsyncTask<String, String, String>() {
        override fun doInBackground(vararg p0: String?): String {
            var result = ""

            val userId = Globals.currentUser?.id
            try {
                val url = URL("https://api.spot-me.xyz/user?id=$userId")
                val conn = url.openConnection() as HttpsURLConnection

                conn.requestMethod = "POST"
                conn.doOutput = true
                conn.setRequestProperty("Content-Type", "application/json; utf-8")
                conn.setRequestProperty("Accept", "application/json")

                val userJson = Globals.currentUser!!.toJson()
                val jsonObject = JSONObject()
                jsonObject.put("user", userJson)
                val os = conn.outputStream
                val osw = OutputStreamWriter(os, "UTF-8")
                osw.write(jsonObject.toString())
                osw.flush()
                osw.close()
                os.close()

                conn.connect()

                val responseCode: Int = conn.responseCode
                Log.d("PreferenceUpdate", "responseCode - $responseCode")

                val inStream = if (responseCode >= 400) {
                    conn.errorStream
                } else {
                    conn.inputStream
                }
                val isReader = InputStreamReader(inStream)
                val bReader = BufferedReader(isReader)

                result = bReader.readText()
            } catch (ex: Exception) {
                Log.d("PreferenceUpdate", "Error in doInBackground " + ex.message)
            }
            return result
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)

            if (result == "") {
                Log.d("Result", "EMPTY")
            } else {
                Log.d("Result", result)
            }
        }
    }
}
