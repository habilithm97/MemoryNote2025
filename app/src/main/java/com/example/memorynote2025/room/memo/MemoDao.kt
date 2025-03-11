package com.example.memorynote2025.room.memo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemo(memo: Memo)

    @Update
    suspend fun updateMemo(memo: Memo)

    @Query("select * from memos order by date")
    fun getAll(): Flow<List<Memo>>
}

/*
* suspend
 -오래 걸리는 작업 (Room DB, 네트워크) 비동기 처리
 -일시 중단 가능 -> UI 스레드 차단 없이 실행

* Flow
 -비동기 데이터 스트림 처리
 -순차적이고 지속적인 데이터 방출
 -UI 생명주기와 독립적으로 작동
 */