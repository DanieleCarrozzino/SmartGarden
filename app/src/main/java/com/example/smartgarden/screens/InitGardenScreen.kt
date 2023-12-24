package com.example.smartgarden.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.smartgarden.R
import com.example.smartgarden.viewmodels.LoginViewModel
import com.google.firebase.firestore.DocumentSnapshot

/**
 * Get the info of this user inside the firebase
 * firestore
 *
 * If there is already a garden linked to this user
 * I have to show the possibility to choose those gardens
 * to pick one of them
 *
 * Otherwise create a new one
 * */

@Composable
fun InitGardenScreen(navController: NavController){

    val viewModel = hiltViewModel<LoginViewModel>()

    // State to track whether data has been fetched
    var dataFetched by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!dataFetched) {
            viewModel.getFirestoreData()
            dataFetched = true
        }
    }

    // Navigate to the next screen
    // if you have finished
    val finished by remember {
        viewModel.creationGardenFinished
    }

    if(finished){
        navController.navigate("init_config_raspberry")
    }

    val loadedGarden by remember {
        viewModel.loadedGarden
    }

    // Get the gardens to show
    // or to create a new one
    if(loadedGarden){
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            if (viewModel.listGardens.size > 0){
                ListOfGardens(list = viewModel.listGardens)
            }
            else {
                EmptyGardens(
                    id_image = R.drawable.garden_xl,
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight())
            }
        }
    }
    // Loading screen
    else{
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            PlaceHolder(
                id_image = R.drawable.plant,
                text = "Waiting gardens...",
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight())
        }
    }
}

@Composable
fun ListOfGardens(list : List<DocumentSnapshot>){

    val tmpList = list.toMutableList()
    tmpList.removeIf {
        it.id == "firebase_token"
    }

    val viewModel = hiltViewModel<LoginViewModel>()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp, 0.dp, 0.dp, 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        contentPadding = PaddingValues(10.dp)
    ) {
        item("top"){
            // title screen
            Text(
                text = "Choose garden",
                modifier = Modifier.padding(10.dp, 0.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                text = "Choose an old garden or create a new one",
                modifier = Modifier.padding(10.dp, 0.dp),
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
        itemsIndexed(tmpList) { _, item ->

            val name = item.get("name").toString()
            val creation = item.get("date_creation").toString()

            val mod = Modifier
                .wrapContentSize()
                .shadow(
                    elevation = 10.dp,
                    ambientColor = Color.Gray,
                    spotColor = Color.Gray,
                    shape = RoundedCornerShape(30.dp, 30.dp, 30.dp, 30.dp)
                )
                .clickable {
                    val garden = hashMapOf(
                        "name" to item["name"].toString(),
                        "id" to item["id"].toString(),
                        "date_creation" to item["date_creation"].toString()
                    )
                    viewModel.saveGardenAndGoOn(garden)
                }
                .background(MaterialTheme.colorScheme.onPrimary)
                .defaultMinSize(minWidth = 0.dp, minHeight = 0.dp)

            SingleGardenItem(mod, name, creation)
        }

        item("bottom"){

            Spacer(modifier = Modifier.height(20.dp))

            EmptyGardens(
                id_image = R.drawable.garden_xl,
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight())

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

}

@Composable
fun SingleGardenItem(modifier : Modifier, name : String, creation : String){
    Row(modifier = modifier){
        Box(modifier = Modifier
            .padding(2.dp)
            .clip(RoundedCornerShape(30.dp))
            .graphicsLayer {
                clip = true
            }
        ) {
            Image(
                painter = painterResource(id = R.drawable.little_garden),
                contentDescription = "little garden icon",
                modifier = Modifier.size(60.dp),
                contentScale = ContentScale.Crop
            )
        }

        Column(
            modifier = Modifier
                .padding(20.dp, 10.dp, 20.dp, 10.dp)
                .align(Alignment.CenterVertically)) {
            Text(
                text = name,
                modifier = Modifier,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                softWrap = true
            )

            Text(
                text = creation,
                modifier = Modifier
            )
        }


    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun EmptyGardens(id_image : Int, modifier: Modifier){

    val viewModel = hiltViewModel<LoginViewModel>()
    var gardenName by remember {
        viewModel.gardenName
    }

    Column(modifier = modifier) {

        if(id_image >= 0){
            Image(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .scale(1f),
                painter = painterResource(id = id_image),
                contentDescription = "placeholder",)
        }

        Text(
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.CenterHorizontally),
            text = "Create a new Garden",
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.CenterHorizontally),
            text = "Define an appropriate name\nfor your wonderful garden!",
            textAlign = TextAlign.Center,
            fontSize = 16.sp
        )

        OutlinedTextField(
            modifier    = Modifier.align(Alignment.CenterHorizontally),
            value       = gardenName,
            onValueChange = { gardenName = it },
            label = { Text("Garden name") },
            singleLine = true,
            shape = RoundedCornerShape(30.dp),
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = MaterialTheme.colorScheme.onBackground, // Set the color of the cursor
                focusedBorderColor = MaterialTheme.colorScheme.onBackground, // Set focused border color to transparent
                unfocusedBorderColor = Color.LightGray, // Set border color to transparent
                focusedLabelColor = Color.LightGray,
                unfocusedLabelColor = Color.LightGray
            ),
        )

        GenericButton(
            modifier = Modifier
                .padding(15.dp)
                .background(MaterialTheme.colorScheme.background)
                .align(Alignment.CenterHorizontally),
            viewModel::createGarden,
            "Create garden",
            null,
        )
    }
}

@Composable
fun GenericButton(
    modifier: Modifier,
    functionClicked : () -> Unit,
    text : String,
    imageVector : ImageVector? = null,
    id_icon : Int = -1){
    Card(
        modifier = modifier,
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp
        ), // Set elevation value for the card
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary
        ),
    ) {
        Row(modifier = Modifier
            .background(MaterialTheme.colorScheme.onPrimary)
            .clickable {
                functionClicked()
            }
            .padding(if (imageVector == null) 40.dp else 20.dp, 4.dp, 40.dp, 4.dp)) {

            if(id_icon >= 0){
                Image(
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.CenterVertically),
                    painter = painterResource(id = id_icon),
                    contentDescription = "google icon",)
            }
            else if(imageVector != null){
                IconButton(onClick = {}, modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.CenterVertically)
                ) {
                    Icon(
                        imageVector = imageVector,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        contentDescription = "Locked")
                }
            }

            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(10.dp),
                text = text,
                textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun BigImageButton(
    modifier: Modifier,
    functionClicked : () -> Unit,
    text : String,
    id_icon : Int = -1){
    Card(
        modifier = modifier,
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp
        ), // Set elevation value for the card
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary
        ),
    ) {
        Row(modifier = Modifier
            .background(MaterialTheme.colorScheme.onPrimary)
            .clickable {
                functionClicked()
            }
            .padding(20.dp, 10.dp, 20.dp, 10.dp)) {

            if(id_icon >= 0){
                Image(
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.CenterVertically),
                    painter = painterResource(id = id_icon),
                    contentDescription = "google icon",)
            }

            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(10.dp),
                text = text,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun PlaceHolder(id_image : Int, text : String, modifier: Modifier){

    Column(modifier = modifier) {
        Image(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            painter = painterResource(id = id_image),
            contentDescription = "placeholder",)
        Text(
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.CenterHorizontally),
            text = text,
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
fun preview(){
    InitGardenScreen(navController = rememberNavController())
}