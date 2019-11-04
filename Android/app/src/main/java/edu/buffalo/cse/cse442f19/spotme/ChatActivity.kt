package edu.buffalo.cse.cse442f19.spotme
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_chat_screen.*
import kotlinx.android.synthetic.main.content_chat_screen.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import android.view.View.TEXT_ALIGNMENT_TEXT_END
import android.widget.ImageButton
import android.widget.LinearLayout
import android.view.inputmethod.EditorInfo
import android.os.AsyncTask
import org.json.JSONObject
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import android.util.Log
import android.view.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URLEncoder

class ChatActivity : AppCompatActivity() {

    /**
     * For ANANYA to read:
     * Use addSenderChatBubble to add a user's own chat bubble as a message
     * Use addChatBubble to add a response message bubble.
     *
     * Preferably in the database, we should only store the latest 15 messages or something so as to not flood it
     * with old messages.
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_screen)
        setSupportActionBar(toolbar)

        val task = LoadHistoryAsyncTask(this)
        //task.userId = position + 1
        //Log.d("Fetching on", "" + task.userId)
        task.execute()

        enterMessage.setOnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_DONE){

                sendButtonClicked()
                true
            } else {
                false
            }
        }

        sendButton.setOnClickListener {
            sendButtonClicked()
        }

            /*var handler = Handler()
        handler.postDelayed(LoadHistoryAsyncTask(this), 2000)*/

//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }

        /*var handler = Handler();

        // Define the code block to be executed
        val runnableCode = object : Runnable {
            override fun run() {
                /*// Do something here on the main thread
                //Log.d("Handlers", "Called on main thread")
                // Repeat this the same runnable code block again another 2 seconds
                // 'this' is referencing the Runnable object

                //val task = LoadHistoryAsyncTask(this)
                //task.userId = position + 1
                //Log.d("Fetching on", "" + task.userId)
                task.execute()

                handler.postDelayed(this, 5000)*/
                var result = ""

                val userId: Int = Globals.currentUser!!.id

                val intent = activity.intent

