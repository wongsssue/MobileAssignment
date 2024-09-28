package com.example.funparkapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: UserType)

    @Query("SELECT * FROM UserType WHERE username = :username")
    suspend fun getUserByUsername(username: String): UserType?

    @Update
    suspend fun updateUser(user: UserType)

    @Query("SELECT * FROM UserType") // Assuming 'UserType' is your table name
    suspend fun getAllUsers(): List<UserType>

    @Delete
    suspend fun deleteUser(user: UserType)
}