package com.example.smartgarden.screens.settings

import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smartgarden.ui.theme.Orange1
import com.example.smartgarden.ui.theme.WhiteOpac
import com.example.smartgarden.utility.Utility
import com.example.smartgarden.viewmodels.SettingsViewModel

@Composable
fun ThresholdScreen(navController: NavController){
    val viewModel       = hiltViewModel<SettingsViewModel>()

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    // Status bar height
    val density = LocalDensity.current.density
    val statusHeight = Utility.getStatusBarSize(LocalContext.current.resources) / density
    val navigationHeight = Utility.getNavigationBarSize(LocalContext.current.resources) / density

    DisposableEffect(true) {
        onDispose {
            viewModel.changeLimits()
        }
    }

    Surface(modifier = Modifier
        .fillMaxSize(),
        color = MaterialTheme.colorScheme.background){

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(0.dp, statusHeight.dp, 0.dp, navigationHeight.dp)){
            MainThresholdLayout(screenWidth, viewModel)
        }

    }
}

@Composable
fun MainThresholdLayout(width : Dp, viewModel: SettingsViewModel){

    val alpha by remember {
        viewModel.alpha
    }

    val enabled by remember {
        viewModel.enabled
    }

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

        // Title and description
        Text(
            text = "Threshold screen",
            modifier = Modifier
                .padding(10.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        SwitchWithDescription(
            text = "Modificare questi valori comporterà l'alterazione e il funzionamenot del giardino, i valori adesso impostati sono valori standard molto validi, consigliata la modifica alle sole persone competenti",
            checked = viewModel.enabled.value
            )
        {
            viewModel.enabled.value = it
            if(it){
                viewModel.alpha.floatValue = 1f
            } else viewModel.alpha.floatValue = 0.3f
        }

        Column(modifier = Modifier
            .alpha(alpha)
            .focusable(enabled)){

            // Hydration threshold, min and max
            HydrationThreshold(width, viewModel)

            // Temperature updates
            TemperatureThreshold(width, viewModel)
        }
    }
}

@Composable
fun TemperatureThreshold(width : Dp, viewModel: SettingsViewModel){

    Column() {

        Text(
            text = "Temperature values",
            modifier = Modifier
                .padding(10.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(text = "Gestione dei limiti di temperatura massimi e minimi",
            modifier = Modifier.padding(10.dp, 0.dp))

        val circleSize      = 80.dp
        val marginCircles   = 20.dp
        DoubleSeekBar(
            width,
            viewModel.percMinTemperature, viewModel.percMaxTemperature,
            viewModel.enabledTemperature, circleSize, marginCircles,
            "℃", Orange1){ value, type ->
            when(type){
                0 -> {
                    viewModel.percMinTemperature.intValue = value
                }
                1 -> {
                    viewModel.percMaxTemperature.intValue = value
                }
            }
        }
    }
}

@Composable
fun HydrationThreshold(width : Dp, viewModel: SettingsViewModel){

    // Hydration double seek bar
    Column {
        Text(
            text = "Irrigation values",
            modifier = Modifier
                .padding(10.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(text = "Gestione dei limiti di irrigazioni massimi e minimi",
            modifier = Modifier.padding(10.dp, 0.dp))

        val circleSize      = 80.dp
        val marginCircles   = 20.dp
        DoubleSeekBar(
            width,
            viewModel.percMin, viewModel.percMax,
            viewModel.enabled, circleSize, marginCircles,
            "%", MaterialTheme.colorScheme.surface){ value, type ->
            when(type){
                0 -> {
                    viewModel.percMin.intValue = value
                }
                1 -> {
                    viewModel.percMax.intValue = value
                }
            }
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DoubleSeekBar(width : Dp,
                  percMin : MutableState<Int>, percMax : MutableState<Int>,
                  enable : MutableState<Boolean>,
                  circleSize : Dp, marginCircles : Dp,
                  symbol : String, color : Color,
                  offsetFunction : (Int, Int) -> Unit){
    val maxWidth = width - circleSize - (marginCircles * 2)

    val density = LocalDensity.current.density

    val perc1 by remember {
        percMin
    }

    val perc2 by remember {
        percMax
    }

    var leftClicked by remember {
        mutableStateOf(false)
    }
    var rightClicked by remember {
        mutableStateOf(false)
    }

    var offsetLeft = Pair((maxWidth * perc1) / 100 + marginCircles, 0.dp)
    var offsetRight = Pair((maxWidth * perc2) / 100 + marginCircles, 0.dp)

    Box(modifier = Modifier
        .padding(0.dp, 15.dp)
        .fillMaxWidth()
        .wrapContentHeight()
        .pointerInput(Unit) {
            detectTransformGestures { off, _, _, _ ->
                // Handle the translation (pan) gesture
                // Update the Box's position based on pan
                // Use translationX and translationY to move the Box
                // You may also want to limit the movement if needed
                // For example, you can use offsetX and offsetY modifiers
                // to apply the translation to the Box
                Log.d("Threshold", off.toString())

                if (!enable.value) return@detectTransformGestures

                offsetLeft = Pair((maxWidth * perc1) / 100 + marginCircles, 0.dp)
                offsetRight = Pair((maxWidth * perc2) / 100 + marginCircles, 0.dp)

                var x = (off.x / density).dp
                if (x < marginCircles) x = marginCircles
                if (x > width - marginCircles - circleSize) x = width - marginCircles - circleSize

                if (leftClicked) {
                    if (x > offsetRight.first) x = offsetRight.first
                    offsetLeft = Pair(x, 0.dp)
                    offsetFunction((((x - marginCircles) / maxWidth) * 100).toInt(), 0)
                } else if (rightClicked) {
                    if (x < offsetLeft.first) x = offsetLeft.first
                    offsetRight = Pair(x, 0.dp)
                    offsetFunction((((x - marginCircles) / maxWidth) * 100).toInt(), 1)
                }
            }
        }
    ){

        Column(modifier = Modifier.fillMaxWidth()) {

            Box(modifier = Modifier.fillMaxWidth()){

                // LEFT
                Surface(modifier = Modifier
                    .offset(offsetLeft.first + marginCircles, 0.dp)
                    .wrapContentSize()
                    .padding(0.dp, 0.dp, 0.dp, 8.dp)
                    .align(Alignment.TopStart),
                    shadowElevation = 4.dp,
                    tonalElevation = 4.dp,
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.background
                ){
                    Text(text = "$perc1$symbol",
                        modifier = Modifier.padding(8.dp),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold)
                }

                // RIGHT
                Surface(modifier = Modifier
                    .offset(offsetRight.first + marginCircles, 0.dp)
                    .wrapContentSize()
                    .padding(0.dp, 0.dp, 0.dp, 8.dp)
                    .align(Alignment.TopStart),
                    shadowElevation = 4.dp,
                    tonalElevation = 4.dp,
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.background
                ){
                    Text(text = "$perc2$symbol",
                        modifier = Modifier.padding(8.dp),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold)
                }
            }

            Box(modifier = Modifier.fillMaxWidth()){
                // BAR
                Surface(modifier = Modifier
                    .fillMaxWidth()
                    .padding((circleSize / 2), 0.dp)
                    .height(circleSize / 2)
                    .align(Alignment.Center),
                    shadowElevation = 4.dp,
                    tonalElevation = 4.dp,
                    shape = RoundedCornerShape(circleSize / 4),
                    color = color
                ){

                }

                // OVER BAR
                Box(modifier = Modifier
                    .height(circleSize / 2 - 4.dp)
                    .width(offsetRight.first - offsetLeft.first)
                    .offset(offsetLeft.first + marginCircles + (circleSize / 4), 0.dp)
                    .background(WhiteOpac)
                    .align(Alignment.CenterStart))

                Surface(modifier = Modifier
                    .width(circleSize / 2)
                    .height(circleSize / 2)
                    .offset(offsetLeft.first + marginCircles, 0.dp)
                    .align(Alignment.CenterStart)
                    .pointerInteropFilter { event ->
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                rightClicked = false
                                leftClicked = true
                            }

                            MotionEvent.ACTION_UP -> {
                                rightClicked = false
                                leftClicked = true
                            }
                        }
                        true
                    }
                    .padding(2.dp),
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp,
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.background
                ){

                }

                Surface(modifier = Modifier
                    .width(circleSize / 2)
                    .height(circleSize / 2)
                    .offset(offsetRight.first + marginCircles, 0.dp)
                    .align(Alignment.CenterStart)
                    .pointerInteropFilter { event ->
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                rightClicked = true
                                leftClicked = false
                            }
                        }
                        true
                    }
                    .padding(2.dp),
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp,
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.background
                ){

                }
            }

        }
    }
}

@Composable
fun SwitchWithDescription(text : String = "", fontWeight: FontWeight = FontWeight.Normal, fontSize : TextUnit = 14.sp, checked : Boolean = false, enableFunction : (Boolean) -> Unit){

    val icon: (@Composable () -> Unit)? = if (checked) {
        {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                modifier = Modifier.size(SwitchDefaults.IconSize),
                tint = MaterialTheme.colorScheme.surface
            )
        }
    } else {
        null
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp, 2.dp),
        horizontalAlignment = Alignment.Start
    ) {

        Text(
            text = text,
            modifier = Modifier.padding(4.dp, 0.dp, 0.dp, 0.dp),
            fontWeight = fontWeight,
            fontSize = fontSize,
        )

        Switch(
            modifier = Modifier.semantics { contentDescription = "Demo with icon" },
            checked = checked,
            onCheckedChange = {
                enableFunction(it)
                Log.d("Threshold", "Switch changed value")
            },
            colors = SwitchDefaults.colors(
                checkedBorderColor = MaterialTheme.colorScheme.surface,
                checkedTrackColor = MaterialTheme.colorScheme.surface,
                uncheckedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                uncheckedTrackColor = MaterialTheme.colorScheme.onSecondaryContainer,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                checkedThumbColor = MaterialTheme.colorScheme.outline
            ),
            thumbContent = icon
        )
    }
}