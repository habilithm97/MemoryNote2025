package com.example.memorynote2025.room.memo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// Room이 내부적으로 구현체를 생성할 수 있도록 추상 클래스로 선언
@Database(entities = [Memo::class], version = 2)
abstract class MemoDatabase : RoomDatabase() {
    abstract fun memoDao(): MemoDao

    companion object { // 클래스 수준의 싱글톤 객체 (static처럼 사용 가능)
        @Volatile // 여러 스레드에서 최신 값을 읽을 수 있도록 보장
        private var INSTANCE: MemoDatabase? = null

        fun getInstance(context: Context): MemoDatabase {
            // synchronized : 여러 스레드가 동시에 인스턴스를 생성하지 못하도록 동기화
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MemoDatabase::class.java, "memo.db")
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance // 새로 생성한 instance를 INSTANCE에 저장
                instance
            }
        }
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("alter table memos add column islocked integer not null default 0")
            }
        }
    }
}
/*
(Volatile : 스레드 간 변수 동기화 -> 변수의 최신 상태를 모든 스레드가 보장 받음)
-> Volatile -> 캐시로 인한 값 불일치 막음
-> synchronized -> 동시 실행 막음
 */