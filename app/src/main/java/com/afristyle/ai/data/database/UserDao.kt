package com.afristyle.ai.data.database

import androidx.room.*
import com.afristyle.ai.data.models.UserPhoto
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    
    @Query("SELECT * FROM user_photos ORDER BY timestamp DESC")
    fun getAllPhotos(): Flow<List<UserPhoto>>
    
    @Query("SELECT * FROM user_photos WHERE id = :photoId")
    suspend fun getPhotoById(photoId: Long): UserPhoto?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: UserPhoto): Long
    
    @Update
    suspend fun updatePhoto(photo: UserPhoto)
    
    @Delete
    suspend fun deletePhoto(photo: UserPhoto)
    
    @Query("DELETE FROM user_photos WHERE id = :photoId")
    suspend fun deletePhotoById(photoId: Long)
    
    @Query("DELETE FROM user_photos")
    suspend fun deleteAllPhotos()
    
    @Query("SELECT COUNT(*) FROM user_photos")
    suspend fun getPhotoCount(): Int
}