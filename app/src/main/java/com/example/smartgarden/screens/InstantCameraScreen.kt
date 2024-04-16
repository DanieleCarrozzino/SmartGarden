package com.example.smartgarden.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.smartgarden.R
import com.example.smartgarden.ui.theme.Black
import com.example.smartgarden.ui.theme.BlackOpac
import com.example.smartgarden.ui.theme.BlackOpacOpac
import com.example.smartgarden.ui.theme.Gray
import com.example.smartgarden.ui.theme.LightLightGray
import com.example.smartgarden.ui.theme.White
import com.example.smartgarden.ui.theme.color4
import com.example.smartgarden.ui.theme.color4_dark
import com.example.smartgarden.ui.theme.ski
import com.example.smartgarden.ui.theme.ski_dark
import com.example.smartgarden.utility.Utility
import com.example.smartgarden.viewmodels.CameraViewModel

@Composable
@Preview
fun InstantCameraPreview(){
    InstantCameraCore()
}

@Composable
fun InstantCameraScreen(
    navController: NavController, viewModel: CameraViewModel
){
    val configuration = LocalConfiguration.current
    val screenWidth  = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    // Status bar height
    val density = LocalDensity.current.density
    val statusHeight        = Utility.getStatusBarSize(LocalContext.current.resources) / density
    val navigationHeight    = Utility.getNavigationBarSize(LocalContext.current.resources) / density

    var dataFetched by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!dataFetched) {
            viewModel.init()
            dataFetched = true
        }
    }

    InstantCameraCore(
        screenWidth, screenHeight,
        statusHeight.dp,
        navigationHeight.dp,
        viewModel.instantCameraName,
        viewModel.instantCameraUrl,
        viewModel.buttonEnable,
        viewModel.canDownload,
        viewModel::takePicture,
        viewModel::downloadInstant,
        viewModel::shareInstantImage
    )
}

@Composable
fun InstantCameraCore(
    screenWidth  : Dp = 400.dp,
    screenHeight : Dp = 700.dp,
    statusHeight : Dp = 5.dp,
    navigationHeight : Dp = 5.dp,

    name    : MutableState<String> = mutableStateOf(""),
    url     : MutableState<String> = mutableStateOf(""),
    enable          : MutableState<Boolean> = mutableStateOf(true),
    enableDownload  : MutableState<Boolean> = mutableStateOf(true),

    takePhoto   : () -> Unit = {},
    download    : () -> Unit = {},
    share       : () -> Unit = {}
){
    val urlImage by remember {
        url
    }

    /**
     * Zoom gesture
     * */
    var scale by remember { mutableFloatStateOf((screenHeight / screenWidth) * 2f) }
    var translationX by remember { mutableFloatStateOf(0f) }
    var translationY by remember { mutableFloatStateOf(0f) }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Black)){

        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            scale = 1f
                            translationX = 0f
                            translationY = 0f
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale *= zoom
                        translationX += pan.x
                        translationY += pan.y
                    }
                }
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = translationX,
                    translationY = translationY
                ),
            model = urlImage,
            contentScale = ContentScale.Fit,
            contentDescription = "")


        //************
        // INFO BOX
        //************
//        InfoBoxInstantScreen(
//            modifier = Modifier
//                .align(Alignment.TopCenter),
//            statusHeight = statusHeight,
//            nameImage = nameImage,
//            screenWidth = screenWidth)
        SmallInfoBoxInstantScreen(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(0.dp, statusHeight + 5.dp, 0.dp, 0.dp),
            dateNotFormatted = name
        )


        //*********************
        // BOTTOM CAMERA LAYOUT
        //*********************
        BottomPictureLayout(
            Modifier.align(Alignment.BottomCenter),
            screenWidth, screenHeight, navigationHeight, enable, enableDownload, takePhoto, download, share
        )
    }

}

@Composable
fun BottomPictureLayout(
    modifier: Modifier = Modifier,
    screenWidth  : Dp = 400.dp,
    screenHeight : Dp = 700.dp,
    navigationHeight : Dp = 5.dp,
    enable          : MutableState<Boolean> = mutableStateOf(true),
    enableDownload  : MutableState<Boolean> = mutableStateOf(true),

    takePhoto   : () -> Unit = {},
    download    : () -> Unit = {},
    share       : () -> Unit = {}
){

    Box(modifier = modifier
        .fillMaxWidth()
        .background(BlackOpacOpac)
    ){

        Box(modifier = Modifier
            .width(screenWidth / 5)
            .height(4.dp)
            .clip(RoundedCornerShape(0.dp, 0.dp, 3.dp, 3.dp))
            .background(White)
            .align(Alignment.TopCenter)
        )

        Row(modifier = Modifier
            .wrapContentSize()
            .align(Alignment.Center)) {
           // Download button
            DownloadButton(
                modifier    = Modifier.align(Alignment.CenterVertically),
                width       = screenWidth,
                size        = 7,
                fontSize    = 14.sp,
                download    = download,
                enable      = enableDownload
                )

            // Picture button
            PictureButton(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(20.dp, 0.dp),
                screenWidth, screenHeight, navigationHeight, enable, takePhoto)

            // Share button
            ShareButton(
                modifier    = Modifier.align(Alignment.CenterVertically),
                width       = screenWidth,
                size        = 7,
                fontSize    = 14.sp,
                share       = share
            )
        }


    }
}

