package com.example.memorynote2025.room.password

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface PasswordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPassword(password: Password)

    @Update
    suspend fun updatePassword(password: Password)

    @Query("select * from password where id = 1")
    suspend fun getPassword(): Password?
}