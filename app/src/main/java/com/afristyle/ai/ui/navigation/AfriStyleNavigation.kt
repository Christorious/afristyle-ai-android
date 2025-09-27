package com.afristyle.ai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.afristyle.ai.ui.screens.camera.PhotoCaptureScreen
import com.afristyle.ai.ui.screens.history.HistoryScreen
import com.afristyle.ai.ui.screens.home.HomeScreen
import com.afristyle.ai.ui.screens.onboarding.OnboardingScreen
import com.afristyle.ai.ui.screens.outfits.OutfitBrowserScreen
import com.afristyle.ai.ui.screens.settings.SettingsScreen
import com.afristyle.ai.ui.screens.splash.SplashScreen
import com.afristyle.ai.ui.screens.tryon.VirtualTryOnScreen

@Composable
fun AfriStyleNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToCamera = {
                    navController.navigate(Screen.Camera.route)
                },
                onNavigateToOutfits = {
                    navController.navigate(Screen.Outfits.route)
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        
        composable(Screen.Camera.route) {
            PhotoCaptureScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToTryOn = { userPhotoId ->
                    navController.navigate(Screen.TryOn.createRoute(userPhotoId))
                }
            )
        }
        
        composable(Screen.Outfits.route) {
            OutfitBrowserScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToTryOn = { outfitId ->
                    navController.navigate(Screen.TryOn.createRoute(outfitId = outfitId))
                }
            )
        }
        
        composable(
            route = Screen.TryOn.route,
            arguments = Screen.TryOn.arguments
        ) { backStackEntry ->
            val userPhotoId = backStackEntry.arguments?.getLong("userPhotoId") ?: 0L
            val outfitId = backStackEntry.arguments?.getLong("outfitId") ?: 0L
            
            VirtualTryOnScreen(
                userPhotoId = userPhotoId,
                outfitId = outfitId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }
        
        composable(Screen.History.route) {
            HistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToTryOn = { resultId ->
                    navController.navigate(Screen.TryOn.createRoute(resultId = resultId))
                }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}