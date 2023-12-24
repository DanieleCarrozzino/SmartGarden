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
            route = "login"
        ){
            LoginScreen(navController)
        }
        composable(
            route = "init_garden"
        ){
            InitGardenScreen(navController)
        }
        composable(
            route = "home"
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
    }
}