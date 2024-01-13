package com.example.smartgarden.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.smartgarden.screens.ConfigRaspberryScreen
import com.example.smartgarden.screens.HomeScreen
import com.example.smartgarden.screens.InitGardenScreen
import com.example.smartgarden.screens.InitRaspberryScreen
import com.example.smartgarden.screens.LoginScreen
import com.example.smartgarden.screens.ThresholdScreen

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Login : Screen("login")
    data object InitGarden : Screen("init_garden")

    data object Threshold : Screen("threshold")
}

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    intiRoute : String
){
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
            route = "init_config_raspberry"
        ){
            InitRaspberryScreen(navController = navController)
        }
        composable(
            route = "threshold"
        ){
            ThresholdScreen(navController = navController)
        }
    }
}