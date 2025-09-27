package com.afristyle.ai.ui.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afristyle.ai.data.models.AppSettings
import com.afristyle.ai.data.models.ImageQuality
import com.afristyle.ai.data.models.ProcessingSpeed
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class SettingsUiState(
    val settings: AppSettings = AppSettings(
        imageQuality = ImageQuality.HIGH,
        processingSpeed = ProcessingSpeed.BALANCED,
        autoSaveResults = true,
        culturalInfoEnabled = true,
        maxStorageSize = 500L // 500 MB
    ),
    val storageUsedMB: Long = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    private val sharedPreferences = context.getSharedPreferences("afristyle_settings", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    init {
        loadSettings()
        calculateStorageUsage()
    }
    
    private fun loadSettings() {
        try {
            val settingsJson = sharedPreferences.getString("app_settings", null)
            val settings = if (settingsJson != null) {
                gson.fromJson(settingsJson, AppSettings::class.java)
            } else {
                // Default settings
                AppSettings(
                    imageQuality = ImageQuality.HIGH,
                    processingSpeed = ProcessingSpeed.BALANCED,
                    autoSaveResults = true,
                    culturalInfoEnabled = true,
                    maxStorageSize = 500L
                )
            }
            
            _uiState.value = _uiState.value.copy(settings = settings)
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                error = "Failed to load settings: ${e.message}"
            )
        }
    }
    
    private fun saveSettings(settings: AppSettings) {
        try {
            val settingsJson = gson.toJson(settings)
            sharedPreferences.edit()
                .putString("app_settings", settingsJson)
                .apply()
            
            _uiState.value = _uiState.value.copy(settings = settings)
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                error = "Failed to save settings: ${e.message}"
            )
        }
    }
    
    private fun calculateStorageUsage() {
        viewModelScope.launch {
            try {
                val imagesDir = File(context.filesDir, "images")
                val resultsDir = File(context.filesDir, "results")
                
                val imagesSize = if (imagesDir.exists()) {
                    imagesDir.walkTopDown().filter { it.isFile }.map { it.length() }.sum()
                } else 0L
                
                val resultsSize = if (resultsDir.exists()) {
                    resultsDir.walkTopDown().filter { it.isFile }.map { it.length() }.sum()
                } else 0L
                
                val totalSizeMB = (imagesSize + resultsSize) / (1024 * 1024)
                
                _uiState.value = _uiState.value.copy(storageUsedMB = totalSizeMB)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to calculate storage usage: ${e.message}"
                )
            }
        }
    }
    
    fun updateImageQuality(quality: ImageQuality) {
        val updatedSettings = _uiState.value.settings.copy(imageQuality = quality)
        saveSettings(updatedSettings)
    }
    
    fun updateProcessingSpeed(speed: ProcessingSpeed) {
        val updatedSettings = _uiState.value.settings.copy(processingSpeed = speed)
        saveSettings(updatedSettings)
    }
    
    fun updateAutoSaveResults(autoSave: Boolean) {
        val updatedSettings = _uiState.value.settings.copy(autoSaveResults = autoSave)
        saveSettings(updatedSettings)
    }
    
    fun updateCulturalInfoEnabled(enabled: Boolean) {
        val updatedSettings = _uiState.value.settings.copy(culturalInfoEnabled = enabled)
        saveSettings(updatedSettings)
    }
    
    fun cleanupOldFiles() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                val cutoffTime = System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000L) // 30 days ago
                
                // Clean up old result files
                val resultsDir = File(context.filesDir, "results")
                if (resultsDir.exists()) {
                    resultsDir.listFiles()?.forEach { file ->
                        if (file.lastModified() < cutoffTime) {
                            file.delete()
                        }
                    }
                }
                
                // Clean up old image files (be more careful with user photos)
                val imagesDir = File(context.filesDir, "images")
                if (imagesDir.exists()) {
                    imagesDir.listFiles()?.forEach { file ->
                        if (file.lastModified() < cutoffTime && file.name.startsWith("gallery_")) {
                            // Only delete gallery images, not user-captured photos
                            file.delete()
                        }
                    }
                }
                
                // Recalculate storage usage
                calculateStorageUsage()
                
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to cleanup files: ${e.message}"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}