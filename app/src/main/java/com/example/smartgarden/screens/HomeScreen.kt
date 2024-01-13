package com.example.smartgarden.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smartgarden.R
import com.example.smartgarden.objects.CHART_TYPE
import com.example.smartgarden.objects.InfoObject
import com.example.smartgarden.ui.theme.Blue20
import com.example.smartgarden.ui.theme.Blue80
import com.example.smartgarden.ui.theme.Green80
import com.example.smartgarden.ui.theme.Orange80
import com.example.smartgarden.utility.Utility
import com.example.smartgarden.viewmodels.MainViewModel


@Composable
fun HomeScreen(navController: NavController){

    val viewModel = hiltViewModel<MainViewModel>()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    // Status bar height
    val density = LocalDensity.current.density
    val statusHeight = Utility.getStatusBarSize(LocalContext.current.resources) / density
    val navigationHeight = Utility.getNavigationBarSize(LocalContext.current.resources) / density

    val name by remember {
        viewModel.name
    }

    val date by remember {
        viewModel.date
    }

    val connected by remember {
        viewModel.connected
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
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp, 0.dp, 0.dp, navigationHeight.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        navController.navigate("threshold")
                    },
                    onDoubleTap = {
                        // Handle double tap here
                        println("Double click detected!")
                    },
                    onPress = {
                        if (viewModel.connected.value)
                            viewModel.changeScreenClick = true
                    }
                )
            },
        color = MaterialTheme.colorScheme.background
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            // Top image rounded, date and name
            // 3 seek bar
            Box(
                modifier = Modifier
                    .fillMaxHeight(0.33f)
                    .fillMaxWidth()
                    .alpha(if (connected) 1f else 0.2f)
                    .background(MaterialTheme.colorScheme.background)
            ) {

                // type to use to animate
                val chart = viewModel.chart.observeAsState().value

                RoundedImage(
                    viewModel,
                    Modifier
                        .width(screenWidth)
                        .aspectRatio(1f)
                        .offset(x = -((screenWidth / 5) * 2), y = -screenHeight / 8),
                    chart?.type ?: CHART_TYPE.HUMIDITY, screenWidth)


                Column(modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .width(screenWidth / 2)
                    .fillMaxHeight()
                    .padding(0.dp, statusHeight.dp, 0.dp, 0.dp)){

                    val alphaTemperature: Float by animateFloatAsState(
                        if(chart?.type == CHART_TYPE.TEMPERATURE) 1f else 0.3f,
                        label = "",
                        animationSpec = tween(1000)
                    )

                    val alphaHumidity: Float by animateFloatAsState(
                        if(chart?.type == CHART_TYPE.HUMIDITY) 1f else 0.3f,
                        label = "",
                        animationSpec = tween(1000)
                    )

                    val alphaSoilMoisture: Float by animateFloatAsState(
                        if(chart?.type == CHART_TYPE.SOIL_MOISTURE) 1f else 0.3f,
                        label = "",
                        animationSpec = tween(1000)
                    )

                    CustomSeekBar(viewModel.temperatureValue.observeAsState().value ?: 0,
                        "Temperature",
                        Modifier
                            .graphicsLayer(alpha = alphaTemperature)
                            .fillMaxWidth()
                            .padding((screenWidth / 6), 5.dp, 15.dp, 5.dp), Orange80)

                    CustomSeekBar(viewModel.hydrationValue.observeAsState().value ?: 0,
                        "Hydration", Modifier
                            .graphicsLayer(alpha = alphaHumidity)
                            .fillMaxWidth()
                            .padding((screenWidth / 9), 10.dp, 16.dp, 5.dp), Blue80)

                    CustomSeekBar(viewModel.wellnessValue.observeAsState().value ?: 0,
                        "Wellness", Modifier
                            .graphicsLayer(alpha = alphaSoilMoisture)
                            .fillMaxWidth()
                            .padding(0.dp, 10.dp, 23.dp, 5.dp), Green80)

                }

            }

            // Main layout
            if(connected){
                MainLayout(viewModel = viewModel)
            }
            // Place holder
            else{
                PlaceHolder(viewModel = viewModel, navController = navController)
            }
        }

    }

}


