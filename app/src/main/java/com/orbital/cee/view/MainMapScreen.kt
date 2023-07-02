package com.orbital.cee.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.graphics.BlurMaskFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
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
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
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
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
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
import com.orbital.cee.core.GeofenceBroadcastReceiver.GBRS.exitReportId
import com.orbital.cee.core.ModalBottomSheetLayout
import com.orbital.cee.core.ModalBottomSheetValue
import com.orbital.cee.core.MyFirebaseMessagingService.NotificationService.body
import com.orbital.cee.core.MyFirebaseMessagingService.NotificationService.title
import com.orbital.cee.core.MyLocationService
import com.orbital.cee.core.MyLocationService.LSS.isChangeDetected
import com.orbital.cee.core.MyLocationService.LSS.lrouteCoordinates
import com.orbital.cee.core.Permissions
import com.orbital.cee.core.rememberModalBottomSheetState
import com.orbital.cee.model.SingleCustomReport
import com.orbital.cee.model.Trip
import com.orbital.cee.model.UserTiers
import com.orbital.cee.ui.theme.black
import com.orbital.cee.ui.theme.white
import com.orbital.cee.utils.MetricsUtils
import com.orbital.cee.utils.Utils.dpToPx
import com.orbital.cee.view.LocationNotAvailable.LocationNotAvailable
import com.orbital.cee.view.home.BottomSheets.AddReportManuallyModal
import com.orbital.cee.view.home.BottomSheets.LoginRequired
import com.orbital.cee.view.home.HomeViewModel
import com.orbital.cee.view.home.SaveUserInfoInAlerted
import com.orbital.cee.view.home.UserInformation
import com.orbital.cee.view.home.components.DynamicModal
import com.orbital.cee.view.home.components.FeedbackToast
import com.orbital.cee.view.home.components.NewBottomBar
import com.orbital.cee.view.home.BottomSheets.NewReportViewDetail
import com.orbital.cee.view.home.BottomSheets.RemoveAds
import com.orbital.cee.view.home.BottomSheets.SoundBottomModal
import com.orbital.cee.view.home.BottomSheets.TabLayout
import com.orbital.cee.view.home.BottomSheets.UpdateCeeMap
import com.orbital.cee.view.home.components.showConfirmationDialog
import com.orbital.cee.view.home.components.showRegisterDialog
import com.orbital.cee.view.home.BottomSheets.speedLimit
import com.orbital.cee.view.home.components.startTripDialog
import com.orbital.cee.view.home.components.TopBar
import com.orbital.cee.view.home.appMenu.AppMenu
import com.orbital.cee.view.home.components.InputValueModal
import com.orbital.cee.view.trip.Speed
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt



