package com.example.smartgarden.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.smartgarden.R
import com.example.smartgarden.viewmodels.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val animation_login_duration = 15000

@Composable
fun LoginScreen(navController: NavController){

    val viewModel = hiltViewModel<LoginViewModel>()

    val signedIn by remember {
        viewModel.signedIn
    }

    // Signed in, go to the next screen
    if(signedIn){
        navController.navigate(route = "init_garden")
    }

    val infiniteAnimatableValue = rememberInfiniteTransition(label = "")

    val animatedValueX2 by infiniteAnimatableValue.animateFloat(
        initialValue = 32f,
        targetValue = 35f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = animation_login_duration / 3,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse // Change to Mirror for different effect
        ), label = ""
    )

    val animatedValueY2 by infiniteAnimatableValue.animateFloat(
        initialValue = -60f,
        targetValue = -80f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = animation_login_duration / 3,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse // Change to Mirror for different effect
        ), label = ""
    )

    val animatedValueRotation2 by infiniteAnimatableValue.animateFloat(
        initialValue = -5f,
        targetValue = -2f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = animation_login_duration / 3,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse // Change to Mirror for different effect
        ), label = ""
    )

    val animatedValueX by infiniteAnimatableValue.animateFloat(
        initialValue = 10f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = animation_login_duration,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse // Change to Mirror for different effect
        ), label = ""
    )

    val animatedValueY by infiniteAnimatableValue.animateFloat(
        initialValue = 30f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = animation_login_duration,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse // Change to Mirror for different effect
        ), label = ""
    )

    val animatedValueRotation by infiniteAnimatableValue.animateFloat(
        initialValue = 0f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = animation_login_duration,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse // Change to Mirror for different effect
        ), label = ""
    )

    var email by remember {
        viewModel.email
    }

    var password by remember {
        viewModel.password
    }

    var passwordError by remember {
        viewModel.passwordError
    }

    var passwordHidden by remember { mutableStateOf(true) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier,
            contentAlignment = Alignment.TopEnd,){

            Image(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .graphicsLayer(
                        translationX = animatedValueX2 + 20f,
                        translationY = animatedValueY2,
                        rotationZ = animatedValueRotation2,
                    ),
                painter = painterResource(id = R.drawable.branch_2),
                contentDescription = "branch 1",)

        }

        Card(
            modifier = Modifier.padding(0.dp, 80.dp, 0.dp, 0.dp).wrapContentSize(align = Alignment.TopCenter),
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = 8.dp
            ), // Set elevation value for the card
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
        ) {
            Text(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clip(RoundedCornerShape(20.dp))
                    .padding(20.dp, 8.dp, 20.dp, 8.dp),
                text = "Smart Garden",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.rubik, FontWeight.Bold)),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }

        Box(modifier = Modifier
            .padding(),
            contentAlignment = Alignment.TopStart,){

//            Image(
//                modifier = Modifier
//                    .align(Alignment.TopStart)
//                    .scale(1f)
//                    .graphicsLayer(
//                        translationX = -animatedValueX2 + 20f,
//                        translationY = -animatedValueY2,
//                        rotationZ = animatedValueRotation2,
//                    ),
//                painter = painterResource(id = R.drawable.branch_2),
//                contentDescription = "plant 1",)

            Image(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(0.dp, 10.dp, 0.dp, 0.dp)
                    .graphicsLayer(
                        translationX = -animatedValueX,
                        translationY = -animatedValueY,
                        rotationZ = animatedValueRotation,
                    ),
                painter = painterResource(id = R.drawable.branch_2_2),
                contentDescription = "plant 1",)

            Image(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(0.dp, 150.dp, 0.dp, 0.dp)
                    .graphicsLayer(
                        translationX = -animatedValueX,
                        translationY = -animatedValueY,
                        rotationZ = animatedValueRotation,
                    ),
                painter = painterResource(id = R.drawable.branch3),
                contentDescription = "plant 1",)

        }


        Column(modifier = Modifier
            .padding(0.dp, 0.dp, 0.dp, 20.dp)
            .fillMaxWidth().fillMaxHeight(),
            verticalArrangement = Arrangement.Bottom
        ) {

            // email
            OutlinedTextField(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                value = email,
                onValueChange = { email = it },
                label = { Text("e-mail") },
                singleLine = true,
                shape = RoundedCornerShape(30.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    cursorColor = MaterialTheme.colorScheme.onBackground, // Set the color of the cursor
                    focusedBorderColor = MaterialTheme.colorScheme.onBackground, // Set focused border color to transparent
                    unfocusedBorderColor = Color.LightGray, // Set border color to transparent
                    focusedLabelColor = Color.LightGray,
                    unfocusedLabelColor = Color.LightGray
                ),
                leadingIcon = {
                    IconButton(onClick = {}, modifier = Modifier.size(20.dp)) {
                        Icon(
                            imageVector = Icons.Rounded.Email,
                            tint = Color.LightGray,
                            contentDescription = "Locked")
                    }
                },
            )

            // password
            OutlinedTextField(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                value = password,
                onValueChange = {
                    password = it
                    passwordError = it.length < 6 },
                isError = passwordError,
                label = { Text("password") },
                singleLine = true,
                shape = RoundedCornerShape(30.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    cursorColor = MaterialTheme.colorScheme.onBackground, // Set the color of the cursor
                    focusedBorderColor = MaterialTheme.colorScheme.onBackground, // Set focused border color to transparent
                    unfocusedBorderColor = Color.LightGray, // Set border color to transparent
                    focusedLabelColor = Color.LightGray,
                    unfocusedLabelColor = Color.LightGray,
                ),
                visualTransformation =
                if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passwordHidden = !passwordHidden }) {
                        val painter =
                            if (passwordHidden) painterResource(id = R.drawable.round_visibility_24)
                            else painterResource(id = R.drawable.round_visibility_off_24)
                        val description = if (passwordHidden) "Show password" else "Hide password"
                        Icon(painter = painter, contentDescription = description)
                    }
                },
                leadingIcon = {
                    IconButton(onClick = {}, modifier = Modifier.size(20.dp)) {
                        Icon(
                            imageVector = Icons.Rounded.Lock,
                            tint = Color.LightGray,
                            contentDescription = "Locked")
                    }
                },
            )

            if (passwordError) {
                var text = "Password must be at least 6 characters long"
                if(password.length >= 6){
                    text = "Wrong password"
                }

                Text(
                    text = text,
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            ButtonSignIn(modifier = Modifier
                .padding(10.dp, 30.dp, 10.dp, 10.dp)
                .background(MaterialTheme.colorScheme.background)
                .align(Alignment.CenterHorizontally), viewModel::signIn)

            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(50.dp, 20.dp, 50.dp, 10.dp)){

                Divider(
                    modifier = Modifier.padding(vertical = 14.dp) // Adjust padding as needed
                )

                Text(text = "or",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(40.dp, 20.dp)
                        .background(MaterialTheme.colorScheme.background))
            }

            ButtonSignIn(modifier = Modifier
                .padding(4.dp)
                .background(MaterialTheme.colorScheme.background)
                .align(Alignment.CenterHorizontally), viewModel::signInWithGoogle,
                R.drawable.google)

            Box(modifier = Modifier
                .padding(50.dp, 80.dp, 50.dp, 40.dp)
                .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.BottomCenter){
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        textAlign = TextAlign.Center,
                        text = "Powered by",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        textAlign = TextAlign.Center,
                        text = "MSouce projects",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        }
    }
}

@Composable
fun ButtonSignIn(modifier: Modifier, signIn : () -> Unit, id_icon : Int = -1){
    Card(
        modifier = modifier,
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp
        ), // Set elevation value for the card
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onSecondary
        ),
    ) {
        Row(modifier = Modifier
            .background(MaterialTheme.colorScheme.onSecondary)
            .clickable {
                signIn()
            }
            .padding(40.dp, 4.dp, 40.dp, 4.dp)) {

            if(id_icon >= 0){
                Image(
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.CenterVertically),
                    painter = painterResource(id = id_icon),
                    contentDescription = "google icon",)
            }

            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(10.dp),
                text = "Sign in",
                textAlign = TextAlign.Center)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview(){
    LoginScreen(rememberNavController())
}