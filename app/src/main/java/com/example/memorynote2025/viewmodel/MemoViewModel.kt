package com.example.memorynote2025.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.memorynote2025.repository.MemoRepository
import com.example.memorynote2025.room.memo.Memo
import com.example.memorynote2025.room.memo.MemoDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Context는 액/프 종료 시 사라지지만, Application은 앱 생명주기와 함께 유지
class MemoViewModel(application: Application) : AndroidViewModel(application) {
    private val memoRepository: MemoRepository
    val getAll: LiveData<List<Memo>>

    init {
        val memoDao = MemoDatabase.getInstance(application).memoDao()
        memoRepository = MemoRepository(memoDao)
        // UI에서 쉽게 관찰할 수 있도록 Flow를 LiveData로 변환
        // UI는 LiveData만 관찰하고, 데이터 흐름은 그대로 유지 가능
        getAll = memoRepository.getAll().asLiveData()
    }

    fun insertMemo(memo: Memo) {
        viewModelScope.launch(Dispatchers.IO) {
            memoRepository.insertMemo(memo)
        }
    }

    fun updateMemo(memo: Memo) {
        viewModelScope.launch(Dispatchers.IO) {
            memoRepository.updateMemo(memo)
        }
    }
}