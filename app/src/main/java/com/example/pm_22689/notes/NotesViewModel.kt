package com.example.pm_22689.notes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.pm_22689.database.Notes
import com.example.pm_22689.database.NotesDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
/**
 * Hold all the data needed for the UI
 * Middle man between the repository and the UI
 */
class NotesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NoteRepository

    val allNotes: LiveData<List<Notes>>

    init {
        val notesDao = NotesDatabase.getInstance(application).notesDatabaseDao
        repository = NoteRepository(notesDao)
        allNotes = repository.allNotes
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(note: Notes) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(note)
    }

    fun update(note: Notes) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(note)
    }

    fun deleteAll() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAll()
    }

    fun deleteNote(note: Notes) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteNote(note)
    }
}