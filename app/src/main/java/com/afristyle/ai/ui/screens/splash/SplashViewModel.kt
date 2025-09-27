package com.afristyle.ai.ui.screens.splash

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    private val sharedPreferences = context.getSharedPreferences("afristyle_prefs", Context.MODE_PRIVATE)
    
    fun isFirstLaunch(): Boolean {
        val isFirstLaunch = sharedPreferences.getBoolean("is_first_launch", true)
        if (isFirstLaunch) {
            sharedPreferences.edit().putBoolean("is_first_launch", false).apply()
        }
        return isFirstLaunch
    }
}