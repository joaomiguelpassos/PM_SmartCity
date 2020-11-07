package com.example.pm_22689

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class NewNoteActivity : AppCompatActivity() {
    private lateinit var editNoteView: EditText

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_note)
        editNoteView = findViewById(R.id.edit_note)

        val extras = intent.extras
        var id = -1
        // Verify if there's nothing coming on intent. If not starts the EditText empty
        if(extras != null){
            val note = intent.getStringExtra("NOTEMESSAGE").toString()
            id = intent.getIntExtra("NOTEID",-1)
            if(note.isNotEmpty()){
                editNoteView.setText(note)
                editNoteView.setSelection(note.length)
                editNoteView.requestFocus()
            }
        }

        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(editNoteView.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                val note = editNoteView.text.toString()
                replyIntent.putExtra(EXTRA_REPLY, note)
                if (id != -1) {
                    replyIntent.putExtra(EXTRA_DATA_ID,id)
                }
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }
    }

    companion object {
        const val EXTRA_REPLY = "com.example.android.wordlistsql.REPLY"
        const val EXTRA_DATA_ID = "extra_data_id"
    }
}