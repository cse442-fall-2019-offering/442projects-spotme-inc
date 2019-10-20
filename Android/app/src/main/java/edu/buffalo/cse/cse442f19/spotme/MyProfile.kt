package edu.buffalo.cse.cse442f19.spotme

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import edu.buffalo.cse.cse442f19.spotme.utils.Date
import kotlinx.android.synthetic.main.activity_my_profile.*

class MyProfile : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)


        updateFields();
    }

    fun updateFields() {

        Log.d("CURRENT USER", Globals.currentUser.toString())
        var fields = Globals.currentUser!!.getProfileFields()

        matchName.text = fields.get("Name")!!
//        addText(fields.get("Name")!!, 35f, Color.BLACK)
//        addText("Gender", 25f, Color.RED)
        val gender = fields.get("Gender")
        var genderText = "Other"
        if (gender.equals("0")) {
            genderText = "Male"
        }
        if (gender.equals("1")) {
            genderText = "Female"
        }
//        addText(genderText, 20f, Color.BLACK);
//
//        addText("Age", 25f, Color.RED)
        val birthdayStr = fields.get("Birthday")!!
        val birthday = Date(birthdayStr)
        val age = Date.getAge(birthday)

        match_age.text = age.toString();
//        addText(age.toString(), 20f, Color.BLACK)
//        addText("Fitness Level", 25f, Color.RED)
        val fitnessLevelStr = fields.get("Level")!!
        val fitnessLevelInt = fitnessLevelStr.toInt()
        var fitnessStr = "NULL";
        if (fitnessLevelInt == 0) {
            fitnessStr = "Beginner"
        } else if (fitnessLevelInt == 1) {

            fitnessStr = "Intermediate"
        } else if (fitnessLevelInt == 2) {

            fitnessStr = "Advanced"
        }
        match_fitness_level.text = fitnessStr
//        addText(fitnessStr, 20f, Color.BLACK)
//        addText("Weight", 25f, Color.RED)
        match_weight.text = fields.get("Weight")!! + "lbs"
//        addText(fields.get("Weight")!! + "lbs", 20f, Color.BLACK);
    }
//    fun addText(str: String, size: Float, col: Int) {
//
//        var texView: TextView = TextView(this)
//        texView.text = str
//        texView.setTextColor(col)
//        texView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
//        linlayout.addView(texView)
//    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_match_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.PreferenceSettingsB -> {
                var intent = Intent(this, PreferenceActivity::class.java)

                startActivity(intent)

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {

        var intent = Intent(this, MatchListActivity::class.java)
        startActivity(intent)
    }
}