@Composable
fun MainLayout(viewModel : MainViewModel){
    // Moving Chart
    Box(
        modifier = Modifier
            .fillMaxHeight(0.7f)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ){

        val type = viewModel.chart.observeAsState().value

        AnimatedContent(
            targetState = type,
            label = "Charts animation",
            transitionSpec = {
                fadeIn(
                    animationSpec = tween(700, delayMillis = 700)) togetherWith
                        fadeOut(animationSpec = tween(700))
            }
        )
        { chart ->

            when(chart?.type){
                CHART_TYPE.TEMPERATURE -> {
                    val temp = chart.values.last().toInt()
                    val info = InfoObject(
                        R.drawable.termometro, R.drawable.sun_into_garden,
                        "$temp${chart.type.getSymbol()}", "8h",
                        "Temperatura attuale del giardino",
                        "Ore di luce continua presa dal giardino"
                    )
                    Info(viewModel, info)
                }
                CHART_TYPE.HUMIDITY -> {
                    val info = InfoObject(
                        R.drawable.water_can, R.drawable.water_tank,
                        "8", "14l",
                        "Innaffiature effettuate durante la giornata di oggi",
                        "Litri di acqua utilizzata durante questa giornata"
                    )
                    Info(viewModel, info)
                }
                CHART_TYPE.SOIL_MOISTURE -> {
                    val info = InfoObject(
                        R.drawable.soil_moisture, R.drawable.sensors,
                        "70%", "~5%",
                        "Percentuale di umidità nel terreno attuale",
                        "Differenza massima tra almeno 2 sensori nel terreno"
                    )
                    Info(viewModel, info)
                }

                else -> {
                    val info = InfoObject(
                        R.drawable.soil_moisture, R.drawable.sensors,
                        "34", "8h",
                        "Temperatura attuale del giardino",
                        "Ore di luce continua presa dal giardino"
                    )
                    Info(viewModel, info)
                }
            }

        }

    }

    // Bottom items
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp, 4.dp, 0.dp, 0.dp),
    ){

        // Chart
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                //.padding(10.dp, 4.dp, 10.dp, 4.dp),
                .padding(0.dp, 10.dp, 0.dp, 0.dp),
            shadowElevation = 0.dp,
            tonalElevation = 0.dp,
            shape = RoundedCornerShape(0.dp),
            color = MaterialTheme.colorScheme.background
        ) {

            val listState = viewModel.chart.observeAsState().value
            ChartBoxWithArrayAnimated(
                listState?.values ?: listOf<Float>(0f, 0f, 0f),
                listState?.type?.getColors()?.second ?: Blue80,
                listState?.type?.getColors()?.first ?: Blue20
            )

            AnimatedContent(
                targetState = listState,
                label = "Charts animation",
                transitionSpec = {
                    fadeIn(
                        animationSpec = tween(700, delayMillis = 700)) togetherWith
                            fadeOut(animationSpec = tween(700))
                }
            )
            { chart ->

                var imageId = R.drawable.sun
                when(chart?.type){
                    CHART_TYPE.HUMIDITY -> {
                        imageId = R.drawable.humidity
                    }
                    CHART_TYPE.TEMPERATURE  -> {
                        imageId = R.drawable.sun
                    }
                    CHART_TYPE.SOIL_MOISTURE  -> {
                        imageId = R.drawable.soil
                    }
                    else -> {

                    }
                }
                ChartBoxInfo(
                    chart?.title ?: "",
                    30f, imageId,
                    chart?.values?.max() ?: 0f,
                    chart?.values?.min() ?: 0f,
                    chart?.type?.getSymbol() ?: "%"
                )
            }
        }

    }
}

@Composable
fun Info(viewModel: MainViewModel, info: InfoObject){
    Column(
        modifier = Modifier
    ) {
        FirstInfo(
            viewModel = viewModel,
            Modifier.weight(1f),
            info.image1, info.value1, info.description1
        )
        SecondInfo(
            viewModel = viewModel,
            Modifier.weight(1f),
            info.image2, info.value2, info.description2
        )
    }
}

