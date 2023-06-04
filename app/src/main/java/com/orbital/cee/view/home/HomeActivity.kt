@file:OptIn(DelicateCoroutinesApi::class)

package com.orbital.cee.view.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.*
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.getSystemService
import androidx.core.view.WindowCompat
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.*
import androidx.navigation.NavController
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.gson.Gson
import com.mapbox.geojson.*

import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapInitOptions

import com.mapbox.maps.MapView
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.style.expressions.dsl.generated.color
import com.mapbox.maps.extension.style.expressions.dsl.generated.literal
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.SymbolLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.extension.style.sources.getSourceAs
import com.mapbox.maps.plugin.animation.MapAnimationOptions.Companion.mapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.AnnotationConfig
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.*
import com.mapbox.maps.plugin.compass.compass
import com.mapbox.maps.plugin.scalebar.scalebar
import com.orbital.cee.R
import com.orbital.cee.core.Constants.DB_REF_USER
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.plugin.animation.CameraAnimatorOptions.Companion.cameraAnimatorOptions
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.annotation.AnnotationSourceOptions
import com.mapbox.maps.plugin.annotation.ClusterOptions
import com.mapbox.maps.plugin.attribution.attribution
import com.mapbox.maps.plugin.gestures.addOnMapLongClickListener
import com.orbital.cee.core.*
import com.orbital.cee.core.Constants.DB_REF_ALERTED
import com.orbital.cee.core.Constants.DB_REF_REPORT
import com.orbital.cee.core.Constants.MAX_SPEED
import com.orbital.cee.core.MyLocationService.LSS.EnteredP2PTime
import com.orbital.cee.core.MyLocationService.LSS.TripAverageSpeed
import com.orbital.cee.core.MyLocationService.LSS.TripDistance
import com.orbital.cee.core.MyLocationService.LSS.TripMaxSpeed
import com.orbital.cee.core.MyLocationService.LSS.TripStartTime
import com.orbital.cee.core.MyLocationService.LSS.calcTrip
import com.orbital.cee.core.MyLocationService.LSS.isLocationChange
import com.orbital.cee.core.MyLocationService.LSS.resetP2PCalc
import com.orbital.cee.core.MyLocationService.LSS.resetTrip
import com.orbital.cee.detectedactivity.DetectedActivityService
import com.orbital.cee.helper.DBHandler
import com.orbital.cee.model.*
import com.orbital.cee.transitions.TRANSITIONS_RECEIVER_ACTION
import com.orbital.cee.transitions.TransitionsReceiver
import com.orbital.cee.ui.theme.CEETheme
import com.orbital.cee.utils.MetricsUtils.Companion.convertDateToLong
import com.orbital.cee.utils.MetricsUtils.Companion.getRemainInt
import com.orbital.cee.utils.MetricsUtils.Companion.isOnline
import com.orbital.cee.utils.Shortcuts
import com.orbital.cee.utils.Utils
import com.orbital.cee.utils.Utils.Toaster.fix
import com.orbital.cee.utils.Utils.getDeviceName
import com.orbital.cee.utils.Utils.getNavigatingBarHeight
import com.orbital.cee.utils.Utils.pxToDp
import com.orbital.cee.view.LocationNotAvailable.LocationNotAvailable
import com.orbital.cee.view.MainActivity
import com.orbital.cee.view.MainMapScreen
import com.orbital.cee.view.home.HomeActivity.Singlt.stopCount
import com.orbital.cee.view.home.Menu.About
import com.orbital.cee.view.home.Menu.General
import com.orbital.cee.view.home.Menu.help
import com.orbital.cee.view.home.Menu.privacy
import com.orbital.cee.view.home.components.*
import com.orbital.cee.view.language.language
import com.orbital.cee.view.setting.setting
import com.orbital.cee.view.sound.sound
import com.orbital.cee.view.trip.Speed
import com.orbital.cee.view.trip.advancedShadow
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.collections.immutable.mutate
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.koin.androidx.viewmodel.ext.android.viewModel
import pub.devrel.easypermissions.EasyPermissions
import java.io.IOException
import java.math.RoundingMode
import java.net.URL
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt
import kotlin.properties.Delegates

