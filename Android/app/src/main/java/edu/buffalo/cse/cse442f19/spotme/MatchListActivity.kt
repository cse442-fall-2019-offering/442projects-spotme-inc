package edu.buffalo.cse.cse442f19.spotme

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

import kotlinx.android.synthetic.main.activity_match_list.*

class MatchListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_list)
       // setSupportActionBar(toolbar)

//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }

        potentialMatch1.setOnClickListener {
            val intent = Intent(this, MatchProfileActivity :: class.java)
            startActivity(intent)
        }
        potentialMatch2.setOnClickListener {
            val intent = Intent(this, MatchProfileActivity :: class.java)
            startActivity(intent)
        }
        potentialMatch3.setOnClickListener {
            val intent = Intent(this, MatchProfileActivity :: class.java)
            startActivity(intent)
        }
        potentialMatch4.setOnClickListener {
            val intent = Intent(this, MatchProfileActivity :: class.java)
            startActivity(intent)
        }
        potentialMatch5.setOnClickListener {
            val intent = Intent(this, MatchProfileActivity :: class.java)
            startActivity(intent)
        }
        potentialMatch6.setOnClickListener {
            val intent = Intent(this, MatchProfileActivity :: class.java)
            startActivity(intent)
        }
        chosenMatch1Button.setOnClickListener {
            val intent = Intent(this, ChatActivity :: class.java)
            startActivity(intent)
        }
        chosenMatch2Button.setOnClickListener {
            val intent = Intent(this, ChatActivity :: class.java)
            startActivity(intent)
        }
        chosenMatch3Button.setOnClickListener {
            val intent = Intent(this, ChatActivity :: class.java)
            startActivity(intent)
        }
        chosenMatch4Button.setOnClickListener {
            val intent = Intent(this, ChatActivity :: class.java)
            startActivity(intent)
        }
        chosenMatch5Button.setOnClickListener {
            val intent = Intent(this, ChatActivity :: class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_my_profile, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.MyProfileB -> {
                startActivity(Intent(this, MyProfile::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
