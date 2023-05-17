@file:OptIn(DelicateCoroutinesApi::class)

package com.orbital.cee.view.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
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
import com.orbital.cee.core.MyLocationService.LSS.calcAverageSpeed
import com.orbital.cee.core.MyLocationService.LSS.calcTrip
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
import com.orbital.cee.view.home.HomeActivity.Singlt.setBer
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
import kotlin.properties.Delegates

@RequiresApi(Build.VERSION_CODES.S)
@AndroidEntryPoint
class HomeActivity : ComponentActivity() , EasyPermissions.PermissionCallbacks  {
    val model: HomeViewModel by viewModel()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var geofencingClient : GeofencingClient
    private lateinit var db : FirebaseFirestore
    private lateinit var mAuth : FirebaseAuth
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var lrouteCoordinates = ArrayList<Point>()
    private var layerIDD = "map_annotation"
    private var layerPinIDD = "map_pin"
    private var reportId = ""
    private val df = DecimalFormat("#.##")
    private var showRegisterDialog = mutableStateOf(false)
    private var isShowMenu = mutableStateOf(false)
    private var showEditReportDialog = mutableStateOf(false)
    private var showAddReportManuallyDialog = mutableStateOf(false)
    private var showTripDialog = mutableStateOf(false)

    private var speedLimit :Int? = 0
    private var reportIdEditing :String = ""

    private var reportType = mutableStateOf(1)
    private var alertCount = mutableStateOf(0)
    private var reportCount = mutableStateOf(0)
    private var timeLastReport = mutableStateOf(0L)
    private var distance = mutableStateOf(0f)
    private var maxSpeed = mutableStateOf(0)
    private var lLocation = Location("")
    var isFromLogin = false
    var isFirstLunch = false
    var mRewardedAd : RewardedAd ? = null
    private var mInterstitialAd: InterstitialAd? = null
    var isPurchasedAdRemove = mutableStateOf(false)
    var isChangeDetected = mutableStateOf(false)
    var deleteReportDialog = mutableStateOf(false)
    var count = mutableStateOf(0)
    var navigationBarHeight = mutableStateOf(0)
    var stopCount = mutableStateOf(0)
    var isInAway = mutableStateOf(true)
    var isInsideP2PZone = mutableStateOf(false)
    var isInsideP2PRoad = mutableStateOf(false)
    var isReachedQuotaDialog = mutableStateOf(false)
    var langCode = mutableStateOf("en")
    var username = mutableStateOf("")
    var userAvatar = mutableStateOf("")
    var timeRemainInt = mutableStateOf(0f)
    var pointClickedOnMap = mutableStateOf(GeoPoint(0.00,0.00))
    var remoteConfig : FirebaseRemoteConfig? = null
    var firstPointId : String? = null
    var showErrorPlaceReport = mutableStateOf(false)
    var rad = 5
    private val tempReports1 = mutableListOf<NewReport>()
    private val tempGeofence1 = mutableListOf<Geofence>()
    private val tempReports2 = mutableListOf<NewReport>()
    private val tempGeofence2 = mutableListOf<Geofence>()
    private val tempReports3 = mutableListOf<NewReport>()
    private val tempGeofence3 = mutableListOf<Geofence>()
    private val tempReports4 = mutableListOf<NewReport>()
    private val tempGeofence4 = mutableListOf<Geofence>()

    private var appUpdate : AppUpdateManager? = null
    private val REQUEST_CODE = 100
    private lateinit var handler: Handler
    private var seconds = 0
    private var pauseOffset: Int = 0

    private var distanceLoc : Location? = null


