package com.example.pm_22689

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
//@RunWith(AndroidJUnit4::class)
//class SleepDatabaseTest {
//
//    private lateinit var sleepDao: NotesDatabaseDao
//    private lateinit var db: NotesDatabase
//
//    @Before
//    fun createDb() {
//        val context = InstrumentationRegistry.getInstrumentation().targetContext
//        // Using an in-memory database because the information stored here disappears when the
//        // process is killed.
//        db = Room.inMemoryDatabaseBuilder(context, NotesDatabase::class.java)
//                // Allowing main thread queries, just for testing.
//                .allowMainThreadQueries()
//                .build()
//        sleepDao = db.notesDatabaseDao
//    }
//
//    @After
//    @Throws(IOException::class)
//    fun closeDb() {
//        db.close()
//    }
//
//    @Test
//    @Throws(Exception::class)
//    suspend fun insertAndGetNight() {
//        val night = Notes(0,"lol123","2020-10-27")
//        sleepDao.insert(night)
//        sleepDao.getAllNotes()
//    }
//}