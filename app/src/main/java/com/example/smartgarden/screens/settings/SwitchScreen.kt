package com.example.smartgarden.screens.settings

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseOutElastic
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smartgarden.R
import com.example.smartgarden.ui.theme.BlackText
import com.example.smartgarden.ui.theme.DarkBlue
import com.example.smartgarden.ui.theme.LightBlue
import com.example.smartgarden.ui.theme.White
import com.example.smartgarden.utility.Utility
import com.example.smartgarden.viewmodels.SettingsViewModel

@Composable
fun SwitchScreen(navController: NavController){

    val viewModel       = hiltViewModel<SettingsViewModel>()

    // Status bar height
    val density = LocalDensity.current.density
    val statusHeight = Utility.getStatusBarSize(LocalContext.current.resources) / density
    val navigationHeight = Utility.getNavigationBarSize(LocalContext.current.resources) / density

    val status by remember {
        viewModel.activated
    }

    DisposableEffect(true) {
        onDispose {
            viewModel.changeLimits()
        }
    }

    val color by animateColorAsState(
        targetValue = if(status) LightBlue else DarkBlue,
        animationSpec = TweenSpec(durationMillis = 800),
        label = "",
    )

    val textColor by animateColorAsState(
        targetValue = if(status) BlackText else White,
        animationSpec = TweenSpec(durationMillis = 400),
        label = "",
    )

    Surface(modifier = Modifier
        .fillMaxSize(),
        color = color.copy(alpha = 0.2f)){

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(0.dp, statusHeight.dp, 0.dp, navigationHeight.dp)){

//            Text(
//                text = "Switch screen",
//                modifier = Modifier
//                    .padding(10.dp),
//                fontWeight = FontWeight.Bold,
//                fontSize = 24.sp,
//                color = MaterialTheme.colorScheme.onBackground
//            )

            Column(modifier = Modifier.align(Alignment.Center)) {
                Text(
                    text = if(status) "Il tuo giardino è\nattivo e funzionante!" else "Il tuo giardino è\nin pausa",
                    modifier = Modifier
                        .padding(20.dp)
                        .align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                )

                BigSwitchWithImages(
                    image1 = R.drawable.garden_active,
                    image2 = R.drawable.garden_not_active,
                    viewModel.activated,
                    modifier = Modifier.align(Alignment.CenterHorizontally)){
                    viewModel.activated.value = it
                    viewModel.setActivation(it)
                }
            }
        }
    }
}


@Composable
fun BigSwitchWithImages(image1 : Int, image2 : Int,
                        activate : MutableState<Boolean>,
                        modifier: Modifier = Modifier,
                        clickFunction : (Boolean) -> Unit
){

    val translateX  = with(LocalDensity.current) { (96.dp).toPx() }
    val animate by remember { activate }

    val animatedValue by animateFloatAsState(
        targetValue = if (animate) translateX else 0f,
        animationSpec = tween(durationMillis = 600, easing = EaseOutElastic),
        label = ""
    )

    val animatedValue2 by animateFloatAsState(
        targetValue = if (animate) translateX else 0f,
        animationSpec = tween(durationMillis = 800, easing = EaseOutElastic),
        label = ""
    )

    val animatedValue3 by animateFloatAsState(
        targetValue = if (animate) translateX else 0f,
        animationSpec = tween(durationMillis = 1000, easing = EaseOutElastic),
        label = ""
    )

    val circlesColor by animateColorAsState(
        targetValue = if(animate) LightBlue else DarkBlue,
        animationSpec = TweenSpec(durationMillis = 800),
        label = "",
    )


    Surface(modifier = modifier
        .padding(14.dp)
        .height(80.dp)
        .aspectRatio(2.2f)
        ,
        shape = RoundedCornerShape(40.dp),
        shadowElevation = 8.dp,
        tonalElevation = 8.dp,
        color = MaterialTheme.colorScheme.background
    ) {

        AnimatedContent(
            targetState = animate,
            label = "Charts animation",
            transitionSpec = {
                fadeIn(
                    animationSpec = tween(500)
                ) togetherWith
                        fadeOut(animationSpec = tween(0))
            }
        )
        { animateValue ->
            Image(
                modifier = Modifier
                    .fillMaxWidth(),
                painter = painterResource(id = if(animateValue) image1 else image2),
                contentDescription = "Background big switch image",
                contentScale = ContentScale.Crop,
            )
        }



        Box(modifier = Modifier.fillMaxSize()
            .clickable {
                clickFunction(!animate)
            }){

            Surface(modifier = Modifier
                .width(158.dp)
                .aspectRatio(1f)
                .graphicsLayer(
                    translationX = animatedValue3 - with(LocalDensity.current) { (40.dp).toPx() }
                )
                .alpha(0.6f)
                .padding(8.dp),
                tonalElevation = 4.dp,
                shadowElevation = 4.dp,
                color = circlesColor,
                shape = CircleShape
            ){}

            Surface(modifier = Modifier
                .width(125.dp)
                .aspectRatio(1f)
                .graphicsLayer(
                    translationX = animatedValue2 - with(LocalDensity.current) { (20.dp).toPx() }
                )
                .alpha(0.7f)
                .padding(8.dp),
                tonalElevation = 4.dp,
                shadowElevation = 4.dp,
                color = circlesColor,
                shape = CircleShape
            ){}

            Surface(modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
                .padding(3.dp)
                .graphicsLayer(
                    translationX = animatedValue
                ),
                tonalElevation = 4.dp,
                shadowElevation = 4.dp,
                color = White,
                shape = CircleShape
            ){

            }
        }
    }
}