package com.afristyle.ai.ui.screens.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afristyle.ai.ai.FaceDetectionProcessor
import com.afristyle.ai.data.models.BodyAnalysis
import com.afristyle.ai.data.models.BodyType
import com.afristyle.ai.data.models.ClothingSize
import com.afristyle.ai.data.models.FaceLandmarks
import com.afristyle.ai.data.models.UserPhoto
import com.afristyle.ai.data.repository.UserRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject

data class PhotoCaptureUiState(
    val isProcessing: Boolean = false,
    val faceDetected: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class PhotoCaptureViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userRepository: UserRepository,
    private val faceDetectionProcessor: FaceDetectionProcessor
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(PhotoCaptureUiState())
    val uiState: StateFlow<PhotoCaptureUiState> = _uiState.asStateFlow()
    
    private val gson = Gson()
    
    fun processCapturedImage(imageFile: File, onSuccess: (Long) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isProcessing = true, error = null)
            
            try {
                val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                if (bitmap == null) {
                    _uiState.value = _uiState.value.copy(
                        isProcessing = false,
                        error = "Failed to load image"
                    )
                    return@launch
                }
                
                // Detect faces
                val faces = faceDetectionProcessor.detectFaces(bitmap)
                
                when {
                    faces.isEmpty() -> {
                        _uiState.value = _uiState.value.copy(
                            isProcessing = false,
                            error = "No face detected. Please try again with a clearer photo."
                        )
                        return@launch
                    }
                    
                    faces.size > 1 -> {
                        _uiState.value = _uiState.value.copy(
                            isProcessing = false,
                            error = "Multiple faces detected. Please use a photo with only one person."
                        )
                        return@launch
                    }
                    
                    !faceDetectionProcessor.isValidFaceForTryOn(faces.first()) -> {
                        _uiState.value = _uiState.value.copy(
                            isProcessing = false,
                            error = "Face is not suitable for try-on. Please use a clearer, more frontal image."
                        )
                        return@launch
                    }
                }
                
                // Extract face landmarks
                val face = faces.first()
                val faceLandmarks = faceDetectionProcessor.extractFaceLandmarks(face)
                
                // Simple body analysis (in a real app, this would be more sophisticated)
                val bodyAnalysis = BodyAnalysis(
                    shoulderWidth = bitmap.width * 0.3f,
                    torsoLength = bitmap.height * 0.4f,
                    estimatedSize = ClothingSize.M,
                    bodyType = BodyType.RECTANGLE
                )
                
                // Save user photo to database
                val userPhoto = UserPhoto(
                    filePath = imageFile.absolutePath,
                    timestamp = System.currentTimeMillis(),
                    faceLandmarks = gson.toJson(faceLandmarks),
                    bodyAnalysis = gson.toJson(bodyAnalysis)
                )
                
                val userPhotoId = userRepository.saveUserPhoto(userPhoto)
                
                _uiState.value = _uiState.value.copy(isProcessing = false)
                onSuccess(userPhotoId)
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isProcessing = false,
                    error = "Failed to process image: ${e.message}"
                )
            }
        }
    }
    
    fun handleGalleryImage(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isProcessing = true, error = null)
            
            try {
                // Copy image from gallery to app's internal storage
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                if (inputStream == null) {
                    _uiState.value = _uiState.value.copy(
                        isProcessing = false,
                        error = "Failed to access selected image"
                    )
                    return@launch
                }
                
                val outputDirectory = File(context.filesDir, "images")
                if (!outputDirectory.exists()) {
                    outputDirectory.mkdirs()
                }
                
                val outputFile = File(outputDirectory, "gallery_${System.currentTimeMillis()}.jpg")
                val outputStream = FileOutputStream(outputFile)
                
                inputStream.copyTo(outputStream)
                inputStream.close()
                outputStream.close()
                
                // Process the copied image
                processCapturedImage(outputFile) { userPhotoId ->
                    // Handle success - this would typically navigate to try-on screen
                }
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isProcessing = false,
                    error = "Failed to process gallery image: ${e.message}"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}