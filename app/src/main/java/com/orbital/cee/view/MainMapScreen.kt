package com.orbital.cee.view

import android.annotation.SuppressLint
import android.app.Activity
import android.location.Location
import android.os.Build
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraState
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.sources.getSourceAs
import com.mapbox.maps.plugin.animation.CameraAnimatorOptions
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.AnnotationConfig
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.attribution.attribution
import com.mapbox.maps.plugin.compass.compass
import com.mapbox.maps.plugin.gestures.addOnMapLongClickListener
import com.mapbox.maps.plugin.scalebar.scalebar
import com.orbital.cee.R
import com.orbital.cee.core.GeofenceBroadcastReceiver
import com.orbital.cee.core.MyLocationService
import com.orbital.cee.core.MyLocationService.LSS.isChangeDetected
import com.orbital.cee.core.MyLocationService.LSS.lrouteCoordinates
import com.orbital.cee.core.Permissions
import com.orbital.cee.model.SingleCustomReport
import com.orbital.cee.model.Trip
import com.orbital.cee.utils.MetricsUtils
import com.orbital.cee.utils.Utils
import com.orbital.cee.view.LocationNotAvailable.LocationNotAvailable
import com.orbital.cee.view.home.HomeActivity
import com.orbital.cee.view.home.HomeViewModel
import com.orbital.cee.view.home.SaveUserInfoInAlerted
import com.orbital.cee.view.home.UserInformation
import com.orbital.cee.view.home.components.AddReportManuallyModal
import com.orbital.cee.view.home.components.AppMenu
import com.orbital.cee.view.home.components.DynamicModal
import com.orbital.cee.view.home.components.FeedbackToast
import com.orbital.cee.view.home.components.InsideReportToast
import com.orbital.cee.view.home.components.NewReportViewDetail
import com.orbital.cee.view.home.components.RemoveAds
import com.orbital.cee.view.home.components.SoundBottomModal
import com.orbital.cee.view.home.components.TabLayout
import com.orbital.cee.view.home.components.UpdateCeeMap
import com.orbital.cee.view.home.components.bottomBar
import com.orbital.cee.view.home.components.fab
import com.orbital.cee.view.home.components.innerShadow
import com.orbital.cee.view.home.components.showAddReportManuallyDialog
import com.orbital.cee.view.home.components.showConfirmationDialog
import com.orbital.cee.view.home.components.showEditReportDialog
import com.orbital.cee.view.home.components.showRegisterDialog
import com.orbital.cee.view.home.components.speedLimit
import com.orbital.cee.view.home.components.startTripDialog
import com.orbital.cee.view.home.components.topBar
import com.orbital.cee.view.trip.Speed
import com.orbital.cee.view.trip.advancedShadow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

