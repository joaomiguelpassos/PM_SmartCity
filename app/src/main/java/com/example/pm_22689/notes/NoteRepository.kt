package com.example.pm_22689.notes

import androidx.lifecycle.LiveData
import com.example.pm_22689.database.Notes
import com.example.pm_22689.database.NotesDatabaseDao

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class NoteRepository(private val notesDao: NotesDatabaseDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allWords: LiveData<List<Notes>> = notesDao.getAllNotes()

    suspend fun insert(word: Notes) {
        notesDao.insert(word)
    }
}