@Composable
fun SecondInfo(viewModel : MainViewModel, modifier: Modifier, image : Int, value : String, description : String){
    Row(modifier = modifier) {

        Text(
            text = description,
            modifier = Modifier
                .weight(2f)
                .align(Alignment.CenterVertically)
                .padding(18.dp, 8.dp, 8.dp, 8.dp),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontSize = 14.sp,
            textAlign = TextAlign.End
        )

        Text(
            text = value,
            modifier = Modifier
                .weight(1.5f)
                .align(Alignment.CenterVertically)
                .padding(4.dp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontSize = 30.sp,
            textAlign = TextAlign.Center
        )

        Image(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
                .weight(2f)
                .padding(8.dp),
            painter = painterResource(id = image),
            contentDescription = "water can",
        )

    }
}

@Composable
fun FirstInfo(viewModel : MainViewModel, modifier: Modifier, image : Int, value : String, description : String){
    Row(modifier = modifier) {

        Image(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
                .weight(3f)
                .padding(18.dp, 8.dp, 8.dp, 8.dp),
            painter = painterResource(id = image),
            contentDescription = "water can",
        )

        Text(
            text = value,
            modifier = Modifier
                .weight(1.5f)
                .align(Alignment.CenterVertically)
                .padding(4.dp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontSize = 30.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = description,
            modifier = Modifier
                .weight(3f)
                .align(Alignment.CenterVertically)
                .padding(12.dp),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontSize = 14.sp
        )

    }
}

@Composable
fun PlaceHolder(viewModel : MainViewModel, navController: NavController){

    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .align(Alignment.TopCenter)
        ) {

            Image(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                painter = painterResource(id = R.drawable.placeholder_unplugged),
                contentDescription = "placeholder",
            )


            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                text = "Garden Unplugged",
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                text = "Connect garden to obtain data",
                textAlign = TextAlign.Center,
                fontSize = 16.sp
            )


            BigImageButton(
                modifier = Modifier
                    .padding(10.dp)
                    .background(MaterialTheme.colorScheme.background)
                    .align(Alignment.CenterHorizontally),
                {
                    navController.navigate("config_raspberry")
                },
                "Configure garden",
                R.drawable.rasp_icon
            )
        }
    }
}

@Composable
fun RoundedImage(viewModel : MainViewModel, modifier: Modifier, type : CHART_TYPE, screenWidth : Dp) {

    val name by remember {
        viewModel.name
    }

    val backgroundColor by animateColorAsState(
        targetValue = type.getColors().second,
        animationSpec = TweenSpec(durationMillis = 1000),
        label = "", // Change these colors as needed
    )

    Box(modifier = modifier){
//        Image(
//            painter = painterResource(id = imageResId),
//            contentDescription = "Rounded image", // Provide proper content description
//            modifier = Modifier
//                .fillMaxSize()
//                .clip(CircleShape)
//                .background(Color.Green),
//            contentScale = ContentScale.Crop
//        )

        Box(modifier = Modifier
            .fillMaxSize()
            .clip(CircleShape)
            .background(backgroundColor),){

        }

//        Image(
//            modifier = Modifier.align(Alignment.BottomCenter).width(sizePlants).aspectRatio(1f),
//            painter = painterResource(id = R.drawable.plant2),
//            contentDescription = "plant 1",
//            contentScale = ContentScale.Crop
//        )
//
//        Image(
//            modifier = Modifier
//                .align(Alignment.BottomCenter)
//                .width(sizePlants)
//                .aspectRatio(1f)
//                .padding(sizePlants / 2, 0.dp, 0.dp, 10.dp),
//            painter = painterResource(id = R.drawable.plant3),
//            contentDescription = "plant 1",
//            contentScale = ContentScale.Crop
//        )
    }
    Text(
        text = name,
        modifier = Modifier
            .width(screenWidth / 2)
            .padding(30.dp, 50.dp, 30.dp, 30.dp),
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        color = MaterialTheme.colorScheme.background
    )
}