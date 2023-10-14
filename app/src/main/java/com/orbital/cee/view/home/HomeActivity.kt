package com.orbital.cee.view.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.os.*
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.*
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.location.*
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.mapbox.geojson.*
import com.mapbox.maps.plugin.annotation.generated.*
import com.orbital.cee.R
import com.orbital.cee.core.Constants.DB_REF_USER
import com.orbital.cee.core.*
import com.orbital.cee.detectedactivity.DetectedActivityService
import com.orbital.cee.model.*
import com.orbital.cee.transitions.TRANSITIONS_RECEIVER_ACTION
import com.orbital.cee.transitions.TransitionsReceiver
import com.orbital.cee.ui.theme.CEETheme
import com.orbital.cee.utils.Shortcuts
import com.orbital.cee.utils.Utils
import com.orbital.cee.utils.Utils.Toaster.fix
import com.orbital.cee.utils.Utils.getDeviceName
import com.orbital.cee.utils.Utils.getNavigatingBarHeight
import com.orbital.cee.utils.Utils.pxToDp
import com.orbital.cee.view.MainMapScreen
import com.orbital.cee.view.authentication.Authentication
import com.orbital.cee.view.authentication.verifyOTP.VerifyOTP
import com.orbital.cee.view.home.components.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

val Context.data_Store by dataStore("alarm-less-reports.json", AppSettingsSerializer)
@RequiresApi(Build.VERSION_CODES.S)
@AndroidEntryPoint
class HomeActivity : ComponentActivity() //,SensorEventListener
{
    val model: HomeViewModel by viewModel()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var geofencingClient : GeofencingClient
    private lateinit var db : FirebaseFirestore
    private lateinit var mAuth : FirebaseAuth
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private val df = DecimalFormat("#.##")
    private var alertCount = mutableStateOf(0)
    private var distance = mutableStateOf(0f)
    private var maxSpeed = mutableStateOf(0)
    private var isFirstLunch = false
    private var lLocation = Location("")
    var langCode = mutableStateOf("en")
    var username = mutableStateOf("")
    var remoteConfig : FirebaseRemoteConfig? = null
    var isInAway = mutableStateOf(true)
//    private var wakeLock: PowerManager.WakeLock? = null
    private var appUpdate : AppUpdateManager? = null
    private val REQUESTCODE = 100
    private var count: Int = 0
    object Singlt {
        var SoundSta = mutableStateOf(1)
        fun set(Type : Int){
            SoundSta.value = Type
        }
    }
    private val transitionBroadcastReceiver: TransitionsReceiver = TransitionsReceiver().apply {
        action = { setDetectedActivity(it) }
    }
        override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.hasExtra(SUPPORTED_ACTIVITY_KEY)) {
            val supportedActivity = intent.getSerializableExtra(
                SUPPORTED_ACTIVITY_KEY) as SupportedActivity
            setDetectedActivity(supportedActivity)
        }
    }
    private fun setDetectedActivity(supportedActivity: SupportedActivity) {
        Log.d("TRANSITION_ACTIVITY", supportedActivity.activityText)

        mAuth.currentUser?.let { it1 ->
            val user: HashMap<String, Any> = HashMap<String, Any>()
            user["userActivity"] = supportedActivity.activityText
            db.collection(DB_REF_USER).document(it1.uid).update(user)
        }
    }
    @OptIn(ExperimentalAnimationApi::class)
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fix()
        Intent(applicationContext, MyLocationService::class.java).apply {
            action = MyLocationService.ACTION_START
            startForegroundService(this)
        }
        locationCallback = object : LocationCallback(){
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                p0.lastLocation?.let {
                    model.lastLocation.value = it
                    isLocationChanged(it)
                    isHasSpeed((it.speed *3.6).toInt())
                }
            }
        }

        isFirstLunch = true
        model.handler = Handler()
        model.startTimer()
        geofencingClient = LocationServices.getGeofencingClient(this)
        df.roundingMode = RoundingMode.DOWN
        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 100
        }
        remoteConfig!!.setConfigSettingsAsync(configSettings)
        val rmDefaultValue = HashMap<String,Any>()
        rmDefaultValue["reportSightRadius"] = 25
        remoteConfig!!.setDefaultsAsync(rmDefaultValue)
        remoteConfig!!.fetch(0)
        remoteConfig!!.fetchAndActivate()
        model.reportSightRadius.value = remoteConfig!!.getLong("reportSightRadius")
        appUpdate = AppUpdateManagerFactory.create(this)
