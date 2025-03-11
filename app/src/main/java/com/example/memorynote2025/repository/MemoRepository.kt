package com.example.memorynote2025.repository

import com.example.memorynote2025.room.memo.Memo
import com.example.memorynote2025.room.memo.MemoDao
import kotlinx.coroutines.flow.Flow

/* Repository
 -데이터 소스 추상화 -> ViewModel은 데이터를 가져오는 방법에 대해 알 필요 없이
   Repository를 통해 데이터 처리 가능
 -비즈니스 로직 캡슐화 -> 중복 감소, 유지 보수 용이
 -ViewModel과 데이터 소스 간의 중재자 역할 -> 의존성 최소화
 */
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