@Suppress("DeferredResultUnused")
@SuppressLint("MissingPermission")
@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterialApi::class, ExperimentalPagerApi::class)
@Composable
fun MainMapScreen(model : HomeViewModel,navController: NavController) {

    val layerIDD = "map_annotation"
    val layerPinIDD = "map_pin"
    var reportId = ""
    var speedLimit :String? = ""
    var reportIdEditing = ""
    var oldId = ""
    var mRewardedAd : RewardedAd ? = null
    var mInterstitialAd: InterstitialAd? = null

    val showRegisterDialog = remember { mutableStateOf(false) }
    val isShowMenu = remember { mutableStateOf(false) }
    val showEditReportDialog = remember { mutableStateOf(false) }
    val showSendNotificationDialog = remember { mutableStateOf(false) }
    val isPurchasedAdRemove = remember { mutableStateOf(false) }
    val isLoading = remember { mutableStateOf(false) }
    val deleteReportDialog = remember { mutableStateOf(false) }
    val showErrorModal = remember { mutableStateOf(false) }
    val isNotificationResponse = remember { mutableStateOf(false) }
    val alarm = remember { mutableStateOf(false) }
    val isRemove = remember { mutableStateOf(false) }
    val isShowReport = remember { mutableStateOf(false) }
    val isRewardedVideoReady = remember { mutableStateOf(false) }
    val isShowReportFromDeepLink = remember { mutableStateOf(false) }
    val reportType = remember { mutableStateOf(1) }
    val alertCount = remember { mutableStateOf(0) }
    val bottomSheetContentId = remember { mutableStateOf(0) }


    val timeRemainInt = remember { mutableStateOf(0f) }
    val pointClickedOnMap = remember {mutableStateOf(GeoPoint(0.00,0.00))}

    val username = remember { mutableStateOf("") }
    val reportOwnerUId = remember { mutableStateOf("") }
    val responseMessage = remember { mutableStateOf("") }
    val userAvatar =remember { mutableStateOf("") }
    val errorModalTitle = remember { mutableStateOf("") }

    val errorModalDescription = remember { mutableStateOf<String?>(null) }
    val cameraState = remember {mutableStateOf<CameraState?>(null)}
    val singleReport = remember { mutableStateOf(SingleCustomReport(isSuccess = false)) }
    val storage = Firebase.storage
    val storageRef = storage.reference
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val activity = context as Activity
    val conf = LocalConfiguration.current
    val bottomSheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed, confirmStateChange = {
        if(it == BottomSheetValue.Collapsed){
            bottomSheetContentId.value = 0
        }
        true
    })
    val traveledDistance = model.readDistance.observeAsState()
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = bottomSheetState)
    val infiniteTransition = rememberInfiniteTransition()
    val configuration = LocalConfiguration.current
    val composition by rememberLottieComposition(
        LottieCompositionSpec
            .RawRes(R.raw.lottie_three_dot_loading)
    )
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true,
        speed = 1f,
        restartOnPlay = false
    )
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
                isShowReport.value = false
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
    val appLinkIntent: Intent = activity.intent
     appLinkIntent.action
    LaunchedEffect(appLinkIntent.data != null){
        appLinkIntent.data?.let {data->
            data.pathSegments[0]?.let {actionType->
                Log.d("MAIN_ACTIVITY_PREV", "1:  $actionType")
                model.onCameraTrackingDismissed()
                if (actionType == "report"){
                    data.pathSegments[1]?.let { id->
                        if(id != oldId){
                            oldId = id
                            Log.d("MAIN_ACTIVITY_PREV", "2:  $id")
                            model.getSingleReportForSharedLink(id)?.let{
                                delay(4000)
                                model.allReports.add(it)
                                model.reportId.value = id
                                model.whichButtonClicked.value = 4
                                it.geoLocation?.let {loco->
                                    isShowReport.value = true
                                    val screenHe = Resources.getSystem().displayMetrics.heightPixels
                                    if (loco != Location("")){
                                        model.onCameraTrackingDismissed()
                                        model.mapView.getMapboxMap().flyTo(cameraOptions {
                                            center(Point.fromLngLat(loco[1] as Double,loco[0] as Double))
                                            zoom(15.5)
                                            pitch(10.0)
                                            padding(EdgeInsets(0.0,0.0, screenHe/3.3,0.0))
                                        },MapAnimationOptions.mapAnimationOptions { duration(1500)}
                                        )
                                    }
                                }
                                isShowReportFromDeepLink.value = true
//                                modalSheetState.show()
//                                coroutineScope.launch {
//
//                                }

                            }

                        }

                    }
                }else {
                    data.pathSegments[1]?.let{uid->
                        data.pathSegments[2]?.let {tripId->
                            val islandRef = storageRef.child("SharedTrips/$uid/$tripId")
                            val localFile = File.createTempFile("temp_trip", "trip")
                            try{
                                coroutineScope.launch {
                                    islandRef.getFile(localFile).await()
                                    val jsonString = localFile.readText()
                                    val trip : Trip = Gson().fromJson(jsonString,Trip::class.java)
                                    model.saveTrip(trip)
                                }

                            }catch(e :IOException){
                                Log.d("MAIN_ACTIVITY_PREV", "10:  ${e.message}")
                            }
                        }
                    }
//                if(actionType == "trip"){
//
//                }
                }
            }
        }
    }





    val nearestReport = model.allReports.firstOrNull()
    nearestReport?.let {report->
        if (report.reportType == 1 || report.reportType == 2 || report.reportType == 3 || report.reportType == 5 || report.reportType == 6){
            val reportLocation = Location("")
            reportLocation.latitude = report.geoLocation?.get(0) as Double
            reportLocation.longitude = report.geoLocation?.get(1) as Double
            Log.d("DEBUG_NEAREST_REPORT_DIS","hey")
            if (model.lastLocation.value.distanceTo(reportLocation) < 1000){
                model.isNearReport.value = true
                model.distanceAway.value = (model.lastLocation.value.distanceTo(reportLocation) *100.0).roundToInt() / 100.0
                model.nearReportType.value = report.reportType
            }else{
                model.isNearReport.value = false
                model.distanceAway.value = 0.0
            }
        }


    }

    val lastWatchedAd = model.lsatAdsWatched.observeAsState()

