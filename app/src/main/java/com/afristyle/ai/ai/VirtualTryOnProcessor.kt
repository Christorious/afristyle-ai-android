package com.afristyle.ai.ai

import android.graphics.Bitmap
import com.afristyle.ai.data.models.AfricanOutfit
import com.afristyle.ai.data.models.ProcessingMetrics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

data class TryOnProgress(
    val stage: ProcessingStage,
    val progress: Int,
    val message: String
)

enum class ProcessingStage {
    FACE_DETECTION,
    BODY_ANALYSIS,
    OUTFIT_FITTING,
    IMAGE_BLENDING,
    LIGHTING_ADJUSTMENT,
    FINALIZATION,
    COMPLETE
}

sealed class TryOnResult {
    object Loading : TryOnResult()
    data class Progress(val progress: TryOnProgress) : TryOnResult()
    data class Success(val resultBitmap: Bitmap, val metrics: ProcessingMetrics) : TryOnResult()
    data class Error(val exception: Exception, val stage: ProcessingStage) : TryOnResult()
}

@Singleton
class VirtualTryOnProcessor @Inject constructor(
    private val faceDetectionProcessor: FaceDetectionProcessor,
    private val outfitFittingEngine: OutfitFittingEngine,
    private val imageBlendingProcessor: ImageBlendingProcessor
) {
    
    suspend fun processTryOn(userImage: Bitmap, outfit: AfricanOutfit): Flow<TryOnResult> = flow {
        val startTime = System.currentTimeMillis()
        var faceDetectionTime = 0L
        var outfitFittingTime = 0L
        var imageBlendingTime = 0L
        
        try {
            emit(TryOnResult.Loading)
            
            // Stage 1: Face Detection
            emit(TryOnResult.Progress(TryOnProgress(
                ProcessingStage.FACE_DETECTION, 10, "Detecting face..."
            )))
            
            val faceDetectionStart = System.currentTimeMillis()
            val faces = faceDetectionProcessor.detectFaces(userImage)
            faceDetectionTime = System.currentTimeMillis() - faceDetectionStart
            
            if (faces.isEmpty()) {
                emit(TryOnResult.Error(
                    Exception("No face detected in the image"),
                    ProcessingStage.FACE_DETECTION
                ))
                return@flow
            }
            
            if (faces.size > 1) {
                emit(TryOnResult.Error(
                    Exception("Multiple faces detected. Please use an image with only one person."),
                    ProcessingStage.FACE_DETECTION
                ))
                return@flow
            }
            
            val face = faces.first()
            if (!faceDetectionProcessor.isValidFaceForTryOn(face)) {
                emit(TryOnResult.Error(
                    Exception("Face is not suitable for try-on. Please use a clearer, more frontal image."),
                    ProcessingStage.FACE_DETECTION
                ))
                return@flow
            }
            
            // Stage 2: Body Analysis
            emit(TryOnResult.Progress(TryOnProgress(
                ProcessingStage.BODY_ANALYSIS, 30, "Analyzing body shape..."
            )))
            
            val bodyAnalysis = outfitFittingEngine.analyzeBodyShape(userImage)
            
            // Stage 3: Extract Face Landmarks
            emit(TryOnResult.Progress(TryOnProgress(
                ProcessingStage.OUTFIT_FITTING, 50, "Fitting outfit..."
            )))
            
            val outfitFittingStart = System.currentTimeMillis()
            val faceLandmarks = faceDetectionProcessor.extractFaceLandmarks(face)
            
            // Stage 4: Fit Outfit to Body
            val fittedOutfit = outfitFittingEngine.fitOutfitToBody(outfit, bodyAnalysis)
            val adjustedOutfit = outfitFittingEngine.adjustForPose(fittedOutfit, faceLandmarks)
            outfitFittingTime = System.currentTimeMillis() - outfitFittingStart
            
            // Stage 5: Image Blending
            emit(TryOnResult.Progress(TryOnProgress(
                ProcessingStage.IMAGE_BLENDING, 70, "Blending outfit with image..."
            )))
            
            val imageBlendingStart = System.currentTimeMillis()
            val blendedImage = imageBlendingProcessor.blendOutfitWithUser(userImage, adjustedOutfit)
            
            // Stage 6: Lighting Adjustment
            emit(TryOnResult.Progress(TryOnProgress(
                ProcessingStage.LIGHTING_ADJUSTMENT, 85, "Adjusting lighting and shadows..."
            )))
            
            val originalLighting = imageBlendingProcessor.analyzeLighting(userImage)
            val lightingAdjustedImage = imageBlendingProcessor.adjustLightingAndShadows(blendedImage, originalLighting)
            
            // Stage 7: Final Enhancement
            emit(TryOnResult.Progress(TryOnProgress(
                ProcessingStage.FINALIZATION, 95, "Enhancing realism..."
            )))
            
            val finalImage = imageBlendingProcessor.enhanceRealism(lightingAdjustedImage)
            imageBlendingTime = System.currentTimeMillis() - imageBlendingStart
            
            // Complete
            emit(TryOnResult.Progress(TryOnProgress(
                ProcessingStage.COMPLETE, 100, "Try-on complete!"
            )))
            
            val totalTime = System.currentTimeMillis() - startTime
            val metrics = ProcessingMetrics(
                faceDetectionTime = faceDetectionTime,
                outfitFittingTime = outfitFittingTime,
                imageBlendingTime = imageBlendingTime,
                totalProcessingTime = totalTime,
                memoryUsage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
            )
            
            emit(TryOnResult.Success(finalImage, metrics))
            
        } catch (e: Exception) {
            emit(TryOnResult.Error(e, ProcessingStage.FACE_DETECTION))
        }
    }
}
        