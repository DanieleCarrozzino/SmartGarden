package com.example.smartgarden.screens.settings

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseInBounce
import androidx.compose.animation.core.EaseInOutElastic
import androidx.compose.animation.core.EaseOutElastic
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
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
import com.example.smartgarden.navigation.Screen
import com.example.smartgarden.ui.theme.BlackText
import com.example.smartgarden.ui.theme.Blue1
import com.example.smartgarden.ui.theme.Blue20
import com.example.smartgarden.ui.theme.DarkBlue
import com.example.smartgarden.ui.theme.Green80
import com.example.smartgarden.ui.theme.LightBlue
import com.example.smartgarden.ui.theme.Pink40
import com.example.smartgarden.ui.theme.Red
import com.example.smartgarden.ui.theme.WhiteOpac
import com.example.smartgarden.utility.Utility
import com.example.smartgarden.viewmodels.MainViewModel
import com.example.smartgarden.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen(navController: NavController){
    val viewModel = hiltViewModel<SettingsViewModel>()

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
            MainSettingsLayout(navController = navController, viewModel)
        }
    }
}

@Composable
fun MainSettingsLayout(navController: NavController, viewModel: SettingsViewModel){

    Column(modifier = Modifier
        .padding(8.dp)) {

        Text(
            text = "Settings screen",
            modifier = Modifier
                .padding(25.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        Divider(
            modifier = Modifier
                .padding(0.dp, 10.dp, 0.dp, 10.dp)
                .fillMaxWidth(),
            color = Color.Transparent,
            thickness = 0.dp
        )

        singleItemSettings(
            title = "Threshold screen",
            description = "modify the threshold of your garden",
            R.drawable.ic_threshold,
            MaterialTheme.colorScheme.onBackground) {
            navController.navigate(Screen.Threshold.route)
        }

        Divider(
            modifier = Modifier
                .padding(horizontal = 0.dp, vertical = 10.dp)
                .fillMaxWidth(),
            color = Color.Transparent,
            thickness = 0.dp
        )

        singleItemSettings(
            title = "Notification screen",
            description = "what notification you want receive",
            R.drawable.ic_notification,
            MaterialTheme.colorScheme.onBackground) {
            navController.navigate(Screen.Notification.route)
        }

        Divider(
            modifier = Modifier
                .padding(horizontal = 0.dp, vertical = 6.dp)
                .fillMaxWidth(),
            color = Color.Transparent,
            thickness = 0.dp
        )

        singleItemSettings(
            title = "Logout",
            description = "",
            R.drawable.ic_logout,
            Red) {
            viewModel.signout()
            navController.navigate(Screen.Login.route)
        }

        //ic_logout
    }
}

@Composable
fun singleItemSettings(title : String, description : String, image : Int, color : Color = BlackText, click : () -> Unit){

    Box(
        modifier = Modifier
            .padding(20.dp, 3.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable {
                click()
            }
    ) {

        Row {
            Image(
                modifier = Modifier
                    .size(40.dp)
                    .padding(10.dp, 0.dp)
                    .align(Alignment.CenterVertically),
                painter = painterResource(id = image),
                contentDescription = "",
                colorFilter = ColorFilter.tint(color))

            Column(
                modifier = Modifier
                    .padding(18.dp, 6.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    fontSize = 15.sp
                )
                if(description != ""){
                    Text(
                        text = description,
                        color = color)
                }

            }
        }


    }
}