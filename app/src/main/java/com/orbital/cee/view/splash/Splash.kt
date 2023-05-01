package com.orbital.cee.view.splash

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.orbital.cee.R
import com.orbital.cee.helper.Screen
import com.orbital.cee.utils.Utils
import com.orbital.cee.utils.Utils.googlePlayServiceEnabled
import com.orbital.cee.view.home.HomeActivity
import com.orbital.cee.view.home.HomeViewModel
import com.orbital.cee.view.home.components.showCustomDialog
import kotlinx.coroutines.delay
import java.util.*
import kotlin.collections.HashMap
import kotlin.system.exitProcess

@Composable
 fun Splash(
    navController : NavController,
    model : HomeViewModel = viewModel()
) {
    var context = LocalContext.current
    val configuration = LocalConfiguration.current
    var remoteConfig : FirebaseRemoteConfig? = null
    val resources = LocalContext.current.resources
    val composition by rememberLottieComposition(
        LottieCompositionSpec
            .RawRes(R.raw.cee_logo_animation)
    )

    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true,
        speed = 1f,
        restartOnPlay = false
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.primary),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().fillMaxHeight()
        ) {
            LottieAnimation(
                composition,
                progress,
                modifier = Modifier.size(150.dp)
            )
//            Spacer(modifier = Modifier.height(height = (configuration.screenHeightDp / 6.5).dp))
//            Box(
//                modifier = Modifier
//                    .height(height = 250.dp)
//                    .width(width = 250.dp)
//                    .background(color = MaterialTheme.colors.primary),
//                contentAlignment = Alignment.Center
//            ) {
////                Box(
////                    Modifier
////                        .size(150.dp)
////                        .background(color = Color.Transparent, shape = RoundedCornerShape(100.dp))
////                        .border(
////                            color = Color.White,
////                            shape = RoundedCornerShape(100.dp),
////                            width = 2.dp
////                        )) {
////                }
//
//            }
//            Icon(painter = painterResource(id = R.drawable.ic_txt_cee), contentDescription ="", tint = Color.White )
        }
    }
    val a = model.readFirstLaunch.observeAsState()
    val langCode = model.langCode.observeAsState()

    var locale = Locale("en")
    langCode.value?.let {
        locale = Locale(it)
    }
    configuration.setLocale(locale)
    //context.createConfigurationContext(configuration)
    if (langCode.value == "ku"){
        configuration.setLayoutDirection(Locale("ar"))
    }
    resources.updateConfiguration(configuration, resources.displayMetrics)

    LaunchedEffect(Unit) {
        remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 100
        }

        remoteConfig!!.setConfigSettingsAsync(configSettings)

        val defaultValue = HashMap<String,Any>()
        defaultValue["minimum_version_supported"] = 0
        defaultValue["newest_version"] = 0

        remoteConfig!!.setDefaultsAsync(defaultValue)
        remoteConfig!!.fetch(0)
        remoteConfig!!.fetchAndActivate()
        Log.d("REMOTE_CONFIG", remoteConfig!!.getLong("minimum_version_supported").toString())
        Log.d("REMOTE_CONFIG", remoteConfig!!.getLong("newest_version").toString())
        val min = remoteConfig!!.getLong("minimum_version_supported")
        //val max = remoteConfig!!.getLong("newest_version")
        delay(1000)
        if(googlePlayServiceEnabled(context)){
            if (Utils.buildNumber(context) >= min ){
                if (a.value != null && a.value == true) {
                    navController.navigate(Screen.Language.route)
                } else {
                    if (model.isLogin()){
                        val navigate = Intent(context, HomeActivity::class.java)
                        context.startActivity(navigate)
                    }else{
                        navController.navigate(Screen.Authentication.route)
                    }
                }
            }else{
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Update required.")
                builder.setMessage("To continue click update, and come back.")
                builder.setIcon(android.R.drawable.ic_dialog_alert)

                builder.setPositiveButton("Update"){dialogInterface, which ->
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.orbital.cee")))
                }

                val alertDialog: AlertDialog = builder.create()
                alertDialog.setCancelable(false)
                alertDialog.show()
            }
        }else{
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Google Play Service Unavailable.")
            builder.setMessage("Sorry cee doesn't support this device.")
            builder.setIcon(android.R.drawable.ic_dialog_alert)

            builder.setPositiveButton("Exit"){dialogInterface, which ->
                exitProcess(-1)
            }

            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()
        }
    }
}