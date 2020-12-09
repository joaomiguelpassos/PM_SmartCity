package com.example.pm_22689

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*

/**
 * Activity where the user selects type and description for a marker
 * The fields can be empty when inserting or filled when updating
 */
class MarkerDetails : AppCompatActivity() {
    private var latitude: String? = null
    private var longitude: String? = null
    private val cameraRequestCode = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marker_details)
        // access the items of the list
        val tipos = resources.getStringArray(R.array.Tipos)
        val spinner = findViewById<Spinner>(R.id.markerTipo)
        val coordsText = findViewById<TextView>(R.id.markerCoords)
        val descrText = findViewById<EditText>(R.id.markerDescr)
        val extras = intent.extras
        var tipo: String? = null
        var descr: String?

        if (extras != null) {
            latitude = intent.getStringExtra("latitude").toString()
            longitude = intent.getStringExtra("longitude").toString()
            coordsText.text = "Lat: $latitude | Long: $longitude"
            descr = intent.getStringExtra("descr").toString()
            if (descr.contentEquals("null")) {
                descrText.requestFocus()                                    // puts the cursor at the end of the text
            }else  {
                descrText.setText(descr)
                descrText.setSelection(descr.length)
                descrText.requestFocus()
            }
        }

        if (spinner != null) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipos)
            spinner.adapter = adapter
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    tipo = tipos[position]
                    Toast.makeText(this@MarkerDetails, "Tipo escolhido", Toast.LENGTH_SHORT).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // does nothing
                }
            }
        }

        val button = findViewById<Button>(R.id.btnSaveMarker)
        button.setOnClickListener {
            val replyIntent = Intent()
            descr = descrText.text.toString()
            replyIntent.putExtra(EXTRA_DATA_DESCR, descr)
            if (latitude != null && longitude != null){
                replyIntent.putExtra(EXTRA_DATA_LAT, latitude)
                replyIntent.putExtra(EXTRA_DATA_LOG, longitude)
                replyIntent.putExtra(EXTRA_DATA_TIPO, tipo)
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }

        val captureBTN = findViewById<Button>(R.id.buttonCapture)
        captureBTN.setOnClickListener{
            val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (callCameraIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(callCameraIntent, cameraRequestCode)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val imageMarker = findViewById<ImageView>(R.id.markerImage)
        when(requestCode) {
            cameraRequestCode -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    imageMarker.setImageBitmap(data.extras?.get("data") as Bitmap)
                }
            }
            else -> {
                Toast.makeText(this, "Unrecognized request code", Toast.LENGTH_SHORT)
            }
        }
    }

    companion object {
        const val EXTRA_DATA_DESCR = "extra_reply_descr"
        const val EXTRA_DATA_TIPO = "extra_data_tipo"
        const val EXTRA_DATA_LAT = "latitude"
        const val EXTRA_DATA_LOG = "longitude"
    }
}