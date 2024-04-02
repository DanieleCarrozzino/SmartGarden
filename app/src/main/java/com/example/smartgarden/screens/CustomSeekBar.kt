package com.example.smartgarden.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartgarden.ui.theme.Green1

@Composable
fun CustomSeekBar(value : Int, name : String, modifier: Modifier, color : Color, max : Int = 100){

    var animateScale by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        animateScale = true
    }

    val animatedValue by animateFloatAsState(
        targetValue = if (animateScale) value.toFloat() / max else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = ""
    )

    Column(modifier = modifier) {
        if(name != ""){
            Text(
                modifier = Modifier.padding(4.dp),
                text = name,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                maxLines = 1)
        }

        Card(
            modifier = Modifier
                .height(25.dp)
                .fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = 8.dp
            ), // Set elevation value for the card
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        ){

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(12.dp)
                        .clip(RoundedCornerShape(12.dp, 0.dp, 0.dp, 12.dp))
                        .background(color)
                )

                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = animatedValue
                            transformOrigin = TransformOrigin(0f, 0f)
                        }
                        .fillMaxSize()
                        .clip(RoundedCornerShape(0.dp, 12.dp, 12.dp, 0.dp))
                        .background(color),
                    contentAlignment = Alignment.CenterStart
                ){

                }
            }
        }
    }
}