//        callInAppUpdate()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val wic = WindowCompat.getInsetsController(window, window.decorView)
        wic.isAppearanceLightStatusBars = true
        val isPlaceCamera = intent?.getIntExtra("action_type",0) == 1
        if (isPlaceCamera){
            model.addReport(0)
        }
        Shortcuts.setUp(applicationContext)
        createLocationRequest()
        setContent {
            val soundSta = model.soundStatus.observeAsState()
            soundSta.value?.let { Singlt.set(it) }
            CEETheme(darkTheme = model.isDarkMode.value,langCode = {langCode.value}) {
                val navController = rememberAnimatedNavController()
                AnimatedNavHost(navController, "home") {
                    composable("home",
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colors.background
                        ) {
                            BackHandler(true) {}
                            MainMapScreen(model = model,navController = navController)
                        }
                        LaunchedEffect(Unit){
                            model.appSetting.value?.let {
                                if (it.preventScreenSleep){
                                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                                }else{
                                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                                }
                            }
                        }
                    }
                    composable("authentication",
                        enterTransition = { slideIntoContainer(AnimatedContentScope.SlideDirection.Up, animationSpec = tween(700))},
                        exitTransition = { slideOutOfContainer(AnimatedContentScope.SlideDirection.Down, animationSpec = tween(700))})
                    {
                        Authentication(navController = navController)
                    }
                    composable("verifyOtp/{cCode}/{phoneNumber}",
                        enterTransition = { slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(700))},
                        exitTransition = { slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(700))})
                    {
                        val vOtp = it.arguments?.getString("cCode").toString()
                        val phoneNumber = it.arguments?.getString("phoneNumber").toString()
                        VerifyOTP(
                            countryCode = vOtp,
                            phoneNumber = phoneNumber,
                            navController = navController
                        )
                    }
                }
                val langCodee = model.langCode.observeAsState()
                langCodee.value?.let {
                    langCode.value = it
                }
            }
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(this, this.getString(R.string.interstitial_ad_three_stop_id) , adRequest, object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    model.mInterstitialAd = null
                }
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    model.mInterstitialAd = interstitialAd
                }
            })
            RewardedAd.load(this,this.getString(R.string.remove_ads_rewarded_video_id) ,adRequest,object : RewardedAdLoadCallback(){
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    model.mRewardedAd = null
                    model.isRewardedVideoReady.value = false
                }
                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    model.mRewardedAd = rewardedAd
                    model.isRewardedVideoReady.value = true
//            Toast.makeText(context,"loaded",Toast.LENGTH_LONG).show()
                }
            })
            model.mRewardedAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
                override fun onAdClicked() {}
                override fun onAdDismissedFullScreenContent() {
                    model.mRewardedAd = null
                    model.isRewardedVideoReady.value = false
                }
                override fun onAdImpression() {}
                override fun onAdShowedFullScreenContent() {}
            }


        }
    }
    fun isHasSpeed(speed: Int) {
        if (speed>20){
            count++
            if (count > 5){
                isInAway.value = true
                count = 0
            }
        }else{
            if (speed<5){
                if (isInAway.value){
                    isInAway.value = false
                    model.stopCount.value++
                }
            }
        }
    }
    private fun isLocationChanged(location: Location?) {
        if (location != null){
            if (lLocation == Location("")){
                model.getReportsAndAddGeofences()
                lLocation = location
            }else{
                if (location.distanceTo(lLocation) > (model.reportSightRadius.value*1000)-500){
                    model.getReportsAndAddGeofences()
                    lLocation = location
                }
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onResume() {
        super.onResume()
        inProgressUpdate()
        startLocationUpdate()
        registerReceiver(transitionBroadcastReceiver, IntentFilter(TRANSITIONS_RECEIVER_ACTION))
        mAuth.currentUser?.let {
            val user : HashMap<String, Any> = HashMap<String, Any>()
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user["pushId"] = task.result
                    user["status"] = "online"
                    db.collection(DB_REF_USER).document(it.uid).update(user)
                }
            }
        }
    }
    override fun onStart() {
        super.onStart()
        model.appLaunchTime.value = Date()
        model.navigationBarHeight.value =  pxToDp(getNavigatingBarHeight(this))

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
    override fun onStop() {
        super.onStop()
        mAuth.currentUser?.let {
            val hash = GeoFireUtils.getGeoHashForLocation(GeoLocation(model.lastLocation.value.latitude, model.lastLocation.value.longitude))
            val user : HashMap<String, Any> = HashMap<String, Any>()
            user["g"] = hash
            user["deviceModele"] = getDeviceName()
            user["status"] = "offline"
            user["lastSeen"] = FieldValue.serverTimestamp()
            user["geoLocation"] = listOf(model.lastLocation.value.latitude, model.lastLocation.value.longitude)
            db.collection(DB_REF_USER).document(it.uid).update(user)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        model.snap1?.remove()
        model.snap2?.remove()
        model.snap3?.remove()
        model.snap4?.remove()

        Intent(applicationContext, MyLocationService::class.java).apply {
            action = MyLocationService.ACTION_STOP
            stopService(this)
        }
//        wakeLock?.release()
//        wakeLock = null
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        unregisterReceiver(transitionBroadcastReceiver)
        model.geofencingClient.removeGeofences(model.geofencePendingIntent)
        stopService(Intent(this, DetectedActivityService::class.java))
        mAuth.currentUser?.let {
            val user : HashMap<String, Any> = HashMap<String, Any>()
            val hash = GeoFireUtils.getGeoHashForLocation(GeoLocation(model.lastLocation.value.latitude, model.lastLocation.value.longitude))
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user["pushId"] = task.result
                    user["g"] = hash
                    user["status"] = "offline"
                    user["deviceModele"] = getDeviceName()
                    user["lastSeen"] = FieldValue.serverTimestamp()
                    user["version"] = Utils.currentVersion(this)
                    user["geoLocation"] = listOf(model.lastLocation.value.latitude, model.lastLocation.value.longitude)
                    db.collection(DB_REF_USER).document(it.uid).update(user)

                    val statistic : HashMap<String, Any> = HashMap<String, Any>()
                    statistic["latestUpdate"] = FieldValue.serverTimestamp()
                    statistic["alertedTime"] = alertCount.value
                    statistic["maxSpeed"] = maxSpeed.value
                    statistic["traveldDistance"] = distance.value * 1000
                    db.collection(DB_REF_USER).document(it.uid).collection("statistic").document("GeneralStats").set(statistic)
                }
            }
        }
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUESTCODE) {
            Toast.makeText(this,"Downloading start",Toast.LENGTH_SHORT).show()
            if (resultCode != RESULT_OK) {
                Log.e("MY_APP", "Update flow failed! Result code: $resultCode")
            }
        }
    }
    private fun createLocationRequest() {
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,1000).apply {
            setMinUpdateDistanceMeters(3f)
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setWaitForAccurateLocation(true)
        }.build()
