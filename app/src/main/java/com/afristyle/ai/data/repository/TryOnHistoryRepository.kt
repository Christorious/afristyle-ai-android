package com.afristyle.ai.data.repository

import com.afristyle.ai.data.database.TryOnHistoryDao
import com.afristyle.ai.data.models.TryOnResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface TryOnHistoryRepository {
    fun getAllTryOnResults(): Flow<List<TryOnResult>>
    suspend fun getTryOnResultById(resultId: Long): TryOnResult?
    fun getTryOnResultsByUserPhoto(userPhotoId: Long): Flow<List<TryOnResult>>
    fun getTryOnResultsByOutfit(outfitId: Long): Flow<List<TryOnResult>>
    suspend fun saveTryOnResult(result: TryOnResult): Long
    suspend fun updateTryOnResult(result: TryOnResult)
    suspend fun deleteTryOnResult(resultId: Long)
    suspend fun deleteAllTryOnResults()
    suspend fun getTryOnResultCount(): Int
    suspend fun deleteOldResults(cutoffTime: Long): Int
}

@Singleton
class TryOnHistoryRepositoryImpl @Inject constructor(
    private val tryOnHistoryDao: TryOnHistoryDao
) : TryOnHistoryRepository {
    
    override fun getAllTryOnResults(): Flow<List<TryOnResult>> {
        return tryOnHistoryDao.getAllTryOnResults()
    }
    
    override suspend fun getTryOnResultById(resultId: Long): TryOnResult? {
        return tryOnHistoryDao.getTryOnResultById(resultId)
    }
    
    override fun getTryOnResultsByUserPhoto(userPhotoId: Long): Flow<List<TryOnResult>> {
        return tryOnHistoryDao.getTryOnResultsByUserPhoto(userPhotoId)
    }
    
    override fun getTryOnResultsByOutfit(outfitId: Long): Flow<List<TryOnResult>> {
        return tryOnHistoryDao.getTryOnResultsByOutfit(outfitId)
    }
    
    override suspend fun saveTryOnResult(result: TryOnResult): Long {
        return tryOnHistoryDao.insertTryOnResult(result)
    }
    
    override suspend fun updateTryOnResult(result: TryOnResult) {
        tryOnHistoryDao.updateTryOnResult(result)
    }
    
    override suspend fun deleteTryOnResult(resultId: Long) {
        tryOnHistoryDao.deleteTryOnResultById(resultId)
    }
    
    override suspend fun deleteAllTryOnResults() {
        tryOnHistoryDao.deleteAllTryOnResults()
    }
    
    override suspend fun getTryOnResultCount(): Int {
        return tryOnHistoryDao.getTryOnResultCount()
    }
    
    override suspend fun deleteOldResults(cutoffTime: Long): Int {
        return tryOnHistoryDao.deleteOldResults(cutoffTime)
    }
}