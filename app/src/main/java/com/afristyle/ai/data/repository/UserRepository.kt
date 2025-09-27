package com.afristyle.ai.data.repository

import com.afristyle.ai.data.database.UserDao
import com.afristyle.ai.data.models.UserPhoto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface UserRepository {
    fun getAllPhotos(): Flow<List<UserPhoto>>
    suspend fun getPhotoById(photoId: Long): UserPhoto?
    suspend fun saveUserPhoto(photo: UserPhoto): Long
    suspend fun updateUserPhoto(photo: UserPhoto)
    suspend fun deleteUserPhoto(photoId: Long)
    suspend fun deleteAllPhotos()
    suspend fun getPhotoCount(): Int
}

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {
    
    override fun getAllPhotos(): Flow<List<UserPhoto>> {
        return userDao.getAllPhotos()
    }
    
    override suspend fun getPhotoById(photoId: Long): UserPhoto? {
        return userDao.getPhotoById(photoId)
    }
    
    override suspend fun saveUserPhoto(photo: UserPhoto): Long {
        return userDao.insertPhoto(photo)
    }
    
    override suspend fun updateUserPhoto(photo: UserPhoto) {
        userDao.updatePhoto(photo)
    }
    
    override suspend fun deleteUserPhoto(photoId: Long) {
        userDao.deletePhotoById(photoId)
    }
    
    override suspend fun deleteAllPhotos() {
        userDao.deleteAllPhotos()
    }
    
    override suspend fun getPhotoCount(): Int {
        return userDao.getPhotoCount()
    }
}