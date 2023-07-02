package com.orbital.cee.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.facebook.FacebookSdk
import com.orbital.cee.core.MyLocationService
import com.orbital.cee.core.Permissions
import com.orbital.cee.ui.theme.CEETheme
import com.orbital.cee.utils.Shortcuts
import com.orbital.cee.utils.Utils
import com.orbital.cee.view.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.koin.androidx.viewmodel.ext.android.viewModel
import pub.devrel.easypermissions.EasyPermissions
import java.util.Date
import android.net.Uri

@AndroidEntryPoint
class MainActivity : ComponentActivity() , EasyPermissions.PermissionCallbacks{
    val model: HomeViewModel by viewModel()
    var langCode = mutableStateOf("en")
    //val authModel: AuthenticationViewModel by viewModel()
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (Build.VERSION.SDK_INT >= 25) {
            Shortcuts.setUp(applicationContext)
        }
        ActivityCompat.requestPermissions(this, arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACTIVITY_RECOGNITION,
            Manifest.permission.POST_NOTIFICATIONS),
            0)
//        val appLinkIntent: Intent = intent
//        val appLinkAction: String? = appLinkIntent.action
//        val appLinkData: Uri? = appLinkIntent.data
//        Log.d("DEBUG_DEEPLINK_DATA",appLinkAction.toString())
//        Log.d("DEBUG_DEEPLINK_DATA","B: "+appLinkData?.lastPathSegment.toString())
        installSplashScreen()
        setContent {
            val langCode = model.langCode.observeAsState()
            CEETheme(langCode = langCode.value?: "en"){
                NavGraph(model)
            }
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)){ view, insets->
            val bottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            view.updatePadding(bottom = bottom)
            insets
        }

//        Toast.makeText(this,appLinkData.toString(),Toast.LENGTH_LONG).show()
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
        Log.d("PERMREQUESTCODE",requestCode.toString())
    }
    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("MissingPermission")
    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Log.d("PERMREQUESTCODE", "GRANT:$requestCode")
    }
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
//        Toast.makeText(this,"Please Set Background Permission.", Toast.LENGTH_LONG).show()
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStart() {
        super.onStart()
        if (!Permissions.hasLocationPermission(this)){
            Permissions.requestsLocationPermission(this)
        }
        if (!Permissions.hasBackgroundLocationPermission(this)){
            Permissions.requestsBackgroundLocationPermission(this)
        }
        if (!Permissions.hasActivityRecognitionPermission(this)){
            Permissions.requestsActivityRecognitionPermission(this)
        }
    }

}
