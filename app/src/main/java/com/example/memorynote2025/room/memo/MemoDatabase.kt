package com.example.memorynote2025.room.memo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// 추상 클래스로 선언 -> Room이 DB 구현체를 자동으로 생성
@Database(entities = [Memo::class], version = 1)
abstract class MemoDatabase : RoomDatabase() {
    abstract fun memoDao(): MemoDao

    companion object { // 싱글톤
        @Volatile // 여러 스레드에서 최신 값을 읽을 수 있도록 보장
        private var INSTANCE: MemoDatabase? = null

        fun getInstance(context: Context): MemoDatabase {
            // synchronized : 여러 스레드가 동시에 인스턴스를 생성하지 못하도록 동기화
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MemoDatabase::class.java, "memo.db")
                    .build()
                INSTANCE = instance // 새로 생성한 instance를 INSTANCE에 저장
                instance
            }
        }
    }
}