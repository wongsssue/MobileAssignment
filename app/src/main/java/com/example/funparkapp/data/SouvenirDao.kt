package com.example.funparkapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SouvenirDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(souvenir: Souvenir)

    @Query("SELECT * FROM souvenir_table")
    fun getAllSouvenirs(): Flow<List<Souvenir>>


}