package com.example.pm_22689

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pm_22689.adapters.NotesAdapter

const val PARAM1_NAME = "PARAM1_NAME"

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
                Toast.makeText(this, "ver notas", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, NotesActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)               //returns false
        }
    }
}