//        locationRequest = LocationRequest.create().apply {
//            interval = 1000
//            fastestInterval = 1500
//            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//            maxWaitTime = 1000
//        }
    }
    private fun startLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }
//    private fun getPerimeterFeature(center:Point, radiusInKilometers: Double = .05, sides: Int = 64): Feature {
//        val positions = mutableListOf<Point>()
//        val latitude = center.latitude()
//        val longitude = center.longitude()
//        val distanceX: Double = radiusInKilometers / (111.319 * cos(latitude * Math.PI / 180))
//        val distanceY: Double = radiusInKilometers / 110.574
//        val slice = (2 * Math.PI) / sides
//        var theta: Double
//        var x: Double
//        var y: Double
//        var position: Point
//        for (i in 0..sides) {
//            theta = i * slice
//            x = distanceX * cos(theta)
//            y = distanceY * sin(theta)
//            position = Point.fromLngLat(longitude + x, latitude + y)
//            positions.add(position)
//        }
//        return Feature.fromGeometry(Polygon.fromLngLats(listOf(positions)))
//    }
    private fun callInAppUpdate(){
        appUpdate?.appUpdateInfo?.
        addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                appUpdate?.startUpdateFlowForResult(appUpdateInfo,AppUpdateType.IMMEDIATE,this,REQUESTCODE)
            }
        }
    }
    private fun inProgressUpdate(){
        appUpdate?.appUpdateInfo?.
        addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                appUpdate?.startUpdateFlowForResult(appUpdateInfo,AppUpdateType.IMMEDIATE,this,REQUESTCODE)
            }
        }
    }
}
fun <T> concatenate(vararg lists: List<T>): List<T> {
    return listOf(*lists).flatten()
}