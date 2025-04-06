package com.example.memorynote2025.room.memo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// 추상 클래스로 선언 -> Room이 DB 구현체를 자동으로 생성
@Database(entities = [Memo::class], version = 2)
abstract class MemoDatabase : RoomDatabase() {
    abstract fun memoDao(): MemoDao

    companion object { // 싱글톤
        @Volatile // 여러 스레드에서 최신 값을 읽을 수 있도록 보장
        private var INSTANCE: MemoDatabase? = null

        /* context.applicationContext
         -DB 파일 저장 경로 지정
         -앱 전체에서 하나의 DB 인스턴스만 유지 (싱글톤)
         -메모리 릭 방지 (액티비티 Context 직접 참조 방지)
         */
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