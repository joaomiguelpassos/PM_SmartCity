package com.example.pm_22689.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pm_22689.entities.Notes

@Dao
interface NotesDao {

    @Query("SELECT * from notes_table ORDER BY text ASC")
    fun getAlphabetizedNotes(): LiveData<List<Notes>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(text: Notes)

    @Query("DELETE FROM notes_table")
    suspend fun deleteAll()
}