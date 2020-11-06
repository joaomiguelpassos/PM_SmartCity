package com.example.pm_22689.notes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.pm_22689.database.Notes
import com.example.pm_22689.database.NotesDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NoteRepository
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
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
}