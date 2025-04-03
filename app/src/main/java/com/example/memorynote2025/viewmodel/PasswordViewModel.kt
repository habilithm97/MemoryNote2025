package com.example.memorynote2025.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.memorynote2025.repository.PasswordRepository
import com.example.memorynote2025.room.password.Password
import com.example.memorynote2025.room.password.PasswordDatabase
import kotlinx.coroutines.launch

class PasswordViewModel(application: Application) : AndroidViewModel(application) {
    private val passwordRepository: PasswordRepository

    init {
        val passwordDao = PasswordDatabase.getInstance(application).passwordDao()
        passwordRepository = PasswordRepository(passwordDao)
    }

    fun insertPassword(password: Password) {
        viewModelScope.launch {
            passwordRepository.insertPassword(password)
        }
    }

    fun updatePassword(password: Password) {
        viewModelScope.launch {
            passwordRepository.updatePassword(password)
        }
    }

    // 저장된 비밀번호를 가져와 콜백 함수에 전달
    fun getPassword(onResult: (Password?) -> Unit) {
        viewModelScope.launch {
            onResult(passwordRepository.getPassword())
        }
    }
}