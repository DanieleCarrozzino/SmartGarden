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
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.smartgarden.R
import com.example.smartgarden.objects.CHART_TYPE
import com.example.smartgarden.objects.ChartObject
import com.example.smartgarden.objects.InfoObject
import com.example.smartgarden.ui.theme.Blue20
import com.example.smartgarden.ui.theme.Blue80
import com.example.smartgarden.ui.theme.Green80
import com.example.smartgarden.ui.theme.LightBlue
import com.example.smartgarden.ui.theme.Orange80
import com.example.smartgarden.utility.Utility
import com.example.smartgarden.viewmodels.MainViewModel
import kotlin.math.abs


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

    HomeCore(
        navController = navController,
        statusHeight, navigationHeight,
        screenWidth, screenHeight,
        viewModel.maxDistanceGesture,
        viewModel.connected,
        viewModel.transitionGestureX,
        viewModel.transitionGestureY,
        viewModel.positionType,
        viewModel.name,
        viewModel.chart,
        viewModel.temperatureValue,
        viewModel.hydrationValue,
        viewModel.wellnessValue,
        viewModel::init,
        viewModel::managePointerEvent
    )
}

@Preview
@Composable
fun PreviewHomeCore(){
    HomeCore(navController = rememberNavController())
}

@Composable
fun HomeCore(
    navController: NavController,
    statusHeight: Float     = 0f,
    navigationHeight: Float = 0f,
    screenWidth  : Dp       = 400.dp,
    screenHeight : Dp       = 700.dp,
    maxDistanceGesture: Int = 400,
    connections: MutableState<Boolean> = mutableStateOf(true),
    gestureX: MutableFloatState = mutableFloatStateOf(0f),
    gestureY: MutableFloatState = mutableFloatStateOf(0f),
    positionType: MutableState<MainViewModel.POSITION_TYPE> = mutableStateOf(MainViewModel.POSITION_TYPE.DEFAULT),
    name: MutableState<String> = mutableStateOf(""),
    charts: MutableLiveData<ChartObject> = MutableLiveData<ChartObject>(),

    temperature : MutableLiveData<Int>   = MutableLiveData<Int>(),
    hydration   : MutableLiveData<Int>      = MutableLiveData<Int>(),
    wellness    : MutableLiveData<Int>      = MutableLiveData<Int>(),

    init: () -> Unit = {},
    managePointer: (PointerEvent, NavController) -> Unit = {_, _ ->}
){

    val connected by remember {
        connections
    }

    // move the entire screen
    val translationX by remember {
        gestureX
    }

    val translationY by remember {
        gestureY
    }

    val gestureType by remember {
        positionType
    }

    val gestureBubbleAnimation : Float by animateFloatAsState(
        if(gestureType == MainViewModel.POSITION_TYPE.DEFAULT) 0f else 1f,
        label = "Gesture navigation animation",
        animationSpec = tween(400)
    )

    // State to track whether data has been fetched
    var dataFetched by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!dataFetched) {
            init()
            dataFetched = true
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp, 0.dp, 0.dp, 0.dp)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()

                        if (!connected) return@awaitPointerEventScope

                        managePointer(
                            event,
                            navController
                        )
                    }
                }
            },
        color = MaterialTheme.colorScheme.background
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .alpha(1 - abs(translationX).coerceAtLeast(abs(translationY)))
                .padding(0.dp, 0.dp, 0.dp, navigationHeight.dp)
                .graphicsLayer(
                    translationX = maxDistanceGesture * translationX,
                    translationY = maxDistanceGesture * translationY
                )
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
                val chart = charts.observeAsState().value

                RoundedImage(
                    name,
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

                    CustomSeekBar(temperature.observeAsState().value ?: 0,
                        "Temperature",
                        Modifier
                            .graphicsLayer(alpha = alphaTemperature)
                            .fillMaxWidth()
                            .padding((screenWidth / 6), 5.dp, 15.dp, 5.dp), Orange80)

                    CustomSeekBar(hydration.observeAsState().value ?: 0,
                        "Hydration", Modifier
                            .graphicsLayer(alpha = alphaHumidity)
                            .fillMaxWidth()
                            .padding((screenWidth / 9), 10.dp, 16.dp, 5.dp), Blue80)

                    CustomSeekBar(wellness.observeAsState().value ?: 0,
                        "Wellness", Modifier
                            .graphicsLayer(alpha = alphaSoilMoisture)
                            .fillMaxWidth()
                            .padding(0.dp, 10.dp, 23.dp, 5.dp), Green80)

                }

            }

            // Main layout
            if(connected){
                MainLayout(
                    charts,
                    maxDistanceGesture,
                    gestureX, gestureY
                )
            }
            // Place holder
            else{
                PlaceHolder(navController = navController)
            }
        }


        when(gestureType) {
            MainViewModel.POSITION_TYPE.LEFT -> {
                DirectionBoxBig(
                    Alignment.CenterStart,
                    gestureBubbleAnimation,
                    "BOH?",
                    statusHeight.dp, navigationHeight.dp,
                    screenWidth, screenHeight,
                    imageId = R.drawable.webcam)
            }
            MainViewModel.POSITION_TYPE.RIGHT -> {
                DirectionBoxBig(
                    Alignment.CenterEnd,
                    gestureBubbleAnimation,
                    "SWITCH",
                    statusHeight.dp, navigationHeight.dp,
                    screenWidth, screenHeight,
                    imageId = R.drawable.off_button)
            }
            MainViewModel.POSITION_TYPE.UP -> {
                DirectionBoxBig(
                    Alignment.TopCenter,
                    gestureBubbleAnimation,
                    "SETTINGS",
                    statusHeight.dp, navigationHeight.dp,
                    screenWidth, screenHeight,
                    imageId = R.drawable.cogwheel)
            }
            MainViewModel.POSITION_TYPE.DOWN -> {
                DirectionBoxBig(
                    Alignment.BottomCenter,
                    gestureBubbleAnimation,
                    "CAMERA",
                    statusHeight.dp, navigationHeight.dp,
                    screenWidth, screenHeight,
                    imageId = R.drawable.webcam)
            }
            MainViewModel.POSITION_TYPE.DEFAULT -> {}
        }
    }
}

