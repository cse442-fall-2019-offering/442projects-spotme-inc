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





class RatingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)

        textView14.setOnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_DONE){

                submitButtonClicked()
                true
            } else {
                false
            }
        }
        button2.setOnClickListener {
            submitButtonClicked()
        }
    }


    private fun submitButtonClicked() {

        val rated = findViewById<RatingBar>(R.id.ratingBar)
        if (rated != null) {
            val button = findViewById<Button>(R.id.button2)
            button.setOnClickListener {
                val star = rated.rating
                val task = SaveRateAsyncTask(star, this)
                task.execute()
            }

        }
    }

    class SaveRateAsyncTask(private var rate: Float, private var activity: RatingActivity) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {

            var result = ""

            val userId: Int = Globals.currentUser!!.id

            val intent = activity.intent

            val ratedID = intent.getIntExtra("rated_user", 1) //partner

            try {
                val url = URL("https://api.spot-me.xyz/ratings?user1=$userId&rated_user=$ratedID&rating=$rate")
                val conn = url.openConnection() as HttpsURLConnection

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
}
