package com.example.memorynote2025.room.memo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memos")
data class Memo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "content") var content: String,
    @ColumnInfo(name = "date") var date: Long
)