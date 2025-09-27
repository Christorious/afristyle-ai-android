package com.afristyle.ai.data.database

import androidx.room.*
import com.afristyle.ai.data.models.AfricanOutfit
import com.afristyle.ai.data.models.AfricanStyle
import kotlinx.coroutines.flow.Flow

@Dao
interface OutfitDao {
    
    @Query("SELECT * FROM african_outfits ORDER BY name ASC")
    fun getAllOutfits(): Flow<List<AfricanOutfit>>
    
    @Query("SELECT * FROM african_outfits WHERE style = :style ORDER BY name ASC")
    fun getOutfitsByStyle(style: AfricanStyle): Flow<List<AfricanOutfit>>
    
    @Query("SELECT * FROM african_outfits WHERE id = :outfitId")
    suspend fun getOutfitById(outfitId: Long): AfricanOutfit?
    
    @Query("SELECT * FROM african_outfits WHERE name LIKE '%' || :searchQuery || '%' OR description LIKE '%' || :searchQuery || '%'")
    fun searchOutfits(searchQuery: String): Flow<List<AfricanOutfit>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutfit(outfit: AfricanOutfit)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutfits(outfits: List<AfricanOutfit>)
    
    @Update
    suspend fun updateOutfit(outfit: AfricanOutfit)
    
    @Delete
    suspend fun deleteOutfit(outfit: AfricanOutfit)
    
    @Query("DELETE FROM african_outfits")
    suspend fun deleteAllOutfits()
    
    @Query("SELECT COUNT(*) FROM african_outfits")
    suspend fun getOutfitCount(): Int
    
    @Query("SELECT DISTINCT style FROM african_outfits")
    suspend fun getAllStyles(): List<AfricanStyle>
}