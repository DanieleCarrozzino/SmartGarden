package com.example.smartgarden.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.smartgarden.ui.theme.Black
import com.example.smartgarden.ui.theme.LightLightGray
import com.example.smartgarden.utility.Utility
import com.example.smartgarden.viewmodels.CameraViewModel

@Composable
@Preview
fun InstantCameraPreview(){
    InstantCameraCore()
}

@Composable
fun InstantCameraScreen(navController: NavController, viewModel: CameraViewModel){
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    // Status bar height
    val density = LocalDensity.current.density
    val statusHeight        = Utility.getStatusBarSize(LocalContext.current.resources) / density
    val navigationHeight    = Utility.getNavigationBarSize(LocalContext.current.resources) / density

    InstantCameraCore(
        screenWidth,
        statusHeight.dp,
        navigationHeight.dp,
        viewModel.instantCameraUrl,
        viewModel.buttonEnable,
        viewModel::takePicture
    )
}

@Composable
fun InstantCameraCore(
    screenWidth : Dp = 400.dp,
    statusHeight : Dp = 5.dp,
    navigationHeight : Dp = 5.dp,

    url : MutableState<String> = mutableStateOf(""),
    enable : MutableState<Boolean> = mutableStateOf(true),

    takePhoto : () -> Unit = {}
){

    val urlImage by remember {
        url
    }

    val buttonEnable by remember {
        enable
    }

    /**
     * Zoom gesture
     * */
    var scale by remember { mutableFloatStateOf(1f) }
    var translationX by remember { mutableFloatStateOf(0f) }
    var translationY by remember { mutableFloatStateOf(0f) }

    Box(modifier = Modifier.fillMaxSize().background(Black)){

        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit){
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

        Surface(modifier = Modifier
            .align(Alignment.TopCenter)
            .fillMaxWidth()
            .padding(statusHeight),
            color = Color.White,
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 4.dp,
            shadowElevation = 4.dp
        ){
            Text(
                text = urlImage,
                modifier = Modifier.padding(15.dp)
            )
        }

        Box(modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(navigationHeight)){

            if(enable.value){
                Surface(modifier = Modifier
                    .padding(30.dp)
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
                        .padding(30.dp)
                        .size(screenWidth / 5),
                    color = Color.White,
                    strokeWidth = 10.dp
                )
            }
        }
    }

}