package com.example.pm_22689.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes_table")

class Notes(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "text") val text: String
)