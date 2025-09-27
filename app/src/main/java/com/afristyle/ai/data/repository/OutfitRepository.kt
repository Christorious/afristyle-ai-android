package com.afristyle.ai.data.repository

import com.afristyle.ai.data.database.OutfitDao
import com.afristyle.ai.data.models.AfricanOutfit
import com.afristyle.ai.data.models.AfricanStyle
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface OutfitRepository {
    fun getAllOutfits(): Flow<List<AfricanOutfit>>
    fun getOutfitsByStyle(style: AfricanStyle): Flow<List<AfricanOutfit>>
    suspend fun getOutfitById(outfitId: Long): AfricanOutfit?
    fun searchOutfits(query: String): Flow<List<AfricanOutfit>>
    suspend fun initializeOutfits(outfits: List<AfricanOutfit>)
    suspend fun getOutfitCount(): Int
    suspend fun getAllStyles(): List<AfricanStyle>
}

@Singleton
class OutfitRepositoryImpl @Inject constructor(
    private val outfitDao: OutfitDao
) : OutfitRepository {
    
    override fun getAllOutfits(): Flow<List<AfricanOutfit>> {
        return outfitDao.getAllOutfits()
    }
    
    override fun getOutfitsByStyle(style: AfricanStyle): Flow<List<AfricanOutfit>> {
        return outfitDao.getOutfitsByStyle(style)
    }
    
    override suspend fun getOutfitById(outfitId: Long): AfricanOutfit? {
        return outfitDao.getOutfitById(outfitId)
    }
    
    override fun searchOutfits(query: String): Flow<List<AfricanOutfit>> {
        return outfitDao.searchOutfits(query)
    }
    
    override suspend fun initializeOutfits(outfits: List<AfricanOutfit>) {
        val currentCount = outfitDao.getOutfitCount()
        if (currentCount == 0) {
            outfitDao.insertOutfits(outfits)
        }
    }
    
    override suspend fun getOutfitCount(): Int {
        return outfitDao.getOutfitCount()
    }
    
    override suspend fun getAllStyles(): List<AfricanStyle> {
        return outfitDao.getAllStyles()
    }
}