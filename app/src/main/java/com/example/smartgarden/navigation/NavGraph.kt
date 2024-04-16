package com.example.smartgarden.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.smartgarden.screens.CameraScreen
import com.example.smartgarden.screens.raspberry.ConfigRaspberryScreen
import com.example.smartgarden.screens.HomeScreen
import com.example.smartgarden.screens.InitGardenScreen
import com.example.smartgarden.screens.InstantCameraScreen
import com.example.smartgarden.screens.raspberry.InitRaspberryScreen
import com.example.smartgarden.screens.LoginScreen
import com.example.smartgarden.screens.settings.NotificationSettingsScreen
import com.example.smartgarden.screens.settings.SettingsScreen
import com.example.smartgarden.screens.settings.SwitchScreen
import com.example.smartgarden.screens.settings.ThresholdScreen
import com.example.smartgarden.viewmodels.CameraViewModel

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Login : Screen("login")
    data object InitGarden : Screen("init_garden")
    data object InitRaspGarden : Screen("init_raspberry_garden")
    data object Threshold : Screen("threshold")
    data object Notification : Screen("notification")
    data object Settings : Screen("settings")
    data object Camera : Screen("camera")
    data object InstantCamera : Screen("instant_camera")
    data object Switch : Screen("switch")
}

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    intiRoute : String,
    cameraViewModel: CameraViewModel
){
    val durationMillis = 400
    NavHost(
        navController = navController,
        startDestination = intiRoute,
    ){
        composable(
            route = Screen.Login.route
        ){
            LoginScreen(navController)
        }
        composable(
            route = Screen.InitGarden.route
        ){
            InitGardenScreen(navController)
        }
        composable(
            route = Screen.Home.route
        ){
            HomeScreen(navController = navController)
        }
        composable(
            route = "config_raspberry"
        ){
            ConfigRaspberryScreen(navController = navController)
        }
        composable(
            route = Screen.InitRaspGarden.route
        ){
            InitRaspberryScreen(navController = navController)
        }
        composable(
            route = Screen.Threshold.route
        ){
            ThresholdScreen(navController = navController)
        }
        composable(
            route = Screen.Notification.route
        ){
            NotificationSettingsScreen(navController = navController)
        }
        composable(
            route = Screen.Settings.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Down,
                    animationSpec = tween(durationMillis)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Up,
                    animationSpec = tween(durationMillis)
                )
            },
        ){
            SettingsScreen(navController = navController)
        }
        composable(
            route = Screen.Camera.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Up,
                    animationSpec = tween(durationMillis)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Down,
                    animationSpec = tween(durationMillis)
                )
            },
        ){
            CameraScreen(navController = navController, cameraViewModel)
        }
        composable(
            route = Screen.Switch.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                    animationSpec = tween(durationMillis)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
                    animationSpec = tween(durationMillis)
                )
            },
        ){
            SwitchScreen(navController = navController)
        }

        composable(
            route = Screen.InstantCamera.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
                    animationSpec = tween(durationMillis)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                    animationSpec = tween(durationMillis)
                )
            },
        ){
            InstantCameraScreen(navController = navController, cameraViewModel)
        }
    }
}