val Context.data_Store by dataStore("alarm-less-reports.json", AppSettingsSerializer)
@RequiresApi(Build.VERSION_CODES.S)
@AndroidEntryPoint
class HomeActivity : ComponentActivity(), EasyPermissions.PermissionCallbacks //,SensorEventListener
{
    val model: HomeViewModel by viewModel()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var geofencingClient : GeofencingClient
    private lateinit var db : FirebaseFirestore
    private lateinit var mAuth : FirebaseAuth
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private val df = DecimalFormat("#.##")
//    private var showTripDialog = mutableStateOf(false)
    private var alertCount = mutableIntStateOf(0)
    private var distance = mutableStateOf(0f)
    private var maxSpeed = mutableStateOf(0)
//    private var lLocation = Location("")
    var isFirstLunch = false
    var mRewardedAd : RewardedAd ? = null
    private var mInterstitialAd: InterstitialAd? = null
    var isPurchasedAdRemove = mutableStateOf(false)
    private var lLocation = Location("")

    var isInsideP2PRoad = mutableStateOf(false)
    var langCode = mutableStateOf("en")
    var username = mutableStateOf("")
    var remoteConfig : FirebaseRemoteConfig? = null
//    var rad = 5
    private var wakeLock: PowerManager.WakeLock? = null
    private var appUpdate : AppUpdateManager? = null
    private val REQUEST_CODE = 100
////    private lateinit var handler: Handler
////    private var seconds = 0
////    private var pauseOffset: Int = 0
//
//    private var distanceLoc : Location? = null

