package com.example.pm_22689.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface NotesDatabaseDao {

    @Insert
    suspend fun insert(note: Notes)


    @Update
    suspend fun update(note: Notes)

    /**
     * Selects and returns the row that matches the supplied start time, which is our key.
     *
     * @param key startTimeMilli to match
     */
    @Query("SELECT * from notes_table WHERE noteId = :key")
    suspend fun get(key: Long): Notes

    /**
     * Deletes all values from the table.
     *
     * This does not delete the table, only its contents.
     */
    @Query("DELETE FROM notes_table")
    suspend fun clear()

    /**
     * Selects and returns all rows in the table,
     *
     * sorted by start time in descending order.
     */
    @Query("SELECT * FROM notes_table ORDER BY noteId DESC")
    fun getAllNotes(): LiveData<List<Notes>>


    /**
     * Selects and returns the note with given noteID.
     */
    @Query("SELECT * from notes_table WHERE noteId = :key")
    fun getNoteWithId(key: Long): LiveData<Notes>
}