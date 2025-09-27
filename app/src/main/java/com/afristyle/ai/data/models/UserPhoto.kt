package com.afristyle.ai.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_photos")
data class UserPhoto(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val filePath: String,
    val timestamp: Long,
    val faceLandmarks: String, // JSON serialized FaceLandmarks
    val bodyAnalysis: String   // JSON serialized BodyAnalysis
)

data class FaceLandmarks(
    val leftEye: Point,
    val rightEye: Point,
    val nose: Point,
    val mouth: Point,
    val jawline: List<Point>,
    val faceContour: List<Point>
)

data class Point(
    val x: Float,
    val y: Float
)

data class BodyAnalysis(
    val shoulderWidth: Float,
    val torsoLength: Float,
    val estimatedSize: ClothingSize,
    val bodyType: BodyType
)

enum class BodyType {
    PEAR, APPLE, HOURGLASS, RECTANGLE, INVERTED_TRIANGLE
}