package com.example.smartgarden.screens.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smartgarden.utility.Utility
import com.example.smartgarden.viewmodels.SettingsViewModel

@Composable
fun NotificationSettingsScreen(navController: NavController){

    val viewModel = hiltViewModel<SettingsViewModel>()

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
            MainNotificationLayout(screenWidth, viewModel)
        }
    }
}

@Composable
fun MainNotificationLayout(width : Dp, viewModel: SettingsViewModel){

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

        // Title and description
        Text(
            text = "Notification screen",
            modifier = Modifier
                .padding(10.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        SwitchWithDescription(
            text = "Activate notifications to receive update",
            checked = viewModel.enabledNotifications.value,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        {
            viewModel.enabledNotifications.value = it
        }

        SwitchWithDescription(
            text = "Activate notifications to stay informed about temperature updates, whether it reaches a maximum or minimum value",
            checked = viewModel.enabledTemperature.value
        )
        {
            viewModel.enabledTemperature.value = it
            if(it){
                viewModel.alphaTemperature.floatValue = 1f
            } else viewModel.alphaTemperature.floatValue = 0.3f
        }

        SwitchWithDescription(
            text = "Activate notifications to stay informed about the number of times the garden has been irrigated",
            checked = viewModel.enabledIrrigation.value
        )
        {
            viewModel.enabledIrrigation.value = it
        }

    }

}