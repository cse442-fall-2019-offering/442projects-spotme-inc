package edu.buffalo.cse.cse442f19.spotme

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MatchProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_profile)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_match_profile, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.rate -> {
                startActivity(Intent(this, RatingActivity::class.java))
                true
            }
            R.id.report -> {
                startActivity(Intent(this, ReportActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