@Suppress("DeferredResultUnused")
@SuppressLint("MissingPermission")
@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterialApi::class, ExperimentalPagerApi::class)
@Composable
fun MainMapScreen(model : HomeViewModel, trips :ArrayList<Trip?>?) {

    val layerIDD = "map_annotation"
    val layerPinIDD = "map_pin"
    var reportId = ""
    val showRegisterDialog = remember { mutableStateOf(false) }
    val isShowMenu = remember { mutableStateOf(false) }
    val showEditReportDialog = remember { mutableStateOf(false) }
    val showAddReportManuallyDialog = remember { mutableStateOf(false) }

    var speedLimit :Int? = 0
     var reportIdEditing = ""

     val reportType = remember { mutableIntStateOf(1) }
     val alertCount = remember { mutableIntStateOf(0) }
    val reportCount = remember { mutableIntStateOf(0) }
     val timeLastReport = remember { mutableLongStateOf(0L) }
     val lLocation = Location("")

    var mRewardedAd : RewardedAd ? = null
    var mInterstitialAd: InterstitialAd? = null

    val isPurchasedAdRemove = remember { mutableStateOf(false) }
    val deleteReportDialog = remember { mutableStateOf(false) }
    val isInsideP2PZone = remember { mutableStateOf(false) }
    val isInsideP2PRoad = remember { mutableStateOf(false) }
    val bottomSheetContentId = remember { mutableIntStateOf(0) }

    val username = remember { mutableStateOf("") }
    val isNotificationResponse = remember { mutableStateOf(false) }
    val responseMessage = remember { mutableStateOf("") }
    val userAvatar =remember { mutableStateOf("") }
    val timeRemainInt = remember { mutableFloatStateOf(0f) }
    val pointClickedOnMap = remember {mutableStateOf(GeoPoint(0.00,0.00))}
    var firstPointId : String? = null
    val showErrorModal = remember { mutableStateOf(false) }
    val errorModalTitle = remember { mutableStateOf("") }
    val errorModalDescription = remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val conf = LocalConfiguration.current
    val bottomSheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed, confirmStateChange = {
//        Log.d("DEBUG_MODAL_BOTTOM_SHEET", "col: $it")
        if(it == BottomSheetValue.Collapsed){
            bottomSheetContentId.intValue = 0
        }
        true
    })
    val traveledDistance = model.readDistance.observeAsState()
    Log.d("DEBUG_MODAL_BOTTOM_SHEET","prog: "+model.navigationBarHeight.value)
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = bottomSheetState)
    val alarm = remember { mutableStateOf(false) }
    val infiniteTransition = rememberInfiniteTransition()
    val configuration = LocalConfiguration.current



    model.loadTimeOfLastReport.observeAsState().value?.let {timeLastReport.longValue = it }
    model.reportCountPerOneHour.observeAsState().value?.let {reportCount.intValue = it }
    val cameraState = remember {mutableStateOf<CameraState?>(null)}
    val lastWatchedAd = model.lsatAdsWatched.observeAsState()
    val userT = model.userType.observeAsState()
    val singleReport = remember { mutableStateOf(SingleCustomReport(isSuccess = false)) }
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
        confirmValueChange = {
            if(it == ModalBottomSheetValue.Hidden && (model.whichButtonClicked.value == 4 || model.whichButtonClicked.value == 7)){
//                Log.d("DEBUG_APP_SETTING_DATA_STORE","hey")
//                pointAnnotationOptions?.let {pointAnnotation->
//                    Log.d("DEBUG_APP_SETTING_DATA_STORE","hey1")
//                    pointAnnotation.iconSize = 0.75
//                    model.pointAnnotationManager!!.update(pointAnnotation)
//                }
                cameraState.value?.let {
                    model.mapView.getMapboxMap().flyTo(cameraOptions {
                        center(it.center)
                        zoom(it.zoom)
                        pitch(it.pitch)
                        padding(EdgeInsets(0.0,0.0,0.0,0.0))
                    },MapAnimationOptions.mapAnimationOptions { duration(500)}
                    )
                }
            }
            true
        }
    )
    var mapView by remember { mutableStateOf<MapView?>(null) }
    mapView = MapView(context, MapInitOptions(context, antialiasingSampleCount = 4))
    DisposableEffect(Unit) {
        val newMapView = MapView(context).apply {
            model.mapView = this

            getMapboxMap().loadStyleUri(styleUri = if(model.isDarkMode.value) "mapbox://styles/orbital-cee/clevkf2lm00l301msgt5l621u" else "mapbox://styles/orbital-cee/clh35qsv500lp01qy0rq23sbt") { sty ->
                //zoomCamera(model.lastLocation.value.longitude,model.lastLocation.value.latitude)

                if (!Permissions.hasLocationPermission(context)) {
                    model.isLocationNotAvailable.value = true
                    Permissions.requestsLocationPermission(context as Activity)
                } else {
                    if (model.checkDeviceLocationSettings(context)) {

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
                        model.annotationApi?.let {
                            model.pointAnnotationManager = it.createPointAnnotationManager(model.annotationConfig)
                            model.pointAnnotationManager!!.addClickListener(OnPointAnnotationClickListener { annotation : PointAnnotation ->
//                                pointAnnotationOptions  = annotation
//                                pointAnnotationOptions?.let{pointAnnotation->
//                                    pointAnnotation.iconSize = 1.5
//                                    model.pointAnnotationManager!!.update(pointAnnotation)
//                                }

                                cameraState.value = this.getMapboxMap().cameraState
                                model.reportClicked.value = true
                                val reportArray = annotation.getData()?.asJsonObject
                                val repo = reportArray?.get("report")
                                val lat = reportArray?.get("lat")?.asDouble
                                val lon = reportArray?.get("lon")?.asDouble
                                model.reportId.value = "${repo?.asString}"
                                model.whichButtonClicked.value = 4
                                if (lat != null && lon != null){
                                    model.onCameraTrackingDismissed()
                                    this.getMapboxMap().flyTo(cameraOptions {
                                            center(Point.fromLngLat(lon,lat))
                                            zoom(15.5)
                                            pitch(10.0)
                                            padding(EdgeInsets(0.0,0.0,590.0,0.0))
                                        },MapAnimationOptions.mapAnimationOptions { duration(500)}
                                        )
                                }
                                true
                            })
                        }
                        model.pointAnnotationManagerr =
                            model.annotationApii!!.createPointAnnotationManager(
                                model.annotationConfigg
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
                    model.isPointClicked.value = true
                    pointClickedOnMap.value = GeoPoint(point.latitude(),point.longitude())
                    model.createClickedPin(point.latitude(),point.longitude())
                    Toast.makeText(context,"Please wait...", Toast.LENGTH_SHORT).show()
                    model.fetchReports(point.latitude(),point.longitude())
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
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, context.getString(R.string.interstitial_ad_three_stop_id) , adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
            }
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
            }
        })
        RewardedAd.load(context,context.getString(R.string.remove_ads_rewarded_video_id) ,adRequest,object : RewardedAdLoadCallback(){
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
        if (HomeActivity.Singlt.stopCount.value == 4){
            HomeActivity.Singlt.stopCount.value = 0
            if (!isPurchasedAdRemove.value){
                mInterstitialAd?.show(context as Activity)
            }
        }
    }

    LaunchedEffect(Unit){
        model.getUid()?.let {
            model.setOnlineStatus()
            val userInformation : UserInformation = model.getUserInformation()
            if (userInformation.userName == ""){ showRegisterDialog.value = true }else{ username.value = userInformation.userName!! }
            userAvatar.value = userInformation.userAvatar
        }
    }
    LaunchedEffect(Unit){
        delay(5)
        if (Permissions.hasLocationPermission(context)){
            while (true){
                model.getJsonAsync(lLocation.latitude,lLocation.longitude)
                delay(180000)
            }
        }
    }
    if (GeofenceBroadcastReceiver.GBRS.GeoId.value != null && GeofenceBroadcastReceiver.GBRS.GeoId.value != reportId){
        model.addAlertCount(alertCount.intValue)
        reportId = GeofenceBroadcastReceiver.GBRS.GeoId.value!!
        model.inSideReportToast.value = true
//            model.slider.value = false
        model.inSideReport.value = true
        model.saveAlerts(SaveUserInfoInAlerted(
            userName = username.value,
            userAvatar = userAvatar.value,
            reportId = reportId
        ))

//        model.getUid()?.let { it1 ->
//            val alert : HashMap<String, Any> = HashMap<String, Any>()
//            alert["lastSeen"] = FieldValue.serverTimestamp()
//            alert["speedWhenEntered"] = model.speed.value
//            alert["userAvatar"] = userAvatar.value
//            alert["userId"] = it1
//            alert["username"] = username.value
//            db.collection(Constants.DB_REF_REPORT).document(reportId).collection(Constants.DB_REF_ALERTED).document(it1).set(alert)
//        }

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
                                MyLocationService.LSS.resetP2PCalc()
                                MyLocationService.LSS.EnteredP2PTime = Date()
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

//    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    Box(modifier = Modifier.fillMaxSize()){
        Log.d("DEBUG_DEBUG_MODE","hey")
        ModalBottomSheetLayout(
            sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            sheetContent =
            {
                when (model.whichButtonClicked.value) {
                    1 -> {
                        UpdateCeeMap(onButtonClicked = {RT->
                            if(GeofenceBroadcastReceiver.GBRS.GeoId.value == null){
                                if (MetricsUtils.isOnline(context)){
                                    model.vibrate(context = context)
                                    reportType.intValue = RT
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
                                    Toast.makeText(context,"sorry not internet connection.",
                                        Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                            onCloseClick = {
                                coroutineScope.launch {
                                    modalSheetState.hide()
                                }
                            }, userType = userT.value ?: 0
                        )
                    }
                    2 -> {
                        TabLayout(model.allReports.iterator(),model= model,currentLocation = model.lastLocation.value){
                            model.onCameraTrackingDismissed()
                            model.mapView.getMapboxMap().flyTo(
                                cameraOptions {
                                    center(
                                        Point.fromLngLat(
                                            it.longitude, it.latitude
                                        ))
                                    zoom(14.5)
                                    pitch(10.0)
                                },
                                MapAnimationOptions.mapAnimationOptions {
                                    duration(1000)
                                }
                            )
                            coroutineScope.launch {
                                modalSheetState.hide()
                            }
                        }
                    }
                    3 -> {
                        SoundBottomModal(model){
                            coroutineScope.launch {
                                modalSheetState.hide()
                            }
                        }
                    }
                    4 -> {
                        NewReportViewDetail(vModel = model,model.reportId,
                            onCloseClick = {
                                coroutineScope.launch {
                                    modalSheetState.hide()
//                                    bottomSheetState.collapse()
                                }
                            }, onReportDelete = {
                                deleteReportDialog.value = true

                            },onEditSpeedLimit={repo,slim->
                                speedLimit = slim
                                reportIdEditing = repo
                                showEditReportDialog.value = true
                            },
                            userId = model.getUid()
                        )
                    }
                    5 -> {
                        RemoveAds(isPurchasedAdRemove, onClickClose = {
                            coroutineScope.launch {
                                modalSheetState.hide()
                            }
                        }, onClickWatchVideo = {
                            if(mRewardedAd != null){
                                mRewardedAd!!.show(context as Activity) {
                                    Log.d("TAG_AD_DEB", "User earned the reward.${it.type} , ${it.amount}")
                                    coroutineScope.launch { isPurchasedAdRemove.value = true  }
                                    model.saveWatchAdTime(Timestamp.now())
                                }
                            }else{
                                Toast.makeText(context,"please wait.", Toast.LENGTH_LONG).show()
                            }
                        })
                    }
                    6 -> {
                        speedLimit(onSelectedSpeed={
                            model.addReport(it)
                        },onDismiss={
                            coroutineScope.launch {
                                modalSheetState.hide()
                            }
                        })
                    }
                    7 -> {
                        AddReportManuallyModal(onPositiveClick = { point, type, time, limit, address, isWithNotification ->
                            model.addReport(
                                geoPoint = point,
                                reportType = type,
                                time = time,
                                speedLimit = limit,
                                address = address
                            )
                            if (isWithNotification){
                                val req = Request
                                    .Builder()
                                    .url("https://us-central1-cee-platform-87d21.cloudfunctions.net/sendNotificationForSpaceficLocation?latitude=${point.latitude}&longitude=${point.longitude}&radius=1&type=${type}")
                                    .post("{}".toRequestBody("application/json".toMediaType()))
                                    .build()

                                val client = OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS).build()
                                client.newCall(req).enqueue(object : Callback {
                                    override fun onFailure(call: Call, e: IOException) {
                                        isNotificationResponse.value = true
                                        responseMessage.value = "error the connection"
                                    }
                                    override fun onResponse(call: Call, response: Response) {
                                        response.body?.string()?.let { responseBody->
                                            isNotificationResponse.value = true
                                            responseMessage.value = responseBody
                                        }

                                    }
                                })
                            }
                            coroutineScope.launch {
                                modalSheetState.hide()
                            }
                        }, clickedPoint = pointClickedOnMap.value)
                    }
                }
            },
            sheetState = modalSheetState,
            modifier = Modifier.fillMaxSize(),
            content = {
                BottomSheetScaffold(
                    sheetPeekHeight = (85 + model.navigationBarHeight.value).dp,
                    sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                    sheetContent = {
                        when (bottomSheetContentId.intValue) {
                            1->{
                                Speed(
                                    model = model,
                                    onClickStart = { model.startTrip()},
                                    onClickFinish = { coroutineScope.launch { model.saveTrip() } },
                                    onClickContinue = {model.continueTrip()},
                                    onClickBack = {
                                        coroutineScope.launch {
                                            bottomSheetContentId.intValue = 44
                                            delay(10)
                                            bottomSheetState.collapse()
                                        }
                                    },
                                    onClickResetTrip = { model.tripReset() },
                                    onClickPause = { model.pauseTrip() },
                                    isPurchasedAdRemove = isPurchasedAdRemove
                                )
                            }
                            else ->{
                                bottomBar(
                                    model = model,
                                    onButtonClicked = {btnId ->
                                        model.whichButtonClicked.value = btnId
                                        coroutineScope.launch {
                                            modalSheetState.show()
                                        }
                                    },
                                    onClickSpeed = {
                                        coroutineScope.launch {
                                            bottomSheetContentId.intValue = 1
                                            delay(10)
                                            bottomSheetState.expand()
                                        }
                                    },
                                    speedLimit = singleReport.value.reportSpeedLimit ?: 0,
                                    navigationBarHeight = model.navigationBarHeight.value,bottomSheetState = bottomSheetState,
                                    startTrip = {}, saveTrip = {}, continueTrip = {},
                                    pauseTrip = {}, tripReset = {}, isPurchasedAdRemove = isPurchasedAdRemove
                                )
                            }
                        }
                    },
                    scaffoldState = scaffoldState,
                    backgroundColor = MaterialTheme.colors.background,
                    sheetBackgroundColor = Color.Transparent,
                    content =
                    { pad ->
                        Log.d("DEBUG_PADDING_In", pad.toString())
                        lastWatchedAd.value?.let {
                            if (it > 0) {
                                LaunchedEffect(Unit) {
                                    while (true) {
                                        //timeRemain.value = getRemain(lastWatchedAd.value,Timestamp.now().seconds)
                                        timeRemainInt.floatValue = MetricsUtils.getRemainInt(
                                            lastWatchedAd.value,
                                            Timestamp.now().seconds
                                        )
                                        isPurchasedAdRemove.value =
                                            (Timestamp.now().seconds - it) < 1800
                                        delay(1000)
                                    }
                                }
                            }
                        }
                        Scaffold(floatingActionButton = {
                            fab(model,
                                navigationBarHeight = model.navigationBarHeight.value,
                                onClickReport = {
                                    if (GeofenceBroadcastReceiver.GBRS.GeoId.value == null) {
                                        if (MetricsUtils.isOnline(context)) {
                                            if ((traveledDistance.value ?: 0f) >= 15f || model.userType.value == 2) {
                                                if (Permissions.hasBackgroundLocationPermission(context = context)) {
                                                    model.vibrate(context = context)
                                                    reportType.intValue = 1
                                                    if (model.userType.value == 2) {
                                                        model.whichButtonClicked.value = 6
                                                        coroutineScope.launch {
                                                            modalSheetState.show()
                                                        }
                                                    } else {
                                                        model.showCustomDialogWithResult.value = true
                                                    }
                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        "sorry, This action needs background location permission.",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                            } else {
                                                errorModalTitle.value =
                                                    context.getString(R.string.lbl_error_report_place_dialog_title)
                                                errorModalDescription.value =
                                                    context.getString(R.string.lbl_error_report_place_dialog_description)
                                                showErrorModal.value = true
                                            }
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "please check internet connection.",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                },
                                onClickIndicator = {
                                    try {
                                        model.vibrate(context = context)
                                        model.isCameraMove.value = !model.isCameraMove.value
                                        model.fusedLocationProviderClient.lastLocation.addOnCompleteListener {
                                            if (it.isComplete) {
                                                if (it.result != null) {
                                                    if (model.userInfo.value.userType == 1 || model.userInfo.value.userType == 2) {
                                                        model.fetchReports(
                                                            it.result.latitude,
                                                            it.result.longitude
                                                        )
                                                    }
                                                    model.mapView.getMapboxMap().flyTo(
                                                        cameraOptions {
                                                            center(
                                                                Point.fromLngLat(
                                                                    it.result.longitude,
                                                                    it.result.latitude
                                                                )
                                                            )
                                                            zoom(14.5)
                                                        },
                                                        MapAnimationOptions.mapAnimationOptions {
                                                            duration(2000)
                                                        }
                                                    )
                                                    model.initLocationComponent()
                                                    model.setupGesturesListener()
                                                } else {
                                                    Toast.makeText(
                                                        context, "Location not found.",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                            }
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "${e.message}", Toast.LENGTH_LONG)
                                            .show()
                                    }
                                },
                                onClickReportAddManually = {
                                    model.whichButtonClicked.value = 7
                                    coroutineScope.launch {
                                        modalSheetState.show()
                                    }
                                    cameraState.value = model.mapView.getMapboxMap().cameraState
                                    model.onCameraTrackingDismissed()
                                    model.mapView.getMapboxMap().flyTo(cameraOptions {
                                        center(Point.fromLngLat(pointClickedOnMap.value.longitude,pointClickedOnMap.value.latitude))
                                        zoom(15.5)
                                        pitch(10.0)
                                        padding(EdgeInsets(0.0,0.0,800.0,0.0))
                                    },MapAnimationOptions.mapAnimationOptions { duration(500)}
                                    )
//                                    showAddReportManuallyDialog.value = true
                                }
                            )
                        }) { padd ->
                            Log.d("DEBUG_PADDING_In", padd.toString())
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
//                        .padding(bottom = if((padd.calculateBottomPadding() - 15.dp) >= 0.dp){padd.calculateBottomPadding() - 15.dp} else{0.dp} )
                                    .background(color = MaterialTheme.colors.background)
                            ) {
//                                if (model.allReports.size != 0 && model.pointAnnotationManager?.annotations?.size != model.allReports.size) {
//                                    Log.d(
//                                        "TESTANOSREPOS",
//                                        model.pointAnnotationManager?.annotations?.size.toString()
//                                    )
//                                    Log.d("TESTANOSREPOS", model.allReports.size.toString())
//                                    model.createMarkerOnMap(model.allReports)
//                                } else {
//                                    if (model.allReports.size == 0) {
//                                        LaunchedEffect(Unit) {
//                                            model.getReportsAndAddGeofences()
//                                        }
//                                    }
//                                }
//                                Log.d("TESTANOSREPOS", model.allReports.size.toString())
//                                Log.d(
//                                    "TESTANOSREPOS",
//                                    "Ano: " + model.pointAnnotationManager?.annotations?.size.toString()
//                                )
                                if(model.isChangeInZone1.value){
                                    model.createMarkerOnMap(model.allReports)
                                    model.isChangeInZone1.value = false
                                }
                                Box(modifier = Modifier.fillMaxSize()) {
                                    AndroidView({ mapView!! })
                                    val lineString = LineString.fromLngLats(lrouteCoordinates)
                                    val feature = Feature.fromGeometry(lineString)

                                    if (model.isCameraZoomChanged.value != model.isShowDots.value) {
                                        model.isCameraZoomChanged.value = model.isShowDots.value
                                        Log.d("DEBUG_CAMERA_ZOOM", "Run")
                                        model.createMarkerOnMap(model.allReports)
                                    }

                                    if (isChangeDetected.value && model.isTripStarted.value) {
                                        model.mapView.getMapboxMap().getStyle {
                                            it.getSourceAs<GeoJsonSource>("line")
                                                ?.featureCollection(
                                                    FeatureCollection.fromFeature(feature)
                                                )
                                        }
                                        isChangeDetected.value = false
                                    }
                                    if(model.clearLine.value){
                                        model.mapView.getMapboxMap().getStyle {
                                            it.getSourceAs<GeoJsonSource>("line")
                                                ?.featureCollection(
                                                    FeatureCollection.fromFeature(feature)
                                                )
                                        }
                                        model.clearLine.value = false
                                    }
//                                    if (isChangeDetected.value) {
//                                        model.mapView.getMapboxMap().getStyle {
//                                            it.getSourceAs<GeoJsonSource>("line")
//                                                ?.featureCollection(
//                                                    FeatureCollection.fromFeature(feature)
//                                                )
//                                        }
//                                        isChangeDetected.value = false
//                                    }
                                    if (GeofenceBroadcastReceiver.GBRS.GeoId.value != null) {
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
                                        Box(
                                            modifier = Modifier
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
                                    if (model.isCameraMove.value && MyLocationService.LSS.isEnteredPointToPointRoad.value) {
                                        if(MyLocationService.LSS.roadMaxSpeed.intValue < MyLocationService.LSS.inP2PAverageSpeed.intValue){
                                            Button(
                                                onClick = {},
                                                colors = ButtonDefaults.buttonColors(
                                                    backgroundColor =Color(0xFFEA4E34)
                                                ),
                                                border = BorderStroke(2.dp, Color.White),
                                                modifier = Modifier
                                                    .width(109.dp)
                                                    .height(44.dp)
                                                    .offset(
                                                        (conf.screenWidthDp / 2).dp,
                                                        ((conf.screenHeightDp / 2) - 58).dp
                                                    ), shape =RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomEnd = 18.dp, bottomStart = 8.dp)
                                            ) {
                                                Text(
                                                    text = "You’re above speed limit.",
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Medium,
                                                    fontSize = 12.sp,
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                        Button(
                                            onClick = {},
                                            contentPadding = PaddingValues(0.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                backgroundColor = if (MyLocationService.LSS.roadMaxSpeed.intValue < MyLocationService.LSS.inP2PAverageSpeed.intValue) {
                                                    Color(0xFFEA4E34)
                                                } else {
                                                    Color(0xFF57D654)
                                                }
                                            ),
                                            border = BorderStroke(2.dp, Color.White),
                                            modifier = Modifier
                                                .width(48.dp)
                                                .height(36.dp)
                                                .offset(
                                                    (conf.screenWidthDp / 2).dp,
                                                    ((conf.screenHeightDp / 2) - 10).dp
                                                ), shape =if (singleReport.value.reportSpeedLimit!! < MyLocationService.LSS.inP2PAverageSpeed.intValue) { RoundedCornerShape(topStart = 8.dp, topEnd = 18.dp, bottomEnd = 18.dp, bottomStart = 18.dp)}else{
                                                RoundedCornerShape(24.dp)
                                                }
                                        ) {
                                            Text(
                                                text = "${MyLocationService.LSS.inP2PAverageSpeed.intValue}",
                                                color = Color.White,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                    if (model.reportClicked.value) {
                                        LaunchedEffect(Unit) {
                                            coroutineScope.launch {
                                                modalSheetState.show()
                                            }
                                        }
                                        model.reportClicked.value = false
                                    }
                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .padding(horizontal = 18.dp),
                                        contentAlignment = Alignment.BottomStart
                                    ) {
                                        AnimatedVisibility(
                                            visible = true,
                                            enter = slideInHorizontally(),
                                            exit = slideOutHorizontally()
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .wrapContentSize()
                                                    .background(color = Color.Transparent),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    IconButton(onClick = {
                                                        model.vibrate(context = context)
                                                        if (!model.isTripStarted.value) {
                                                            model.showTripDialog.value = true
                                                        } else {
                                                            model.isTripStarted.value = false
                                                            model.trip.value.endTime = Date()
                                                            model.trip.value.distance =
                                                                MyLocationService.LSS.TripDistance.floatValue
                                                            model.trip.value.startTime =
                                                                MyLocationService.LSS.TripStartTime
                                                            model.trip.value.maxSpeed =
                                                                MyLocationService.LSS.TripMaxSpeed.intValue
                                                            model.trip.value.speedAverage =
                                                                MyLocationService.LSS.TripAverageSpeed.intValue
                                                            model.trip.value.listOfLatLon.addAll(
                                                                lrouteCoordinates
                                                            ).let { ite ->
                                                                if (ite) {
                                                                    trips?.add(model.trip.value)
                                                                        ?.let { itt ->
                                                                            if (itt) {
                                                                                model.saveTrip(
                                                                                    trips
                                                                                )
                                                                                MyLocationService.LSS.resetTrip()
                                                                                lrouteCoordinates.clear()
                                                                            }
                                                                        }
                                                                }
                                                            }
                                                        }
                                                    },
                                                        modifier = Modifier.advancedShadow(
                                                            color = Color(0xFF495CE8),
                                                            alpha = 0.06f,
                                                            cornersRadius = 23.dp,
                                                            shadowBlurRadius = 8.dp,
                                                            offsetX = 0.dp,
                                                            offsetY = 5.dp
                                                        )
                                                    ) {
                                                        Icon(painter = painterResource(id = R.drawable.bg_btn_fab_my_location), contentDescription = "", tint = Color.Unspecified)
                                                        Icon(
                                                            painter = painterResource(id =if (model.isTripStarted.value){R.drawable.ic_pause}else{R.drawable.ic_play} ),
                                                            modifier = Modifier.size(21.dp),
                                                            contentDescription = "",
                                                            tint =Color(0xFF495CE8)
                                                        )
                                                    }
//                                                    Button(
//                                                        contentPadding = PaddingValues(0.dp),
//                                                        onClick = {
//
//                                                        },
//
//                                                        colors = ButtonDefaults.buttonColors(
//                                                            backgroundColor = Color.White
//                                                        ),
//                                                        modifier = Modifier
//                                                            .size(50.dp)
//                                                            .advancedShadow(
//                                                                color = Color.Black,
//                                                                alpha = 0.06f,
//                                                                cornersRadius = 18.dp,
//                                                                shadowBlurRadius = 8.dp,
//                                                                offsetX = 0.dp,
//                                                                offsetY = 5.dp
//                                                            ),
//                                                        elevation = ButtonDefaults.elevation(
//                                                            defaultElevation = 0.dp,
//                                                            pressedElevation = 0.dp,
//                                                            disabledElevation = 0.dp,
//                                                            hoveredElevation = 0.dp,
//                                                            focusedElevation = 0.dp
//                                                        ),
//                                                        shape = RoundedCornerShape(20.dp)
//                                                    ) {
//                                                        if (model.isTripStarted.value) {
//                                                            Column(
//                                                                verticalArrangement = Arrangement.Center,
//                                                                horizontalAlignment = Alignment.CenterHorizontally
//                                                            ) {
//                                                                Icon(
//                                                                    painter = painterResource(id = R.drawable.ic_pause),
//                                                                    modifier = Modifier.size(22.dp),
//                                                                    tint = Color.Unspecified,
//                                                                    contentDescription = ""
//                                                                )
//                                                            }
//                                                        } else {
//                                                            Column(
//                                                                verticalArrangement = Arrangement.Center,
//                                                                horizontalAlignment = Alignment.CenterHorizontally
//                                                            ) {
//                                                                Icon(
//                                                                    painter = painterResource(id = R.drawable.ic_play),
//                                                                    modifier = Modifier.size(22.dp),
//                                                                    tint = Color.Unspecified,
//                                                                    contentDescription = ""
//                                                                )
//                                                            }
//                                                        }
//                                                    }
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    if (model.isTripStarted.value) {
                                                        Text(
                                                            modifier = Modifier
                                                                .width(50.dp)
                                                                .advancedShadow(
                                                                    color = Color(0xFF495CE8),
                                                                    alpha = 0.06f,
                                                                    cornersRadius = 12.dp,
                                                                    shadowBlurRadius = 8.dp,
                                                                    offsetX = 0.dp,
                                                                    offsetY = 2.dp
                                                                ),
                                                            text = stringResource(id = R.string.lbl_home_end_trip),
                                                            textAlign = TextAlign.Center,
                                                            color = Color(0xFF171729),
                                                            fontSize = 7.sp,
                                                            letterSpacing = 0.sp,
                                                            fontWeight = FontWeight.W600
                                                        )
                                                    } else {
                                                        Text(
                                                            modifier = Modifier
                                                                .width(50.dp)
                                                                .advancedShadow(
                                                                    color = Color.Black,
                                                                    alpha = 0.06f,
                                                                    cornersRadius = 12.dp,
                                                                    shadowBlurRadius = 8.dp,
                                                                    offsetX = 0.dp,
                                                                    offsetY = 2.dp
                                                                ),
                                                            text = stringResource(id = R.string.lbl_home_start_trip),
                                                            textAlign = TextAlign.Center,
                                                            color = Color(0xFF171729),
                                                            fontSize = 7.sp,
                                                            letterSpacing = 0.sp,
                                                            fontWeight = FontWeight.W600
                                                        )
                                                    }
                                                    Spacer(
                                                        modifier = Modifier.height(
                                                            (model.navigationBarHeight.value + 95).dp
                                                        )
                                                    )
                                                }
                                            }

                                        }
                                    }
                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .padding(horizontal = 15.dp, vertical = 10.dp),
                                        contentAlignment = Alignment.BottomStart
                                    ) {
                                        AnimatedVisibility(
                                            visible = !model.inSideReportToast.value && model.slider.value,
                                            enter = slideInHorizontally(),
                                            exit = slideOutHorizontally()
                                        ) {
                                            FeedbackToast(reportType = singleReport.value.reportType,
                                                onLike = {
                                                    model.isLiked.value = true
                                                    model.addReportFeedback(reportId, true)
                                                    coroutineScope.launch {
                                                        delay(2000)
                                                        model.slider.value = false
                                                    }
                                                },
                                                onUnlike = {
                                                    model.isLiked.value = false
                                                    model.addReportFeedback(reportId, false)
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
                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .padding(horizontal = 15.dp, vertical = 10.dp),
                                        contentAlignment = Alignment.BottomStart
                                    ) {
                                        AnimatedVisibility(
                                            visible = model.inSideReportToast.value && !model.slider.value,
                                            enter = slideInHorizontally(),
                                            exit = slideOutHorizontally()
                                        ) {
                                            InsideReportToast(
                                                singleReport.value.reportType,
                                                singleReport.value.reportSpeedLimit
                                            ) {
                                                model.inSideReportToast.value = false
                                            }
                                        }
                                    }
                                    Column {
                                        if (!isPurchasedAdRemove.value) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(color = Color.Transparent),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Box(
                                                    modifier = Modifier
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
                                                        ), contentAlignment = Alignment.Center
                                                ) {
                                                    AndroidView(factory = {
                                                        AdView(it).apply {
                                                            this.setAdSize(AdSize.BANNER)
                                                            adUnitId =
                                                                resources.getString(R.string.main_screen_ad_banner_id)
                                                            loadAd(AdRequest.Builder().build())
                                                        }
                                                    })
                                                }

                                            }
                                        }
                                        Box(
                                            modifier = Modifier.padding(
                                                top = if (isPurchasedAdRemove.value) {
                                                    25.dp
                                                } else {
                                                    0.dp
                                                }
                                            )
                                        ) {
                                            topBar(
                                                onClickMenu = {
                                                    isShowMenu.value = true
                                                },
                                                temp = model.temperature.value,
                                                onClickAds = {
                                                    coroutineScope.launch {
                                                        model.whichButtonClicked.value = 5
                                                        modalSheetState.show()
                                                    }
                                                },
                                                isAdLoaded = mRewardedAd != null,
                                                isWatchedRewardVideo = { isPurchasedAdRemove.value },
                                                timeRemain = timeRemainInt,
                                                model.onlineUserCounter
                                            )
                                        }
                                    }
                                    if (model.showCustomDialogWithResult.value) {
                                        showConfirmationDialog(
                                            onDismiss = {
                                                model.showCustomDialogWithResult.value = false
                                            },
                                            onNegativeClick = {
                                                model.showCustomDialogWithResult.value = false
                                            },
                                            onPositiveClick = {
                                                model.addReport(0)
                                                model.getReportsAndAddGeofences()
                                                model.showCustomDialogWithResult.value = false
                                            },
                                            model
                                        )
                                    }
                                    if (showErrorModal.value) {
                                        DynamicModal(
                                            title = errorModalTitle.value,
                                            description = errorModalDescription.value,
                                            icon = R.drawable.ic_cee_two,
                                            positiveButtonAction = {
                                                showErrorModal.value = false
                                            },
                                            negativeButtonAction = {},
                                            positiveButtonText = stringResource(id = R.string.btn_home_alert_done),
                                            positiveButtonModifier = Modifier.fillMaxWidth(0.49f),
                                        )
                                    }
                                    if (model.isReachedQuotaDialog.value) {
                                        DynamicModal(
                                            title = stringResource(id = R.string.lbl_reach_permitted_quota_report_title),
                                            description = stringResource(id = R.string.lbl_reach_permitted_quota_report_description),
                                            icon = R.drawable.ic_cee_two,
                                            positiveButtonAction = {
                                                model.isReachedQuotaDialog.value = false
                                            },
                                            negativeButtonAction = {},
                                            positiveButtonText = stringResource(id = R.string.btn_auth_alert_ok),
                                            positiveButtonModifier = Modifier.fillMaxWidth(0.49f),
                                        )
                                    }
                                    if (isNotificationResponse.value) {
                                        DynamicModal(
                                            title = responseMessage.value,
                                            icon = R.drawable.ic_cee_two,
                                            positiveButtonAction = {
                                                isNotificationResponse.value = false
                                            },
                                            negativeButtonAction = {},
                                            positiveButtonText = stringResource(id = R.string.btn_auth_alert_ok),
                                            positiveButtonModifier = Modifier.fillMaxWidth(0.49f),
                                        )
                                    }
                                    if (deleteReportDialog.value) {
                                        DynamicModal(
                                            title = "Delete report",
                                            description = "Are you sure to delete this report",
                                            icon = R.drawable.ic_cee_two,
                                            positiveButtonAction = {
                                                deleteReportDialog.value = false
                                                if (model.userInfo.value.userType == 2 && !model.isDebugMode.value!!) {
                                                    model.isDeleteReportRequested.value = true
                                                    model.getUid()?.let {
                                                        val req = Request
                                                            .Builder()
                                                            .url("https://us-central1-cee-platform-87d21.cloudfunctions.net/onDeleteReportByAdmin?reportId=${model.reportId.value}&adminId=${it}")
                                                            .post("{}".toRequestBody("application/json".toMediaType()))
                                                            .build()

                                                        val client = OkHttpClient()
                                                        client
                                                            .newCall(req)
                                                            .enqueue(object : Callback {
                                                                override fun onFailure(
                                                                    call: Call,
                                                                    e: IOException
                                                                ) {
                                                                    model.isDeleteReportRequested.value =
                                                                        false
                                                                    Toast.makeText(
                                                                        context,
                                                                        e.message.toString(),
                                                                        Toast.LENGTH_LONG
                                                                    ).show()
                                                                    Log.d(
                                                                        "DEBUG_HTTP_REQUEST_DELETE_REPORT",
                                                                        e.message.toString()
                                                                    )
                                                                }

                                                                override fun onResponse(
                                                                    call: Call,
                                                                    response: Response
                                                                ) {
                                                                    model.isDeleteReportRequested.value =
                                                                        false
                                                                    coroutineScope.launch {
                                                                        modalSheetState.hide()
//                                                            modalBottomSheetState.hide()
//                                                            bottomSheetState.collapse()
                                                                    }
                                                                    Log.d(
                                                                        "DEBUG_HTTP_REQUEST_DELETE_REPORT",
                                                                        response.code.toString()
                                                                    )
                                                                }
                                                            })
                                                    }
                                                } else {
                                                    model.deleteReport(model.reportId.value)
                                                }
                                            },
                                            negativeButtonAction = {
                                                deleteReportDialog.value = false
                                            },
                                            positiveButtonText = stringResource(id = R.string.btn_trip_history_detail_alert_delete),
                                            negativeButtonText = stringResource(id = R.string.btn_setting_appBar_cancel),
                                            positiveButtonTextColor = Color(0xFFEA4E34),
                                            negativeButtonTextColor = Color.Black,
                                            positiveButtonModifier = Modifier
                                                .fillMaxWidth(0.49f)
                                                .border(
                                                    width = 1.dp,
                                                    color = Color(0xFFEA4E34),
                                                    shape = RoundedCornerShape(8.dp)
                                                ),
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
                                    if (model.showTripDialog.value) {
                                        startTripDialog(
                                            onClickStart = {
                                                model.startTrip()
                                            },
                                            onClickContinue = {
                                                model.continueTrip()
                                            }, onDismiss = {
                                                model.showTripDialog.value = false
                                            })
                                    }
                                    if (showRegisterDialog.value) {
                                        showRegisterDialog(
                                            onDismiss = { showRegisterDialog.value = true },
                                            onPositiveClick = {
                                                coroutineScope.launch {
                                                    model.completeUserRegister(it)
                                                        .collect { response ->
                                                            if (response.isSuccess) {
                                                                showRegisterDialog.value = false
                                                            } else {
                                                                errorModalTitle.value =
                                                                    response.serverMessage
                                                                errorModalDescription.value = null
                                                                showErrorModal.value = true
                                                            }
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
                                                it?.let {
                                                    coroutineScope.launch {
                                                        model.updateReportSpeedLimit(
                                                            speedLimit = it,
                                                            reportId = reportIdEditing
                                                        ).collect { response ->
                                                            if (response.isSuccess) {
                                                                showEditReportDialog.value = false
                                                                Toast.makeText(
                                                                    context,
                                                                    response.serverMessage,
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            } else {
                                                                errorModalTitle.value =
                                                                    response.serverMessage
                                                                errorModalDescription.value = null
                                                                showErrorModal.value = true
                                                            }
                                                        }
                                                    }
                                                }
                                            },
                                        )
                                    }
                                    if (showAddReportManuallyDialog.value) {
                                        showAddReportManuallyDialog(
                                            onDismiss = {
                                                showAddReportManuallyDialog.value = false
                                            },
                                            onPositiveClick = { point, type, time, limit, address, _ ->
                                                model.addReport(
                                                    geoPoint = point,
                                                    reportType = type,
                                                    time = time,
                                                    speedLimit = limit,
                                                    address = address
                                                )
                                                showAddReportManuallyDialog.value = false
                                            }, clickedPoint = pointClickedOnMap.value
                                        )
                                    }
                                    LaunchedEffect(Unit) {
                                        delay(7000)
                                        model.createMarkerOnMap(model.allReports)
                                    }
                                    LaunchedEffect(Unit) {

                                        model.mapView.camera.apply {
                                            val zoom = createZoomAnimator(
                                                CameraAnimatorOptions.cameraAnimatorOptions(14.5) {
                                                    startValue(7.0)
                                                }
                                            ) {
                                                startDelay = 1000
                                                duration = 2000
                                                interpolator =
                                                    AccelerateDecelerateInterpolator()
                                            }
                                            playAnimatorsSequentially(zoom)
                                        }




//                                        if (isFirstLunch) {
//                                            Log.d("MAP_ANIMATION_DEBUG", "Hi you")
//
//                                            isFirstLunch = false
//                                        } else {
////                                        model.initLocationComponent()
////                                        model.setupGesturesListener()
//                                            val cameraPosition = CameraOptions.Builder()
//                                                .zoom(14.5)
//                                                .center(
//                                                    Point.fromLngLat(
//                                                        model.lastLocation.value.latitude,
//                                                        model.lastLocation.value.latitude
//                                                    )
//                                                )
//                                                .build()
//                                            model.mapView.getMapboxMap().setCamera(cameraPosition)
//                                            model.isCameraMove.value
//                                        }
                                    }
                                }

                                if(model.isDebugMode.value!!){

                                    var offsetY by remember { mutableFloatStateOf(450f) }
                                    Button(
                                        onClick = {},
                                        contentPadding = PaddingValues(0.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            backgroundColor = Color(0xFFEA4E34)
                                        ),
                                        border = BorderStroke(1.5.dp, Color.White),
                                        modifier = Modifier
                                            .width(60.dp)
                                            .height(25.dp)
                                            .offset { IntOffset(-3, offsetY.roundToInt()) }
                                            .draggable(
                                                orientation = Orientation.Vertical,
                                                state = rememberDraggableState { delta ->
                                                    offsetY += delta
                                                }
                                            )
                                        , shape = RoundedCornerShape(topEnd = 60.dp, bottomEnd = 60.dp)
                                    ) {
                                        Text(
                                            text = "Debug",
                                            color = Color.White,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                                MyLocationService.GlobalStreetSpeed.streetSpeedLimit.value?.let {
                                    var offsetY by remember { mutableFloatStateOf(650f) }
                                    Button(
                                        onClick = {},
                                        contentPadding = PaddingValues(0.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            backgroundColor = Color.White
                                        ),
                                        border = BorderStroke(4.dp, Color(0xFFEA4E34)),
                                        modifier = Modifier
                                            .width(60.dp)
                                            .height(60.dp)
                                            .offset {
                                                IntOffset(
                                                    Utils
                                                        .dpToPx(conf.screenWidthDp)
                                                        .minus(190), offsetY.roundToInt()
                                                )
                                            }
                                            .draggable(
                                                orientation = Orientation.Vertical,
                                                state = rememberDraggableState { delta ->
                                                    offsetY += delta
                                                }
                                            ), shape = RoundedCornerShape(60.dp)
                                    ) {
                                        Text(
                                            text = "${it.toInt()}",
                                            color = Color.Black,
                                            fontSize = 20.sp
                                        )
                                    }
                                }
                            }
                            val langCode = model.langCode.observeAsState()
                            var locale = Locale("en")
                            langCode.value?.let {
                                locale = Locale(it)
                            }
                            configuration.setLocale(locale)
                            if (langCode.value == "ku") {
                                configuration.setLayoutDirection(Locale("ar"))
                            }
                            context.resources.updateConfiguration(
                                configuration,
                                context.resources.displayMetrics
                            )
                            Log.d(
                                "CurrentLocalDebug",
                                context.resources.configuration.locales.get(0).language
                            )
                        }
                    }
                )
            })
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

//fun sendNotification(point: GeoPoint, type: Int) {
//    val req = Request
//        .Builder()
//        .url("https://us-central1-cee-platform-87d21.cloudfunctions.net/sendNotificationForSpaceficLocation?latitude=${point.latitude}&longitude=${point.longitude}&radius=1&type=${type}")
//        .post("{}".toRequestBody("application/json".toMediaType()))
//        .build()
//
//    val client = OkHttpClient()
//    client.newCall(req).enqueue(object : Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                is
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//
//            }
//        })
//}
