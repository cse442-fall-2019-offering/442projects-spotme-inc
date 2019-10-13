package edu.buffalo.cse.cse442f19.spotme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        button.setOnClickListener {
            val intent = Intent(this, MainActivity :: class.java)
            startActivity(intent)
        }

        button3.setOnClickListener {
            val intent = Intent(this, MatchListActivity :: class.java)
            startActivity(intent)
        }

        button4.setOnClickListener {
            val intent = Intent(this, MatchListActivity :: class.java)
            startActivity(intent)
        }


    }
}
