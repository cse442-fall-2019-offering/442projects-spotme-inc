package edu.buffalo.cse.cse442f19.spotme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import kotlinx.android.synthetic.main.activity_preference.*


class PreferenceActivity() : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preference)

        if (Globals.currentUser == null) {
            val intent = Intent(this, LoginActivity :: class.java)
            startActivity(intent)
        }
        val user = Globals.currentUser!!

        prefName.setText(user.name)
        prefPartnerGender.setSelection(user.partner_gender, false)
        prefDistance.setSelection(user.radius, false)
        prefPartnerLevel.setSelection(user.partner_level, false)
        prefLevel.setSelection(user.level, false)

        prefName.setOnFocusChangeListener { _, gainFocus ->
            if (!gainFocus) {
                updatePreferences()
            }
        }

        val spinnerUpdateListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updatePreferences()
            }
        }

        prefPartnerGender.onItemSelectedListener = spinnerUpdateListener
        prefDistance.onItemSelectedListener = spinnerUpdateListener
        prefPartnerLevel.onItemSelectedListener = spinnerUpdateListener
        prefLevel.onItemSelectedListener = spinnerUpdateListener
    }

    fun updatePreferences() {
        val user = Globals.currentUser!!
        user.name = prefName.text.toString()
        user.partner_gender = prefPartnerGender.selectedItemPosition
        user.radius = prefDistance.selectedItemPosition
        user.partner_level = prefPartnerLevel.selectedItemPosition
        user.level = prefLevel.selectedItemPosition

        UpdateUserAsyncTask().execute()
    }
    
    override fun onBackPressed() {

        var intent = Intent(this, MatchProfileActivity::class.java)
        startActivity(intent)
    }
}
