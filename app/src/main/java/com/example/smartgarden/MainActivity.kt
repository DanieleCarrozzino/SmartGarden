package com.example.smartgarden

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.smartgarden.firebase.authentication.FirebaseAuthenticator
import com.example.smartgarden.navigation.SetupNavGraph
import com.example.smartgarden.repository.DataInternalRepository
import com.example.smartgarden.ui.theme.SmartGardenTheme
import com.example.smartgarden.viewmodels.LoginViewModel
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var navController  : NavHostController
    @Inject lateinit var viewModelLogin : LoginViewModel
    @Inject lateinit var auth : FirebaseAuthenticator
    @Inject lateinit var dataInternalRepository: DataInternalRepository

    private val launcher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        auth.responseSignInWithGoogle(result)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Permissions
        permissions()

        var initRoute = "init_garden"
        if(auth.currentUser == null) {
            // set the activity result
            // to sign in with google
            auth.setActivityResultLanucher(launcher)
            initRoute = "login"
        }
        else if(dataInternalRepository.getGarden().size > 0){
            initRoute = "home"
        }

        setContent {
            SmartGardenTheme {
                navController = rememberNavController()
                SetupNavGraph(navController = navController, initRoute)
            }
        }
    }

    private fun permissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 0)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0)
        }
    }


}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SmartGardenTheme {

    }
}
