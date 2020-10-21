package com.example.pm_22689.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.pm_22689.dao.NotesDao
import com.example.pm_22689.entities.Notes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(entities = arrayOf(Notes::class), version = 5, exportSchema = false)
public abstract class NotesDatabase : RoomDatabase() {

    abstract fun notesDao(): NotesDao

    private class NotesDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback(){
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    var notes = database.notesDao()

                    // Delete all content here
                    notes.deleteAll()

                    // Add sample words
                    var note = Notes(1,"21-10-2020", "Teste1")
                    notes.insert(note)
                    note = Notes(2,"21-10-2020", "Teste2")
                    notes.insert(note)
                }
            }
        }
    }

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: NotesDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): NotesDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = databaseBuilder(
                    context.applicationContext,
                    NotesDatabase::class.java,
                    "notes_database"
                )
                    .addCallback(NotesDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