    object Singlt {
        var SoundSta = mutableStateOf(1)
        var stopCount = mutableStateOf(0)
        fun set(Type : Int){
            SoundSta.value = Type
        }
    }
    var isDone : Boolean by Delegates.observable(false){
            _, _, newValue ->
        if (newValue){
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }
//    fun calcDis(cloc: Location) {
//        if (distanceLoc == null) {
//            distanceLoc = cloc
//        }else{
//            val tempDistance = distanceLoc!!.distanceTo(cloc)/1000
//            distanceLoc = cloc
////            model.addDistance(tempDistance)
//        }
//    }
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
//    private fun startTimer() {
//        // create a new runnable and post it to the handler every second
//        handler.post(object : Runnable {
//            override fun run() {
//                seconds++
//                handler.postDelayed(this, 1000)
//                model.timeRemain.value = seconds
//                Log.d("TIMER_COUNT",seconds.toString())
//            }
//        })
//
//        model.isTimerRunning.value = true
//    }
//    private fun pauseTimer() {
//        handler.removeCallbacksAndMessages(null)
//        pauseOffset = seconds
//        model.isTimerRunning.value = false
//    }
//    private fun resetTimer() {
//        if (model.isTimerRunning.value) {
//            pauseTimer()
//        }
//        seconds = 0
//        pauseOffset = 0
//        model.timeRemain.value = seconds
//    }

//    private var sensorManager: SensorManager? = null
//    private var rotationMatrix = FloatArray(9)
//    private var orientationAngles = FloatArray(3)
//    private var currentDegree = 0f

    @OptIn(ExperimentalAnimationApi::class)
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fix()
        locationCallback = object : LocationCallback(){
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                if (!isDone){
                    p0.lastLocation?.let {
                        model.lastLocation.value = it
                        isLocationChanged(it)
                    }
                }
            }
        }
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.SCREEN_DIM_WAKE_LOCK or PowerManager.ON_AFTER_RELEASE,
            "Cee:WAKE_POWER_CEE_MAIN_SCREEN"
        )



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

        val defaultValue = HashMap<String,Any>()
        defaultValue["reportSightRadius"] = 5

        remoteConfig!!.setDefaultsAsync(defaultValue)
        remoteConfig!!.fetch(0)
        remoteConfig!!.fetchAndActivate()

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACTIVITY_RECOGNITION,Manifest.permission.POST_NOTIFICATIONS), 0)

        appUpdate = AppUpdateManagerFactory.create(this)
        callInAppUpdate()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val isPlaceCamera = intent?.getIntExtra("action_type",0) == 1
        if(mAuth.currentUser == null){
            val navigate = Intent(this, MainActivity::class.java)
            this.startActivity(navigate)
            Toast.makeText(this,"please login before.",Toast.LENGTH_LONG).show()
        }
        if (isPlaceCamera){
            model.addReport(0)
        }
        if (Build.VERSION.SDK_INT >= 25) {
            Shortcuts.setUp(applicationContext)
        }
        createLocationRequest()

        setContent {

            val soundSta = model.soundStatus.observeAsState()
            soundSta.value?.let { Singlt.set(it) }
            CEETheme(darkTheme = model.isDarkMode.value,langCode = langCode.value) {
                val navController = rememberAnimatedNavController()
                val trips = model.trips.observeAsState()
                AnimatedNavHost(navController, "home") {
                    composable("home") {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colors.background
                        ) {

                            BackHandler(true) {}
                            MainMapScreen(model = model,trips.value)

                            if (isLocationChange.value){
                                model.getReportsAndAddGeofences()
                            }
                        }

                        LaunchedEffect(Unit){
                            model.appSetting.value?.let {
                                if (it.preventScreenSleep){
                                    Log.d("DEBUG_TIMEOUT_SLEEP_SCREEN", it.toString())
                                    wakeLock?.acquire((it.screenSleepTimeOutInSecond.div(10))*360*1000L)
                                }else{
                                    wakeLock = null
                                }
                            }
                        }
                    }
                    composable("trip",
                        enterTransition = { slideIntoContainer(AnimatedContentScope.SlideDirection.Up, animationSpec = tween(700))},
                        exitTransition = { slideOutOfContainer(AnimatedContentScope.SlideDirection.Down, animationSpec = tween(700))})
                    {
                        Speed(model = model,onClickStart = {model.startTrip() },onClickFinish = { GlobalScope.launch { model.saveTrip() } },onClickContinue = {model.continueTrip()},
                            onClickBack = { navController.popBackStack() },onClickResetTrip = {model.tripReset()},onClickPause = { model.pauseTrip()},
                            isPurchasedAdRemove = isPurchasedAdRemove
                        )

                    }
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
                if (location.distanceTo(lLocation) > (25*1000)-500){
                    model.getReportsAndAddGeofences()
                    lLocation = location
                }
            }
        }
    }