@Composable
fun PictureButton(
    modifier        : Modifier,
    screenWidth     : Dp = 400.dp,
    screenHeight    : Dp = 700.dp,
    navigationHeight : Dp = 5.dp,
    enable  : MutableState<Boolean> = mutableStateOf(true),

    takePhoto : () -> Unit = {}
){

    val buttonEnable by remember {
        enable
    }

    Column(modifier = modifier.padding(0.dp, 15.dp)) {
        Text(
            text = "Picture",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(0.dp, 15.dp, 0.dp, 10.dp),
            color = White,
            fontWeight = FontWeight.Bold,
        )

        Box(modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(0.dp, 0.dp, 0.dp, navigationHeight)){

            if(enable.value){
                Surface(modifier = Modifier
                    .size(screenWidth / 5),
                    shape = CircleShape,
                    color = LightLightGray,
                    shadowElevation = 4.dp,
                    tonalElevation = 4.dp
                ){
                    Surface(modifier = Modifier
                        .padding(5.dp)
                        .fillMaxSize(),
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 4.dp,
                        tonalElevation = 4.dp
                    ){
                        Box(modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                if (buttonEnable)
                                    takePhoto()
                            })
                    }
                }
            }
            else{
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(screenWidth / 5),
                    color = Color.White,
                    strokeWidth = 1.dp
                )
            }
        }
    }
}

@Preview
@Composable
fun SmallInfoBoxPreview(){
    SmallInfoBoxInstantScreen()
}

@Composable
fun SmallInfoBoxInstantScreen(
    modifier            : Modifier = Modifier,
    dateNotFormatted    : MutableState<String> = mutableStateOf("")
){
    val date by remember {
        dateNotFormatted
    }

    Surface(
        modifier = modifier,
        color = White,
        shape = RoundedCornerShape(18.dp),
        shadowElevation = 4.dp,
        tonalElevation = 4.dp
        ) {
        Text(
            text = Utility.stringToDate(
                date.ifEmpty { "2012-12-12_12-12-123" }
            ).toString(),
            color = Black,
            modifier = Modifier
                .padding(12.dp, 6.dp, 12.dp, 6.dp),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start,
        )
    }

}

@Composable
fun InfoBoxInstantScreen(
    modifier        : Modifier = Modifier,
    statusHeight    : Dp,
    nameImage       : String = "2012-12-12_12-12-123",
    screenWidth     : Dp = 400.dp
){
    val date = Utility.stringToDate(
        nameImage.ifEmpty { "2012-12-12_12-12-123" }
    )
    val night by remember {
        mutableStateOf((date.hours > 19 || date.hours < 9))
    }

    var firstOpening by remember {
        mutableStateOf(true)
    }
    LaunchedEffect(key1 = firstOpening) {
        firstOpening = false
    }

    val sky by animateColorAsState(
        targetValue = if(firstOpening) White else (if(night) ski_dark else ski),
        animationSpec = TweenSpec(durationMillis = 4000),
        label = "",
    )

    val infiniteAnimationValue = rememberInfiniteTransition(label = "")
    val cloudAnimation by infiniteAnimationValue.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 3000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse // Change to Mirror for different effect
        ), label = ""
    )

    //***************
    // Info boxes
    //***************
    Row(
        modifier
            .fillMaxWidth()
            .padding(10.dp, statusHeight, 10.dp, 0.dp)
    ) {

        Surface(modifier = Modifier
            .fillMaxWidth()
            .weight(4f)
            .padding(5.dp),
            color = sky,
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 4.dp,
            shadowElevation = 4.dp
        ){
            Row(modifier = Modifier.padding(15.dp)) {
                Image(
                    modifier = Modifier
                        .size(26.dp)
                        .padding(14.dp)
                        .align(Alignment.CenterVertically),
                    contentScale = ContentScale.Fit,
                    painter = painterResource(id = R.drawable.calendar),
                    contentDescription = "1")
                Text(
                    text = date.toString(),
                    color = if(night) White else Black,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(7.dp, 0.dp, 5.dp, 0.dp),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                )
            }
        }

        Surface(modifier = Modifier
            .fillMaxWidth()
            .weight(2f)
            .aspectRatio(1f)
            .padding(5.dp),
            color = sky,
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 4.dp,
            shadowElevation = 4.dp
        ){
            Text(
                text = "Meteo",
                color = if(night) White else Black,
                modifier = Modifier
                    .align(Alignment.Top)
                    .fillMaxWidth()
                    .padding(0.dp, 5.dp, 0.dp, 0.dp),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Box(modifier = Modifier.padding(12.dp)){
                Image(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(14.dp)
                        .graphicsLayer {
                            scaleX = 0.910f + cloudAnimation / 4
                            scaleY = 0.910f + cloudAnimation / 4
                        },
                    contentScale = ContentScale.Fit,
                    painter = painterResource(id = if(night)
                        R.drawable.moon else R.drawable.sun),
                    contentDescription = "1")

                Image(
                    modifier = Modifier
                        .graphicsLayer(
                            translationX = 20f * cloudAnimation
                        )
                        .align(Alignment.BottomCenter)
                        .padding(0.dp, 20.dp, screenWidth.div(9), (cloudAnimation * 10).dp),
                    contentScale = ContentScale.Fit,
                    painter = painterResource(id = R.drawable.cloud2),
                    contentDescription = "1")

                Image(
                    modifier = Modifier
                        .graphicsLayer(
                            translationX = -20f * cloudAnimation
                        )
                        .align(Alignment.BottomCenter)
                        .padding((screenWidth.div(10)), 20.dp, 0.dp, (cloudAnimation * 6).dp),
                    contentScale = ContentScale.Fit,
                    painter = painterResource(id = R.drawable.cloud),
                    contentDescription = "1")
            }
        }
    }
}

@Preview
@Composable
fun MeteoBoxPreview() {
    InfoBoxInstantScreen(statusHeight = 5.dp)
}