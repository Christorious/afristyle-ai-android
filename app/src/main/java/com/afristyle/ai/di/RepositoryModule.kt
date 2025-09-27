package com.afristyle.ai.di

import com.afristyle.ai.data.repository.OutfitRepository
import com.afristyle.ai.data.repository.OutfitRepositoryImpl
import com.afristyle.ai.data.repository.TryOnHistoryRepository
import com.afristyle.ai.data.repository.TryOnHistoryRepositoryImpl
import com.afristyle.ai.data.repository.UserRepository
import com.afristyle.ai.data.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository
    
    @Binds
    @Singleton
    abstract fun bindOutfitRepository(
        outfitRepositoryImpl: OutfitRepositoryImpl
    ): OutfitRepository
    
    @Binds
    @Singleton
    abstract fun bindTryOnHistoryRepository(
        tryOnHistoryRepositoryImpl: TryOnHistoryRepositoryImpl
    ): TryOnHistoryRepository
}