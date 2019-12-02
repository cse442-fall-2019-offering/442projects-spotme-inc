package edu.buffalo.cse.cse442f19.spotme

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import edu.buffalo.cse.cse442f19.spotme.utils.Date
import kotlinx.android.synthetic.main.activity_my_profile.*

class MyProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        updateFields()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        updateFields()

        val newIntent = Intent(this, MatchListActivity :: class.java)
        startActivity(newIntent)
        /*val intent = Intent(this, MyProfileActivity :: class.java)
        startActivity(intent)*/
    }

    fun updateFields() {
        val user = Globals.currentUser!!
        Log.d("CURRENT USER", user.toString())

        match_name.text = user.name

        val genders = resources.getStringArray(R.array.select_gender)
        match_gender.text = genders[user.gender]

        val birthday = Date(user.dob)
        val age = Date.getAge(birthday)
        match_age.text = age.toString()

        val levels = resources.getStringArray(R.array.select_level)
        match_fitness_level.text = levels[user.level]

        match_weight.text = resources.getString(R.string.kg, user.weight.toInt())

        val bitmap = BitmapFactory.decodeByteArray(user.picture, 0, user.picture.size)
        imageView.setImageBitmap(bitmap)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_match_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.PreferenceSettingsB -> {
                var intent = Intent(this, PreferenceActivity::class.java)

                startActivity(intent)

                finish()

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
