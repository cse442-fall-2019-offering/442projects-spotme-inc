package edu.buffalo.cse.cse442f19.spotme

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import kotlinx.android.synthetic.main.activity_rating.*
import kotlinx.android.synthetic.main.content_chat_screen.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.net.URLEncoder
import javax.net.ssl.HttpsURLConnection
import android.widget.RatingBar
import android.widget.RatingBar.OnRatingBarChangeListener
import android.R
import android.widget.TextView



class RatingActivity : AppCompatActivity() {

    private val fltRatingValue = null

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

        spinner7.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            fun onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, id: long) {
                // your code here
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        button2.setOnClickListener {
            submitButtonClicked()
        }
    }

    class SpinnerActivity : Activity, AdapterView.OnItemSelectedListener {

        override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
            // An item was selected. You can retrieve the selected item using
            // parent.getItemAtPosition(pos)
        }

        override fun onNothingSelected(parent: AdapterView<*>) {
            // Another interface callback
        }
    }

    private fun addListenerOnRatingBar() {

        ratingBar = findViewById(R.id.ratingBar) as RatingBar
        txtRatingValue = findViewById(R.id.txtRatingValue) as TextView

        //if rating value is changed,
        //display the current rating value in the result (textview) automatically
        ratingBar.setOnRatingBarChangeListener(OnRatingBarChangeListener { ratingBar, rating, _ ->
        fltRatingValue.setText(
            rating.toString()
        )
    })
}

    private fun submitButtonClicked() {

        val star =

            enterMessage.text.toString()

        if (!userMessage.isBlank()) {

            addSenderChatBubble(userMessage)
            //addChatBubble("Wow, that\'s so cool!")
            val task = ChatActivity.SaveChatAsyncTask(userMessage, this)
            //task.userId = position + 1
            //Log.d("Fetching on", "" + task.userId)
            task.execute()
            //save_chat(userMessage)
        }

    }

    class SaveChatAsyncTask(private var stars: Float, private var activity: ChatActivity) : AsyncTask<String, String, String>() {

        //val temp_user: Int = 2

        override fun doInBackground(vararg p0: String?): String {

            var result = ""

            val userId: Int = Globals.currentUser!!.id

            val intent = activity.intent

            val matchID = intent.getIntExtra("match_id", 1) //partner

            val stars = URLEncoder.encode(stars, "UTF-8")


            val mess = URLEncoder.encode(text, "UTF-8")

            try {
                val url = URL("https://api.spot-me.xyz/stored-chats?user1=$userId&user2=$matchID&message=$mess")
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
