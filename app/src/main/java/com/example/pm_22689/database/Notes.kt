package com.example.pm_22689.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity definition for SQL table
 */
@Entity(tableName = "notes_table")
data class Notes(
        @PrimaryKey(autoGenerate = true)
        var noteId: Int? = null,

        @ColumnInfo(name = "message")
        val noteMessage: String,

        @ColumnInfo(name = "date")
        val noteDate: String? = null)
