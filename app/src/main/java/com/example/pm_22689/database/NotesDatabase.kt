package com.example.pm_22689.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Notes::class], version = 3, exportSchema = false)
abstract class NotesDatabase : RoomDatabase() {

    /**
     * Connects the database to the DAO.
     */
    abstract val notesDatabaseDao: NotesDatabaseDao

    /**
     * Database pre population
     * Currently disabled
     */
//    private class NoteDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
//
//        override fun onOpen(db: SupportSQLiteDatabase) {
//            super.onOpen(db)
//            INSTANCE?.let { database ->
//                scope.launch {
//                    populateDatabase(database.notesDatabaseDao)
//                }
//            }
//        }
//
//        suspend fun populateDatabase(noteDao: NotesDatabaseDao) {
//            // Delete all content here.
//            noteDao.clear()
//
//            // Add sample words.
//            var note = Notes(1,"Teste", "20201027")
//            noteDao.insert(note)
//            note = Notes(2,"Teste2", "20201028")
//            noteDao.insert(note)
//        }
//    }

    companion object {

        @Volatile
        private var INSTANCE: NotesDatabase? = null


        fun getInstance(context: Context): NotesDatabase {
            synchronized(this) {

                // Copy the current value of INSTANCE to a local variable so Kotlin can smart cast.
                var instance = INSTANCE

                // If instance is `null` make a new database instance.
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            NotesDatabase::class.java,
                            "notes_database"
                    )
                            .fallbackToDestructiveMigration()
                            //.addCallback(NoteDatabaseCallback(scope))
                            .build()
                    // Assign INSTANCE to the newly created database.
                    INSTANCE = instance
                }

                // Return instance; smart cast to be non-null.
                return instance
            }
        }
    }
}
