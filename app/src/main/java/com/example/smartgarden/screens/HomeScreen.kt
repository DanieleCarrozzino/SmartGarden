package com.example.smartgarden.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smartgarden.R
import com.example.smartgarden.objects.CHART_TYPE
import com.example.smartgarden.ui.theme.Blue20
import com.example.smartgarden.ui.theme.Blue80
import com.example.smartgarden.ui.theme.Green20
import com.example.smartgarden.ui.theme.Green80
import com.example.smartgarden.ui.theme.Pink40
import com.example.smartgarden.ui.theme.Pink80
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
            modifier = Modifier.fillMaxSize()
        ) {

            Box(
                modifier = Modifier
                    .fillMaxHeight(0.33f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
            ){


                Row(
                    modifier = Modifier
                ) {

                    Surface(
                        modifier = Modifier.weight(1f).padding(10.dp).aspectRatio(1f),
                        shadowElevation = 4.dp,
                        tonalElevation = 4.dp,
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.background
                    ) {

                        val listState = viewModel.chart.observeAsState().value

                        AnimatedContent(
                            targetState = listState,
                            label = "Charts animation",
                            transitionSpec = {
                                slideInHorizontally(
                                    animationSpec = tween(1000),
                                    initialOffsetX = { fullWidth -> fullWidth }
                                ) togetherWith
                                        slideOutHorizontally(
                                            animationSpec = tween(1000),
                                            targetOffsetX = { fullWidth -> -fullWidth }
                                        )
                            }
                        )
                        { chart ->

                            var color1 = Blue80
                            var color2 = Blue20
                            when(chart?.type){
                                CHART_TYPE.HUMIDITY -> {

                                }
                                else -> {
                                    color1 = Green80
                                    color2 = Green20
                                }
                            }
                            ChartBoxWithArray(
                                chart?.values ?: listOf<Float>(0f, 0f, 0f),
                                chart?.title ?: "",
                                30f, color1, color2
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier.weight(1f).padding(10.dp).aspectRatio(1f),
                        shadowElevation = 4.dp,
                        tonalElevation = 4.dp,
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.background
                    ) {

                        val listState = viewModel.chart.observeAsState().value

                        AnimatedContent(
                            targetState = listState,
                            label = "Charts animation",
                            transitionSpec = {
                                slideInHorizontally(
                                    animationSpec = tween(1000),
                                    initialOffsetX = { fullWidth -> fullWidth }
                                ) togetherWith
                                        slideOutHorizontally(
                                            animationSpec = tween(1000),
                                            targetOffsetX = { fullWidth -> -fullWidth }
                                        )
                            }
                        )
                        { chart ->
                            ChartBoxWithArray(
                                chart?.values ?: listOf<Float>(0f, 0f, 0f),
                                chart?.title ?: "",
                                30f, Blue80, Blue20
                            )
                        }
                    }
                }

            }
            Box(
                modifier = Modifier
                    .fillMaxHeight(0.5f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
            ){

            }
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp, 4.dp, 0.dp, 0.dp),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = 8.dp
                ), // Set elevation value for the card
                shape = RoundedCornerShape(30.dp, 30.dp, 0.dp, 0.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
            ){

                if(!viewModel.connected.value){
                    BigImageButton(modifier = Modifier
                        .padding(10.dp)
                        .background(MaterialTheme.colorScheme.background)
                        .align(Alignment.CenterHorizontally),
                        {
                            navController.navigate("config_raspberry")
                        },
                        "Configure garden",
                        R.drawable.rasp_icon)
                }
            }
        }


//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text(
//                modifier = Modifier
//                    .padding(10.dp)
//                    .align(Alignment.CenterHorizontally),
//                text = name,
//                textAlign = TextAlign.Center,
//                fontSize = 18.sp,
//                fontWeight = FontWeight.Bold
//            )
//
//            Text(
//                modifier = Modifier
//                    .padding(10.dp)
//                    .align(Alignment.CenterHorizontally),
//                text = date,
//                textAlign = TextAlign.Center,
//                fontSize = 16.sp
//            )
//
//            if(!viewModel.connected.value){
//                BigImageButton(modifier = Modifier
//                    .padding(10.dp)
//                    .background(MaterialTheme.colorScheme.background)
//                    .align(Alignment.CenterHorizontally),
//                    {
//                        navController.navigate("config_raspberry")
//                    },
//                    "Configure garden",
//                    R.drawable.rasp_icon)
//            }
//        }

    }

}