package edu.buffalo.cse.cse442f19.spotme

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import kotlinx.android.synthetic.main.activity_rating.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import android.widget.RatingBar
import android.widget.Button
import android.R.*
import android.content.Intent
import android.graphics.Rect
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_match_profile.*
import org.json.JSONObject
import java.net.HttpURLConnection


class RatingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_rating)

//        textView14.setOnEditorActionListener { _, actionId, _ ->
//            if(actionId == EditorInfo.IME_ACTION_DONE){
//
//                submitButtonClicked()
//                true
//            } else {
//                false
//            }
//        }
        button2.setOnClickListener {
            submitButtonClicked()
        }

        LoadRateAsyncTask(this).execute()
    }


    private fun submitButtonClicked() {

//      Save the stars portion of the rating.
        val stars = ratingBar.rating
        Log.d("SENDING STAR RATING", "$stars stars")
        SaveRateAsyncTask(stars, this).execute()

        //Exit the rating screen.
        this.onBackPressed()
    }

    fun setCurrentRating(rating: Any) {

        if (rating is Double) {

            ratingBar.rating = rating.toFloat()
        } else {

            ratingBar.rating = 0f
        }
    }

    class SaveRateAsyncTask(private var rate: Float, private var activity: RatingActivity) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {

            var result = ""

            val userId: Int = Globals.currentUser!!.id

            val intent = activity.intent

            val ratedID = intent.getIntExtra("match_id", 1) //partner

            try {
                val url = URL("${Globals.ENDPOINT_BASE}/ratings?user1=$userId&rated_user=$ratedID&rating=$rate")
                val conn = url.openConnection() as HttpURLConnection

                conn.requestMethod = "PUT"
                conn.doOutput = true
                conn.connect()


                val responseCode: Int = conn.responseCode
                Log.d("SaveUpdate", "responseCode - $responseCode")

                val inStream = if (responseCode >= 400) {
                    conn.errorStream
                } else {
                    conn.inputStream
                }
                val isReader = InputStreamReader(inStream)
                val bReader = BufferedReader(isReader)

                result = bReader.readText()

            } catch (ex: Exception) {
                Log.d("SaveChat", "Error in doInBackground " + ex.message)
            }

            return result
        }
    }

    class LoadRateAsyncTask(private var activity: RatingActivity) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {

            var result = ""

            val intent = activity.intent

            val currentUserId: Int = Globals.currentUser!!.id
            val matchID: Int = intent.getIntExtra("match_id", 1) //user1

            try {

                val url = URL("${Globals.ENDPOINT_BASE}/singlerating?rater=$currentUserId&ratee=$matchID")
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


                val rating = jsonObject.get("rating")

                activity.setCurrentRating(rating)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        val newIntent = Intent(this, MatchProfileActivity :: class.java)
        newIntent.putExtra("match_id", intent.getIntExtra("match_id", 1))
        startActivity(newIntent)
    }
}
