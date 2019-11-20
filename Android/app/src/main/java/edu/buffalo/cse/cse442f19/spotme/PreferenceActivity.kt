package edu.buffalo.cse.cse442f19.spotme

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_preference.*
import android.graphics.Rect
import android.util.TypedValue
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.content.pm.PackageManager

import android.Manifest
import android.app.Activity
import android.graphics.Bitmap
import android.os.Build
import android.os.Build.*
import kotlinx.android.synthetic.main.activity_main.*
import android.graphics.ImageDecoder
import android.view.Gravity
import android.widget.*
import androidx.core.view.setPadding

import java.io.File
import java.util.Base64
import java.io.ByteArrayOutputStream;


class PreferenceActivity() : AppCompatActivity() {

    lateinit var viewName: TextView;// = TextView(this);'
    lateinit var byteArray: ByteArray;
    lateinit var upButton: Button;

    override fun onCreate(savedInstanceState: Bundle?) {
        var img: Unit
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preference)

        if (Globals.currentUser == null) {
            val intent = Intent(this, LoginActivity :: class.java)
            startActivity(intent)
        }
        val user = Globals.currentUser!!

        byteArray = user.picture

        uploadButton.setOnClickListener {
            if (VERSION.SDK_INT >= VERSION_CODES.M){
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED){
                    //permission denied
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    //show popup to request runtime permission
                    requestPermissions(permissions, PERMISSION_CODE)
                }
                else{
                    //permission already granted
                    img = choosePhotoFromGallery()
                    //Log.e("Let's see!", img.toString())
                }
            }
            else{
                //system OS is < Marshmallow
                img = choosePhotoFromGallery()
                //Log.e("Let's see!", img.toString())
            }
            //val intent = Intent(this, MatchListActivity :: class.java)
            /*startActivityForResult(
                Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI
                ), GET_FROM_GALLERY
            )*/
        }
        prefPartnerGender.setSelection(user.partner_gender, false)
        prefDistance.setSelection(user.radius, false)
        prefPartnerLevel.setSelection(user.partner_level, false)
        prefLevel.setSelection(user.level, false)

        val nameChangeIndex = 3;

        val bitmap = BitmapFactory.decodeByteArray(user.picture, 0, user.picture.size)
        UploadPhoto.setImageBitmap(bitmap)
        //UploadPhoto.setPadding(0,0,0,0)

        uploadButton.setText("UPLOAD PHOTO")

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

    fun toBase(image : String): String{
        val bytes = File(image).readBytes()
        val base64 = Base64.getEncoder().encodeToString(bytes)
        return base64
    }

    fun choosePhotoFromGallery() {

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000;
        //Permission code
        private val PERMISSION_CODE = 1001;
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.size >0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    //permission from popup granted
                    choosePhotoFromGallery()
                }
                else{
                    //permission from popup denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun bToBase(bit : Bitmap) : ByteArray{
        val bytesArray = ByteArrayOutputStream()
        Log.e("Array holds", "Success!")
        bit.compress(Bitmap.CompressFormat.PNG, 100, bytesArray);
        val bArray = bytesArray.toByteArray();
        byteArray = bArray
        return bArray //toBase(sArray)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, d: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            //UploadPhoto.setImageURI(d?.data)
            val imageUri = d?.data
            val bitmap : Bitmap
            if (imageUri != null){
                val imageStream = getContentResolver().openInputStream(imageUri);
                bitmap = BitmapFactory.decodeStream(imageStream);
                bToBase(bitmap)
                UploadPhoto.setImageURI(imageUri)
                updatePreferences()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        updatePreferences()
        val intent = Intent(this, MyProfileActivity :: class.java)
        startActivity(intent)
    }

    fun updatePreferences() {
        val user = Globals.currentUser!!
        user.name = viewName.text.toString()
        user.partner_gender = prefPartnerGender.selectedItemPosition
        user.radius = prefDistance.selectedItemPosition
        user.partner_level = prefPartnerLevel.selectedItemPosition
        user.level = prefLevel.selectedItemPosition
        user.picture = byteArray

        UpdateUserAsyncTask().execute()
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
