package com.afristyle.ai.data

import android.content.Context
import com.afristyle.ai.R
import com.afristyle.ai.data.models.AfricanOutfit
import com.afristyle.ai.data.repository.OutfitRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataInitializer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val outfitRepository: OutfitRepository
) {
    
    private val gson = Gson()
    
    suspend fun initializeData() {
        withContext(Dispatchers.IO) {
            try {
                // Check if data is already initialized
                val existingCount = outfitRepository.getOutfitCount()
                if (existingCount > 0) {
                    return@withContext // Data already exists
                }
                
                // Load African fashion catalog from JSON
                val jsonString = context.resources.openRawResource(R.raw.african_fashion_catalog)
                    .bufferedReader()
                    .use { it.readText() }
                
                val listType = object : TypeToken<List<AfricanOutfit>>() {}.type
                val outfits: List<AfricanOutfit> = gson.fromJson(jsonString, listType)
                
                // Initialize the repository with outfit data
                outfitRepository.initializeOutfits(outfits)
                
            } catch (e: Exception) {
                e.printStackTrace()
                // Log error but don't crash the app
            }
        }
    }
}