package com.example.memorynote2025.repository

import com.example.memorynote2025.room.memo.Memo
import com.example.memorynote2025.room.memo.MemoDao
import kotlinx.coroutines.flow.Flow

class MemoRepository(private val memoDao: MemoDao) {
    suspend fun insertMemo(memo: Memo) {
        memoDao.insertMemo(memo)
    }
    suspend fun updateMemo(memo: Memo) {
        memoDao.updateMemo(memo)
    }
    suspend fun deleteMemo(memo: Memo) {
        memoDao.deleteMemo(memo)
    }
    fun getAll(): Flow<List<Memo>> {
        return memoDao.getAll()
    }
}
/*
* Repository
 -데이터 소스 추상화 -> ViewModel은 데이터 출처를 몰라도 됨
 -비즈니스 로직 캡슐화 -> 중복 감소, 유지 보수 용이
 -ViewModel과 데이터 소스 간의 중재자 -> 의존성 최소화

* 데이터 소스 : 데이터를 실제로 저장하거나 불러오는 곳 (Room 등)
* 비즈니스 로직 : 특정 기능을 수행하기 위해 처리하는 로직 (정렬 순서 변경 등)
 */