//    val userT = model.userType.observeAsState()


    var mapView by remember { mutableStateOf<MapView?>(null) }
    mapView = MapView(context, MapInitOptions(context, antialiasingSampleCount = 4))
    DisposableEffect(Unit) {
        val newMapView = MapView(context).apply {
            model.mapView = this
            camera.addCameraZoomChangeListener {dd->
                Log.d("DEBUG_ZOOM_LISTENER",dd.toString())
                model.isShowDots.value = dd <= 12.0
            }
            getMapboxMap().loadStyleUri(styleUri = if(model.isDarkMode.value) "mapbox://styles/orbital-cee/clevkf2lm00l301msgt5l621u" else "mapbox://styles/orbital-cee/clh35qsv500lp01qy0rq23sbt") { sty ->
                if (!Permissions.hasLocationPermission(context)) {
                    model.isLocationNotAvailable.value = true
                    Permissions.requestsLocationPermission(context)
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
                            model.pointAnnotationManager!!.addDragListener(model.onDragAnnotation)
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
                                isShowReport.value = true
                                val screenHe = Resources.getSystem().displayMetrics.heightPixels
                                if (lat != null && lon != null){
                                    model.onCameraTrackingDismissed()
                                    this.getMapboxMap().flyTo(cameraOptions {
                                            center(Point.fromLngLat(lon,lat))
                                            zoom(15.5)
                                            pitch(10.0)
                                            padding(EdgeInsets(0.0,0.0, screenHe/3.3,0.0))
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
            if (model.currentUserTier.value == UserTiers.ADMIN){
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
//            mapView?.onDestroy()
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
                isRewardedVideoReady.value = false
            }
            override fun onAdLoaded(rewardedAd: RewardedAd) {
                mRewardedAd = rewardedAd
                isRewardedVideoReady.value = true
//            Toast.makeText(context,"loaded",Toast.LENGTH_LONG).show()
            }
        })
        mRewardedAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdClicked() {}
            override fun onAdDismissedFullScreenContent() {
                mRewardedAd = null
                isRewardedVideoReady.value = false
            }
            override fun onAdImpression() {}
            override fun onAdShowedFullScreenContent() {}
        }
    }
    if (model.stopCount.value == 4){
        model.stopCount.value = 0
        if (!isPurchasedAdRemove.value){
            mInterstitialAd?.show(context)
        }
    }
    LaunchedEffect(Unit){
        model.getUid()?.let {
            model.loadUserInfoFromFirebase()
//            model.setOnlineStatus()
            val userInformation : UserInformation = model.getUserInformation()
            if (userInformation.userName == ""){ showRegisterDialog.value = true }else{ username.value = userInformation.userName!! }
            userAvatar.value = userInformation.userAvatar
        }
    }
    LaunchedEffect(Unit){
        delay(5)
        if (Permissions.hasLocationPermission(context)){
            while (true){
                model.getJsonAsync()
                delay(180000)
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize()){
        Log.d("DEBUG_DEBUG_MODE","hey")
        ModalBottomSheetLayout(
            sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            isShowReport = isShowReport.value,
            sheetContent =
            {
                when (model.whichButtonClicked.value) {
                    1 -> {
                        UpdateCeeMap(onButtonClicked = {RT->
                            Log.d("DEBUG_REPORT_TYPE_UPDATE",RT.toString())
                            if(GeofenceBroadcastReceiver.GBRS.GeoId.value == null){
                                if (MetricsUtils.isOnline(context)){
                                    model.vibrate(context = context)
                                    reportType.value = RT
                                    if (model.currentUserTier.value == UserTiers.ADMIN){
                                        if(RT == 1 || RT == 6 || RT == 5){
                                            model.whichButtonClicked.value =6
                                        }else{
                                            model.showCustomDialogWithResult.value = true
                                        }
                                    }else{
                                        model.showCustomDialogWithResult.value = true
                                    }
                                }else{
                                    Toast.makeText(context,"sorry not internet connection.", Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                            onCloseClick = {
                                coroutineScope.launch {
                                    modalSheetState.hide()
                                }
                            }, userType = model.currentUserTier.value
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
                            onReportDelete = {
                                deleteReportDialog.value = true
                            },onEditSpeedLimit={repo,slim->
                                speedLimit = slim.toString()
                                reportIdEditing = repo
                                showEditReportDialog.value = true
                            }, onClickSendNotification = {
                                reportOwnerUId.value = it
                                showSendNotificationDialog.value = true
                            },
                        )
                    }
                    5 -> {
                        RemoveAds(isPurchasedAdRemove, onClickClose = {
                            coroutineScope.launch {
                                modalSheetState.hide()
                            }
                        }, onClickWatchVideo = {
                            if(isRewardedVideoReady.value){
                                mRewardedAd?.show(context) {
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
                        AddReportManuallyModal(onPositiveClick = { point, type, time, limit, address, isWithNotification,title,description ->
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
                                    .url("https://us-central1-cee-platform-87d21.cloudfunctions.net/sendNotificationForSpaceficLocation?latitude=${point.latitude}&longitude=${point.longitude}&radius=1&type=${type}&description=${description}&title=${title}")
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
                    8->{
                        LoginRequired(
                            onClickLoginWithGoogle = {
                                isLoading.value = true
                            },onClickLoginWithPhone = {
                                navController.navigate("authentication")
                            }, onResult = {isSuccess,message->
                                if (isSuccess){
                                    isLoading.value = false
                                    coroutineScope.launch {
                                        modalSheetState.hide()
                                    }
                                }else{
                                    coroutineScope.launch {
                                        modalSheetState.hide()
                                    }
                                    errorModalTitle.value = "Login Failed."
                                    errorModalDescription.value = message
                                    isLoading.value = false
                                    showErrorModal.value = true
                                    // show Error
                                }
                            },
                            bottomNavBar = model.navigationBarHeight.value
                        )
                    }
                }
            },
            sheetState = modalSheetState,
            modifier = Modifier.fillMaxSize(),
            content = {
                BottomSheetScaffold(
                    sheetPeekHeight = 0.dp,
                    sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                    sheetContent = {
                        when (bottomSheetContentId.value) {
                            1->{
                                Speed(
                                    model = model,
                                    showBottomModalSheet = {
                                        coroutineScope.launch {
                                            modalSheetState.show()
                                        }
                                    },
                                    onClickStart = {model.startTrip()},
                                    onClickFinish = { coroutineScope.launch { model.saveTrip() } },
                                    onClickContinue = {model.continueTrip()},
                                    onClickBack = {
                                        coroutineScope.launch {
                                            delay(10)
                                            bottomSheetState.collapse()
                                        }
                                    },
                                    onClickResetTrip = { model.tripReset() },
                                    onClickPause = { model.pauseTrip() },
                                    isPurchasedAdRemove = isPurchasedAdRemove
                                )
                            }
                            else ->{}
                        }
                    },
                    scaffoldState = scaffoldState,
                    backgroundColor = MaterialTheme.colors.background,
                    sheetBackgroundColor = Color.Transparent,
                    content =
                    { pad ->
                        Log.d("DEBUG_PADDING_In", pad.toString())
                        lastWatchedAd.value?.let {i->
                            if (i > 0) {
                                LaunchedEffect(Unit) {
                                    while (true) {
                                        //timeRemain.value = getRemain(lastWatchedAd.value,Timestamp.now().seconds)
                                        timeRemainInt.value = MetricsUtils.getRemainInt(
                                            lastWatchedAd.value,
                                            Timestamp.now().seconds
                                        )
                                        isPurchasedAdRemove.value =
                                            (Timestamp.now().seconds - i) < 1800
                                        delay(1000)
                                    }
                                }
                            }
                        }
                        Scaffold { padd ->
                            Log.d("DEBUG_PADDING_In", padd.toString())
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(color = MaterialTheme.colors.background)
                            ) {
                                if(model.isChangeInZone1.value){
                                    model.allReports.sortBy {report->
                                        report.geoLocation?.let { location->
                                            val reportLocation = Location("")
                                            reportLocation.latitude = location[0] as Double
                                            reportLocation.longitude = location[1] as Double
                                            model.lastLocation.value.distanceTo(reportLocation)
                                        }
                                    }
                                    model.createMarkerOnMap(model.allReports)
                                    model.isChangeInZone1.value = false
                                }
                                LaunchedEffect(isShowReportFromDeepLink.value){
                                    coroutineScope.launch {
                                        modalSheetState.show()
                                        isShowReportFromDeepLink.value = false
                                    }
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
                                        if(MyLocationService.LSS.roadMaxSpeed.value < MyLocationService.LSS.inP2PAverageSpeed.value){
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
                                                        ((conf.screenHeightDp / 1.5) - 58).dp
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
                                                backgroundColor = if (MyLocationService.LSS.roadMaxSpeed.value < MyLocationService.LSS.inP2PAverageSpeed.value) {
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
                                                    ((conf.screenHeightDp / 1.5) - 10).dp
                                                ), shape =if (singleReport.value.reportSpeedLimit!! < MyLocationService.LSS.inP2PAverageSpeed.value) { RoundedCornerShape(topStart = 8.dp, topEnd = 18.dp, bottomEnd = 18.dp, bottomStart = 18.dp)}else{
                                                RoundedCornerShape(24.dp)
                                                }
                                        ) {
                                            Text(
                                                text = "${MyLocationService.LSS.inP2PAverageSpeed.value}",
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

//                                    Box(
//                                        modifier = Modifier
//                                            .matchParentSize()
//                                            .padding(horizontal = 15.dp, vertical = 10.dp),
//                                        contentAlignment = Alignment.BottomStart
//                                    ) {
//                                        AnimatedVisibility(
//                                            visible = model.inSideReportToast.value && !model.slider.value,
//                                            enter = slideInHorizontally(),
//                                            exit = slideOutHorizontally()
//                                        ) {
//                                            InsideReportToast(
//                                                singleReport.value.reportType,
//                                                singleReport.value.reportSpeedLimit
//                                            ) {
//                                                model.inSideReportToast.value = false
//                                            }
//                                        }
//                                    }
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize(),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Canvas(modifier = Modifier
                                            .fillMaxWidth()
                                            .height((conf.screenHeightDp / 5).dp)){
                                            drawRect(topLeft = Offset(0f,0f), brush = Brush.verticalGradient(
                                                colors = listOf(
                                                    Color(0xFFFFFFFF),
                                                    Color(0x03FFFFFF),
                                                ), tileMode = TileMode.Mirror
                                            ))
                                        }
                                        Canvas(modifier = Modifier
                                            .fillMaxWidth()
                                            .height(((conf.screenHeightDp / 6) + model.navigationBarHeight.value).dp)){
                                            drawRect(topLeft = Offset(0f,0f), brush = Brush.verticalGradient(
                                                colors = listOf(
                                                    Color(0x03FFFFFF),
                                                    Color(0xFFFFFFFF),
                                                ), tileMode = TileMode.Mirror
                                            ))
                                        }
                                    }
                                    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
                                        NewBottomBar(
                                            navigationBarHeight =model.navigationBarHeight.value,
                                            isToastAppeared = model.slider,
                                            onClickSpeedometer = {
                                                coroutineScope.launch {
                                                    model.vibrate(context)
                                                    bottomSheetContentId.value = 1
                                                    delay(10)
                                                    bottomSheetState.expand()
                                                }
                                            },
                                            onClickSound = {
                                                model.vibrate(context)
                                                model.whichButtonClicked.value = 3
                                                coroutineScope.launch {
                                                    modalSheetState.show()
                                                }
                                            },
                                            onClickAddNewReport = {
                                                model.vibrate(context)
                                                if(model.isGuest()){
                                                    model.whichButtonClicked.value = 8
                                                    coroutineScope.launch {
                                                        modalSheetState.show()
                                                    }
                                                }else{
                                                    model.whichButtonClicked.value = 1
                                                    coroutineScope.launch {
                                                        modalSheetState.show()
                                                    }
                                                }
                                            },
                                            onClickIndicator = {
                                                model.vibrate(context)
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
                                            isCameraMove = model.isCameraMove,
                                            soundStatus = model.soundStatus,
                                            userType = model.currentUserTier,
                                            isPointClicked = model.isPointClicked,
                                            onClickReport = {
                                                if (GeofenceBroadcastReceiver.GBRS.GeoId.value == null) {
                                                    if (MetricsUtils.isOnline(context)) {
                                                        if ((traveledDistance.value ?: 0f) >= 15f || model.currentUserTier.value == UserTiers.ADMIN) {
                                                            if (Permissions.hasBackgroundLocationPermission(context = context)) {
                                                                model.vibrate(context = context)
                                                                reportType.value = 1
                                                                if (model.currentUserTier.value == UserTiers.ADMIN) {
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
                                                },MapAnimationOptions.mapAnimationOptions { duration(500)})
                                            }
                                        )
                                        AnimatedVisibility(
                                            visible = model.slider.value,
                                            enter = slideInVertically(
                                                initialOffsetY = { fullHeight -> fullHeight },
                                            ),
                                            exit = slideOutVertically(
                                                targetOffsetY = { fullHeight -> fullHeight },
                                            )
                                        ) {
                                            LaunchedEffect(Unit){
                                                exitReportId.value?.let{id->
                                                    model.getSingleReport(id).collect{
                                                        if (it.isSuccess){
                                                            singleReport.value = it
                                                            model.isLiked.value = null
                                                        }
                                                    }
                                                }

                                            }
                                            FeedbackToast(
                                                reportType = singleReport.value.reportType,
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
                                                },
                                                bottomNavigationHeight = model.navigationBarHeight.value
                                            )
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
                                                    30.dp
                                                } else {
                                                    10.dp
                                                }
                                            )
                                        ) {
                                            TopBar(
                                                onClickMenu = {
                                                    model.vibrate(context)
                                                    isShowMenu.value = true
                                                },
                                                temp = model.temperature.value,
                                                onClickAds = {
                                                    model.vibrate(context)
                                                    coroutineScope.launch {
                                                        model.whichButtonClicked.value = 5
                                                        modalSheetState.show()
                                                    }
                                                },
                                                isAdLoaded = isRewardedVideoReady.value,
                                                isWatchedRewardVideo = { isPurchasedAdRemove.value },
                                                timeRemain = timeRemainInt,
                                            )

                                            this@Column.AnimatedVisibility(
                                                visible = (model.isNearReport.value && !isRemove.value)
                                            ) {
                                                val reportUI = MetricsUtils.getReportUiByReportType(model.nearReportType.value,context)

                                                var alpha by remember { mutableStateOf(1f) }
                                                var offsetY by remember { mutableStateOf(dpToPx(0).toFloat()) }

                                                Box(modifier = Modifier
                                                    .offset {
                                                        IntOffset(0, offsetY.roundToInt())
                                                    }
                                                    .fillMaxWidth()
                                                    .height(92.dp)
                                                    .pointerInput(Unit) {
                                                        detectDragGestures(
                                                            onDragStart = { },
                                                            onDragEnd = {
                                                                if (offsetY > -150) {
                                                                    offsetY = 0f
                                                                }
                                                            },
                                                            onDrag = { change, dragAmount ->
                                                                offsetY += dragAmount.y
                                                                if (offsetY > 0) {
                                                                    offsetY = 0F
                                                                }
                                                                alpha += (dragAmount.y / 200)
                                                                if (offsetY <= -150) {
                                                                    model.isNearReport.value = false
                                                                    isRemove.value = true
                                                                }

                                                                change.consume()
                                                            }
                                                        )
                                                    }
                                                    .padding(horizontal = 24.dp)
                                                    .background(
                                                        color = reportUI.color1.copy(
                                                            alpha = 1f.coerceAtMost(
                                                                0f.coerceAtLeast(alpha)
                                                            )
                                                        ),
                                                        shape = RoundedCornerShape(20.dp)
                                                    )
                                                    .padding(23.dp)){
                                                    Row(modifier = Modifier
                                                        .fillMaxSize()
                                                        , verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(modifier = Modifier.size(35.dp),painter = painterResource(id = reportUI.icon), tint = white, contentDescription = "")
                                                        Spacer(modifier = Modifier.width(15.dp))
                                                        Column {
                                                            Text(text = "In ${model.distanceAway.value}M", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = white)
                                                            Text(text = "${reportUI.title} in your way",fontSize = 12.sp, color = white)
                                                        }
                                                    }
                                                    LaunchedEffect(Unit){
                                                        model.vibrate(context)
//                                                        delay(7500)
//                                                        title.value = null
//                                                        body.value = null
                                                    }
                                                }
                                            }
                                            this@Column.AnimatedVisibility(
                                                visible = title.value != null
                                            ) {
                                                Box(modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(92.dp)
                                                    .padding(horizontal = 24.dp)
                                                    .background(
                                                        color = white,
                                                        shape = RoundedCornerShape(20.dp)
                                                    )
                                                    .padding(23.dp)){
                                                    Row(modifier = Modifier
                                                        .fillMaxSize()
                                                        , verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(modifier = Modifier.size(52.dp),painter = painterResource(id = R.drawable.ic_message_with_background), tint = Color.Unspecified, contentDescription = "")
                                                        Spacer(modifier = Modifier.width(15.dp))
                                                        Column {
                                                            Text(text = "${title.value}", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = black)
                                                            Text(text = "${body.value}",fontSize = 12.sp, color = black)
                                                        }
                                                    }
                                                    LaunchedEffect(Unit){
                                                        model.vibrate(context)
                                                        delay(7500)
                                                        title.value = null
                                                        body.value = null
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if (model.showCustomDialogWithResult.value) {
                                        showConfirmationDialog(
                                            onDismiss = { model.showCustomDialogWithResult.value = false },
                                            onNegativeClick = { model.showCustomDialogWithResult.value = false },
                                            onPositiveClick = {
                                                model.addReport(speedLimit = 0, reportType = reportType.value)
//                                                model.getReportsAndAddGeofences()
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
                                                        val req = Request.Builder()
                                                            .url("https://us-central1-cee-platform-87d21.cloudfunctions.net/onDeleteReportByAdmin?reportId=${model.reportId.value}&adminId=${it}")
                                                            .post("{}".toRequestBody("application/json".toMediaType())).build()
                                                        val client = OkHttpClient()
                                                        client.newCall(req).enqueue(object : Callback {
                                                                override fun onFailure(call: Call, e: IOException) {
                                                                    model.isDeleteReportRequested.value = false
                                                                    Toast.makeText(context, e.message.toString(), Toast.LENGTH_LONG).show()
                                                                    Log.d("DEBUG_HTTP_REQUEST_DELETE_REPORT", e.message.toString())
                                                                }
                                                                override fun onResponse(call: Call, response: Response) {
                                                                    model.isDeleteReportRequested.value = false
                                                                    coroutineScope.launch {
                                                                        modalSheetState.hide()
                                                                    }
                                                                    Log.d("DEBUG_HTTP_REQUEST_DELETE_REPORT", response.code.toString())
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
                                            onClickStart = { model.startTrip() },
                                            onClickContinue = { model.continueTrip() },
                                            onDismiss = { model.showTripDialog.value = false })
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
                                                                errorModalTitle.value = response.serverMessage
                                                                errorModalDescription.value = null
                                                                showErrorModal.value = true
                                                            }
                                                        }
                                                }
                                            },
                                        )
                                    }
                                    if (showEditReportDialog.value) {
                                        InputValueModal(
                                            onDismiss = { showEditReportDialog.value = false },
                                            speedLimit = speedLimit.toString(),
                                            labelOne = "Update speed Limit",
                                            labelTwo = null,
                                            onPositiveClick = {
                                                it?.let {
                                                    coroutineScope.launch {
                                                        model.updateReportSpeedLimit(
                                                            speedLimit = it.toInt(),
                                                            reportId = reportIdEditing
                                                        ).collect { response ->
                                                            if (response.isSuccess) {
                                                                showEditReportDialog.value = false
                                                                Toast.makeText(context, response.serverMessage, Toast.LENGTH_SHORT).show()
                                                            } else {
                                                                errorModalTitle.value = response.serverMessage
                                                                errorModalDescription.value = null
                                                                showErrorModal.value = true
                                                            }
                                                        }
                                                    }
                                                }
                                            },
                                        )
                                    }
                                    if (showSendNotificationDialog.value) {
                                        InputValueModal(
                                            isString = true,
                                            onDismiss = { showSendNotificationDialog.value = false },
                                            speedLimit = speedLimit.toString(),
                                            labelOne = "Send Notification.",
                                            labelTwo = "Please enter the message you want to reach the specified user",
                                            onPositiveClick = {
                                                val req = Request
                                                    .Builder()
                                                    .url("https://us-central1-cee-platform-87d21.cloudfunctions.net/sendNotificationForSpecificUser?userId=${reportOwnerUId.value}&description=${it}")
                                                    .post("{}".toRequestBody("application/json".toMediaType()))
                                                    .build()

                                                val client = OkHttpClient()
                                                client.newCall(req).enqueue(object : Callback {
                                                    override fun onFailure(call: Call, e: IOException) {
                                                        model.isDeleteReportRequested.value = false
                                                        Toast.makeText(context, e.message.toString(), Toast.LENGTH_LONG).show()
                                                        Log.d("DEBUG_HTTP_REQUEST_DELETE_REPORT", e.message.toString())
                                                    }
                                                    override fun onResponse(call: Call, response: Response) {
                                                        showSendNotificationDialog.value = false
                                                        Log.d("DEBUG_HTTP_REQUEST_DELETE_REPORT", response.code.toString())
                                                    }
                                                })
                                            },
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
                                                interpolator = AccelerateDecelerateInterpolator()
                                            }
                                            playAnimatorsSequentially(zoom)
                                        }
                                    }
                                }
                                if(model.isDebugMode.value!!){

                                    val offsetY  = remember { mutableStateOf(450f) }
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
                                            .offset { IntOffset(-3, offsetY.value.roundToInt()) }
                                            .draggable(
                                                orientation = Orientation.Vertical,
                                                state = rememberDraggableState { delta ->
                                                    offsetY.value += delta
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
            enter = slideInHorizontally(animationSpec = tween(100), initialOffsetX = {-600}),
            exit = slideOutHorizontally(animationSpec = tween(300), targetOffsetX = {-it})
        ) {
            Box(modifier = Modifier
                .wrapContentSize()
                .background(color = Color.Transparent), contentAlignment = Alignment.Center) {
                AppMenu(model = model, onCloseDrawer = {
                    isShowMenu.value = false
                }, onClickLoginWithPhone = {
                    navController.navigate("authentication")
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
        if(isLoading.value){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color(0xB2000000))
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {

                        })
                    },
                contentAlignment = Alignment.Center
            ) {
                LottieAnimation(
                    composition,
                    progress,
                    modifier = Modifier.size(65.dp)
                )
            }
        }

        LaunchedEffect(GeofenceBroadcastReceiver.GBRS.GeoId.value != null && GeofenceBroadcastReceiver.GBRS.GeoId.value != reportId){
            if (GeofenceBroadcastReceiver.GBRS.GeoId.value != null && GeofenceBroadcastReceiver.GBRS.GeoId.value != reportId){
                model.addAlertCount(alertCount.value)
                reportId = GeofenceBroadcastReceiver.GBRS.GeoId.value!!
                model.inSideReportToast.value = true
                model.inSideReport.value = true
                model.saveAlerts(SaveUserInfoInAlerted(
                    userName = username.value,
                    userAvatar = userAvatar.value,
                    reportId = reportId
                ))
                model.getSingleReport(reportId).collect{
                    if (it.isSuccess){
                        singleReport.value = it
                        model.isLiked.value = null
                    }
                }
            }else{
                if(GeofenceBroadcastReceiver.GBRS.GeoId.value == null){
                    alarm.value = false
                    model.inSideReportToast.value = false
                }
            }
        }
        if(GeofenceBroadcastReceiver.GBRS.isExit.value == true){
            LaunchedEffect(Unit){
                delay(5000)
                model.slider.value = true
                GeofenceBroadcastReceiver.GBRS.isExit.value = false
            }
        }


    }
}
fun Modifier.innerShadow(
    blur: Dp = 0.dp,
    color: Color= Color.Black,
    cornersRadius: Dp = 0.dp,
    offsetX: Dp = 0.dp,
    spread: Dp = 0.dp,
    offsetY: Dp = 0.dp,
) = drawWithContent {
    drawContent()
    val rect = Rect(Offset.Zero, size)
    val paint = Paint()

    drawIntoCanvas {
        paint.color = color
        paint.isAntiAlias = true
        it.saveLayer(rect, paint)
        it.drawRoundRect(
            left = rect.left,
            top = rect.top,
            right = rect.right,
            bottom = rect.bottom,
            cornersRadius.toPx(),
            cornersRadius.toPx(),
            paint
        )
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
        if (blur.toPx() > 0) {
            frameworkPaint.maskFilter = BlurMaskFilter(blur.toPx(), BlurMaskFilter.Blur.NORMAL)
        }
        val left = if (offsetX > 0.dp) {
            rect.left + offsetX.toPx()
        } else {
            rect.left
        }
        val top = if (offsetY > 0.dp) {
            rect.top + offsetY.toPx()
        } else {
            rect.top
        }
        val right = if (offsetX > 0.dp) {
            rect.right + ((offsetX.toPx()*-1 ) +11)
        } else {
            rect.right
        }
        val bottom = if (offsetY < 0.dp) {
            rect.bottom + offsetY.toPx()
        } else {
            rect.bottom
        }
        paint.color = Color.Black
        it.drawRoundRect(
            left = left + spread.toPx() / 2,
            top = top + spread.toPx() / 2,
            right = right - spread.toPx() / 2,
            bottom = bottom - spread.toPx() / 2,
            cornersRadius.toPx(),
            cornersRadius.toPx(),
            paint
        )
        frameworkPaint.xfermode = null
        frameworkPaint.maskFilter = null
    }
}