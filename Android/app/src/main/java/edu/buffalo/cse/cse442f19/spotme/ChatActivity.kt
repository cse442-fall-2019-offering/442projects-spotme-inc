package edu.buffalo.cse.cse442f19.spotme

import android.graphics.BitmapFactory
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
import android.util.Log
import android.view.*
import edu.buffalo.cse.cse442f19.spotme.utils.ChatHistory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URLEncoder

class ChatActivity : AppCompatActivity() {

    var otherUserId: Int = 1;
    lateinit var otherUser: User.ScoredUser;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_screen)
            //activity_chat_screen)
        setSupportActionBar(toolbar)

//      Start off by loading all the history currently stored in Globals.
        this.otherUserId = intent.getIntExtra("match_id", 1)
        for (u in Globals.currentAcceptedUsers) {
            if (u.id == intent.getIntExtra("match_id", 1)) {
                this.otherUser = u
                break
            }
        }

        if (!Globals.chatHistories.containsKey(otherUserId)) {

            //Put an empty chat history in for the user.
            Globals.chatHistories.put(otherUserId, ChatHistory())
        }

        var history: ChatHistory = Globals.chatHistories.get(otherUserId)!!;
        for (message in history.messages) {

            if (message.self) {

                currentUserChatBubble(message.message, message.time)
            } else {
                receiverChatBubble(message.message, message.time)
            }
        }

        val listenerObj = object : ChatHistory.ChatChangeListener {
            override fun onChange(message: ChatHistory.ChatMessage) {

                if (message.self) {

                    currentUserChatBubble(message.message, message.time)
                } else {

                    receiverChatBubble(message.message, message.time)
                }
            }
        }

        history.listener = listenerObj

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
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_chat_screen, menu)
        return true
    }

    private fun sendButtonClicked() {

        val userMessage = enterMessage.text.toString()

        if (!userMessage.isBlank()) {


//        Add to Globals
            val chatHistory: ChatHistory = Globals.chatHistories.get(otherUserId)!!

            val currentTime = LocalDateTime.now()
            val timeStampText = currentTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))

            chatHistory.addMessage(ChatHistory.ChatMessage(true, userMessage, timeStampText))

            //Clear enter message text
            enterMessage.text.clear();

           // addSenderChatBubble(userMessage)

            SaveChatAsyncTask(userMessage, this).execute()
        }

    }

    private fun addSenderChatBubble(text: String) {

        val chatLayout = LinearLayout(this)

        val chatBubble = TextView(this)
        val timeStamp = TextView(this)

        chatBubble.text = text
        chatBubble.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)

        val currentTime = LocalDateTime.now()

        val timeStampText = currentTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
        timeStamp.text = timeStampText
        timeStamp.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
        timeStamp.textAlignment = TEXT_ALIGNMENT_TEXT_END



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

    fun currentUserChatBubble(text: String, time: String) {

        val chatLayout = LinearLayout(this)

        val chatBubble = TextView(this)
        val timeStamp = TextView(this)

        chatBubble.text = text
        chatBubble.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)


        timeStamp.text = time.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
        timeStamp.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
        timeStamp.textAlignment = TEXT_ALIGNMENT_TEXT_END

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

        if (this.otherUser != null) {
            val bitmap = BitmapFactory.decodeByteArray(this.otherUser.picture, 0, this.otherUser.picture.size)
            profile.setImageBitmap(bitmap)
        } else {
            profile.setBackgroundResource(R.drawable.match_avatar)
        }

        timeStamp.text = time.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
        timeStamp.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)

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

    class LoadHistoryAsyncTask(private var activity: ChatActivity) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {

            var result = ""

            val userId: Int = Globals.currentUser!!.id

            val intent = activity.intent

            val matchID: Int = intent.getIntExtra("match_id", 1) //user2
            try {

                val url = URL("${Globals.ENDPOINT_BASE}/stored-chats?id=$userId&other_id=$matchID")
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
                val url = URL("${Globals.ENDPOINT_BASE}/stored-chats?user1=$userId&user2=$matchID&message=$mess")
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
}
