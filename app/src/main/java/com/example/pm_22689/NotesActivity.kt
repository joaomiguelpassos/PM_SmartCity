package com.example.pm_22689

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pm_22689.database.Notes
import com.example.pm_22689.notes.NoteListAdapter
import com.example.pm_22689.notes.NotesViewModel
import com.example.pm_22689.notes.OnNoteItemClickListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDate.now


class NotesActivity : AppCompatActivity(), OnNoteItemClickListener {
    private lateinit var noteViewModel: NotesViewModel
    private val newNoteActivityRequestCode = 1
    private val updateNoteActivityRequestCode = 2


    @RequiresApi(Build.VERSION_CODES.O)
    val currentTime = now().toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = NoteListAdapter(this, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        noteViewModel = ViewModelProvider(this).get(NotesViewModel::class.java)

        noteViewModel.allNotes.observe(this, { notes ->
            notes?.let { adapter.setNotes(it) }
        })

        val fabAdd = findViewById<FloatingActionButton>(R.id.fab_add)                               // Find do botão flutuante para add nota
        fabAdd.setOnClickListener {
            val intent = Intent(this@NotesActivity, NewNoteActivity::class.java)
            startActivityForResult(intent, newNoteActivityRequestCode)
        }

        // Add the functionality to swipe items in the
        // recycler view to delete that item
        val helper = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) {
                    val position = viewHolder.adapterPosition
                    val myNote: Notes = adapter.getNoteAtPosition(position)

                    noteViewModel.deleteNote(myNote)
                }
            })

        helper.attachToRecyclerView(recyclerView)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_deletenote, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.clear_data -> {
                Toast.makeText(this, R.string.deleting_all_data, Toast.LENGTH_SHORT).show()
                noteViewModel.deleteAll()
                true
            }
            else -> super.onOptionsItemSelected(item)               //returns false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newNoteActivityRequestCode && resultCode == Activity.RESULT_OK) {
            data?.getStringExtra(NewNoteActivity.EXTRA_REPLY)?.let {
                val note = Notes(noteMessage = it, noteDate = currentTime)
                noteViewModel.insert(note)
            }
        } else if (requestCode == updateNoteActivityRequestCode && resultCode == Activity.RESULT_OK) {
            data?.getStringExtra(NewNoteActivity.EXTRA_REPLY)?.let {
                val id = data.getIntExtra(NewNoteActivity.EXTRA_DATA_ID,-1)
                val note = Notes(noteId = id, noteMessage = it, noteDate = currentTime)
                Toast.makeText(applicationContext, id.toString(), Toast.LENGTH_LONG).show()
                noteViewModel.update(note)
            }

        } else {
            Toast.makeText(applicationContext, R.string.empty_not_saved, Toast.LENGTH_LONG).show()
        }
    }


    override fun onItemClick(item: Notes, position: Int) {
        //Toast.makeText(this, item.noteMessage, Toast.LENGTH_LONG).show()
        val intent = Intent(this, NewNoteActivity::class.java)
        intent.putExtra("NOTEID", item.noteId)
        intent.putExtra("NOTEMESSAGE", item.noteMessage)
        startActivityForResult(intent, updateNoteActivityRequestCode)
    }
}