    object Singlt {
        var SoundSta = mutableStateOf(1)
        var bearingLoc = mutableStateOf(1)
        fun set(Type : Int){
            SoundSta.value = Type
        }
        fun setBer(value : Int){
            bearingLoc.value = value
            Log.d("BERING_TEST",value.toString())
        }
    }
    var isDone : Boolean by Delegates.observable(false){
            _, _, newValue ->
        if (newValue){
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }
    fun calcDis(cloc: Location) {
        if (distanceLoc == null) {
            distanceLoc = cloc
        }else{
            val tempDistance = distanceLoc!!.distanceTo(cloc)/1000
            distanceLoc = cloc
            model.addDistance(tempDistance)
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
    private fun startTimer() {
        // create a new runnable and post it to the handler every second
        handler.post(object : Runnable {
            override fun run() {
                seconds++
                handler.postDelayed(this, 1000)
                model.timeRemain.value = seconds
                Log.d("TIMER_COUNT",seconds.toString())
            }
        })

        model.isTimerRunning.value = true
    }
    private fun pauseTimer() {
        handler.removeCallbacksAndMessages(null)
        pauseOffset = seconds
        model.isTimerRunning.value = false
    }
    private fun resetTimer() {
        if (model.isTimerRunning.value) {
            pauseTimer()
        }
        seconds = 0
        pauseOffset = 0
        model.timeRemain.value = seconds
    }



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
                        speed(it.speed)
                        calcTrip(it)
                        calcDis(it)
                        isLocationChanged(it)
                        setBer(it.bearing.toInt())
                        if(model.isTripStarted.value){
                            lrouteCoordinates.add(Point.fromLngLat(it.longitude,it.latitude))
                        }
                        isChangeDetected.value = true
                        if (isInsideP2PRoad.value){
                            calcAverageSpeed(it)
                        }
                    }
                }
            }
        }

        Log.d("DEBUG_PADDING_In", pxToDp(getNavigatingBarHeight(this)).toString())
        isFirstLunch = true

        handler = Handler()
        startTimer()
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

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,), 0)

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
            addReport(0)
        }
        if (Build.VERSION.SDK_INT >= 25) {
            Shortcuts.setUp(applicationContext)
        }
        createLocationRequest()

        setContent {
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(this,resources.getString(R.string.interstitial_ad_three_stop_id), adRequest, object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                }
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                }
            })
            RewardedAd.load(this,resources.getString(R.string.remove_ads_rewarded_video_id),adRequest,object : RewardedAdLoadCallback(){
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mRewardedAd = null
                }
                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    mRewardedAd = rewardedAd
                }
            })
            mRewardedAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
                override fun onAdClicked() {}
                override fun onAdDismissedFullScreenContent() {
                    mRewardedAd = null
                }
                override fun onAdImpression() {}
                override fun onAdShowedFullScreenContent() {}
            }
            if (stopCount.value == 4){
                stopCount.value = 0
                if (!isPurchasedAdRemove.value){
                    mInterstitialAd?.show(this)
                }
            }
            val soundSta = model.soundStatus.observeAsState()
            soundSta.value?.let { Singlt.set(it) }
            CEETheme(darkTheme = model.isDarkMode.value,langCode = langCode.value) {
                val trips = model.trips.observeAsState()
                val navController = rememberAnimatedNavController()
                AnimatedNavHost(navController, "home") {
                    composable("home") {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colors.background
                        ) {
                            BackHandler(true) {}
                            MainMapScreen(model = model,trips.value)
                        }
                    }
                    composable("trip",
                        enterTransition = { slideIntoContainer(AnimatedContentScope.SlideDirection.Up, animationSpec = tween(700))},
                        exitTransition = { slideOutOfContainer(AnimatedContentScope.SlideDirection.Down, animationSpec = tween(700))})
                    {
                        Speed(
                            model = model,onClickStart = {startTrip() },onClickFinish = {saveTrip()},onClickContinue = {
                                model.isTripStarted.value = true
                                showTripDialog.value = false
//                                if (!model.isTimerRunning.value){
//                                    startTimer()
//                                }
                            },onClickBack = {
                                navController.popBackStack()
                            },onClickResetTrip = {
                                resetTimer()
                                model.isTripStarted.value = false
                                resetTrip()
                            },onClickPause = {
                                if (model.isTimerRunning.value){
                                    pauseTimer()
                                }else{
                                    startTimer()
                                }
                                MyLocationService.LSS.isTripPused.value = !MyLocationService.LSS.isTripPused.value
                            },isPurchasedAdRemove = isPurchasedAdRemove
                        )

                    }
                }
            }
        }
    }
    fun speed(speed: Float?) {
        if (speed != null){
            val speedToInt = (speed * 3.6).toInt()
            model.speed.value = speedToInt
            model.saveMaxSpeed(speedToInt)
            model.speedPercent.value = ((speedToInt.toFloat() / MAX_SPEED.toFloat()) * 0.8).toFloat()
            isHasSpeed(speedToInt)
        }
    }
    private fun isHasSpeed(speed: Int) {
        if (speed>20){
            count.value++
            if (count.value > 5){
                isInAway.value = true
                count.value = 0
            }
        }else{
            if (speed<5){
                if (isInAway.value){
                    isInAway.value = false
                    stopCount.value++
                    isInsideP2PRoad.value = false
                }
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("MissingPermission")
    fun getJsonAsync(lat: Double, lon: Double) = GlobalScope.async{
        if (isOnline(this@HomeActivity)){
            val respo = URL("https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${lon}&appid=02c2bac30e0194dff2c04877257c322e").readText()
            val data = Gson().fromJson(respo,WeatherDto::class.java)
            model.temperature.value = (data.main?.temp?.minus(273))?.toInt()
            Log.d("OWAPID",data.name.toString() )
        }
    }
    @RequiresApi(Build.VERSION_CODES.S)
    private fun isLocationChanged(location: Location?) {
        if (location != null){
            if (lLocation == Location("")){
                model.getReportsAndAddGeofences()
                lLocation = location
            }else{
                if (location.distanceTo(lLocation) > (rad*1000)-500){
                    model.getReportsAndAddGeofences()
                    lLocation = location
                }
            }
        }
    }
    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.S)
    @OptIn(ExperimentalMaterialApi::class, ExperimentalPagerApi::class,)
    @Composable
    fun MainMap(model : HomeViewModel, trips :ArrayList<Trip?>?) {
        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current
        val conf = LocalConfiguration.current
        var bottomSheetState = rememberBottomSheetState(initialValue =BottomSheetValue.Collapsed, confirmStateChange = {
            Log.d("DEBUG_MODAL_BOTTOM_SHEET", "col: $it")
            if(it == BottomSheetValue.Collapsed){
                model.whichButtonClicked.value = 0
            }
            true
        })
        Log.d("DEBUG_MODAL_BOTTOM_SHEET","prog: "+bottomSheetState.progress)


        val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = bottomSheetState)
        val alarm = remember {mutableStateOf(false)}
        val infiniteTransition = rememberInfiniteTransition()
        val configuration = LocalConfiguration.current
        model.readAlertsCount.observeAsState().value?.let {alertCount.value = it }
        model.loadTimeOfLastReport.observeAsState().value?.let {timeLastReport.value = it }
        model.reportCountPerOneHour.observeAsState().value?.let {reportCount.value = it }
        model.readDistance.observeAsState().value?.let {distance.value = it }
        model.readMaxSpeed.observeAsState().value?.let {maxSpeed.value = it }
        val lastWatchedAd = model.lsatAdsWatched.observeAsState()
        val userT = model.userType.observeAsState()
        val singleReport = remember {mutableStateOf(SingleCustomReport(isSuccess = false))}

        var mapView by remember { mutableStateOf<MapView?>(null) }
        mapView = MapView(context, MapInitOptions(context, antialiasingSampleCount = 4))
        DisposableEffect(Unit) {
            val newMapView = MapView(context).apply {
                model.mapView = this

                getMapboxMap().loadStyleUri(styleUri = if(model.isDarkMode.value) "mapbox://styles/orbital-cee/clevkf2lm00l301msgt5l621u" else "mapbox://styles/orbital-cee/clh35qsv500lp01qy0rq23sbt") { sty ->
                    //zoomCamera(model.lastLocation.value.longitude,model.lastLocation.value.latitude)

                    if (!Permissions.hasLocationPermission(this@HomeActivity)) {
                        model.isLocationNotAvailable.value = true
                        Permissions.requestsLocationPermission(this@HomeActivity)
                    } else {
                        if (model.checkDeviceLocationSettings(this@HomeActivity)) {

                            val data = geoJsonSource("line") {}
                            sty.addSource(data)
                            sty.addLayer(lineLayer("linelayer", "line") {
                                lineCap(LineCap.ROUND)
                                lineJoin(LineJoin.ROUND)
                                lineWidth(5.0)
                                lineColor("#495CE8")
                            })
                            // geofence circle
//                          sty.addLayer(fillLayer(layerId = "circlee", sourceId = "reportss"){
//                          fillColor("#495CE8")
//                                                    })
                            model.annotationApi = annotations
                            model.annotationApii = annotations
                            model.annotationConfig = AnnotationConfig(
                                layerId = layerIDD
                            )
                            model.annotationConfigg = AnnotationConfig(

                                layerId = layerPinIDD
                            )
                            model.pointAnnotationManagerr =
                                model.annotationApii!!.createPointAnnotationManager(
                                    model.annotationConfigg
                                )
                            model.pointAnnotationManager =
                                model.annotationApi!!.createPointAnnotationManager(
                                    model.annotationConfig
                                )


//                          flyCamera(model.lastLocation.value.longitude,model.lastLocation.value.latitude)
                            model.initLocationComponent()
                            model.setupGesturesListener()
                        } else {
                            model.isLocationNotAvailable.value = true
                        }
                    }
                }
                if (model.userType.value == 2){
                    getMapboxMap().addOnMapLongClickListener{point ->
                        pointClickedOnMap.value = GeoPoint(point.latitude(),point.longitude())
                        model.createClickedPin(point.latitude(),point.longitude())
                        Toast.makeText(this@HomeActivity,"Please wait...",Toast.LENGTH_SHORT).show()
                        fetchReports(point.latitude(),point.longitude())
                        false
                    }
                }
                scalebar.enabled = false
                compass.enabled = false
                attribution.enabled = false
            }
            mapView = newMapView
            onDispose {
                model.removeGesturesListener()
                mapView?.onDestroy()
            }

        }

        LaunchedEffect(Unit){
            mAuth.currentUser?.let { it1 ->
//                rad = remoteConfig!!.getLong("reportSightRadius").toInt()
                Log.d("REMOTE_CONFIG", remoteConfig!!.getLong("reportSightRadius").toString())
                FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        return@OnCompleteListener
                    }
                    val token = task.result
                    val user : HashMap<String, Any> = HashMap<String, Any>()
                    user["pushId"] = token
                    user["status"] = "online"
                    user["uniqueDeviceID"] = Utils.getDeviceUniqueID(context as? Activity)
                    db.collection(DB_REF_USER).document(it1.uid).update(user)
                })
                db.collection(DB_REF_USER).document(it1.uid).get().addOnSuccessListener {
                    username.value = it.data?.get("username") as? String? ?:
                    ""
                    userAvatar.value = it.data?.get("userAvatar") as? String? ?:
                    ""
                    val userType = (it.data?.get("userType") as? Long?)?.toInt()
                    model.saveUserType(userType = userType?:0)
                    if (username.value == ""){
                        showRegisterDialog.value = true
                    }
                }
                if(isFromLogin){
                    db.collection(DB_REF_USER).document(it1.uid).collection("statistic").document("GeneralStats").get().addOnSuccessListener {
                        val alert = (it.data?.get("alertedTime") as? Long?)?.toInt()
                        val maxSpeed = (it.data?.get("maxSpeed") as? Long?)?.toInt()
                        val distance = (it.data?.get("traveldDistance") as? Double?)?.toFloat()
                        if (alert != null) {
                            model.addAlertCount(alert-1)
                        }
                        if (distance != null) {
                            model.addDistance(distance)
                        }
                        if (maxSpeed != null) {
                            model.saveMaxSpeed(maxSpeed)
                        }
                    }
                }

            }
        }
        LaunchedEffect(Unit){
            delay(5)
            if (Permissions.hasLocationPermission(this@HomeActivity)){
                while (true){
                    getJsonAsync(lLocation.latitude,lLocation.longitude)
                    delay(180000)
                }
            }
        }
        if (GeofenceBroadcastReceiver.GBRS.GeoId.value != null && GeofenceBroadcastReceiver.GBRS.GeoId.value != reportId){
            model.addAlertCount(alertCount.value)
            reportId = GeofenceBroadcastReceiver.GBRS.GeoId.value!!
            model.inSideReportToast.value = true
//            model.slider.value = false
            model.inSideReport.value = true

            mAuth.currentUser?.let { it1 ->
                val alert : HashMap<String, Any> = HashMap<String, Any>()
                alert["lastSeen"] = FieldValue.serverTimestamp()
                alert["speedWhenEntered"] = model.speed.value
                alert["userAvatar"] = userAvatar.value
                alert["userId"] = it1.uid
                alert["username"] = username.value
                db.collection(DB_REF_REPORT).document(reportId).collection(DB_REF_ALERTED).document(it1.uid).set(alert)
            }

            LaunchedEffect(Unit){
                coroutineScope.launch {
                    model.getSingleReport(reportId).collect{
                        if (it.isSuccess){
                            singleReport.value = it
                            model.isLiked.value = null
                            if(it.reportType == 6){
                                Log.d("P2PDebug",reportId)
                                if (firstPointId == null){
                                    firstPointId = reportId
                                    isInsideP2PZone.value = true
                                    isInsideP2PRoad.value = true
                                    resetP2PCalc()
                                    EnteredP2PTime = Date()
                                }else{
                                    if (firstPointId != reportId){
                                        firstPointId = null
                                        delay(10000)
                                        isInsideP2PZone.value = false

                                    }
                                }
                            }
                        }
                    }
                }
            }


        }else{
            if(GeofenceBroadcastReceiver.GBRS.GeoId.value == null){
                alarm.value = false
                model.inSideReportToast.value = false
            }
        }
        if (GeofenceBroadcastReceiver.GBRS.GeoId.value == null && model.inSideReport.value){
            model.inSideReport.value = false
            LaunchedEffect(Unit){
                coroutineScope.launch {
                    delay(5000)
                    model.slider.value = true
                }
            }
        }else{
            if(GeofenceBroadcastReceiver.GBRS.GeoId.value == null){
                model.slider.value = false
            }
        }
        Box(modifier = Modifier.fillMaxSize()){
            BottomSheetScaffold(
                sheetPeekHeight = (90 + navigationBarHeight.value).dp,
                sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                sheetContent = {

                    when (model.whichButtonClicked.value) {
                        0->{
                            bottomBar(
                                model = model,
                                onButtonClicked = {btnId ->
                                    model.whichButtonClicked.value = btnId
                                    coroutineScope.launch {
                                        delay(5)
                                        bottomSheetState.expand()
                                    }
                                },
                                onClickSpeed = {
                                    coroutineScope.launch {
                                        model.whichButtonClicked.value = 7
                                        delay(5)
                                        scaffoldState.bottomSheetState.expand()
                                    }
                                },
                                speedLimit = singleReport.value.reportSpeedLimit ?: 0,
                                navigationBarHeight = navigationBarHeight.value,bottomSheetState = bottomSheetState,
                                startTrip = {startTrip()}, saveTrip = {saveTrip()}, continueTrip = {continueTrip()},
                                pauseTrip = {pauseTrip()}, tripReset = {tripReset()}, isPurchasedAdRemove = isPurchasedAdRemove
                            )
                        }
                        1 -> {
                            UpdateCeeMap(onButtonClicked = {RT->
                                if(GeofenceBroadcastReceiver.GBRS.GeoId.value != null){

                                }else{
                                    if (isOnline(this@HomeActivity)){
                                        model.vibrate(context = context)
                                        reportType.value = RT
                                        if (model.userType.value == 2){
                                            if(RT == 1 || RT == 6 || RT == 5){
                                                model.whichButtonClicked.value =6
                                            }else{
                                                model.showCustomDialogWithResult.value = true
                                            }
                                        }else{
                                            model.showCustomDialogWithResult.value = true
                                        }
                                    }else{
                                        Toast.makeText(this@HomeActivity,"sorry not internet connection.",Toast.LENGTH_LONG).show()
                                    }
                                }
                            },
                                onCloseClick = {
                                    coroutineScope.launch {
                                        bottomSheetState.collapse()
//                                        modalBottomSheetState.hide()
                                    }
                                }, userType = userT.value ?: 0
                            )
                        }
                        2 -> {
                            TabLayout(model.allReports.iterator()){
                                model.onCameraTrackingDismissed()
                                model.mapView.getMapboxMap().flyTo(
                                    cameraOptions {
                                        center(Point.fromLngLat(
                                            it.longitude, it.latitude
                                        ))
                                        zoom(14.5)
                                        pitch(10.0)
                                    },
                                    mapAnimationOptions {
                                        duration(1000)
                                    }
                                )
                                coroutineScope.launch {
                                    bottomSheetState.collapse()
                                }
                            }
                        }
                        3 -> {
                            SoundBottomModal(model){
                                coroutineScope.launch {
                                    bottomSheetState.collapse()
                                }
                            }
                        }
                        4 -> {
                            NewReportViewDetail(vModel = model,model.reportId,
                                onCloseClick = {
                                    coroutineScope.launch {
                                        bottomSheetState.collapse()
                                    }
                                }, onReportDelete = {
                                    deleteReportDialog.value = true

                                },onEditSpeedLimit={repo,slim->
                                    speedLimit = slim
                                    reportIdEditing = repo
                                    showEditReportDialog.value = true
                                },
                                userId = mAuth.currentUser?.uid
                            )
                        }
                        5 -> {
                            RemoveAds(isPurchasedAdRemove, onClickClose = {
                                coroutineScope.launch {
                                    bottomSheetState.collapse()
                                }
                            }, onClickWatchVideo = {
                                if(mRewardedAd != null){
                                    mRewardedAd?.show(this@HomeActivity) {
                                        Log.d("TAG_AD_DEB", "User earned the reward.${it.type} , ${it.amount}")
                                        coroutineScope.launch { isPurchasedAdRemove.value = true  }
                                        model.saveWatchAdTime(Timestamp.now())
                                    }
                                }else{
                                    Toast.makeText(this@HomeActivity,"please wait.",Toast.LENGTH_LONG).show()
                                }
                            })
                        }
                        6-> {
                            speedLimit(onSelectedSpeed={addReport(it)},onDismiss={
                                coroutineScope.launch {
                                    bottomSheetState.collapse()
                                }
                            })
                        }
                        7-> {
                            Speed(
                                model = model,onClickStart = {startTrip()},
                                onClickFinish = {saveTrip()},
                                onClickContinue = {continueTrip()},
                                onClickBack = {
                                    coroutineScope.launch {
                                        bottomSheetState.collapse()
                                    }
                                },onClickResetTrip = {tripReset()},onClickPause = {pauseTrip()},isPurchasedAdRemove = isPurchasedAdRemove
                            )
                        }
                    }
                },
                scaffoldState = scaffoldState,
                backgroundColor = MaterialTheme.colors.background,
                sheetBackgroundColor = Color.Transparent


            ) {pad->
                Log.d("DEBUG_PADDING_In",pad.toString())
                lastWatchedAd.value?.let{
                    if(it > 0){
                        LaunchedEffect(Unit){
                            while (true){
                                //timeRemain.value = getRemain(lastWatchedAd.value,Timestamp.now().seconds)
                                timeRemainInt.value = getRemainInt(lastWatchedAd.value,Timestamp.now().seconds)
                                isPurchasedAdRemove.value = (Timestamp.now().seconds - it) < 1800
                                delay(1000)
                            }
                        }
                    }
                }
                Scaffold(floatingActionButton = {
                    fab(model,
                        navigationBarHeight = navigationBarHeight.value,
                        onClickReport = {
                        if(GeofenceBroadcastReceiver.GBRS.GeoId.value != null){
                        }else{
                            if (isOnline(this@HomeActivity)){
                                if(distance.value >= 15){

                                    if(Permissions.hasBackgroundLocationPermission(context = context)){
                                        model.vibrate(context = context)
                                        reportType.value = 1
                                        if(model.userType.value == 2){
                                            model.whichButtonClicked.value =6
                                            coroutineScope.launch {
//                                                modalBottomSheetState.show()
                                                delay(5)
                                                bottomSheetState.expand()
                                            }
                                        }else{
                                            model.showCustomDialogWithResult.value = true
                                        }
                                    }else{
                                        Toast.makeText(context,"sorry, This action needs background location permission.",Toast.LENGTH_LONG).show()
                                    }
                                }else{
                                    showErrorPlaceReport.value = true
                                }
                            }else{
                                Toast.makeText(context,"please check internet connection.",Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                        onClickIndicator = {
                            try {
                                model.vibrate(context = context)
                                model.isCameraMove.value = !model.isCameraMove.value
                                fusedLocationProviderClient.lastLocation.addOnCompleteListener {
                                    if (it.isComplete) {
                                        if (it.result != null) {
                                            if (model.userInfo.value.userType == 1 || model.userInfo.value.userType == 2){
                                                fetchReports(it.result.latitude,it.result.longitude)
                                            }

                                            model.mapView.getMapboxMap().flyTo(
                                                cameraOptions {
                                                    center(
                                                        Point.fromLngLat(
                                                            it.result.longitude, it.result.latitude
                                                        ))
                                                    zoom(14.5)
                                                },
                                                mapAnimationOptions {
                                                    duration(2000)
                                                }
                                            )
                                            model.initLocationComponent()
                                            model.setupGesturesListener()
                                        }else{
                                            Toast.makeText(this@HomeActivity,"Location not found.",Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                            }catch (e:Exception){
                                Toast.makeText(this@HomeActivity,"${e.message}",Toast.LENGTH_LONG).show()
                            }
                        },
                        onClickReportAddManually = {
//                            coroutineScope.launch {
//                                model.whichButtonClicked.value = 3
//                                delay(100)
//                                bottomSheetState.expand()
////                                modalBottomSheetState.show()
//                            }
                            showAddReportManuallyDialog.value = true
                        }
                    )
                }) {padd->
                    Log.d("DEBUG_PADDING_In",padd.toString())
                    Box(modifier = Modifier
                        .fillMaxSize()
//                        .padding(bottom = if((padd.calculateBottomPadding() - 15.dp) >= 0.dp){padd.calculateBottomPadding() - 15.dp} else{0.dp} )
                        .background(color = MaterialTheme.colors.background)) {
                        if(model.allReports.size != 0 && model.pointAnnotationManager?.annotations?.size != model.allReports.size){
                            Log.d("TESTANOSREPOS", model.pointAnnotationManager?.annotations?.size.toString())
                            Log.d("TESTANOSREPOS", model.allReports.size.toString())
                            model.createMarkerOnMap(model.allReports)
                        }else{
                            if(model.allReports.size == 0 ){
                                LaunchedEffect(Unit){
                                    model.getReportsAndAddGeofences()
                                }
                            }
                        }
                        Log.d("TESTANOSREPOS", model.allReports.size.toString())
                        Log.d("TESTANOSREPOS", "Ano: "+model.pointAnnotationManager?.annotations?.size.toString())
                        Box(modifier = Modifier.fillMaxSize()) {
                            AndroidView({ mapView!! })
                            val lineString = LineString.fromLngLats(lrouteCoordinates)
                            val feature = Feature.fromGeometry(lineString)

                            if(model.isCameraZoomChanged.value != model.isShowDots.value){
                                model.isCameraZoomChanged.value = model.isShowDots.value
                                Log.d("DEBUG_CAMERA_ZOOM", "Run")
                                model.createMarkerOnMap(model.allReports)
                            }

                            if(isChangeDetected.value){
                                model.mapView.getMapboxMap().getStyle {
                                    it.getSourceAs<GeoJsonSource>("line")
                                        ?.featureCollection(
                                            FeatureCollection.fromFeature(feature)
                                        )
                                }
                                isChangeDetected.value = false
                            }
                            if(isChangeDetected.value){
                                model.mapView.getMapboxMap().getStyle {
                                    it.getSourceAs<GeoJsonSource>("line")
                                        ?.featureCollection(
                                            FeatureCollection.fromFeature(feature)
                                        )
                                }
                                isChangeDetected.value = false
                            }
                            if (GeofenceBroadcastReceiver.GBRS.GeoId.value != null){
                                val corR by infiniteTransition.animateFloat(
                                    initialValue = 5.0F,
                                    targetValue = 18.0F,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(
                                            durationMillis = 1500,
                                            delayMillis = 100,
                                            easing = FastOutSlowInEasing
                                        ),
                                        repeatMode = RepeatMode.Reverse
                                    )
                                )
                                Box(modifier = Modifier
                                    .fillMaxSize()
                                    .innerShadow(
                                        blur = 40.dp,
                                        color = Color(0xFF495CE8),
                                        cornersRadius = 0.dp,
                                        offsetX = corR.dp,
                                        offsetY = corR.dp
                                    )
                                )
                            }
                            if(model.isCameraMove.value && isInsideP2PRoad.value){
                                Button(
                                    onClick ={},
                                    contentPadding = PaddingValues(0.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = if (isInsideP2PZone.value){
                                            if(singleReport.value.reportSpeedLimit == null){
                                                Color(0xFFEA4E34)
                                            }else{
                                                if(singleReport.value.reportSpeedLimit!! < MyLocationService.LSS.inP2PAverageSpeed.value){
                                                    Color(0xFFEA4E34)
                                                } else{
                                                    Color(0xFF57D654)
                                                }
                                            }
                                        }else{
                                            Color(0xFFFF9800)
                                        }
                                    ),
                                    border = BorderStroke(1.5.dp,Color.White),
                                    modifier = Modifier
                                        .width(40.dp)
                                        .height(25.dp)
                                        .offset(
                                            (conf.screenWidthDp / 2).dp,
                                            (conf.screenHeightDp / 2).dp
                                        )
                                    , shape = RoundedCornerShape(20.dp)
                                ){
                                    Text(text = "${MyLocationService.LSS.inP2PAverageSpeed.value}", color = Color.White, fontSize = 10.sp)
                                }
                            }
                            if(model.reportClicked.value){
                                LaunchedEffect(Unit){

                                    coroutineScope.launch {
                                        bottomSheetState.expand()
                                    }
                                }
                                model.reportClicked.value = false
                            }
                            Box(modifier = Modifier
                                .matchParentSize()
                                .padding(horizontal = 18.dp), contentAlignment = Alignment.BottomStart) {
                                AnimatedVisibility(
                                    visible = true,
                                    enter = slideInHorizontally(),
                                    exit = slideOutHorizontally()
                                ) {
                                    Box(modifier = Modifier
                                        .wrapContentSize()
                                        .background(color = Color.Transparent), contentAlignment = Alignment.Center) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Button(
                                                contentPadding = PaddingValues(0.dp),
                                                onClick = {
                                                    model.vibrate(context = context)
                                                    if(!model.isTripStarted.value){
                                                        showTripDialog.value = true
                                                    }else{
                                                        model.isTripStarted.value = false
                                                        model.trip.value.endTime = Date()
                                                        model.trip.value.distance = TripDistance.value
                                                        model.trip.value.startTime = TripStartTime
                                                        model.trip.value.maxSpeed =TripMaxSpeed.value
                                                        model.trip.value.speedAverage =TripAverageSpeed.value
                                                        model.trip.value.listOfLatLon.addAll(lrouteCoordinates).let { ite ->
                                                            if (ite){
                                                                trips?.add(model.trip.value)
                                                                    ?.let { itt->
                                                                        if (itt){
                                                                            model.saveTrip(trips)
                                                                            resetTrip()
                                                                            lrouteCoordinates.clear()
                                                                        }
                                                                    }
                                                            }
                                                        }
                                                    }
                                                },

                                                colors = ButtonDefaults.buttonColors(backgroundColor =  Color.White),
                                                modifier = Modifier
                                                    .size(50.dp)
                                                    .advancedShadow(
                                                        color = Color.Black,
                                                        alpha = 0.06f,
                                                        cornersRadius = 18.dp,
                                                        shadowBlurRadius = 8.dp,
                                                        offsetX = 0.dp,
                                                        offsetY = 5.dp
                                                    ),
                                                elevation =  ButtonDefaults.elevation(
                                                    defaultElevation = 0.dp,
                                                    pressedElevation = 0.dp,
                                                    disabledElevation = 0.dp,
                                                    hoveredElevation = 0.dp,
                                                    focusedElevation = 0.dp
                                                ),
                                                shape = RoundedCornerShape(20.dp)
                                            ) {
                                                if(model.isTripStarted.value){
                                                    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                                                        Icon(
                                                            painter = painterResource(id =R.drawable.ic_pause),
                                                            modifier = Modifier.size(22.dp),
                                                            tint = Color.Unspecified,
                                                            contentDescription = ""
                                                        )
                                                    }
                                                }else{
                                                    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                                                        Icon(
                                                            painter = painterResource(id =R.drawable.ic_play),
                                                            modifier = Modifier.size(22.dp),
                                                            tint = Color.Unspecified,
                                                            contentDescription = ""
                                                        )
                                                    }
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            if(model.isTripStarted.value){
                                                Text(modifier = Modifier
                                                    .width(50.dp)
                                                    .advancedShadow(
                                                        color = Color(0xFF495CE8),
                                                        alpha = 0.06f,
                                                        cornersRadius = 12.dp,
                                                        shadowBlurRadius = 8.dp,
                                                        offsetX = 0.dp,
                                                        offsetY = 2.dp
                                                    ),text = stringResource(id = R.string.lbl_home_end_trip), textAlign = TextAlign.Center , color = Color(0xFF171729), fontSize = 7.sp, letterSpacing = 0.sp, fontWeight = FontWeight.W600)
                                            }else{
                                                Text(modifier = Modifier
                                                    .width(50.dp)
                                                    .advancedShadow(
                                                        color = Color.Black,
                                                        alpha = 0.06f,
                                                        cornersRadius = 12.dp,
                                                        shadowBlurRadius = 8.dp,
                                                        offsetX = 0.dp,
                                                        offsetY = 2.dp
                                                    ),text =stringResource(id = R.string.lbl_home_start_trip),textAlign = TextAlign.Center, color = Color(0xFF171729), fontSize = 7.sp, letterSpacing = 0.sp, fontWeight = FontWeight.W600)
                                            }
                                            Spacer(modifier = Modifier.height((navigationBarHeight.value + 105).dp))
                                        }
                                    }

                                }
                            }
                            Box(modifier = Modifier
                                .matchParentSize()
                                .padding(horizontal = 15.dp, vertical = 10.dp), contentAlignment = Alignment.BottomStart) {
                                AnimatedVisibility(
                                    visible = !model.inSideReportToast.value && model.slider.value,
                                    enter = slideInHorizontally(),
                                    exit = slideOutHorizontally()
                                ) {
                                    FeedbackToast(reportType = singleReport.value.reportType,
                                        onLike = {
                                            model.isLiked.value = true
                                            model.addReportFeedback(reportId,true)
                                            coroutineScope.launch {
                                                delay(2000)
                                                model.slider.value = false
                                            }
                                        },
                                        onUnlike = { model.isLiked.value = false
                                            model.addReportFeedback(reportId,false)
                                            coroutineScope.launch {
                                                delay(2000)
                                                model.slider.value = false
                                            }
                                        },
                                        onClose = {
                                            model.slider.value = false
                                        },
                                        isLiked = model.isLiked,
                                        onDrag = { _, offset ->
                                            if (offset.x < -30) {
                                                model.inSideReportToast.value = false
                                            }
                                        }
                                    )
                                }
                            }
                            Box(modifier = Modifier
                                .matchParentSize()
                                .padding(horizontal = 15.dp, vertical = 10.dp), contentAlignment = Alignment.BottomStart) {
                                AnimatedVisibility(
                                    visible = model.inSideReportToast.value && !model.slider.value,
                                    enter = slideInHorizontally(),
                                    exit = slideOutHorizontally()
                                ) {
                                    InsideReportToast(singleReport.value.reportType,singleReport.value.reportSpeedLimit) {
                                        model.inSideReportToast.value = false
                                    }
                                }
                            }
                            Column() {
                                if(!isPurchasedAdRemove.value){
                                    Box(modifier = Modifier
                                        .fillMaxWidth()
                                        .background(color = Color.Transparent), contentAlignment = Alignment.Center) {
                                        Box(modifier = Modifier
                                            .background(
                                                color = Color.Transparent,
                                                shape = RoundedCornerShape(
                                                    bottomEnd = 18.dp,
                                                    bottomStart = 18.dp
                                                )
                                            )
                                            .fillMaxWidth()
                                            .padding(
                                                top = 35.dp,
                                                start = 8.dp,
                                                bottom = 0.dp,
                                                end = 8.dp
                                            ), contentAlignment = Alignment.Center) {
                                            AndroidView(factory ={
                                                AdView(it).apply {
                                                    this.setAdSize(AdSize.BANNER)
                                                    adUnitId = resources.getString(R.string.main_screen_ad_banner_id)
                                                    loadAd(AdRequest.Builder().build())
                                                }
                                            })
                                        }

                                    }
                                }
                                Box(modifier = Modifier.padding(top = if(isPurchasedAdRemove.value){25.dp}else{0.dp})) {
                                    topBar(onClickMenu = {
                                        isShowMenu.value = true
                                    },temp = model.temperature.value,onClickAds = {
                                        coroutineScope.launch {
                                            model.whichButtonClicked.value = 5
                                            delay(5)
                                            bottomSheetState.expand()
                                        }
                                    }, isAdLoaded = mRewardedAd != null,isWatchedRewardVideo = {isPurchasedAdRemove.value},timeRemain = timeRemainInt,model.onlineUserCounter)
                                }
                            }
                            if (model.showCustomDialogWithResult.value) {
                                showConfirmationDialog(
                                    onDismiss = { model.showCustomDialogWithResult.value = false },
                                    onNegativeClick = { model.showCustomDialogWithResult.value = false },
                                    onPositiveClick = {
                                        addReport(0)
                                        model.getReportsAndAddGeofences()
                                        model.showCustomDialogWithResult.value = false
                                    },
                                    model
                                )
                            }
                            if (showErrorPlaceReport.value) {
                                DynamicModal(
                                    title = stringResource(id =R.string.lbl_error_report_place_dialog_title),
                                    description = stringResource(id =R.string.lbl_error_report_place_dialog_description),
                                    icon = R.drawable.ic_cee_two,
                                    positiveButtonAction = {
                                        showErrorPlaceReport.value = false
                                    },
                                    negativeButtonAction = {},
                                    positiveButtonText = stringResource(id =R.string.btn_home_alert_done),
                                    positiveButtonModifier =Modifier.fillMaxWidth(0.49f) ,
                                )
                            }
                            if (isReachedQuotaDialog.value) {
                                DynamicModal(
                                    title = stringResource(id =R.string.lbl_reach_permitted_quota_report_title),
                                    description = stringResource(id =R.string.lbl_reach_permitted_quota_report_description),
                                    icon = R.drawable.ic_cee_two,
                                    positiveButtonAction = {
                                        isReachedQuotaDialog.value = false
                                    },
                                    negativeButtonAction = {},
                                    positiveButtonText = stringResource(id =R.string.btn_auth_alert_ok),
                                    positiveButtonModifier =Modifier.fillMaxWidth(0.49f) ,
                                )
                            }
                            if (deleteReportDialog.value) {
                                DynamicModal(
                                    title = "Delete report",
                                    description = "Are you sure to delete this report",
                                    icon = R.drawable.ic_cee_two,
                                    positiveButtonAction = {
                                        deleteReportDialog.value = false
                                        if(model.userInfo.value.userType == 2){
                                            model.isDeleteReportRequested.value = true
                                            mAuth.currentUser?.uid.let {
                                                val req = Request
                                                    .Builder()
                                                    .url("https://us-central1-cee-platform-87d21.cloudfunctions.net/onDeleteReportByAdmin?reportId=${model.reportId.value}&adminId=${it}")
                                                    .post("{}".toRequestBody("application/json".toMediaType()))
                                                    .build()

                                                val client = OkHttpClient()
                                                client
                                                    .newCall(req)
                                                    .enqueue(object : Callback {
                                                        override fun onFailure(call: Call, e: IOException) {
                                                            model.isDeleteReportRequested.value = false
                                                            Toast.makeText(this@HomeActivity,e.message.toString(),Toast.LENGTH_LONG).show()
                                                            Log.d(
                                                                "DEBUG_HTTP_REQUEST_DELETE_REPORT",
                                                                e.message.toString()
                                                            )
                                                        }

                                                        override fun onResponse(
                                                            call: Call,
                                                            response: Response
                                                        ) {
                                                            model.isDeleteReportRequested.value = false
                                                            coroutineScope.launch {
//                                                            modalBottomSheetState.hide()
                                                                bottomSheetState.collapse()
                                                            }
                                                            Log.d(
                                                                "DEBUG_HTTP_REQUEST_DELETE_REPORT",
                                                                response.code.toString()
                                                            )
                                                        }
                                                    })
                                            }
                                        }else{
                                            model.deleteReport(model.reportId.value)
                                        }
                                    },
                                    negativeButtonAction = {
                                        deleteReportDialog.value = false
                                    },
                                    positiveButtonText = stringResource(id =R.string.btn_trip_history_detail_alert_delete),
                                    negativeButtonText = stringResource(id = R.string.btn_setting_appBar_cancel),
                                    positiveButtonTextColor = Color(0xFFEA4E34),
                                    negativeButtonTextColor = Color.Black,
                                    positiveButtonModifier = Modifier
                                        .fillMaxWidth(0.49f)
                                        .border(
                                            width = 1.dp,
                                            color = Color(0xFFEA4E34),
                                            shape = RoundedCornerShape(8.dp)
                                        ) ,
                                    negativeButtonModifier = Modifier
                                        .fillMaxWidth()
                                        .border(
                                            width = 1.dp,
                                            color = Color.Black,
                                            shape = RoundedCornerShape(8.dp)
                                        ),
                                    positiveButtonBgColor = Color.White,
                                    negativeButtonBgColor = Color.White,

                                    )
                            }
                            if(showTripDialog.value){
                                startTripDialog(
                                    onClickStart = {
                                        model.isTripStarted.value = true
                                        resetTrip()
                                        lrouteCoordinates.clear()
                                        showTripDialog.value = false
                                    },
                                    onClickContinue = {
//                                            routeCoordinates.addAll(LocationService.LSS.TripRoutePoints)
                                        model.isTripStarted.value = true
                                        showTripDialog.value = false
//                                            tempRouteCoordinates.clear()
                                    }, onDismiss = {
                                        showTripDialog.value = false
                                    })
                            }
                            if (showRegisterDialog.value) {
                                showRegisterDialog(
                                    onDismiss = { showRegisterDialog.value = true },
                                    onPositiveClick = {
                                        if (it.length >= 2){
                                            db.collection(DB_REF_USER).document(mAuth.currentUser!!.uid).update("username",it).addOnSuccessListener {
                                                showRegisterDialog.value = false
                                            }
                                        }
                                    },
                                )
                            }
                            if (showEditReportDialog.value) {
                                showEditReportDialog(
                                    onDismiss = { showEditReportDialog.value = false },
                                    speedLimit = speedLimit,
                                    onPositiveClick = {
                                        it?.let {updatedSpeedLimit ->
                                            db.collection(DB_REF_REPORT).document(reportIdEditing).update("reportSpeedLimit",updatedSpeedLimit).addOnSuccessListener {
                                                showEditReportDialog.value = false
                                                Toast.makeText(context,"Speed limit updated successfully to $updatedSpeedLimit.",Toast.LENGTH_SHORT).show()
                                            }
                                        }

                                    },
                                )
                            }
                            if (showAddReportManuallyDialog.value) {
                                showAddReportManuallyDialog(
                                    onDismiss = { showAddReportManuallyDialog.value = false },
                                    onPositiveClick = {point,type,time,limit, address ->
                                        model.addReport(geoPoint = point, reportType = type,time = time,speedLimit = limit,address = address)
                                        showAddReportManuallyDialog.value = false
                                    }, clickedPoint = pointClickedOnMap.value
                                )
                            }
                            LaunchedEffect(Unit){
                                delay(7000)
                                model.createMarkerOnMap(model.allReports)
                            }
                            LaunchedEffect(Unit){
                                if(isFirstLunch){
                                    Log.d("MAP_ANIMATION_DEBUG","Hi you")
                                    model.mapView.camera.apply {
                                        val zoom = createZoomAnimator(
                                            cameraAnimatorOptions(14.5) {
                                                startValue(7.0)
                                            }
                                        ) {
                                            startDelay = 1500
                                            duration = 2000
                                            interpolator = AccelerateDecelerateInterpolator()
                                        }
                                        playAnimatorsSequentially(zoom)
                                    }
                                    isFirstLunch = false
                                }else{
//                                        model.initLocationComponent()
//                                        model.setupGesturesListener()

                                    val cameraPosition = CameraOptions.Builder()
                                        .zoom(14.5)
                                        .center(Point.fromLngLat(model.lastLocation.value.latitude,model.lastLocation.value.latitude))
                                        .build()
                                    model.mapView.getMapboxMap().setCamera(cameraPosition)
                                    model.isCameraMove.value
                                }

                            }

                        }
                    }
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

                    Log.d("CurrentLocalDebug",resources.configuration.locales.get(0).language)

                }


            }
            AnimatedVisibility(
                modifier = Modifier.matchParentSize(),
                visible = isShowMenu.value,
                enter = slideInHorizontally(),
                exit = slideOutHorizontally()
            ) {
                Box(modifier = Modifier
                    .wrapContentSize()
                    .background(color = Color.Transparent), contentAlignment = Alignment.Center) {
                    AppMenu(model = model, onCloseDrawer = {
                        isShowMenu.value = false
                    })

                }
            }
            AnimatedVisibility(
                modifier = Modifier.fillMaxSize(),
                visible = model.isLocationNotAvailable.value,
                enter =  fadeIn(),
                exit = fadeOut()
            ) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.White), contentAlignment = Alignment.Center) {
                    LocationNotAvailable(model)
                }
            }
        }
    }

    private fun continueTrip() {
        model.isTripStarted.value = true
        showTripDialog.value = false
    }

    private fun tripReset() {
        resetTimer()
        model.isTripStarted.value = false
        resetTrip()
    }

    private fun pauseTrip() {
        if (model.isTimerRunning.value){
            pauseTimer()
        }else{
            startTimer()
        }
        MyLocationService.LSS.isTripPused.value = !MyLocationService.LSS.isTripPused.value
    }

    private fun startTrip() {
        if (!model.isTimerRunning.value){
            startTimer()
        }else{
            resetTimer()
            startTimer()
        }
        model.isTripStarted.value = true
        resetTrip()
        lrouteCoordinates.clear()
        showTripDialog.value = false
    }

    private fun saveTrip() {
        model.isTripStarted.value = false
        model.trip.value.endTime = Date()
        model.trip.value.distance = TripDistance.value
        model.trip.value.maxSpeed =TripMaxSpeed.value
        model.trip.value.startTime = TripStartTime
        model.trip.value.speedAverage = TripAverageSpeed.value
//                                    model.trip.value.listOfLatLon.addAll(lrouteCoordinates).let { ite ->
//                                        if (ite){
//                                            trips.value?.add(model.trip.value)?.let {itt->
//                                                if (itt){
//
//                                                    model.saveTrip(trips.value!!)
//                                                    lrouteCoordinates.clear()
//                                                    resetTrip()
//                                                }
//                                            }
//                                        }
//                                    }
        resetTimer()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun addReport(speedLimit:Int?) {
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
        fusedLocationProviderClient.lastLocation.addOnCompleteListener {
            if (it.isComplete) {
                if (it.result != null) {
                    if (!Utils.isMockLocationEnabled(it.result)){
                        if(model.placeReportValidation(reportCount.value,timeLastReport.value)){
                            model.addReport(geoPoint = GeoPoint(it.result.latitude,it.result.longitude), reportType = reportType.value, speedLimit = speedLimit)
                        }else{
                         isReachedQuotaDialog.value = true
                        }
                    }else{
                        Toast.makeText(this,"Sorry unable, Please turn off mock location.",Toast.LENGTH_LONG).show()
                    }
                }else{
                    Toast.makeText(this,"Sorry Unable",Toast.LENGTH_LONG).show()
                }
            }
        }
    }

//    @RequiresApi(Build.VERSION_CODES.S)
//    private fun getReportsAndAddGeofences()  {
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            return
//        }
//        fusedLocationProviderClient.lastLocation.addOnCompleteListener{
//            if (it.isComplete){
//                if (it.result == null) {
//                    model.isLocationNotAvailable.value = true
//                }else{
//                    if (isOnline(this)){
//                        fetchReports(it.result.latitude,it.result.longitude)
//                    }
//                }
//            }
//        }
//        Log.d("TES-gRAAG","set false")
//    }
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onResume() {
        super.onResume()
        inProgressUpdate()
        startLocationUpdate()
        registerReceiver(transitionBroadcastReceiver, IntentFilter(TRANSITIONS_RECEIVER_ACTION))
    }
    override fun onStart() {
        super.onStart()
        model.appLaunchTime.value = Date()
        navigationBarHeight.value =  pxToDp(getNavigatingBarHeight(this))
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
//        model.mapView.onStop()
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
//    override fun onPause() {
//
//        super.onPause()
//        model.mapView.onPause()
//    }
//    override fun onLowMemory() {
//        super.onLowMemory()
//        model.mapView.onLowMemory()
//
//    }
    override fun onDestroy() {
        super.onDestroy()
//        Intent(applicationContext, MyLocationService::class.java).apply {
//            action = MyLocationService.ACTION_STOP
//            startService(this)
//        }
//        model.mapView.onDestroy()
        snap1?.remove()
        snap2?.remove()
        snap3?.remove()
        snap4?.remove()
        //timer.cancel()

        Intent(applicationContext, MyLocationService::class.java).apply {
            action = MyLocationService.ACTION_STOP
            stopService(this)
        }

        unregisterReceiver(transitionBroadcastReceiver)

        geofencingClient.removeGeofences(geofencePendingIntent)
        //removeActivityTransitionUpdates()
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
//    @Deprecated("Deprecated in Java")
//    override fun onBackPressed() {
//        isShowMenu.value = false
//        isShowTrip.value = false
//        Log.d("BCK","Clicked")
//    }
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
//        if(requestCode == 1){
//            this.recreate()
//        }
    }
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Toast.makeText(this,"Please Set Background Permission.",Toast.LENGTH_LONG).show()
    }
    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = 4000
            fastestInterval = 2500
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            maxWaitTime = 1000
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







    var snap1 : ListenerRegistration? = null
    var snap2 : ListenerRegistration? = null
    var snap3 : ListenerRegistration? = null
    var snap4 : ListenerRegistration? = null
    private fun fetchReports(latt: Double, lonn: Double) {

        try {
            val scope = CoroutineScope(Dispatchers.Default)
            val radius = when (model.userType.value) {
                2 -> {50}
                1 -> {25}
                else -> {remoteConfig!!.getLong("reportSightRadius")}
            }

            val center =  GeoLocation(latt, lonn)
            val radiusInM = radius * 1000.0
            val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInM)
            val tasks = arrayListOf<Task<QuerySnapshot>>()
            geofencingClient.removeGeofences(geofencePendingIntent)
            Log.d("BOUNDSSIZE",bounds.size.toString())

            for ((i, b) in bounds.withIndex()) {
                val q = db.collection("Reports")
                    .orderBy("g")
                    .startAt(b.startHash)
                    .endAt(b.endHash)
                tasks.add(q.get());
                when (i){
                    0->{
                        snap1?.remove()
                        snap1 = q.addSnapshotListener{ _, _ ->
                            tempReports1.clear()
                            tempGeofence1.clear()
                            scope.launch {
                                dataToList(q.get(),i,latt,lonn,bounds.size)
                            }
                        }
                    }
                    1->{
                        snap2?.remove()
                        snap2 = q.addSnapshotListener{ _, _ ->
                            tempReports2.clear()
                            tempGeofence2.clear()
                            scope.launch {
                                dataToList(q.get(),i,latt,lonn,bounds.size)
                            }
                        }
                    }
                    2->{
                        snap3?.remove()
                        snap3 = q.addSnapshotListener{ _, _ ->
                            tempReports3.clear()
                            tempGeofence3.clear()
                            scope.launch {
                                dataToList(q.get(),i,latt,lonn,bounds.size)
                            }
                        }
                    }
                    3->{
                        snap4?.remove()
                        snap4 = q.addSnapshotListener{ _, _ ->
                            tempReports4.clear()
                            tempGeofence4.clear()
                            scope.launch {
                                dataToList(q.get(),i,latt,lonn,bounds.size)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("LISTSIZE_HSpeedH",e.message.toString())
        }
    }
    var counter = 0
    private suspend fun dataToList(task : Task<QuerySnapshot>,i:Int, latitude: Double, longitude: Double,size:Int){
try {
    val radius = when (model.userType.value) {
        2 -> {50}
        1 -> {25}
        else -> {remoteConfig!!.getLong("reportSightRadius")}
    }
    val center =  GeoLocation(latitude, longitude)
    val radiusInM = radius * 1000.0
    task.await().documents.forEach { document->
        val geoLocation = document.get("geoLocation") as? List<*>?
        Log.d("ERROR-11232",document.id)
        if (geoLocation != null) {
            val lat = geoLocation[0] as Double
            val lng = geoLocation[1] as Double
            val docLocation = GeoLocation(lat, lng)
            val distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center)
            if (distanceInM <= radiusInM) {
                val report = document.toObject(NewReport::class.java)
                Log.d("ERROR-11232",document.get("reportId") as String)
                report?.let { repo ->
                    when(i){
                        0->{
                            tempReports1.add(repo)
                            model.createMarkerOnMap(tempReports3)
                            if (report.reportType != 405 && report.reportType != 7) {
                                tempGeofence1.add(
                                    Geofence.Builder()
                                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                        .setRequestId("${report.reportId},${0}")
                                        .setCircularRegion(
                                            report.geoLocation?.get(0) as Double,
                                            report.geoLocation?.get(1) as Double,
                                            model.geofenceRadius.value?.toFloat() ?: 250f
                                        )

                                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                                        .build()
                                )
                            }
                            //break
                        }
                        1->{
                            tempReports2.add(repo)
                            model.createMarkerOnMap(tempReports3)
                            if (report.reportType != 405 && report.reportType != 7) {
                                tempGeofence2.add(
                                    Geofence.Builder()
                                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                        .setRequestId("${report.reportId},${0}")
                                        .setCircularRegion(
                                            report.geoLocation?.get(0) as Double,
                                            report.geoLocation?.get(1) as Double,
                                            model.geofenceRadius.value?.toFloat() ?: 250f
                                        )
                                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                                        .build()
                                )
                            }
                        }
                        2->{
                            tempReports3.add(repo)
                            model.createMarkerOnMap(tempReports3)
                            if (report.reportType != 405 && report.reportType != 7) {
                                tempGeofence3.add(
                                    Geofence.Builder()
                                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                        .setRequestId("${report.reportId},${0}")
                                        .setCircularRegion(
                                            report.geoLocation?.get(0) as Double,
                                            report.geoLocation?.get(1) as Double,
                                            model.geofenceRadius.value?.toFloat() ?: 250f
                                        )
                                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                                        .build()
                                )
                            }
                        }
                        3->{
                            tempReports4.add(repo)
                            model.createMarkerOnMap(tempReports3)
                            if (report.reportType != 405 && report.reportType != 7) {
                                tempGeofence4.add(
                                    Geofence.Builder()
                                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                        .setRequestId("${report.reportId},${0}")
                                        .setCircularRegion(
                                            report.geoLocation?.get(0) as Double,
                                            report.geoLocation?.get(1) as Double,
                                            model.geofenceRadius.value?.toFloat() ?: 250f
                                        )
                                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                                        .build()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    Log.d("TESTGEOQURY_IMPO", "B: $size  |  $counter")
    if (size >= counter){
        model.allReports.clear()
        addGeofences(concatenate(tempGeofence1, tempGeofence2, tempGeofence3,tempGeofence4))
        model.allReports.addAll(concatenate(tempReports1, tempReports2, tempReports3,tempReports4))
//        model.createMarkerOnMap(concatenate(tempReports1, tempReports2, tempReports3,tempReports4))
//        Log.d("TESTGEOQURY_IMPO","B: "+concatenate(tempReports1, tempReports2, tempReports3,tempReports4).size.toString())
        counter = 0
    }else{
        counter++
    }

}catch (e:Exception){
    Log.d("ERROR-11232",e.message.toString())
}
    }
    private fun addGeofences(geo: List<Geofence>) {
        if(geo.isNotEmpty()){
            Log.d("GEOBR","${geo.size} added success")
            geofencingClient.removeGeofences(geofencePendingIntent)
            GeofenceBroadcastReceiver.GBRS.add(null)
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            geofencingClient.addGeofences(getGeofencingRequest(geo), geofencePendingIntent)
                .run {
                    addOnSuccessListener {
                        Log.d("GEOBR","Geo added success")
                    }
                    addOnFailureListener {
                        Log.d("GEOBR","Geo added filed")
                    }
                }
        }
    }
    private fun getGeofencingRequest(reports: List<Geofence> = ArrayList()): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(reports)
        }.build()
    }



    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT  )
        } else {
            PendingIntent.getBroadcast(this, 0, intent,  PendingIntent.FLAG_UPDATE_CURRENT  )
        }
    }

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

}

fun <T> concatenate(vararg lists: List<T>): List<T> {
    return listOf(*lists).flatten()
}