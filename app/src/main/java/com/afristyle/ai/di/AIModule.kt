package com.afristyle.ai.di

import com.afristyle.ai.ai.FaceDetectionProcessor
import com.afristyle.ai.ai.FaceDetectionProcessorImpl
import com.afristyle.ai.ai.ImageBlendingProcessor
import com.afristyle.ai.ai.ImageBlendingProcessorImpl
import com.afristyle.ai.ai.OutfitFittingEngine
import com.afristyle.ai.ai.OutfitFittingEngineImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AIModule {
    
    @Binds
    @Singleton
    abstract fun bindFaceDetectionProcessor(
        faceDetectionProcessorImpl: FaceDetectionProcessorImpl
    ): FaceDetectionProcessor
    
    @Binds
    @Singleton
    abstract fun bindOutfitFittingEngine(
        outfitFittingEngineImpl: OutfitFittingEngineImpl
    ): OutfitFittingEngine
    
    @Binds
    @Singleton
    abstract fun bindImageBlendingProcessor(
        imageBlendingProcessorImpl: ImageBlendingProcessorImpl
    ): ImageBlendingProcessor
}