package com.example.smartgarden

import android.Manifest
import android.app.DownloadManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.smartgarden.firebase.authentication.FirebaseAuthenticator
import com.example.smartgarden.navigation.Screen
import com.example.smartgarden.navigation.SetupNavGraph
import com.example.smartgarden.repository.DataInternalRepository
import com.example.smartgarden.ui.theme.SmartGardenTheme
import com.example.smartgarden.utility.Utility
import com.example.smartgarden.viewmodels.CameraViewModel
import com.example.smartgarden.viewmodels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var navController  : NavHostController
    @Inject lateinit var viewModelLogin : LoginViewModel
    @Inject lateinit var auth           : FirebaseAuthenticator
    @Inject lateinit var cameraViewModel: CameraViewModel
    @Inject lateinit var dataInternalRepository: DataInternalRepository

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()) { result ->
        auth.responseSignInWithGoogle(result)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Permissions
        permissions()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        var initRoute = Screen.InitGarden.route
        auth.setActivityResultLanucher(launcher)
        if(auth.currentUser == null) {
            // set the activity result
            // to sign in with google
            initRoute = Screen.Login.route
        }
        else if(dataInternalRepository.getGarden().size > 0){
            initRoute = Screen.Home.route
        }

        // Init context methods
        cameraViewModel.downloadFromUrl  = ::downloadFile
        cameraViewModel.shareFromText    = ::shareText


        setContent {
            SmartGardenTheme {
                navController = rememberNavController()
                SetupNavGraph(
                    navController = navController,
                    initRoute,
                    cameraViewModel
                )
            }
        }
    }

    private fun permissions(){
        val permissions = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA
        )

        val permissionToRequest = mutableListOf<String>()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionToRequest.add(permission)
            }
        }

        if (permissionToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionToRequest.toTypedArray(), 0)
        }
    }

    private fun downloadFile(url : String, filename : String = "image"){
        Utility.downloadFile(this, url, filename)
    }

    private fun shareText(text : String){
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraViewModel.releaseVideo()
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SmartGardenTheme {

    }
}