                val matchID: Int = intent.getIntExtra("match_id", 1) //user2
                try {

                    val url = URL("https://api.spot-me.xyz/stored-chats?id=$userId&other_id=$matchID")
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
            }
        };
        handler.post(runnableCode);*/
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_chat_screen, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.refresh -> {
                val task = LoadHistoryAsyncTask(this)
                task.execute()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

//    fun load_history(){
//
//        Log.d("Current user", "" + Globals.currentUser.toString())
//
//        val userId: Int = Globals.currentUser!!.id
//
//        //val intent = getIntent()
//
//        //val matchID: Int = intent.getStringExtra("match_id") //user2
//
//        val url: URL = URL("https://api.spot-me.xyz/stored-chats?id=$userId&other_id=$temp_user")
//        val httpsConnection: HttpsURLConnection = url.openConnection() as HttpsURLConnection
//
//        httpsConnection.requestMethod = "GET"
//        httpsConnection.connect()
//
//        val responseCode: Int = httpsConnection.responseCode
//        Log.d("GetUser", "responseCode - $responseCode")
//
//        val inStream = if (responseCode >= 400) {
//            httpsConnection.errorStream
//        } else {
//            httpsConnection.inputStream
//        }
//        val isReader = InputStreamReader(inStream)
//        val bReader = BufferedReader(isReader)
//
//        val result = bReader.readText()
//
//        val jsonObject = JSONObject(result)
//
//        val chatArray = jsonObject.getJSONArray("messages")
//
//        for (i in 0 until chatArray.length()){
//
//            val chatObj = chatArray.getJSONObject(i)
//
//            if(chatObj.getInt("sender") == userId){
//
//                val mess = chatObj.getString("message")
//
//                val time = chatObj.getString("time")
//
//                currentUserChatBubble(mess, time)
//            }
//
//            else{
//
//                val mess = chatObj.getString("message")
//
//                val time = chatObj.getString("time")
//
//                receiverChatBubble(mess, time)
//            }
//
//        }
//
//    }

    private fun sendButtonClicked() {

        val userMessage = enterMessage.text.toString()

        if (!userMessage.isBlank()) {

            addSenderChatBubble(userMessage)
            //addChatBubble("Wow, that\'s so cool!")
            val task = SaveChatAsyncTask(userMessage, this)
            //task.userId = position + 1
            //Log.d("Fetching on", "" + task.userId)
            task.execute()
            //save_chat(userMessage)
        }

    }

//    fun save_chat(text: String) {
//
//        val userId: Int = Globals.currentUser!!.id
//
//        //val intent = getIntent()
//
//        //var matchID: Int = intent.getStringExtra("match_id") //user2
//
//        val mess = text
//
//        try
//        {
//
//            val url = URL("https://api.spot-me.xyz/stored-chats?user1=$userId&user2=$temp_user&message=$mess")
//            val httpURLConnection = url.openConnection() as HttpsURLConnection
//
//            httpURLConnection.requestMethod = "PUT"
//            httpURLConnection.connect()
//
//        } catch (ex: Exception)
//        {
//            Log.d("", "Error in saving chat " + ex.message)
//        }
//
//    }


    fun currentUserChatBubble(text: String, time: String) {

        val chatLayout = LinearLayout(this)

        val chatBubble = TextView(this)
        val timeStamp = TextView(this)

        chatBubble.text = text
        chatBubble.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)


        timeStamp.text = time.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
        timeStamp.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
        timeStamp.textAlignment = TEXT_ALIGNMENT_TEXT_END

        //Clear enter message text
        enterMessage.setText("")

        //Add chat to the main layout
        chatLayout.addView(chatBubble)

        scrollViewMainLayout.addView(chatLayout)
        scrollViewMainLayout.addView(timeStamp)

        //Scroll to the end of the chat
        scrollView4.post {
            scrollView4.fullScroll(View.FOCUS_DOWN)
        }

        chatBubble.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        )
        chatBubble.setPadding(10, 10, 50, 0)
        timeStamp.setPadding(0, 0, 50, 0)

        chatLayout.gravity = Gravity.END
    }

    private fun addSenderChatBubble(text: String) {

        val chatLayout = LinearLayout(this)

        val chatBubble = TextView(this)
        val timeStamp = TextView(this)

        chatBubble.text = text
        chatBubble.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)

        val currentTime = LocalDateTime.now()

        timeStamp.text = currentTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
        timeStamp.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
        timeStamp.textAlignment = TEXT_ALIGNMENT_TEXT_END

        //Clear enter message text
        enterMessage.setText("")

        //Add chat to the main layout
        chatLayout.addView(chatBubble)

        scrollViewMainLayout.addView(chatLayout)
        scrollViewMainLayout.addView(timeStamp)

        //Scroll to the end of the chat
        scrollView4.post {
            scrollView4.fullScroll(View.FOCUS_DOWN)
        }

        chatBubble.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        chatBubble.setPadding(10, 10, 50, 0)
        timeStamp.setPadding(0, 0, 50, 0)

        chatLayout.gravity = Gravity.END
    }

    fun receiverChatBubble(text: String, time: String) {
        val chatLayout = LinearLayout(this)

        val chatBubble = TextView(this)
        val timeStamp = TextView(this)

        val profile = ImageButton(this)

        chatBubble.text = text
        chatBubble.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)

        profile.setBackgroundResource(R.drawable.match_avatar)

        timeStamp.text = time.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
        timeStamp.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)

        //Clear enter message text
        enterMessage.setText("")

        //Add chat to the main layout
        chatLayout.addView(profile)
        chatLayout.addView(chatBubble)

        scrollViewMainLayout.addView(chatLayout)
        scrollViewMainLayout.addView(timeStamp)

        //Scroll to the end of the chat
        scrollView4.post {
            scrollView4.fullScroll(View.FOCUS_DOWN)
        }

        //chatBubble.getLayoutParams().width = 1000
        profile.layoutParams.width = 200
        profile.layoutParams.height = 200

        chatBubble.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        chatBubble.setPadding(10, 10, 10, 0)
    }