@Preview(showBackground = false)
@Composable
fun DirectionBoxPreview(){
    DirectionBoxBig(
        text = "GARDEN",
        status = 0.dp,
        navigation = 0.dp,
        width = 400.dp,
        height = 700.dp,
        anim = 1f)
}

@Preview(showBackground = false)
@Composable
fun DirectionBoxPreview2(){
    DirectionBoxBig(
        alignment = Alignment.TopCenter,
        text = "SETTINGS",
        status = 0.dp,
        navigation = 0.dp,
        width = 400.dp,
        height = 700.dp,
        anim  = 1f,
        imageId = R.drawable.cogwheel)
}

@Composable
fun DirectionBoxBig(alignment: Alignment = Alignment.CenterStart,
                    anim : Float = 0.3f, text : String = "",
                    status : Dp, navigation : Dp,
                    width  : Dp, height : Dp,
                    imageId : Int = R.drawable.off_button,
                    color       : Color = Color(0xFFE2E2FF),
                    waveColor   : Color = Color(0x99BBBBEF)){

    var translationX = 0.dp
    var translationY = 0.dp
    var intAlignment = Alignment.CenterStart
    when(alignment){
        Alignment.CenterStart -> {
            translationX = - width / 2
            intAlignment = Alignment.CenterEnd
        }
        Alignment.CenterEnd -> {
            translationX = width / 2
            intAlignment = Alignment.CenterStart
        }
        Alignment.TopCenter -> {
            translationY = - height / 2
            intAlignment = Alignment.BottomCenter
        }
        Alignment.BottomCenter -> {
            translationY = height / 2
            intAlignment = Alignment.TopCenter
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()){

        //***********
        //
        // WAVE ANIM
        //
        //***********
        Box(modifier = Modifier
            .graphicsLayer {
                scaleX = anim * 10f
                scaleY = anim * 10f
                alpha  = 1 - anim
                this.translationY = translationY.toPx()
                this.translationX = translationX.toPx()
            }
            .align(alignment)
            .fillMaxSize()
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x00000000), color, Color(0x00000000)
                ))
            ))

        //***********
        //
        // CIRCLE
        //
        //***********
        Box(modifier = Modifier
            .graphicsLayer {
                alpha  = anim
                scaleX = anim
                scaleY = anim
                this.translationX = translationX.toPx()
                this.translationY = translationY.toPx()
            }
            .background(
                brush = Brush.radialGradient(
                    listOf(color, Color(0x00000000))
                )
            )
            .align(Alignment.Center)
            .width(width)
            .aspectRatio(1f)){

            Column(modifier = Modifier
                .align(intAlignment)
                .padding(width / 12, status.coerceAtLeast(5.dp), width / 12, navigation.coerceAtLeast(5.dp))) {
                Image(painter = painterResource(id = imageId),
                    contentDescription = "",
                    modifier = Modifier
                        .width(width / 6)
                        .aspectRatio(1f)
                        .align(Alignment.CenterHorizontally))

                Text(text = text,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold)

                Text(text = "Description screen only some words",
                    modifier = Modifier
                        .width(width / 3)
                        .align(Alignment.CenterHorizontally),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center)
            }

        }
    }
}

