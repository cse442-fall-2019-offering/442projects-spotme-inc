package edu.buffalo.cse.cse442f19.spotme

import android.app.Activity
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import edu.buffalo.cse.cse442f19.spotme.utils.ChatHistory

class Globals {
    companion object {
        @JvmStatic
        var currentUser: User? = null

        var matchListActivity: AppCompatActivity? = null;
        //Update handler for all update polls
        var updateHandler: Handler? = null;
        //Keep this constantly updated and load from here.  Also use for notifications with constant updating
        var chatHistories: HashMap<Int, ChatHistory> = hashMapOf()

        var acceptedUsersOneWay = arrayListOf<User.ScoredUser>()
        var currentAcceptedUsers = arrayListOf<User.ScoredUser>()
        var oustring: String = ""

        var selectedMatch: Int? = null
        const val ENDPOINT_BASE = "http://10.84.25.80:5000"//"https://api.spot-me.xyz"
    }
}

