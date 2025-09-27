package com.afristyle.ai.ui.screens.tryon

import android.content.Context
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afristyle.ai.ai.ProcessingStage
import com.afristyle.ai.ai.TryOnResult
import com.afristyle.ai.ai.VirtualTryOnProcessor
import com.afristyle.ai.data.models.TryOnResult as DataTryOnResult
import com.afristyle.ai.data.repository.OutfitRepository
import com.afristyle.ai.data.repository.TryOnHistoryRepository
import com.afristyle.ai.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ProcessingState {
    IDLE, PROCESSING, COMPLETE, ERROR
}

data class VirtualTryOnUiState(
    val processingState: ProcessingState = ProcessingState.IDLE,
    val userPhotoPath: String? = null,
    val outfitName: String? = null,
    val resultImagePath: String? = null,
    val progress: Int = 0,
    val currentStage: ProcessingStage? = null,
    val progressMessage: String = "",
    val processingTime: Long = 0,
    val error: String? = null,
    val isSaved: Boolean = false
)

@HiltViewModel
class VirtualTryOnViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userRepository: UserRepository,
    private val outfitRepository: OutfitRepository,
    private val tryOnHistoryRepository: TryOnHistoryRepository,
    private val virtualTryOnProcessor: VirtualTryOnProcessor
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(VirtualTryOnUiState())
    val uiState: StateFlow<VirtualTryOnUiState> = _uiState.asStateFlow()
    
    private var currentUserPhotoId: Long = 0
    private var currentOutfitId: Long = 0
    
    fun startTryOn(userPhotoId: Long, outfitId: Long) {
        currentUserPhotoId = userPhotoId
        currentOutfitId = outfitId
        
        viewModelScope.launch {
            try {
                // Load user photo and outfit data
                val userPhoto = userRepository.getPhotoById(userPhotoId)
                val outfit = outfitRepository.getOutfitById(outfitId)
                
                if (userPhoto == null || outfit == null) {
                    _uiState.value = _uiState.value.copy(
                        processingState = ProcessingState.ERROR,
                        error = "Failed to load user photo or outfit data"
                    )
                    return@launch
                }
                
                // Update UI with loaded data
                _uiState.value = _uiState.value.copy(
                    userPhotoPath = userPhoto.filePath,
                    outfitName = outfit.name
                )
                
                // Load user image bitmap
                val userBitmap = BitmapFactory.decodeFile(userPhoto.filePath)
                if (userBitmap == null) {
                    _uiState.value = _uiState.value.copy(
                        processingState = ProcessingState.ERROR,
                        error = "Failed to load user image"
                    )
                    return@launch
                }
                
                // Start AI processing
                _uiState.value = _uiState.value.copy(
                    processingState = ProcessingState.PROCESSING,
                    progress = 0,
                    error = null
                )
                
                virtualTryOnProcessor.processTryOn(userBitmap, outfit).collect { result ->
                    when (result) {
                        is TryOnResult.Loading -> {
                            _uiState.value = _uiState.value.copy(
                                processingState = ProcessingState.PROCESSING,
                                progress = 0,
                                progressMessage = "Initializing..."
                            )
                        }
                        
                        is TryOnResult.Progress -> {
                            _uiState.value = _uiState.value.copy(
                                processingState = ProcessingState.PROCESSING,
                                progress = result.progress.progress,
                                currentStage = result.progress.stage,
                                progressMessage = result.progress.message
                            )
                        }
                        
                        is TryOnResult.Success -> {
                            // Save result bitmap to file
                            val resultFile = saveResultBitmap(result.resultBitmap)
                            
                            _uiState.value = _uiState.value.copy(
                                processingState = ProcessingState.COMPLETE,
                                resultImagePath = resultFile?.absolutePath,
                                processingTime = result.metrics.totalProcessingTime,
                                progress = 100,
                                progressMessage = "Complete!"
                            )
                        }
                        
                        is TryOnResult.Error -> {
                            _uiState.value = _uiState.value.copy(
                                processingState = ProcessingState.ERROR,
                                error = result.exception.message ?: "Processing failed"
                            )
                        }
                    }
                }
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    processingState = ProcessingState.ERROR,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }
    
    fun saveResult() {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                if (currentState.resultImagePath != null && !currentState.isSaved) {
                    val tryOnResult = DataTryOnResult(
                        userPhotoId = currentUserPhotoId,
                        outfitId = currentOutfitId,
                        resultImagePath = currentState.resultImagePath,
                        processingTime = currentState.processingTime,
                        timestamp = System.currentTimeMillis()
                    )
                    
                    tryOnHistoryRepository.saveTryOnResult(tryOnResult)
                    
                    _uiState.value = _uiState.value.copy(isSaved = true)
                }
            } catch (e: Exception) {
                // Handle save error
            }
        }
    }
    
    fun shareResult() {
        // Implement sharing functionality
        // This would typically use Android's sharing intent
    }
    
    fun retryTryOn() {
        _uiState.value = _uiState.value.copy(
            processingState = ProcessingState.IDLE,
            progress = 0,
            error = null,
            resultImagePath = null,
            isSaved = false
        )
    }
    
    private fun saveResultBitmap(bitmap: android.graphics.Bitmap): java.io.File? {
        return try {
            val outputDirectory = java.io.File(context.filesDir, "results")
            if (!outputDirectory.exists()) {
                outputDirectory.mkdirs()
            }
            
            val resultFile = java.io.File(
                outputDirectory,
                "result_${System.currentTimeMillis()}.jpg"
            )
            
            val outputStream = java.io.FileOutputStream(resultFile)
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.close()
            
            resultFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}