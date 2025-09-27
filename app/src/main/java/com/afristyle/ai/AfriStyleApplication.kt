package com.afristyle.ai

import android.app.Application
import com.afristyle.ai.data.DataInitializer
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class AfriStyleApplication : Application() {
    
    @Inject
    lateinit var dataInitializer: DataInitializer
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize data in background
        applicationScope.launch {
            dataInitializer.initializeData()
        }
        
        // Initialize OpenCV
        initializeOpenCV()
        
        // Initialize TensorFlow Lite
        initializeTensorFlowLite()
    }
    
    private fun initializeOpenCV() {
        try {
            // OpenCV initialization will be handled in the AI processing classes
            // to avoid blocking the main thread during app startup
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun initializeTensorFlowLite() {
        try {
            // TensorFlow Lite initialization will be handled lazily
            // when first needed to improve startup performance
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}