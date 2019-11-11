package edu.buffalo.cse.cse442f19.spotme

import android.os.AsyncTask
import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class UpdateUserAsyncTask : AsyncTask<String, String, String>() {
    override fun doInBackground(vararg p0: String?): String {
        var result = ""

        val userId = Globals.currentUser?.id
        try {
            val url = URL("${Globals.ENDPOINT_BASE}/user?id=$userId")
            val conn = url.openConnection() as HttpURLConnection

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