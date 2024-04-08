package com.example.smartgarden.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.alpha
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.Player
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.smartgarden.R
import com.example.smartgarden.screens.settings.MainSettingsLayout
import com.example.smartgarden.ui.theme.Black
import com.example.smartgarden.ui.theme.Gray
import com.example.smartgarden.ui.theme.Green
import com.example.smartgarden.ui.theme.Green1
import com.example.smartgarden.ui.theme.Green80
import com.example.smartgarden.ui.theme.LightLightGray
import com.example.smartgarden.ui.theme.White
import com.example.smartgarden.ui.theme.WhiteOpac
import com.example.smartgarden.ui.theme.WhiteOpacOpac
import com.example.smartgarden.utility.Utility
import com.example.smartgarden.viewmodels.CameraViewModel
import com.example.smartgarden.viewmodels.SettingsViewModel
import kotlin.math.abs

@Composable
fun CameraCore(
    videoVisibility : MutableState<Boolean> = mutableStateOf(true),
    cameraUrl : MutableState<String> = mutableStateOf(""),
    enableDownload : MutableState<Boolean> = mutableStateOf(true),
    player : Player? = null,

    screenWidth : Dp = 400.dp,
    statusHeight : Dp = 5.dp,
    navigationHeight : Dp = 5.dp,

    getImageAndVideoUrl : () -> Unit = {},
    releaseVideo : () -> Unit = {},
    startVideo : () -> Unit = {},
    download : () -> Unit = {},
    share : () -> Unit = {}
){
    val visibility by remember {
        videoVisibility
    }

    DisposableEffect(Unit){
        onDispose {
            releaseVideo()
        }
    }

    Surface(modifier = Modifier
        .fillMaxSize(),
        color = MaterialTheme.colorScheme.background){

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(0.dp, 0.dp, 0.dp, 0.dp)){

            AndroidView(
                factory = { context ->
                    PlayerView(context).also {
                        it.player = player
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .clip(RoundedCornerShape(0.dp, 0.dp, 12.dp, 12.dp))
                    .background(Black)
            ){

            }

            if(visibility) {
                TopCameraLayout(
                    Modifier.fillMaxHeight(0.5f),
                    cameraUrl,
                ) {
                    startVideo()
                }
            }

            BottomCameraLayout(
                Modifier
                    .fillMaxHeight(0.5f)
                    .align(Alignment.BottomCenter),
                screenWidth,
                navigationHeight,
                enableDownload,
                download, share
            )
        }
    }
}

@Preview
@Composable
fun CameraPreview(){
    CameraCore()
}

@Composable
fun CameraScreen(navController: NavController, viewModel: CameraViewModel){
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    // Status bar height
    val density = LocalDensity.current.density
    val statusHeight        = Utility.getStatusBarSize(LocalContext.current.resources) / density
    val navigationHeight    = Utility.getNavigationBarSize(LocalContext.current.resources) / density

    CameraCore(
        viewModel.videoVisibility,
        viewModel.cameraUrl,
        viewModel.canDownload,
        viewModel.player,

        screenWidth,
        statusHeight.dp,
        navigationHeight.dp,

        viewModel::getImageAndVideoUrl,
        viewModel::releaseVideo,
        viewModel::startVideo,
        viewModel::download,
        viewModel::share
    )
}

@Composable
fun TopCameraLayout(
    modifier: Modifier = Modifier,
    url : MutableState<String> = mutableStateOf(""),
    clickVideo : () -> Unit){

    val cameraUrl by remember {
        url
    }

    Box(modifier = modifier){

        AsyncImage(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            model = cameraUrl,
            contentScale = ContentScale.Crop,
            contentDescription = "")

        // Play button
        Box(modifier = Modifier
            .clip(CircleShape)
            .background(WhiteOpac)
            .align(Alignment.Center)
            .clickable {
                clickVideo()
            }
            ){
            Image(
                modifier = Modifier
                    .fillMaxWidth(0.2f)
                    .aspectRatio(1f)
                    .padding(20.dp),
                painter = painterResource(id = R.drawable.ic_play),
                contentDescription = "")
        }

        // Top info
//        Box(modifier = Modifier
//            .padding(10.dp, statusBarHeight + 10.dp, 10.dp, 10.dp)
//            .fillMaxWidth(0.3f)
//            .aspectRatio(1f)
//            .align(Alignment.TopStart)
//            .clip(RoundedCornerShape(14.dp))
//            .background(WhiteOpacOpac)){
//
//            Image(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .fillMaxHeight(0.66f)
//                    .padding(10.dp),
//                painter = painterResource(id = R.drawable.sun),
//                contentDescription = "")
//
//            Text("24 s",
//                modifier = Modifier
//                    .align(Alignment.BottomCenter)
//                    .padding(3.dp),
//                textAlign = TextAlign.Center)
//
//        }

        // Bottom text
        Box(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(10.dp)
            .align(Alignment.BottomCenter)
            .clip(RoundedCornerShape(14.dp))
            .background(WhiteOpacOpac)){

            Column(modifier = Modifier.padding(15.dp)) {
                Text(
                    text = "TimeLaps del giardino",
                    modifier = Modifier.align(Alignment.Start),
                    textAlign = TextAlign.Start,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Text(
                    text = "Un incredibile timelaps da quando questo giardino è attivo",
                    modifier = Modifier.align(Alignment.Start),
                    textAlign = TextAlign.Start,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }

        }
    }
}

@Composable
fun BottomCameraLayout(
    modifier: Modifier,
    width : Dp, navigationHeight : Dp,
    enable : MutableState<Boolean> = mutableStateOf(true),
    download : () -> Unit = {},
    share : () -> Unit = {}
){

    Box(modifier = modifier){

        Column(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .align(Alignment.TopCenter)) {
            // Top text info
            Box(modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(10.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(LightLightGray)){

                Column(modifier = Modifier.padding(15.dp)) {
                    Text(
                        text = "Time daily photo",
                        modifier = Modifier.align(Alignment.Start),
                        textAlign = TextAlign.Start,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Text(
                        text = "Choose a time interval for taking the daily photo",
                        modifier = Modifier.align(Alignment.Start),
                        textAlign = TextAlign.Start,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }

            }

            Text(
                text = "12:00",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(10.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                )
        }

        // Multiple balloons
        BottomImage(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .align(Alignment.BottomCenter))

        Column(modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(0.dp, 0.dp, 0.dp, navigationHeight + 20.dp)) {

            BottomButton(
                width = width,
                enable = enable,
                download = download,
                share = share)
        }

    }
}

@Composable
fun BottomButton(
    width : Dp,
    enable : MutableState<Boolean> = mutableStateOf(true),
    share : () -> Unit = {},
    download : () -> Unit = {}
){

    val enableDownload by remember {
        enable
    }

    Row(modifier = Modifier
        .wrapContentSize()
    ) {

        // Download
        Column(modifier = Modifier.align(Alignment.CenterVertically)) {

            Text(
                text = "download",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(0.dp, 0.dp, 0.dp, 10.dp),
                color = MaterialTheme.colorScheme.background,
                fontSize = 15.sp)


            if(enableDownload){
                Surface(
                    modifier = Modifier
                        .padding(15.dp, 0.dp)
                        .width(width / 6)
                        .aspectRatio(1f),
                    shadowElevation = 4.dp,
                    tonalElevation = 4.dp,
                    shape = CircleShape,
                    color = LightLightGray){

                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        shadowElevation = 2.dp,
                        tonalElevation = 2.dp,
                        shape = CircleShape,
                        color = White){
                        Box(modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                download()
                            }) {
                            Image(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(width / 30)
                                    .align(Alignment.Center),
                                painter = painterResource(id = R.drawable.ic_download),
                                contentDescription = "",
                                colorFilter = ColorFilter.tint(Gray)
                            )
                        }
                    }

                }
            }else{
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(15.dp, 0.dp)
                        .size(width / 6),
                    color = Color.White,
                    strokeWidth = 10.dp
                )
            }
        }

        // Share
        Column(modifier = Modifier.align(Alignment.CenterVertically)) {

            Text(
                text = "share",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(0.dp, 0.dp, 0.dp, 10.dp),
                color = MaterialTheme.colorScheme.background,
                fontSize = 15.sp)

            Surface(
                modifier = Modifier
                    .padding(15.dp, 0.dp)
                    .width(width / 6)
                    .aspectRatio(1f),
                shadowElevation = 4.dp,
                tonalElevation = 4.dp,
                shape = CircleShape,
                color = LightLightGray
            ) {

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    shadowElevation = 2.dp,
                    tonalElevation = 2.dp,
                    shape = CircleShape,
                    color = White
                ) {
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            share()
                        }){
                        Image(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(width / 30)
                                .align(Alignment.Center),
                            painter = painterResource(id = R.drawable.ic_share),
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(Gray))
                    }
                }

            }
        }

    }
}

@Composable
fun BottomImage(modifier: Modifier){

    Box(modifier = modifier){
        ChartBoxSemiCircle()
    }

//    // Main Image
//    Box(modifier = modifier
//        .width(width * 2)
//        .aspectRatio(1f)
//        .offset(x = - width / 2, y = width / 2),) {
//
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .clip(CircleShape)
//                .background(Green80),
//        ) {
//
//        }
//    }
}