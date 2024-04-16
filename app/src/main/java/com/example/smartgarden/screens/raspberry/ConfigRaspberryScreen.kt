package com.example.smartgarden.screens.raspberry

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.smartgarden.R
import com.example.smartgarden.manager.RaspberryConnectionManager
import com.example.smartgarden.navigation.Screen
import com.example.smartgarden.screens.CustomSeekBar
import com.example.smartgarden.ui.theme.Green1
import com.example.smartgarden.utility.Utility
import com.example.smartgarden.viewmodels.MainViewModel
import com.google.common.util.concurrent.ListenableFuture

@Composable
fun ConfigRaspberryScreen(navController: NavController){

    val viewModel = hiltViewModel<MainViewModel>()

    val context         = LocalContext.current
    val lifecycleOwner  = LocalLifecycleOwner.current

    // Status and navigation bar height
    val density             = LocalDensity.current.density
    val statusHeight        = Utility.getStatusBarSize(context.resources) / density
    val navigationHeight    = Utility.getNavigationBarSize(context.resources) / density

    val cameraProviderFuture    = ProcessCameraProvider.getInstance(context)
    val previewView             = PreviewView(context)
    previewView.layoutParams    = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )

    ConfigRaspberryCore(
        navController           = navController,
        statusHeight            = statusHeight,
        navigationHeight        = navigationHeight,
        previewView             = previewView,
        cameraProviderFuture    = cameraProviderFuture,
        lifecycleOwner          = lifecycleOwner,
        context                 = context,
        doingConfiguration      = viewModel.doingConfiguration,
        bindCamera              = viewModel::bindCameraPreview
    )
}

@Preview
@Composable
fun ConfigRaspberryPreview(){

    val context         = LocalContext.current
    val lifecycleOwner  = LocalLifecycleOwner.current

    val cameraProviderFuture    = ProcessCameraProvider.getInstance(context)
    val previewView             = PreviewView(context)

    ConfigRaspberryCore(
        navController           = rememberNavController(),
        statusHeight            = 0f,
        navigationHeight        = 0f,
        previewView             = previewView,
        cameraProviderFuture    = cameraProviderFuture,
        lifecycleOwner          = lifecycleOwner,
        context                 = context
    )
}

@Composable
fun ConfigRaspberryCore(
    navController           : NavController,
    statusHeight            : Float, navigationHeight : Float,
    previewView             : PreviewView,
    cameraProviderFuture    : ListenableFuture<ProcessCameraProvider>,
    lifecycleOwner          : LifecycleOwner,
    context                 : Context,
    doingConfiguration      : MutableState<Boolean> = mutableStateOf(false),
    bindCamera : (ProcessCameraProvider, PreviewView, LifecycleOwner) -> Unit = {_, _, _ ->}
    ){
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp, statusHeight.dp, 0.dp, navigationHeight.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize(),
            update = { view ->
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    bindCamera(cameraProvider, view, lifecycleOwner)
                }, ContextCompat.getMainExecutor(context))
            }
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(
                    align = Alignment.Top
                ),
            shape = RoundedCornerShape(0.dp, 0.dp, 20.dp, 20.dp),
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = 7.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
        ){

            IconTitleAndDescription(
                iconId = R.drawable.qr_icon,
                title = "QR Camera Connection: Linking Your Raspberry Garden",
                description = "To link your Raspberry Garden, connect this device to the same network of your raspberry and then simply aim your camera at the QR code",
                Modifier.padding(20.dp)
            )

        }

        val config by remember {
            doingConfiguration
        }

        if(config){
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(64.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeWidth = 8.dp,
                )
            }

            BottomStatusAnimated(navController)

        }
        else{
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(70.dp),
                painter = painterResource(id = R.drawable.scanner),
                contentDescription = "scanner",
                alignment = Alignment.Center)
        }
    }
}

@Composable
fun BottomStatusAnimated(navController: NavController){

    val viewModel = hiltViewModel<MainViewModel>()
    var close by remember {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(
                align = Alignment.Bottom
            ),
        shape = RoundedCornerShape(20.dp, 20.dp, 0.dp, 0.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 7.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
    ){

        Column {

            val liveStatusConfig = viewModel.statusConfiguration.observeAsState().value

            AnimatedContent(
                targetState = liveStatusConfig,
                transitionSpec = {
                    fadeIn() + slideInVertically(animationSpec = tween(400),
                        initialOffsetY = { fullHeight -> fullHeight }) togetherWith
                            fadeOut(animationSpec = tween(200))
                },
                label = ""
            ) { state ->

                var title = "Default title"
                val description = "Please keep this page open to complete the procedure."

                when (state) {
                    RaspberryConnectionManager.RaspberryStatus.INIT -> {
                        title = "Init process"
                    }

                    RaspberryConnectionManager.RaspberryStatus.GET_CODE -> {
                        title = "Getting code"
                    }

                    RaspberryConnectionManager.RaspberryStatus.CREATE_CONFIGURATOR_FILE -> {
                        title = "Create your personal file"
                    }

                    RaspberryConnectionManager.RaspberryStatus.SETTING_RASP_INFO -> {
                        title = "Setting the raspberry"
                    }

                    RaspberryConnectionManager.RaspberryStatus.SENDING_FILE -> {
                        title = "Sending file"
                    }

                    RaspberryConnectionManager.RaspberryStatus.CONNECTED -> {
                        title = "Connected!"
                    }

                    RaspberryConnectionManager.RaspberryStatus.SENT -> {
                        title = "Sent!"
                    }

                    RaspberryConnectionManager.RaspberryStatus.DISCONNECTED -> {
                        title = "Disconnected"
                    }

                    RaspberryConnectionManager.RaspberryStatus.ERROR -> {
                        title = "Error"
                    }

                    RaspberryConnectionManager.RaspberryStatus.FINISHED -> {
                        val route = navController.previousBackStackEntry?.destination?.route
                        if (route == Screen.Home.route) {
                            if (!close)
                                navController.popBackStack()
                        } else {
                            if (!close)
                                navController.navigate(Screen.Home.route)
                        }

                        close = true
                        return@AnimatedContent
                    }

                    else -> {
                        Log.d("RaspScreen", "Something bad is happened")
                    }
                }
                IconTitleAndDescription(
                    iconId = -1,
                    title = title,
                    description = description,
                    Modifier.padding(20.dp)
                )
            }

            CustomSeekBar(liveStatusConfig?.ordinal ?: 0,
                "",
                Modifier
                    .fillMaxWidth()
                    .padding(25.dp), Green1,
                RaspberryConnectionManager.RaspberryStatus.entries.size
            )
        }
    }
}

@Composable
fun IconTitleAndDescription(iconId : Int, title : String, description : String, modifier: Modifier){
    Row(modifier = modifier){
        if(iconId > 0){
            Image(
                modifier = Modifier
                    .width(60.dp)
                    .aspectRatio(1f)
                    .padding(0.dp, 0.dp, 10.dp, 0.dp)
                    .align(Alignment.CenterVertically),
                painter = painterResource(id = iconId),
                contentDescription = "qr icon",
                alignment = Alignment.Center)
        }
        Column {
            Text(
                text = title,
                modifier = Modifier.align(Alignment.Start),
                textAlign = TextAlign.Start,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Text(
                text = description,
                modifier = Modifier.align(Alignment.Start),
                textAlign = TextAlign.Start,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}