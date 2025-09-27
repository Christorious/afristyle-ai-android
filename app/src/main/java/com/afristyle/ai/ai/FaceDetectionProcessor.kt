package com.afristyle.ai.ai

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.afristyle.ai.data.models.FaceLandmarks
import com.afristyle.ai.data.models.Point
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

interface FaceDetectionProcessor {
    suspend fun detectFaces(bitmap: Bitmap): List<Face>
    suspend fun extractFaceLandmarks(face: Face): FaceLandmarks
    fun isValidFaceForTryOn(face: Face): Boolean
}

@Singleton
class FaceDetectionProcessorImpl @Inject constructor() : FaceDetectionProcessor {
    
    private val detector by lazy {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .setMinFaceSize(0.15f)
            .enableTracking()
            .build()
        
        FaceDetection.getClient(options)
    }
    
    override suspend fun detectFaces(bitmap: Bitmap): List<Face> {
        return try {
            val image = InputImage.fromBitmap(bitmap, 0)
            detector.process(image).await()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    override suspend fun extractFaceLandmarks(face: Face): FaceLandmarks {
        val landmarks = face.allLandmarks
        
        // Extract key landmarks
        val leftEye = landmarks.find { it.landmarkType == com.google.mlkit.vision.face.FaceLandmark.LEFT_EYE }
            ?.position?.let { Point(it.x, it.y) } ?: Point(0f, 0f)
        
        val rightEye = landmarks.find { it.landmarkType == com.google.mlkit.vision.face.FaceLandmark.RIGHT_EYE }
            ?.position?.let { Point(it.x, it.y) } ?: Point(0f, 0f)
        
        val nose = landmarks.find { it.landmarkType == com.google.mlkit.vision.face.FaceLandmark.NOSE_BASE }
            ?.position?.let { Point(it.x, it.y) } ?: Point(0f, 0f)
        
        val mouth = landmarks.find { it.landmarkType == com.google.mlkit.vision.face.FaceLandmark.MOUTH_BOTTOM }
            ?.position?.let { Point(it.x, it.y) } ?: Point(0f, 0f)
        
        // Create approximate jawline and face contour from available landmarks
        val jawline = landmarks.mapNotNull { landmark ->
            when (landmark.landmarkType) {
                com.google.mlkit.vision.face.FaceLandmark.LEFT_CHEEK,
                com.google.mlkit.vision.face.FaceLandmark.RIGHT_CHEEK -> {
                    Point(landmark.position.x, landmark.position.y)
                }
                else -> null
            }
        }
        
        val faceContour = landmarks.map { landmark ->
            Point(landmark.position.x, landmark.position.y)
        }
        
        return FaceLandmarks(
            leftEye = leftEye,
            rightEye = rightEye,
            nose = nose,
            mouth = mouth,
            jawline = jawline,
            faceContour = faceContour
        )
    }
    
    override fun isValidFaceForTryOn(face: Face): Boolean {
        // Check if face is large enough
        val boundingBox = face.boundingBox
        val faceArea = boundingBox.width() * boundingBox.height()
        val minFaceArea = 10000 // Minimum face area in pixels
        
        if (faceArea < minFaceArea) return false
        
        // Check if face is reasonably frontal
        val rotY = face.headEulerAngleY
        val rotZ = face.headEulerAngleZ
        
        if (Math.abs(rotY) > 30 || Math.abs(rotZ) > 30) return false
        
        // Check if key landmarks are detected
        val hasEyes = face.allLandmarks.any { 
            it.landmarkType == com.google.mlkit.vision.face.FaceLandmark.LEFT_EYE 
        } && face.allLandmarks.any { 
            it.landmarkType == com.google.mlkit.vision.face.FaceLandmark.RIGHT_EYE 
        }
        
        val hasNose = face.allLandmarks.any { 
            it.landmarkType == com.google.mlkit.vision.face.FaceLandmark.NOSE_BASE 
        }
        
        val hasMouth = face.allLandmarks.any { 
            it.landmarkType == com.google.mlkit.vision.face.FaceLandmark.MOUTH_BOTTOM 
        }
        
        return hasEyes && hasNose && hasMouth
    }
}