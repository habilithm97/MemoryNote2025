package com.example.memorynote2025.room.memo

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize // Parcelable 자동 구현
@Entity(tableName = "memos")
data class Memo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "content") var content: String,
    @ColumnInfo(name = "date") var date: Long
) : Parcelable // 컴포넌트 간에 데이터를 전달할 수 있도록 직렬화