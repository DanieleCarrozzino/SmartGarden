package com.example.smartgarden.screens.raspberry

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.smartgarden.R
import com.example.smartgarden.screens.GenericButton

@Composable
fun InitRaspberryScreen(navController: NavController){

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .wrapContentHeight()
        ) {
            Image(
                modifier = Modifier
                    .padding(60.dp, 20.dp, 60.dp, 40.dp)
                    .align(Alignment.CenterHorizontally),
                painter = painterResource(id = R.drawable.connect_rasp_place),
                contentDescription = "qr icon",
                alignment = Alignment.Center)
            Text(
                text = "Linking Your Raspberry Garden",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Text(
                text = "Connect your Raspberry Pi to your garden in seconds. Get started with the linking process now!",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(40.dp, 4.dp),
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )

            GenericButton(
                modifier = Modifier
                    .padding(15.dp)
                    .background(MaterialTheme.colorScheme.background)
                    .align(Alignment.CenterHorizontally),
                {
                    Log.d("Click", "init linking raspberry")
                    navController.navigate("config_raspberry")
                },
                "Start linking",
                null,
            )
        }

        // Skip
        Box(
            contentAlignment = Alignment.BottomEnd
        ){
            Text(
                text = "Skip",
                modifier = Modifier
                    .padding(40.dp)
                    .clickable {
                        navController.navigate("home")
                    },
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )
        }
    }

}

@Preview
@Composable
fun previewRaspberry(){
    InitRaspberryScreen(navController = rememberNavController())
}