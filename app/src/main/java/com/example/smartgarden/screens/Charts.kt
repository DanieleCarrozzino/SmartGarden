package com.example.smartgarden.screens

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.Group
import androidx.compose.ui.graphics.vector.PathNode
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartgarden.R
import com.example.smartgarden.ui.theme.Blue20
import com.example.smartgarden.ui.theme.Green
import com.example.smartgarden.ui.theme.Green80
import com.example.smartgarden.ui.theme.Opac
import com.example.smartgarden.ui.theme.Purple40
import com.example.smartgarden.ui.theme.Purple80
import com.example.smartgarden.utility.Utility.Companion.getPath
import com.example.smartgarden.utility.Utility.Companion.getPath2
import com.example.smartgarden.utility.Utility.Companion.getPathNodes
import com.example.smartgarden.utility.Utility.Companion.lerp
import com.example.smartgarden.viewmodels.MainViewModel
import java.text.DecimalFormat
import kotlin.math.pow

@Composable
fun SingleChartBox(title : String, perc : Float){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {

        Canvas(modifier = Modifier
            .padding(0.dp)
            .fillMaxWidth()
            .fillMaxHeight()){

            val path = Path().apply {

                //Start position
                var prevOffset = Offset(0f, size.height)
                moveTo(prevOffset.x, prevOffset.y)
                lineTo(prevOffset.x, prevOffset.y)

                // every 100 pixels define a point
                for(i in 100..size.width.toInt() step 100){
                    val y = (0..(size.height).toInt()).random().toFloat()
                    quadraticBezierTo(prevOffset.x, prevOffset.y, (i.toFloat() + prevOffset.x) / 2, (y + prevOffset.y) / 2)
                    prevOffset = Offset(i.toFloat(), y)
                }
                quadraticBezierTo(prevOffset.x, prevOffset.y, ((size.width + 100) + prevOffset.x) / 2, ((0..(size.height).toInt()).random().toFloat() + prevOffset.y) / 2)
                lineTo(size.width, size.height)
            }


            //This draw the graph point by point
            drawPath(
                path = path,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Purple80,
                        Purple40
                    ),
                    endY = size.height))
        }

        Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 4.dp, 0.dp, 6.dp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold,
                lineHeight = 15.sp
            )
            Text(
                text = "${DecimalFormat("##.##").format(perc)}%",
                modifier = Modifier
                    .padding(0.dp, 0.dp, 0.dp, 0.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                lineHeight = 22.sp,
                fontSize = if(perc > 1000f) 18.sp else if(perc > 100f) 21.sp else 23.sp
            )
        }
    }
}

@Composable
fun ChartBoxSemiCircle(){

    val listValues = mutableListOf<Float>()

    val intervalStart = 1.0
    val intervalEnd = 3
    val intervalStep = 0.1f

    var x = intervalStart
    while (x <= intervalEnd) {
        val y = kotlin.math.sqrt(4 - (x - 2).pow(2))
        listValues.add(y.toFloat())
        println("x = $x, y = $y")
        x += intervalStep
    }
    listValues.add(1.73205f)

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Canvas(modifier = Modifier
            .padding(0.dp)
            .fillMaxSize()){

            // Get the random path
            val path = getPath2(listValues, size)

            //This draw the graph point by point
            drawPath(
                path = path,
                color = Green80)
        }
    }
}

@Composable
fun ChartBoxWithArrayAnimated(array : List<Float>, color1 : Color, color2 : Color){

    var animateScale by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        animateScale = true
    }

    val listValues = mutableListOf<Float>()

    for (v in array){
        val animatedValue by animateFloatAsState(
            targetValue = if (animateScale) v else 0f,
            animationSpec = tween(durationMillis = 3000),
            label = ""
        )
        listValues.add(animatedValue)
    }

    val backgroundColor1 by animateColorAsState(
        targetValue = color1,
        animationSpec = TweenSpec(durationMillis = 1000),
        label = "", // Change these colors as needed
    )

    val backgroundColor2 by animateColorAsState(
        targetValue = color2,
        animationSpec = TweenSpec(durationMillis = 1000),
        label = "", // Change these colors as needed
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Canvas(modifier = Modifier
            .padding(0.dp)
            .fillMaxSize()){

            // Get the random path
            val path = getPath(listValues, size)

            //This draw the graph point by point
            drawPath(
                path = path,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor1,
                        backgroundColor2
                    ),
                    endY = size.height))
        }
    }
}


