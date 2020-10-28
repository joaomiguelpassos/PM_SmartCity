package com.example.pm_22689.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents one night's sleep through start, end times, and the sleep quality.
 */
@Entity(tableName = "notes_table")
data class Notes(
        @PrimaryKey(autoGenerate = true)
        var noteId: Int? = null,

        @ColumnInfo(name = "message")
        val noteMessage: String,

        @ColumnInfo(name = "date")
        val noteDate: String? = null)