@Composable
fun DirectionBox(alignment: Alignment = Alignment.CenterStart,
                 gestureBubbleAnimation : Float = 0.3f,
                 text : String = "", status : Dp, navigation : Dp){
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(0.dp, status, 0.dp, navigation)){
        Box(modifier = Modifier
            .align(alignment)
            .padding(20.dp)
            .wrapContentSize()
            .defaultMinSize(100.dp, 40.dp)
            .alpha(gestureBubbleAnimation)
            .graphicsLayer(
                scaleX = gestureBubbleAnimation,
                scaleY = gestureBubbleAnimation
            )
            .clip(RoundedCornerShape(32.dp))
            .background(LightBlue)
        )
        {
            Text(text = text,
                modifier = Modifier
                    .padding(20.dp, 10.dp, 20.dp, 10.dp)
                    .align(Alignment.Center),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold)
        }
    }
}


@Composable
fun MainLayout(
    charts: MutableLiveData<ChartObject>,
    max : Int,
    gestX : MutableFloatState,
    gestY : MutableFloatState,
) {
    // Middle info
    Box(
        modifier = Modifier
            .fillMaxHeight(0.7f)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ){

        val type = charts.observeAsState().value

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

            val info = when(chart?.type){
                CHART_TYPE.TEMPERATURE -> {
                    val temp = chart.values.last().toInt()
                     InfoObject(
                        R.drawable.termometro, R.drawable.sun_into_garden,
                        "$temp${chart.type.getSymbol()}", "8h",
                        "Temperatura attuale del giardino",
                        "Ore di luce continua presa dal giardino"
                    )

                }
                CHART_TYPE.HUMIDITY -> {
                    InfoObject(
                        R.drawable.water_can, R.drawable.water_tank,
                        "8", "14l",
                        "Innaffiature effettuate durante la giornata di oggi",
                        "Litri di acqua utilizzata durante questa giornata"
                    )
                }
                CHART_TYPE.SOIL_MOISTURE -> {
                    InfoObject(
                        R.drawable.soil_moisture, R.drawable.sensors,
                        "70%", "~5%",
                        "Percentuale di umidità nel terreno attuale",
                        "Differenza massima tra almeno 2 sensori nel terreno"
                    )
                }

                else -> {
                    InfoObject(
                        R.drawable.soil_moisture, R.drawable.sensors,
                        "34", "8h",
                        "Temperatura attuale del giardino",
                        "Ore di luce continua presa dal giardino"
                    )
                }
            }
            Info(
                max,
                gestX, gestY,
                info)

        }

    }

    // Bottom Chart
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

            val listState = charts.observeAsState().value
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
fun Info(
    max : Int,
    gestX : MutableFloatState,
    gestY : MutableFloatState,
    info: InfoObject
){

    // move the entire screen
    val translationX by remember {
        gestX
    }

    val translationY by remember {
        gestY
    }

    Column(
        modifier = Modifier.graphicsLayer(
            translationX = max * translationX.div(2f),
            translationY = max * translationY.div(2f)
        )
    ) {
        FirstInfo(
            Modifier.weight(1f),
            info.image1, info.value1, info.description1
        )
        SecondInfo(
            Modifier.weight(1f),
            info.image2, info.value2, info.description2
        )
    }
}

@Composable
fun SecondInfo(modifier: Modifier, image : Int, value : String, description : String){
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
fun FirstInfo(modifier: Modifier, image : Int, value : String, description : String){
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
fun PlaceHolder(navController: NavController){

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
fun RoundedImage(nameChart : MutableState<String>, modifier: Modifier, type : CHART_TYPE, screenWidth : Dp) {

    val name by remember {
        nameChart
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