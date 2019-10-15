package edu.buffalo.cse.cse442f19.spotme

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*
import java.net.HttpURLConnection
import java.net.URL
import java.io.BufferedOutputStream
import android.os.StrictMode
import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URI
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSocketFactory


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        button.setOnClickListener {
            val intent = Intent(this, MainActivity :: class.java)
            startActivity(intent)
        }

        staticlogin1.setOnClickListener {
            AsyncTaskExample(this).execute()
//            val url = URL("https://api.spot-me.xyz/user")
//            val urlConnection = url.openConnection() as HttpURLConnection
//
//            try {
//                GlobalScope.launch {
//
//                }
//                urlConnection.requestMethod = "GET";
//                urlConnection.addRequestProperty("id", "1")
//                urlConnection.doOutput = true;
//
//
//                val outputPost = BufferedOutputStream(urlConnection.outputStream)
//
//                print(outputPost)
//                print("MADE IT")
//                outputPost.flush()
//                outputPost.close()
//
//                //urlConnection.addRequestProperty("user2", "POTENTIAL MATCH 2 USER ID")
////                val inputStream = BufferedInputStream(urlConnection.getInputStream())
////                readStream(inputStream);
//            } finally {
//
//                urlConnection.disconnect();
//            }
        }
    }
    class AsyncTaskExample(private var activity: LoginActivity?) : AsyncTask<String, String, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            //activity?.MyprogressBar?.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg p0: String?): String {

            var result = ""
            try {
                //var uri = URI.Builder()
                val url = URL("https://api.spot-me.xyz/user?id=1")
                val httpURLConnection = url.openConnection() as HttpsURLConnection//TODO CHANGE HTTP vs HTTPS deepnding

                httpURLConnection.requestMethod = "GET";
               // httpURLConnection.addRequestProperty("id", "1")
               // THIS IS FOR POST ONLY httpURLConnection.doOutput = true
                httpURLConnection.connect()
                Log.d("", httpURLConnection.url.toString())
               // Log.d("", httpURLConnection.url.)
                val responseCode: Int = httpURLConnection.responseCode
                Log.d(activity?.localClassName, "responseCode - $responseCode")

                val inStream: InputStream;
                if (responseCode < 400) {// == 200) {
                    //val inStream: InputStream = httpURLConnection.inputStream
                    inStream = httpURLConnection.inputStream;
                } else {
                    inStream = httpURLConnection.errorStream
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
//                }
            } catch (ex: Exception) {
                Log.d("", "Error in doInBackground " + ex.message)
            }
            return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
//            activity?.MyprogressBar?.visibility = View.INVISIBLE
            if (result == "") {
                print("RESULT EMPTY");
//                activity?.my_text?.text = activity?.getString(R.string.network_error)
            } else {
                print(result);
                Log.d("Res", result)

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

                Globals.currentUser = user;
            }
        }
    }
}
