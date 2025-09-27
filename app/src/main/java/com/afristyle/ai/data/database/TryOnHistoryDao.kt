package com.afristyle.ai.data.database

import androidx.room.*
import com.afristyle.ai.data.models.TryOnResult
import kotlinx.coroutines.flow.Flow

@Dao
interface TryOnHistoryDao {
    
    @Query("SELECT * FROM tryon_results ORDER BY timestamp DESC")
    fun getAllTryOnResults(): Flow<List<TryOnResult>>
    
    @Query("SELECT * FROM tryon_results WHERE id = :resultId")
    suspend fun getTryOnResultById(resultId: Long): TryOnResult?
    
    @Query("SELECT * FROM tryon_results WHERE userPhotoId = :userPhotoId ORDER BY timestamp DESC")
    fun getTryOnResultsByUserPhoto(userPhotoId: Long): Flow<List<TryOnResult>>
    
    @Query("SELECT * FROM tryon_results WHERE outfitId = :outfitId ORDER BY timestamp DESC")
    fun getTryOnResultsByOutfit(outfitId: Long): Flow<List<TryOnResult>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTryOnResult(result: TryOnResult): Long
    
    @Update
    suspend fun updateTryOnResult(result: TryOnResult)
    
    @Delete
    suspend fun deleteTryOnResult(result: TryOnResult)
    
    @Query("DELETE FROM tryon_results WHERE id = :resultId")
    suspend fun deleteTryOnResultById(resultId: Long)
    
    @Query("DELETE FROM tryon_results")
    suspend fun deleteAllTryOnResults()
    
    @Query("SELECT COUNT(*) FROM tryon_results")
    suspend fun getTryOnResultCount(): Int
    
    @Query("DELETE FROM tryon_results WHERE timestamp < :cutoffTime")
    suspend fun deleteOldResults(cutoffTime: Long): Int
}