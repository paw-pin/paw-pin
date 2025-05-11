package com.example.pawpin_v2.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WalkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWalk(walk: Walk): Long

    @Query("SELECT * FROM walks ORDER BY date DESC")
    fun getAllWalks(): Flow<List<Walk>>

}




