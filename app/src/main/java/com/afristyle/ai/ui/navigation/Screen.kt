package com.afristyle.ai.ui.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(
    val route: String,
    val arguments: List<NamedNavArgument> = emptyList()
) {
    object Splash : Screen("splash")
    
    object Onboarding : Screen("onboarding")
    
    object Home : Screen("home")
    
    object Camera : Screen("camera")
    
    object Outfits : Screen("outfits")
    
    object TryOn : Screen(
        route = "try_on?userPhotoId={userPhotoId}&outfitId={outfitId}&resultId={resultId}",
        arguments = listOf(
            navArgument("userPhotoId") {
                type = NavType.LongType
                defaultValue = 0L
            },
            navArgument("outfitId") {
                type = NavType.LongType
                defaultValue = 0L
            },
            navArgument("resultId") {
                type = NavType.LongType
                defaultValue = 0L
            }
        )
    ) {
        fun createRoute(
            userPhotoId: Long = 0L,
            outfitId: Long = 0L,
            resultId: Long = 0L
        ): String {
            return "try_on?userPhotoId=$userPhotoId&outfitId=$outfitId&resultId=$resultId"
        }
    }
    
    object History : Screen("history")
    
    object Settings : Screen("settings")
}