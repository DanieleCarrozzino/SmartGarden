package com.example.smartgarden.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smartgarden.viewmodels.MainViewModel

@Composable
fun HomeScreen(navController: NavController){

    val viewModel = hiltViewModel<MainViewModel>()

    // State to track whether data has been fetched
    var dataFetched by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!dataFetched) {
            viewModel.startGardenListener()
            dataFetched = true
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

    }

}