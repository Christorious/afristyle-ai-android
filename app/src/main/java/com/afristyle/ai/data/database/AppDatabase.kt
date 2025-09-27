package com.afristyle.ai.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.afristyle.ai.data.models.AfricanOutfit
import com.afristyle.ai.data.models.ClothingSizeListConverter
import com.afristyle.ai.data.models.StringListConverter
import com.afristyle.ai.data.models.TryOnResult
import com.afristyle.ai.data.models.UserPhoto

@Database(
    entities = [UserPhoto::class, AfricanOutfit::class, TryOnResult::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(StringListConverter::class, ClothingSizeListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun userDao(): UserDao
    abstract fun outfitDao(): OutfitDao
    abstract fun tryOnHistoryDao(): TryOnHistoryDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "afristyle_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}