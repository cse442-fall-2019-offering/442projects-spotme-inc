package edu.buffalo.cse.cse442f19.spotme

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_preference.*
import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Rect
import android.util.TypedValue
import androidx.core.content.ContextCompat.getSystemService
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager


class PreferenceActivity() : AppCompatActivity() {

    lateinit var viewName: TextView;// = TextView(this);

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preference)

        if (Globals.currentUser == null) {
            val intent = Intent(this, LoginActivity :: class.java)
            startActivity(intent)
        }
        val user = Globals.currentUser!!

        prefPartnerGender.setSelection(user.partner_gender, false)
        prefDistance.setSelection(user.radius, false)
        prefPartnerLevel.setSelection(user.partner_level, false)
        prefLevel.setSelection(user.level, false)

        val nameChangeIndex = 1;

        var editName = EditText(this);
        editName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        editName.setText(user.name)

        viewName = TextView(this)
        viewName.text = user.name
        viewName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        viewName.setPadding(editName.paddingLeft, editName.paddingTop, editName.paddingRight, editName.paddingBottom)
        linlayout.addView(viewName, nameChangeIndex);

        viewName.setOnClickListener {

            linlayout.addView(editName, nameChangeIndex);
            linlayout.removeView(viewName);
        }

        editName.setOnFocusChangeListener { _, gainFocus ->

            if (!gainFocus) {

                if (!editName.text.isBlank()) {//Prevent setting a blank name

                    viewName.text = editName.text
                }

                linlayout.addView(viewName, nameChangeIndex);
                linlayout.removeView(editName);

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
        user.name = viewName.text.toString()
        user.partner_gender = prefPartnerGender.selectedItemPosition
        user.radius = prefDistance.selectedItemPosition
        user.partner_level = prefPartnerLevel.selectedItemPosition
        user.level = prefLevel.selectedItemPosition

        UpdateUserAsyncTask().execute()
    }

    override fun onBackPressed() {

        var intent = Intent(this, MyProfile::class.java)
        startActivity(intent)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}
