package com.example.pm_22689.db

import androidx.lifecycle.LiveData
import com.example.pm_22689.dao.NotesDao
import com.example.pm_22689.entities.Notes

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class NotesRepository(private val notesDao: NotesDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allNotes: LiveData<List<Notes>> = notesDao.getAlphabetizedNotes()

    suspend fun insert(text: Notes) {
        notesDao.insert(text)
    }
}