@Composable
fun ChartBoxInfo(title : String, perc : Float, imageId : Int, max : Float, min : Float, symbol : String){

    val cornerShape = 14.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        // MAX
        Surface(
            shape = RoundedCornerShape(cornerShape),
            modifier = Modifier
                .padding(5.dp)
                .align(Alignment.TopStart)
                .wrapContentWidth(),
            color = Opac,
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {

                Box(modifier = Modifier
                    .padding(3.dp)
                    .clip(RoundedCornerShape(cornerShape))
                    .background(MaterialTheme.colorScheme.background)){
                    Text(
                        modifier = Modifier.padding(4.dp),
                        text = String.format("%.2f", max) + symbol,
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp)
                }

                Text(
                    modifier = Modifier
                        .padding(3.dp, 3.dp, 6.dp, 3.dp),
                    text = "Max",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp)

            }
        }

        // MIN
        Surface(
            shape = RoundedCornerShape(cornerShape),
            modifier = Modifier
                .padding(5.dp)
                .align(Alignment.BottomStart)
                .wrapContentWidth(),
            color = Opac,
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {

                Box(modifier = Modifier
                    .padding(3.dp)
                    .clip(RoundedCornerShape(cornerShape))
                    .background(MaterialTheme.colorScheme.background)){
                    Text(
                        modifier = Modifier.padding(4.dp),
                        text = String.format("%.2f", min) + symbol,
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp)
                }

                Text(
                    modifier = Modifier
                        .padding(3.dp, 3.dp, 6.dp, 3.dp),
                    text = "Min",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp)

            }
        }

//        // Main panel
//        Surface(
//            shape = RoundedCornerShape(cornerShape),
//            modifier = Modifier
//                .padding(5.dp)
//                .align(Alignment.TopStart)
//                .fillMaxWidth(0.5f)
//                .wrapContentHeight(),
//            color = Opac,
//        ) {
//
//
//            Column(modifier = Modifier) {
//
//                Row(verticalAlignment = Alignment.Top,
//                    modifier = Modifier.padding(10.dp, 8.dp)) {
//                    Image(
//                        modifier = Modifier
//                            .width(50.dp)
//                            .aspectRatio(1f),
//                        painter = painterResource(id = imageId),
//                        contentDescription = "Image chart")
//
//                    Column(
//                        horizontalAlignment = Alignment.Start,
//                        modifier = Modifier.padding(5.dp, 0.dp)
//                    ) {
//                        Text(
//                            text = title,
//                            modifier = Modifier
//                                .padding(4.dp, 0.dp)
//                                .fillMaxWidth(),
//                            textAlign = TextAlign.Center,
//                            color = MaterialTheme.colorScheme.onPrimaryContainer,
//                            fontWeight = FontWeight.Bold,
//                            lineHeight = 14.sp
//                        )
//                        Text(
//                            text = "${DecimalFormat("##.##").format(perc)}%",
//                            modifier = Modifier
//                                .padding(4.dp, 0.dp)
//                                .fillMaxWidth(),
//                            textAlign = TextAlign.Center,
//                            fontWeight = FontWeight.Bold,
//                            color = MaterialTheme.colorScheme.onPrimaryContainer,
//                            lineHeight = 22.sp,
//                            fontSize = if (perc > 1000f) 18.sp else if (perc > 100f) 21.sp else 23.sp
//                        )
//                    }
//                }
//                Text(
//                    text = "Little description to create a fake description and info",
//                    modifier = Modifier
//                        .padding(10.dp, 0.dp, 10.dp, 10.dp)
//                        .fillMaxWidth(),
//                    textAlign = TextAlign.Start,
//                    color = MaterialTheme.colorScheme.onPrimaryContainer,
//                    fontWeight = FontWeight.Bold,
//                    lineHeight = 13.sp
//                )
//            }
//        }
    }
}

@Composable
fun ChatBoxWithArrayAnimated() : Painter{

    val viewModel   = hiltViewModel<MainViewModel>()
    val chart       = viewModel.chart.observeAsState().value

    // 0 or 1
    val pos = (chart?.type?.ordinal ?: 0f).toFloat() % 2

    return rememberVectorPainter(
        defaultWidth = 22.dp,
        defaultHeight = 22.dp,
        viewportWidth = 22f,
        viewportHeight = 22f,
        autoMirror = false,
    ) { _,_ ->

        val fraction by animateFloatAsState(
            targetValue     = pos,
            animationSpec   = tween(durationMillis = 1000),
            label = "1"
        )

        // Get the new list of PathNode
        val path = getPathNodes(chart?.values ?: listOf<Float>(0f, 0f, 0f), Size(100f, 100f))

        // Define the animation array
        val array = mutableListOf<List<PathNode>>(listOf(), listOf())
        array[pos.toInt()] = path
        array[((pos + 1) % 2).toInt()] = viewModel.lastListPathNode

        val pathNodes = try {
            lerp(viewModel.lastListPathNode, path, fraction)
        }catch(ex : Exception){
            null
        }

        viewModel.lastListPathNode = path

        Group(
            name = "GroupCheckClose2",
            translationX = 0.0f,
            translationY = 0.0f,
            rotation = 0f,
            pivotX = 11.0f,
            pivotY = 11.0f,
        ) {
            androidx.compose.ui.graphics.vector.Path(
                pathData = pathNodes ?: listOf(),
                stroke = SolidColor(MaterialTheme.colorScheme.tertiary),
                fill = SolidColor(MaterialTheme.colorScheme.tertiary),
                strokeLineWidth = 0.0f,
                strokeLineCap = StrokeCap.Round,
            )
        }
    }
}