package com.example.pm_22689

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

/**
 * Activity where the user can ADD or UPDATE a note
 * First checks if there's intent passed out, so that it knows that it's to update a note
 * If there are no intents passed, the user pretends to add a new note
 */
class NewNoteActivity : AppCompatActivity() {
    private lateinit var editNoteView: EditText         // declaration of variable that will contain an EditText

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_note)
        editNoteView = findViewById(R.id.edit_note)     // setting the var with the EditText from resources

        val extras = intent.extras
        var id = -1                                     //default value in order to not get a possible ID from the DB table
        // Verify if there's nothing coming on intent. If not starts the EditText empty
        if (extras != null) {
            val note = intent.getStringExtra("NOTEMESSAGE").toString()
            id = intent.getIntExtra("NOTEID", -1)
            if (note.isNotEmpty()) {
                editNoteView.setText(note)              // sets the text of the EditText with the intent content
                editNoteView.setSelection(note.length)
                editNoteView.requestFocus()             // puts the cursor at the end of the text
            }
        }

        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(editNoteView.text)) {         // if no text is written on the EditText
                setResult(Activity.RESULT_CANCELED, replyIntent)    // returns result code CANCELED in order to not save the note
            } else {
                val note = editNoteView.text.toString()
                replyIntent.putExtra(EXTRA_REPLY, note)
                if (id != -1) {                                 // if sentence to garantee that an ID is correctly passed in intent
                    replyIntent.putExtra(EXTRA_DATA_ID, id)
                }
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }
    }

    companion object {
        const val EXTRA_REPLY = "extra_reply"
        const val EXTRA_DATA_ID = "extra_data_id"
    }
}