package edu.buffalo.cse.cse442f19.spotme.utils

import android.os.AsyncTask
import android.util.Log
import edu.buffalo.cse.cse442f19.spotme.Globals
import edu.buffalo.cse.cse442f19.spotme.Notifications
import edu.buffalo.cse.cse442f19.spotme.User
import edu.buffalo.cse.cse442f19.spotme.utils.ChatHistory.ChatMessage
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ChatPollTask(var displayNotifications: Boolean) : AsyncTask<HashMap<User.ScoredUser, JSONObject>, HashMap<User.ScoredUser, JSONObject>, HashMap<User.ScoredUser, JSONObject>>() {

    override fun doInBackground(vararg p0: HashMap<User.ScoredUser, JSONObject>): HashMap<User.ScoredUser, JSONObject>? {

        val userId: Int = Globals.currentUser!!.id

        var acceptedMatches = Globals.currentAcceptedUsers;

        var ret: HashMap<User.ScoredUser, JSONObject> = hashMapOf()

        for (acceptedMatch: User.ScoredUser in acceptedMatches) {

            //Pull the chats from that match.
            try {

                val matchID = acceptedMatch.id;

                val url =
                    URL("${Globals.ENDPOINT_BASE}/stored-chats?id=$userId&other_id=$matchID")

                val conn = url.openConnection() as HttpURLConnection

                conn.requestMethod = "GET"
                conn.connect()

                val responseCode: Int = conn.responseCode
                Log.d("GetUser", "responseCode - $responseCode")

                if (responseCode >= 400) {

                    continue
                } else {

                    val inStream = conn.inputStream;

                    val isReader = InputStreamReader(inStream)
                    val bReader = BufferedReader(isReader)

                    var singleResult = bReader.readText()
                    if (singleResult == "") {

                        continue
                    }

                    var response = JSONObject(singleResult)

                    ret[acceptedMatch] = response
                }
            } catch (ex: Exception) {

                Log.d("GetUser", "Error in doInBackground " + ex.message)
            }
        }

        return ret;
    }

    //Muse use onPostExecute to modify views.
    override fun onPostExecute(result: HashMap<User.ScoredUser, JSONObject>) {

        super.onPostExecute(result)


        for (otherUser: User.ScoredUser in result.keys) {

            val jsonObject: JSONObject = result.get(otherUser)!!


            val chatArray = jsonObject.getJSONArray("messages")

            val alreadyHasHistory = Globals.chatHistories.containsKey(otherUser.id)

            var chatHistory: ChatHistory;

            if (alreadyHasHistory) {
                chatHistory = Globals.chatHistories.get(otherUser.id)!!
            } else {
                chatHistory = ChatHistory()
            }

            var oldHistorySize = chatHistory.getSize()
            //chatHistory.messages = arrayListOf() //Clear the chat

            //Start at oldHistorySize instead of 0 to only add the new chats.
            for (i in oldHistorySize until chatArray.length()) {

                val chatObj = chatArray.getJSONObject(i)

                var sender: Boolean = false;
                if (chatObj.getInt("sender") == Globals.currentUser!!.id) {
                    sender = true;
                }
                val message = chatObj.getString("message")
                val time = chatObj.getString("time")

                var cm = ChatMessage(sender, message, time)
                chatHistory.addMessage(cm);
            }

            if (displayNotifications) {
                //Check if the chat is different for notification.
                var newAmt: Int = chatHistory.getSize() - oldHistorySize;

                if (newAmt > 0) {

                    Notifications.displayNotification(
                        "$newAmt new messages from " + otherUser.name + "!",
                        Globals.matchListActivity!!
                    )
                }
            }
            Globals.chatHistories.put(otherUser.id, chatHistory)
        }
    }
}