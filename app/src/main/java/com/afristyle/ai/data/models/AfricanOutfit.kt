package com.afristyle.ai.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "african_outfits")
@TypeConverters(StringListConverter::class)
data class AfricanOutfit(
    @PrimaryKey
    val id: Long,
    val name: String,
    val style: AfricanStyle,
    val description: String,
    val culturalContext: String,
    val colors: List<String>,
    val patterns: List<String>,
    val availableSizes: List<ClothingSize>,
    val imagePath: String,
    val overlayPath: String, // For try-on processing
    val priceRange: String
)

enum class AfricanStyle {
    ANKARA, KENTE, DASHIKI, KITENGE, BOUBOU, AGBADA, KAFTAN, HABESHA
}

enum class ClothingSize {
    XS, S, M, L, XL, XXL, XXXL
}

class StringListConverter {
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }
}

class ClothingSizeListConverter {
    @TypeConverter
    fun fromClothingSizeList(value: List<ClothingSize>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toClothingSizeList(value: String): List<ClothingSize> {
        val listType = object : TypeToken<List<ClothingSize>>() {}.type
        return Gson().fromJson(value, listType)
    }
}