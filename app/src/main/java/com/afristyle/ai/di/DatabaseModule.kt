package com.afristyle.ai.di

import android.content.Context
import androidx.room.Room
import com.afristyle.ai.data.database.AppDatabase
import com.afristyle.ai.data.database.OutfitDao
import com.afristyle.ai.data.database.TryOnHistoryDao
import com.afristyle.ai.data.database.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "afristyle_database"
        )
        .fallbackToDestructiveMigration()
        .build()
    }
    
    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }
    
    @Provides
    fun provideOutfitDao(database: AppDatabase): OutfitDao {
        return database.outfitDao()
    }
    
    @Provides
    fun provideTryOnHistoryDao(database: AppDatabase): TryOnHistoryDao {
        return database.tryOnHistoryDao()
    }
}