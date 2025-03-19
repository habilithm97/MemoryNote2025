package com.example.memorynote2025.repository

import com.example.memorynote2025.room.password.Password
import com.example.memorynote2025.room.password.PasswordDao

class PasswordRepository(private val passwordDao: PasswordDao) {
    suspend fun insertPassword(password: Password) {
        passwordDao.insertPassword(password)
    }
    suspend fun updatePassword(password: Password) {
        passwordDao.updatePassword(password)
    }
    suspend fun getPassword(): Password? {
        return passwordDao.getPassword()
    }
}