//    fun addChatBubble(text: String) {
//        val chatLayout = LinearLayout(this)
//
//        val chatBubble = TextView(this)
//        val timeStamp = TextView(this)
//
//        val profile = ImageButton(this)
//
//        chatBubble.setText(text)
//        chatBubble.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
//
//        profile.setBackgroundResource(R.drawable.match_avatar)
//
//        val currentTime = LocalDateTime.now()
//
//        timeStamp.setText(currentTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)))
//        timeStamp.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
//
//        //Clear enter message text
//        enterMessage.setText("")
//
//        //Add chat to the main layout
//        chatLayout.addView(profile)
//        chatLayout.addView(chatBubble)
//
//        scrollViewMainLayout.addView(chatLayout)
//        scrollViewMainLayout.addView(timeStamp)
//
//        //Scroll to the end of the chat
//        scrollView4.post {
//            scrollView4.fullScroll(View.FOCUS_DOWN)
//        }
//
//        //chatBubble.getLayoutParams().width = 1000
//        profile.getLayoutParams().width = 200
//        profile.getLayoutParams().height = 200
//
//        chatBubble.setLayoutParams(
//            android.widget.LinearLayout.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//            )
//        )
//        chatBubble.setPadding(10, 10, 10, 0)
//    }

    class LoadHistoryAsyncTask(private var activity: ChatActivity) : AsyncTask<String, String, String>() {

        /*//var temp_user: Int = 2

        val handler = Handler()

        // Define the code block to be executed
        val runnableCode = object : Runnable {
            override fun run() {
                /*// Do something here on the main thread
                //Log.d("Handlers", "Called on main thread")
                // Repeat this the same runnable code block again another 2 seconds
                // 'this' is referencing the Runnable object

                //val task = LoadHistoryAsyncTask(this)
                //task.userId = position + 1
                //Log.d("Fetching on", "" + task.userId)
                task.execute()

                handler.postDelayed(this, 5000)*/

                doInBackground()

                handler.postDelayed(this, 5000)
            }
        }*/

        override fun doInBackground(vararg p0: String?): String {

            var result = ""

            val userId: Int = Globals.currentUser!!.id

            val intent = activity.intent

            val matchID: Int = intent.getIntExtra("match_id", 1) //user2
            try {

                val url = URL("https://api.spot-me.xyz/stored-chats?id=$userId&other_id=$matchID")
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
            //handler.post(runnableCode)

            if (result == "") {
                Log.d("Result", "EMPTY")
            } else {
                Log.d("Result", result)

//                Globals.currentUser = User.fromJson(JSONObject(result))
                //Use result to get values for chat
                val jsonObject = JSONObject(result)

                activity.scrollViewMainLayout.removeAllViews()

                val chatArray = jsonObject.getJSONArray("messages")

                for (i in 0 until chatArray.length()){

                    val chatObj = chatArray.getJSONObject(i)


                    if(chatObj.getInt("sender") == Globals.currentUser!!.id){

                        val mess = chatObj.getString("message")

                        val time = chatObj.getString("time")

                        activity.currentUserChatBubble(mess, time)
                    }

                    else{

                        val mess = chatObj.getString("message")

                        val time = chatObj.getString("time")

                        activity.receiverChatBubble(mess, time)
                    }

                 }
            }
        }
    }

    class SaveChatAsyncTask(private var text: String, private var activity: ChatActivity) : AsyncTask<String, String, String>() {

        //val temp_user: Int = 2

        override fun doInBackground(vararg p0: String?): String {

            var result = ""

            val userId: Int = Globals.currentUser!!.id

            val intent = activity.intent

            val matchID = intent.getIntExtra("match_id", 1) //user2


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
