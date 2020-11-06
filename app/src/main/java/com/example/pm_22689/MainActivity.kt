package com.example.pm_22689

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

//const val PARAM1_NAME = "PARAM1_NAME"

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.create_new_note -> {
                Toast.makeText(this, "criar nova nota", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.view_notes -> {
                val intent = Intent(this, NotesActivity::class.java)
                startActivity(intent)           //Abre a atividade de consulta de notas
                true
            }
            else -> super.onOptionsItemSelected(item)               //returns false
        }
    }
}