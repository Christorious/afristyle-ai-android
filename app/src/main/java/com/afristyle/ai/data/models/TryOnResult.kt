package com.afristyle.ai.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tryon_results")
data class TryOnResult(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userPhotoId: Long,
    val outfitId: Long,
    val resultImagePath: String,
    val processingTime: Long,
    val timestamp: Long,
    val userRating: Int? = null,
    val notes: String? = null
)

data class ProcessingMetrics(
    val faceDetectionTime: Long,
    val outfitFittingTime: Long,
    val imageBlendingTime: Long,
    val totalProcessingTime: Long,
    val memoryUsage: Long
)

data class AppSettings(
    val imageQuality: ImageQuality,
    val processingSpeed: ProcessingSpeed,
    val autoSaveResults: Boolean,
    val culturalInfoEnabled: Boolean,
    val maxStorageSize: Long
)

enum class ImageQuality { LOW, MEDIUM, HIGH, ULTRA }
enum class ProcessingSpeed { FAST, BALANCED, QUALITY }