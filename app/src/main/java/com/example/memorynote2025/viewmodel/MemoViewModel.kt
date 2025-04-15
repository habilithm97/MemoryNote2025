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

// Context는 UI 컴포넌트 종료 시 사라지지만, Application은 앱 생명주기와 함께 유지
class MemoViewModel(application: Application) : AndroidViewModel(application) {
    private val memoRepository: MemoRepository
    val getAll: LiveData<List<Memo>>

    init {
        val memoDao = MemoDatabase.getInstance(application).memoDao()
        memoRepository = MemoRepository(memoDao)
        // UI에서 쉽게 관찰할 수 있도록 LiveData로 변환
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

    fun deleteMemo(memo: Memo) {
        viewModelScope.launch(Dispatchers.IO) {
            memoRepository.deleteMemo(memo)
        }
    }
}
/*
* AAC ViewModel
 -AndroidViewModel은 UI 컴포넌트보다 오래 살아남고, Application Context 사용 가능
 -viewModelScope는 ViewModel 생명주기에 맞춰 비동기 작업을 안전하게 관리

* MVVM ViewModel
 -UI와 데이터 간의 중재자
 -LiveData를 통해 UI에 데이터를 제공
 -Repository를 통해 비즈니스 로직을 처리
 */