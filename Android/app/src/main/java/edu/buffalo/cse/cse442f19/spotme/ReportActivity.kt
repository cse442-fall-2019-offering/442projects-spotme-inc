package edu.buffalo.cse.cse442f19.spotme

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_report.*
import kotlinx.android.synthetic.main.content_chat_screen.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.net.URLEncoder
import javax.net.ssl.HttpsURLConnection

class ReportActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)


        reportData.setOnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_DONE){

                submitButtonClicked()
                true
            } else {
                false
            }
        }

        Submit_button.setOnClickListener {
            // Displays Submission Successful on report screen when
            // submit button is pressed
            Toast.makeText(this, "Submission Successful", Toast.LENGTH_LONG).show()

            // Calls the submitButtonClicked function
            submitButtonClicked()
        }
    }

    // Function to send report text to database when submit button is clicked
    private fun submitButtonClicked() {

        val userReport = reportData.text.toString()

        // If userReport is not blank it saves the report to the database
        // using SaveReportInformationAsyncTask
        if (!userReport.isBlank()) {
            val task = SaveReportInformationAsyncTask(userReport, this)
            task.execute()
        }

    }


    // Put request to send report information to database
    class SaveReportInformationAsyncTask(private var text: String, private var activity: ReportActivity) : AsyncTask<String, String, String>() {
        override fun doInBackground(vararg p0: String?): String {

            var result = ""

            val userId: Int = Globals.currentUser!!.id

            val mess = URLEncoder.encode(text, "UTF-8")

            try {
                val url = URL("https://api.spot-me.xyz/stored-reports?user1=$userId&message=$mess")
                val conn = url.openConnection() as HttpsURLConnection

                conn.requestMethod = "PUT"
                conn.doOutput = true
                conn.connect()



                val responseCode: Int = conn.responseCode
                Log.d("SaveReportInformation", "responseCode - $responseCode")

                val inStream = if (responseCode >= 400) {
                    conn.errorStream
                } else {
                    conn.inputStream
                }
                val isReader = InputStreamReader(inStream)
                val bReader = BufferedReader(isReader)

                result = bReader.readText()

            } catch (ex: Exception) {
                Log.d("SaveReportInformation", "Error in doInBackground " + ex.message)
            }

            return result
        }


    }
}