//    fun speed(speed: Float?) {
//        if (speed != null){
//            val speedToInt = (speed * 3.6).toInt()
//            speed.value = speedToInt
//            model.saveMaxSpeed(speedToInt)
////            model.speedPercent.value = ((speedToInt.toFloat() / MAX_SPEED.toFloat()) * 0.8).toFloat()
//            isHasSpeed(speedToInt)
//        }
//    }
//    private fun isHasSpeed(speed: Int) {
//        if (speed>20){
//            count.value++
//            if (count.value > 5){
//                isInAway.value = true
//                count.value = 0
//            }
//        }else{
//            if (speed<5){
//                if (isInAway.value){
//                    isInAway.value = false
//                    stopCount.value++
//                    isInsideP2PRoad.value = false
//                }
//            }
//        }
//    }
//    @RequiresApi(Build.VERSION_CODES.S)
//    private fun isLocationChanged(location: Location?) {
//        if (location != null){
//            if (lLocation == Location("")){
//                model.getReportsAndAddGeofences()
//                lLocation = location
//            }else{
//                if (location.distanceTo(lLocation) > (rad*1000)-500){
//                    model.getReportsAndAddGeofences()
//                    lLocation = location
//                }
//            }
//        }
//    }




    @RequiresApi(Build.VERSION_CODES.S)
    override fun onResume() {
        super.onResume()
        inProgressUpdate()
        startLocationUpdate()
        registerReceiver(transitionBroadcastReceiver, IntentFilter(TRANSITIONS_RECEIVER_ACTION))

//        sensorManager?.getDefaultSensor(Sensor.TYPE_ORIENTATION)?.also { orientationSensor ->
//            sensorManager?.registerListener(
//                this,
//                orientationSensor,
//                SensorManager.SENSOR_DELAY_NORMAL
//            )
//        }

    }
    override fun onStart() {
        super.onStart()
        model.appLaunchTime.value = Date()
        model.navigationBarHeight.value =  pxToDp(getNavigatingBarHeight(this))
        Intent(applicationContext, MyLocationService::class.java).apply {

            action = MyLocationService.ACTION_START
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(this)
            }else{
                startService(this)
            }

        }
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
        wakeLock?.release()
        wakeLock = null

        unregisterReceiver(transitionBroadcastReceiver)
        model.geofencingClient.removeGeofences(model.geofencePendingIntent)
        stopService(Intent(this, DetectedActivityService::class.java))
        mAuth.currentUser?.let {
            val hash = GeoFireUtils.getGeoHashForLocation(GeoLocation(model.lastLocation.value.latitude, model.lastLocation.value.longitude))
            val user : HashMap<String, Any> = HashMap<String, Any>()
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
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            Toast.makeText(this,"Downloading start",Toast.LENGTH_SHORT).show()
            if (resultCode != RESULT_OK) {
                Log.e("MY_APP", "Update flow failed! Result code: $resultCode")
            }
        }
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
        Toast.makeText(this,"Please Set Background Permission.",Toast.LENGTH_LONG).show()
    }
    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = 40000
            fastestInterval = 15000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            maxWaitTime = 10000
        }
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

//    private fun updateCompass() {
//        val azimuth = orientationAngles[0]
//        val newDegree = azimuth
//        changeCompaseValue(newDegree)
//        Log.d("DEBUG_ROT_BER",newDegree.toString())
//        GlobalScope.launch {
//            currentDegree = newDegree
//        }
//    }
//    var oldCompassValue = 0f
//    private fun changeCompaseValue(newDegree : Float) {
//        if (oldCompassValue == 0f){
//            oldCompassValue = newDegree
//            updateBearing(newDegree)
//        }else{
//            if (oldCompassValue-10 > newDegree || oldCompassValue+10 <newDegree){
//                oldCompassValue = newDegree
//                updateBearing(newDegree)
//            }
//        }
//    }
//
//    private fun updateBearing(newDegree : Float) {
//        model.mapView.getMapboxMap().setCamera(CameraOptions.Builder().bearing(newDegree.toDouble()).build())
//    }

    private fun callInAppUpdate(){
        appUpdate?.appUpdateInfo?.
        addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                appUpdate?.startUpdateFlowForResult(appUpdateInfo,AppUpdateType.IMMEDIATE,this,REQUEST_CODE)
            }
        }
    }
    private fun inProgressUpdate(){
        appUpdate?.appUpdateInfo?.
        addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                appUpdate?.startUpdateFlowForResult(appUpdateInfo,AppUpdateType.IMMEDIATE,this,REQUEST_CODE)
            }
        }
    }
//
//    override fun onSensorChanged(event: SensorEvent?) {
//        event?.let {
//            if (it.sensor.type == Sensor.TYPE_ORIENTATION) {
//                orientationAngles = it.values.clone()
//                updateCompass()
//            }
//        }
//    }
//
//    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
//        //jj
//    }
}

fun <T> concatenate(vararg lists: List<T>): List<T> {
    return listOf(*lists).flatten()
}