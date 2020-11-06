package com.example.pm_22689

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView

class EditNoteActivity : AppCompatActivity() {
    private lateinit var editNoteTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)

        editNoteTextView = findViewById(R.id.edit_note2)
        editNoteTextView.text = intent.getStringExtra("NOTEMESSAGE")

        val buttonEdit = findViewById<Button>(R.id.button_edit)
        val buttonDelete = findViewById<Button>(R.id.button_delete)

        buttonEdit.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(editNoteTextView.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                val note = editNoteTextView.text.toString()
                replyIntent.putExtra(EXTRA_REPLY, note)
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }

        /*buttonDelete.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(editNoteTextView.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                val note = editNoteTextView.text.toString()
                replyIntent.putExtra(EXTRA_REPLY, note)
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }*/
    }

    companion object {
        const val EXTRA_REPLY = "com.example.android.wordlistsql.REPLY"
    }
}