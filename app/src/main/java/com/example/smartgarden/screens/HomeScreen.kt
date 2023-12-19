package com.example.smartgarden.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smartgarden.viewmodels.MainViewModel

@Composable
fun HomeScreen(navController: NavController){

    val viewModel = hiltViewModel<MainViewModel>()

    val name by remember {
        viewModel.name
    }

    val date by remember {
        viewModel.date
    }

    // State to track whether data has been fetched
    var dataFetched by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!dataFetched) {
            viewModel.init()
            dataFetched = true
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.CenterHorizontally),
                text = name,
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.CenterHorizontally),
                text = date,
                textAlign = TextAlign.Center,
                fontSize = 16.sp
            )
        }

    }

}