package com.example.pm_22689.database

import androidx.lifecycle.LiveData
import androidx.room.*
/**
 * Data access object. Mapping of SQL queries to functions. Calls the methods only, Rooms takes care of the rest
 */
@Dao
interface NotesDatabaseDao {

    @Insert
    suspend fun insert(note: Notes)


    @Update (onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(note: Notes)


    @Delete
    suspend fun deleteNote(note: Notes)


    @Query("SELECT * from notes_table WHERE noteId = :key")
    suspend fun get(key: Long): Notes

    /**
     * Deletes all values from the table.
     *
     * This does not delete the table, only its contents.
     */
    @Query("DELETE FROM notes_table")
    suspend fun deleteAll()

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