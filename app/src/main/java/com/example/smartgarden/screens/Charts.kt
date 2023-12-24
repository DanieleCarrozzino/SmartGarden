package com.example.smartgarden.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartgarden.R
import com.example.smartgarden.ui.theme.Blue20
import com.example.smartgarden.ui.theme.Opac
import com.example.smartgarden.ui.theme.Purple40
import com.example.smartgarden.ui.theme.Purple80
import java.text.DecimalFormat

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
fun ChartBoxWithArray(array : List<Float>, title : String, perc : Float, color1 : Color, color2 : Color){
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Canvas(modifier = Modifier
            .padding(0.dp)
            .fillMaxSize()){

            // Get max of the array to adapt the chart
            val max = array.max()
            // delta to multiply to every elements inside the array
            val delta  = size.height / max
            // every x step to create a new point path
            val deltaX = size.width / (array.size - 1)

            val path = Path().apply {

                //Start position
                var prevOffset = Offset(0f, size.height)
                moveTo(prevOffset.x, prevOffset.y)

                // Start form the first arrayPosition
                lineTo(prevOffset.x, size.height - array[0] * delta)

                for (i in 1 until array.size){
                    val y = size.height - array[i] * delta
                    val x = (i * deltaX)
                    quadraticBezierTo(prevOffset.x, prevOffset.y, (x + prevOffset.x) / 2, (y + prevOffset.y) / 2)
                    prevOffset = Offset(x, y)
                }
                lineTo(size.width, size.height - array.last() * delta)
                lineTo(size.width, size.height)
            }


            //This draw the graph point by point
            drawPath(
                path = path,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        color1,
                        color2
                    ),
                    endY = size.height))
        }


        Surface(
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .padding(5.dp)
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = Opac,
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    modifier = Modifier
                        .padding(4.dp, 2.dp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 15.sp
                )
                Text(
                    text = "${DecimalFormat("##.##").format(perc)}%",
                    modifier = Modifier,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    lineHeight = 22.sp,
                    fontSize = if (perc > 1000f) 18.sp else if (perc > 100f) 21.sp else 23.sp
                )
            }

        }
    }
}