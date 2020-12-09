package com.example.pm_22689

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

/**
 * Activity where the user inserts the data relative to filter
 * onCreate checks how user pretends to filter and changes TextViews text's
 */
class FilterActivity : AppCompatActivity() {
    private lateinit var textFilter: TextView
    private lateinit var editFilter: EditText
    private lateinit var buttonFilter: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)
        textFilter = findViewById(R.id.textViewFilter)
        editFilter = findViewById(R.id.edit_filter)
        buttonFilter = findViewById(R.id.button_filter)

        if(intent.hasExtra("type")){
            textFilter.text = getString(R.string.filterType)
            editFilter.hint = getString(R.string.hint_filter)
        }

        buttonFilter.setOnClickListener{
            val replyIntent = Intent()
            if(TextUtils.isEmpty(editFilter.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                var answer = editFilter.text.toString()
                if(intent.hasExtra("type")){
                    answer.trim()
                    answer.capitalize()
                    Log.d("****Button", "onCreate: $answer")
                }
                replyIntent.putExtra("answer", answer)
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }
    }
}