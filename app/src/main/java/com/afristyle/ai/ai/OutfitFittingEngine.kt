package com.afristyle.ai.ai

import android.graphics.Bitmap
import com.afristyle.ai.data.models.AfricanOutfit
import com.afristyle.ai.data.models.BodyAnalysis
import com.afristyle.ai.data.models.BodyType
import com.afristyle.ai.data.models.ClothingSize
import com.afristyle.ai.data.models.FaceLandmarks
import javax.inject.Inject
import javax.inject.Singleton

data class FittedOutfit(
    val outfit: AfricanOutfit,
    val adjustedSize: ClothingSize,
    val positionX: Float,
    val positionY: Float,
    val scaleX: Float,
    val scaleY: Float,
    val rotation: Float
)

interface OutfitFittingEngine {
    suspend fun analyzeBodyShape(userImage: Bitmap): BodyAnalysis
    suspend fun fitOutfitToBody(outfit: AfricanOutfit, bodyAnalysis: BodyAnalysis): FittedOutfit
    suspend fun adjustForPose(fittedOutfit: FittedOutfit, faceLandmarks: FaceLandmarks): FittedOutfit
}

@Singleton
class OutfitFittingEngineImpl @Inject constructor() : OutfitFittingEngine {
    
    override suspend fun analyzeBodyShape(userImage: Bitmap): BodyAnalysis {
        // Simplified body analysis - in a real implementation, this would use
        // more sophisticated computer vision techniques
        val width = userImage.width.toFloat()
        val height = userImage.height.toFloat()
        
        // Estimate shoulder width based on image proportions
        val shoulderWidth = width * 0.3f
        
        // Estimate torso length based on image height
        val torsoLength = height * 0.4f
        
        // Simple size estimation based on proportions
        val estimatedSize = when {
            shoulderWidth < width * 0.25f -> ClothingSize.S
            shoulderWidth < width * 0.35f -> ClothingSize.M
            shoulderWidth < width * 0.4f -> ClothingSize.L
            else -> ClothingSize.XL
        }
        
        // Simple body type classification
        val bodyType = when {
            shoulderWidth > torsoLength * 0.8f -> BodyType.INVERTED_TRIANGLE
            shoulderWidth < torsoLength * 0.6f -> BodyType.PEAR
            else -> BodyType.RECTANGLE
        }
        
        return BodyAnalysis(
            shoulderWidth = shoulderWidth,
            torsoLength = torsoLength,
            estimatedSize = estimatedSize,
            bodyType = bodyType
        )
    }
    
    override suspend fun fitOutfitToBody(outfit: AfricanOutfit, bodyAnalysis: BodyAnalysis): FittedOutfit {
        // Determine the best size from available sizes
        val adjustedSize = findBestSize(outfit.availableSizes, bodyAnalysis.estimatedSize)
        
        // Calculate scaling factors based on body analysis
        val scaleX = calculateHorizontalScale(bodyAnalysis)
        val scaleY = calculateVerticalScale(bodyAnalysis)
        
        // Position the outfit based on body type
        val (positionX, positionY) = calculatePosition(bodyAnalysis)
        
        return FittedOutfit(
            outfit = outfit,
            adjustedSize = adjustedSize,
            positionX = positionX,
            positionY = positionY,
            scaleX = scaleX,
            scaleY = scaleY,
            rotation = 0f
        )
    }
    
    override suspend fun adjustForPose(fittedOutfit: FittedOutfit, faceLandmarks: FaceLandmarks): FittedOutfit {
        // Calculate head tilt based on eye positions
        val eyeVector = faceLandmarks.rightEye.x - faceLandmarks.leftEye.x to 
                      faceLandmarks.rightEye.y - faceLandmarks.leftEye.y
        
        val headTilt = Math.atan2(eyeVector.second.toDouble(), eyeVector.first.toDouble())
            .toFloat() * (180f / Math.PI.toFloat())
        
        // Adjust outfit position based on face position
        val faceCenter = (faceLandmarks.leftEye.x + faceLandmarks.rightEye.x) / 2f
        val adjustedX = fittedOutfit.positionX + (faceCenter - 0.5f) * 0.1f
        
        return fittedOutfit.copy(
            positionX = adjustedX,
            rotation = headTilt * 0.3f // Reduce the rotation effect
        )
    }
    
    private fun findBestSize(availableSizes: List<ClothingSize>, estimatedSize: ClothingSize): ClothingSize {
        if (availableSizes.contains(estimatedSize)) {
            return estimatedSize
        }
        
        // Find the closest available size
        val sizeOrder = listOf(ClothingSize.XS, ClothingSize.S, ClothingSize.M, 
                              ClothingSize.L, ClothingSize.XL, ClothingSize.XXL, ClothingSize.XXXL)
        
        val estimatedIndex = sizeOrder.indexOf(estimatedSize)
        
        // Look for closest size
        for (i in 0 until sizeOrder.size) {
            val lowerIndex = estimatedIndex - i
            val upperIndex = estimatedIndex + i
            
            if (lowerIndex >= 0 && availableSizes.contains(sizeOrder[lowerIndex])) {
                return sizeOrder[lowerIndex]
            }
            if (upperIndex < sizeOrder.size && availableSizes.contains(sizeOrder[upperIndex])) {
                return sizeOrder[upperIndex]
            }
        }
        
        return availableSizes.firstOrNull() ?: ClothingSize.M
    }
    
    private fun calculateHorizontalScale(bodyAnalysis: BodyAnalysis): Float {
        return when (bodyAnalysis.bodyType) {
            BodyType.PEAR -> 0.9f
            BodyType.INVERTED_TRIANGLE -> 1.1f
            BodyType.HOURGLASS -> 1.0f
            BodyType.APPLE -> 1.05f
            BodyType.RECTANGLE -> 1.0f
        }
    }
    
    private fun calculateVerticalScale(bodyAnalysis: BodyAnalysis): Float {
        return when (bodyAnalysis.estimatedSize) {
            ClothingSize.XS -> 0.85f
            ClothingSize.S -> 0.9f
            ClothingSize.M -> 1.0f
            ClothingSize.L -> 1.1f
            ClothingSize.XL -> 1.2f
            ClothingSize.XXL -> 1.3f
            ClothingSize.XXXL -> 1.4f
        }
    }
    
    private fun calculatePosition(bodyAnalysis: BodyAnalysis): Pair<Float, Float> {
        val baseX = 0.5f // Center horizontally
        val baseY = when (bodyAnalysis.bodyType) {
            BodyType.PEAR -> 0.45f // Slightly higher
            BodyType.INVERTED_TRIANGLE -> 0.55f // Slightly lower
            else -> 0.5f // Center vertically
        }
        
        return baseX to baseY
    }
}