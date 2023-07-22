package com.orbital.cee.view.home
import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.runtime.*
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.orbital.cee.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Source
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point

import com.mapbox.maps.CameraOptions
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.extension.style.utils.toValue
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.annotation.Annotation
import com.mapbox.maps.plugin.annotation.AnnotationConfig
import com.mapbox.maps.plugin.annotation.AnnotationPlugin
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationDragListener
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.orbital.cee.core.Constants
import com.orbital.cee.core.Constants.DB_REF_REPORT
import com.orbital.cee.core.Constants.DB_REF_REPORT_DEBUG
import com.orbital.cee.core.Constants.DB_REF_USER
import com.orbital.cee.core.GeofenceBroadcastReceiver
import com.orbital.cee.core.MyLocationService
import com.orbital.cee.core.MyLocationService.LSS.lrouteCoordinates
import com.orbital.cee.core.MyLocationService.LSS.resetTrip
import com.orbital.cee.core.MyLocationService.LSS.speed
import com.orbital.cee.data.Event
import com.orbital.cee.data.repository.AppSetting
import com.orbital.cee.data.repository.DSRepositoryImpl
import com.orbital.cee.data.repository.DataStoreRepository
import com.orbital.cee.data.repository.UserStatistics
import com.orbital.cee.model.*
import com.orbital.cee.utils.MetricsUtils
import com.orbital.cee.utils.MetricsUtils.Companion.getPermissionsByUserTier
import com.orbital.cee.utils.MetricsUtils.Companion.getReportTypeByReportTypeAndSpeedLimit
import com.orbital.cee.utils.MetricsUtils.Companion.userTypeToUserTier
import com.orbital.cee.utils.Utils
import com.orbital.cee.view.MainActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json
import org.json.JSONObject
import java.net.URL
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


@HiltViewModel
@RequiresApi(Build.VERSION_CODES.S)
class HomeViewModel @Inject  constructor(
    val auth: FirebaseAuth,
    val db: FirebaseFirestore,
    val ds : DSRepositoryImpl,
    private val dataStoreRepository: DataStoreRepository,
    val storage: FirebaseStorage,
    application: Application,
): AndroidViewModel(application) {
    val app = application
    val readFirstLaunch = dataStoreRepository.readFirstLaunch.asLiveData()
    val lsatAdsWatched = dataStoreRepository.watchTime.asLiveData()
    val loadTimeOfLastReport = dataStoreRepository.loadTimeOfLastReport.asLiveData()
//    val readMaxSpeed = dataStoreRepository.readMaxSpeed.asLiveData()
//    val readAlertsCount = dataStoreRepository.readAlertsCount.asLiveData()
    val readDistance = dataStoreRepository.readDistance.asLiveData()
    val soundStatus = dataStoreRepository.readSoundStatus.asLiveData()
//    val userType = dataStoreRepository.readUserType.asLiveData()
    val reportCountPerOneHour : LiveData<Int> = dataStoreRepository.reportCountPerOneHour.asLiveData()
    val trips = dataStoreRepository.tripList.asLiveData()
    val langCode = dataStoreRepository.languageCode.asLiveData()
    val speedometerId = dataStoreRepository.speedometerId.asLiveData()
    val cursorId = dataStoreRepository.cursorId.asLiveData()
    val geofenceRadius = dataStoreRepository.readGeofenceRadius.asLiveData()
    var appLaunchTime = mutableStateOf<Date?>(null)
    var userInfo = mutableStateOf(UserNew())
    var trip = mutableStateOf(Trip())
    var isCameraMove = mutableStateOf(true)
    var clearLine = mutableStateOf(true)
    var showCustomDialogWithResult = mutableStateOf(false)
    var geofencingClient : GeofencingClient = LocationServices.getGeofencingClient(app)

    var userStatistics : MutableLiveData<UserStatistics> = MutableLiveData(UserStatistics(0,0f,0))
    var navigationBarHeight = mutableStateOf(0)
    var isReachedQuotaDialog = mutableStateOf(false)
    var isDebugMode :MutableLiveData<Boolean> = MutableLiveData(false)
//    var currentUserType :MutableLiveData<Int> = MutableLiveData(0)
    var appSetting :MutableLiveData<AppSetting> = MutableLiveData(AppSetting(false,1000f))

    var isDeleteReportRequested =  mutableStateOf(false)
    var lastLocation = mutableStateOf(Location(""))
    var isTripStarted = mutableStateOf(false)
    var isDarkMode = mutableStateOf(false)
    var isPointClicked = mutableStateOf(false)
//    var isPurchasedAdRemove = mutableStateOf(false)
    var reportClicked = mutableStateOf(false)
    var isShowDots = mutableStateOf(false)
    var isCameraZoomChanged = mutableStateOf(false)

//    val onlineUserCounter = mutableStateOf(0)
    val clickedUserId = mutableStateOf("")

//    val deepLinkActionType = mutableStateOf<String?>(null)
//    val deepLinkFirstArg = mutableStateOf<String?>(null)
//    val deepLinkSecondArg = mutableStateOf<String?>(null)
//    val speedPercent = mutableStateOf(0.0f)
    var whichButtonClicked = mutableStateOf(0)
    val slider = mutableStateOf(false)
    val inSideReportToast = mutableStateOf(false)
    val inSideReport = mutableStateOf(false)
    val usersFound = mutableStateOf(false)
//    val isDarkModeEnabled = mutableStateOf(false)
    var reportId = mutableStateOf<String>("")
    val myReports = mutableListOf<SingleCustomReport>()
    val resultUser = mutableListOf<UserNameAndID>()
    var temperature = mutableStateOf<Int?>(null)
//    var tripDurationInSeconds = mutableStateOf<Int?>(0)
    private val responseMessage = mutableStateOf<Event<String>?>(null)
    var isLocationNotAvailable = mutableStateOf(false)
    var fusedLocationProviderClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(app)
    val isLiked = mutableStateOf<Boolean?>(null)
    val isNearReport = mutableStateOf<Boolean>(false)
    val nearReportType = mutableStateOf(1)
    val reportSightRadius = mutableStateOf(1L)
    val distanceAway = mutableStateOf(0.0)
    val allReports = ArrayList<NewReport>()
    var annotationApi : AnnotationPlugin? = null
    var annotationApii : AnnotationPlugin? = null
    var markerList : ArrayList<PointAnnotationOptions> = ArrayList()
    var pointAnnotationManager : PointAnnotationManager? = null
    var pointAnnotationManagerr : PointAnnotationManager? = null
    lateinit var annotationConfig : AnnotationConfig
    lateinit var annotationConfigg : AnnotationConfig
    var userDetail = mutableStateOf(UserNew())
    val isChangeInZone1 = mutableStateOf(false)
    val currentUserTier: MutableLiveData<UserTiers> = MutableLiveData(UserTiers.GUEST)
    private val currentUserPermission: MutableLiveData<UserPermission> = MutableLiveData(UserPermission())
    val stopCount =  mutableStateOf(0)

    var mRewardedAd : RewardedAd? = null
    var mInterstitialAd: InterstitialAd? = null
    val isRewardedVideoReady = mutableStateOf(false)
    private val tempReports1 = mutableListOf<NewReport>()
    private val tempGeofence1 = mutableListOf<Geofence>()
    private val tempReports2 = mutableListOf<NewReport>()
    private val tempGeofence2 = mutableListOf<Geofence>()
    private val tempReports3 = mutableListOf<NewReport>()
    private val tempGeofence3 = mutableListOf<Geofence>()
    private val tempReports4 = mutableListOf<NewReport>()
    private val tempGeofence4 = mutableListOf<Geofence>()

    var snap1 : ListenerRegistration? = null
    var snap2 : ListenerRegistration? = null
    var snap3 : ListenerRegistration? = null
    var snap4 : ListenerRegistration? = null
    var counter = 0

    var mapView = MapView(app)

    var timeRemain = mutableStateOf(0)
    var isTimerRunning = mutableStateOf(false)
    init {
        retrieveUserType()
        retrieveIsDebugMode()
        retrieveIsPreventScreenSleep()
        retrieveReportMuted()
    }

    private fun retrieveReportMuted(){
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.readMuted.collect{
                MyLocationService.LSR.allMutedReports.addAll(it.mutedReports)
            }
        }
    }

    private fun retrieveUserType() {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.readUserType.collect{
                currentUserTier.postValue((userTypeToUserTier(it)))
                currentUserPermission.postValue(getPermissionsByUserTier(userTypeToUserTier(it)))
            }
        }
    }
    private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener {
        if (speed.value > 5){
            mapView.getMapboxMap().setCamera(CameraOptions.Builder().bearing(it).build())
        }
    }
    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        val padding = Resources.getSystem().displayMetrics.heightPixels/3.0
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(it).padding(EdgeInsets(padding,0.0,0.0,0.0)).build())
        mapView.gestures.focalPoint = mapView.getMapboxMap().pixelForCoordinate(it)
    }
    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            onCameraTrackingDismissed()
        }
        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }
        override fun onMoveEnd(detector: MoveGestureDetector) {
        }

    }

     val onDragAnnotation = object : OnPointAnnotationDragListener{
        override fun onAnnotationDrag(annotation: Annotation<*>) {
        }
        override fun onAnnotationDragFinished(annotation: Annotation<*>) {
            val reportArray = annotation.getData()?.asJsonObject
            val reportId = reportArray?.get("report")
             val myJson = Json { ignoreUnknownKeys = true }
            val data = myJson.decodeFromString<GeometryAno>(annotation.geometry.toValue().contents.toString())
            val point = GeoPoint(data.coordinates[1],data.coordinates[0])
            reportId?.asString?.let {
                updateReportLocation(it,point)
            }
        }
        override fun onAnnotationDragStarted(annotation: Annotation<*>) {
            Log.d("DEBUG_DRAG_ANNOTATION","hey2")
        }
    }
    private fun updateReportLocation(reportId: String, point: GeoPoint) {
        try {
            val hash = GeoFireUtils.getGeoHashForLocation(GeoLocation(point.latitude, point.longitude))
            val report : HashMap<String, Any> = HashMap<String, Any>()
            report["g"] = hash
            report["geoLocation"] = listOf(point.latitude, point.longitude)
            db.collection(if(!isDebugMode.value!!){DB_REF_REPORT}else{ DB_REF_REPORT_DEBUG }).document(reportId).update(report)
        }catch (e:Exception){
            Log.d("DEBUG_HOME_VIEW_MODEL", "updateReportLocation() : ${e.message.toString()}")
        }

    }


    //    fun saveStatistics(alertedCount:Int, traveledDistance : Float, _maxSpeed: Int){
//        viewModelScope.launch(Dispatchers.IO) {
//            ds.retrieveStatistics().collect{
//                ds.saveStatistics(uStatistics = UserStatistics(
//                    alertedCount = it.alertedCount + alertedCount,
//                    traveledDistance =it.traveledDistance +traveledDistance ,
//                    maxSpeed =it.maxSpeed + _maxSpeed
//                ))
//            }
//        }
//    }
    suspend fun retrieveStatistics(){
        viewModelScope.launch(Dispatchers.IO) {
            ds.retrieveStatistics().collect{
                userStatistics.postValue(it)
            }
        }
    }
    fun initLocationComponent() {
        val locationComponentPlugin =  mapView.location
        val cursorIcon = mutableStateOf(R.drawable.ic_default_cursor)
        viewModelScope.launch {
            dataStoreRepository.cursorId.collect{
                if (it == "HITEX"){
                    cursorIcon.value = R.drawable.ic_hitex_cursor
                }
            }
        }
        locationComponentPlugin.updateSettings {
            this.enabled = true
            this.pulsingEnabled = false
            this.pulsingColor = R.color.primary
            this.locationPuck = LocationPuck2D(
                topImage = AppCompatResources.getDrawable(
                    app,
                    cursorIcon.value,
                ),
                bearingImage = AppCompatResources.getDrawable(
                    app,
                    R.drawable.user_puck_shadow_solid,
                ),
            )
        }
        locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        locationComponentPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
    }
    fun onCameraTrackingDismissed() {
        mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.location
            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
        isCameraMove.value = false
    }
    fun setupGesturesListener() {
        mapView.gestures.addOnMoveListener(onMoveListener)

    }
    fun removeGesturesListener() {
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }
    fun addReport(geoPoint: GeoPoint,
                  reportType:Int,
                  time: Timestamp = Timestamp.now(),
                  speedLimit:Int? = null,
                  address : String? = null,
                  bearing : Float = -1f
    ) = viewModelScope.launch {
        try {
            auth.currentUser?.let {
                val id = db.collection(if(!isDebugMode.value!!){DB_REF_REPORT}else{ DB_REF_REPORT_DEBUG }).document().id
                val document = db.collection(if(!isDebugMode.value!!){DB_REF_REPORT}else{ DB_REF_REPORT_DEBUG }).document(id)
                val idReportInUserDocument = db.collection(if(!isDebugMode.value!!){DB_REF_REPORT}else{ DB_REF_REPORT_DEBUG }).document(it.uid).collection("reports").document(id)

                val hash = GeoFireUtils.getGeoHashForLocation(GeoLocation(geoPoint.latitude, geoPoint.longitude))
                val report : HashMap<String, Any> = HashMap<String, Any>()
                val reportt : HashMap<String, Any> = HashMap<String, Any>()




                report["g"] = hash
                report["geoLocation"] = listOf(geoPoint.latitude, geoPoint.longitude)
                report["reportTimeStamp"] = time
                if (address != null){
                    report["reportAddress"] = address
                }else{
                    report["reportAddress"] = getAddress(geoPoint.latitude,geoPoint.longitude, app)
                }
                report["reportType"] = reportType
                report["reportId"] = id
                if (speedLimit!=null){
                    report["reportSpeedLimit"] = speedLimit
                }else{
                    MyLocationService.GlobalStreetSpeed.streetSpeedLimit.value?.let {speedLimit->
                        report["reportSpeedLimit"] = speedLimit.toInt()
                    }
                }
                if (bearing == 0f){
                    report["reportDirection"] = -1
                }else{
                    report["reportDirection"] = bearing
                }
                report["reportByUID"] = it.uid
                document.set(report).await()
                idReportInUserDocument.set(reportt).await()
            }
        } catch (e: Exception) {
            Log.d("VIEW_MODEL_LOGS",e.message.toString())
        }
    }
    fun addReportFeedback(reportId : String,status : Boolean) = viewModelScope.launch{
        try {
            auth.currentUser?.let {
                val document = db.collection(if(!isDebugMode.value!!){DB_REF_REPORT}else{ DB_REF_REPORT_DEBUG }).document(reportId).collection("Feedback").document(it.uid)
                val feedback : HashMap<String, Any> = HashMap<String, Any>()
                feedback["feedbackByUID"] = it.uid
                feedback["feedbackTimestamp"] = Timestamp.now()
                feedback["feedbackType"] = status
                document.set(feedback).await()
            }
        }catch (e:Exception){
            Log.d("VIEW_MODEL_LOGS",e.message.toString())
        }
    }
    fun deleteUser() = viewModelScope.launch {
        auth.currentUser?.delete()?.addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("TAG", "User account deleted.")
            }
        }
    }
    suspend fun findUserByPhoneOrEmail(phoneOrEmail:String){
        try {
            usersFound.value = false
            val res0 = db.collection(DB_REF_USER).whereEqualTo("phoneNumber",phoneOrEmail).get().await().documents
            val res1 = db.collection(DB_REF_USER).whereEqualTo("email",phoneOrEmail).get().await().documents
            val res2 = db.collection(DB_REF_USER).whereEqualTo("username",phoneOrEmail).get().await().documents
            val res = concatenate(res0,res1,res2)
            resultUser.clear()
            res.forEach {user->
                Log.d("RESULTS_BLAB",res.size.toString())
                resultUser.add(
                    UserNameAndID(
                        userId = user.get("userId") as String,
                        username = user.get("username") as String
                    )
                )
            }
            usersFound.value = true
        }catch (e:Exception){
            usersFound.value = false
            Log.d("DEBUG_HOME_VIEW_MODEL", "updateReportLocation() : ${e.message.toString()}")
        }

    }
    suspend fun findUserByUID(userId:String){
        val res = db.collection(DB_REF_USER).document(userId).get().await()
        if (res.data != null){
            userDetail.value = res.toObject(UserNew::class.java)!!
        }
    }
    fun deleteReport(reportId: String) = viewModelScope.launch {
        db.collection(if(!isDebugMode.value!!){DB_REF_REPORT}else{ DB_REF_REPORT_DEBUG }).document(reportId).delete().await()
    }
    fun saveFirstLaunch(firstLaunch: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveFirstLaunch(firstLaunch)
        }
    fun saveTrip(tripList: ArrayList<Trip?>) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveTripHistory(tripList)
        }
    fun saveWatchAdTime(time: Timestamp) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveAdsWatchTime(time)
        }
//    fun saveMaxSpeed(speed: Int) =
//        viewModelScope.launch(Dispatchers.IO) {
//            dataStoreRepository.saveMaxSpeed(speed)
//        }
    fun addAlertCount(alertCount : Int) =
        viewModelScope.launch(Dispatchers.IO) {
            val temp = alertCount.plus(1)
            dataStoreRepository.addAlertCount(temp)
        }
    private fun saveUserType(userType : Int) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveUserType(userType)
        }
    fun saveLanguageCode(langCode : String) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveLanguageCode(langCode)
        }
    fun saveGeofenceRadius(radius : Int) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveGeoFenceRadius(radius)
        }
//    fun addDistance(distanceKM: Float) =
//        viewModelScope.launch(Dispatchers.IO) {
//            dataStoreRepository.addDistance(distanceKM)
//        }
    fun updateSoundStatus(soundStatusId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.updateSoundPreferences(soundStatusId)
        }
    suspend fun getMyReportCount() : Int{
        try {
            auth.currentUser?.let {
                var a = 0
                a += db.collection(if(!isDebugMode.value!!){DB_REF_REPORT}else{ DB_REF_REPORT_DEBUG }).whereEqualTo("reportByUID", it.uid).get().await().documents.size
                return a
            }
            return 0
        }catch (e:Exception){
            Log.d("PrintGetMyReportCount_Error",e.message.toString())
            return 0
        }
    }
     private fun placeReportValidation(reportCounterVal : Int?, lastReportPlaceTime : Long):ResponseDto{
         return currentUserPermission.value?.let {permission->
              if (permission.isCanAddReport){
                 if (permission.reportLimitPerHour == -1){
                     ResponseDto(isSuccess = true,"success")

                 }else{
                     if (Timestamp.now().seconds.minus(lastReportPlaceTime) < 360){
                         if((reportCounterVal?:0) > permission.reportLimitPerHour){
                             ResponseDto(isSuccess = false,"success")
                         }else{
                             saveTheTimeOfTheLastReport()
                             incrementReportCountPerOneHour(reportCounterVal ?: 0)
                             ResponseDto(isSuccess = true,"success")
                         }
                     }else{
                         saveTheTimeOfTheLastReport()
                         resetIncrementedReportCounterPerOneHour()
                         ResponseDto(isSuccess = true,"success")
                     }
                 }
             }else{
                  ResponseDto(isSuccess = false,"success")
             }
         } ?: ResponseDto(isSuccess = false,"success")
    }
    private fun resetIncrementedReportCounterPerOneHour() =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.incrementReportCountPerOneHour(0)
        }
    private fun incrementReportCountPerOneHour(reportCounterVal : Int) =
        viewModelScope.launch(Dispatchers.IO) {
            val tempCounter = reportCounterVal.plus(1)
            dataStoreRepository.incrementReportCountPerOneHour(tempCounter)
    }
    private fun saveTheTimeOfTheLastReport()=
        viewModelScope.launch(Dispatchers.IO) {
            val cTime = Timestamp.now()
         dataStoreRepository.saveTheTimeOfTheLastReport(cTime)
    }
    fun createClickedPin(lat: Double,lon:Double){
        pointAnnotationManagerr?.deleteAll()
        val pointAnnotationOptions : PointAnnotationOptions = PointAnnotationOptions()
            .withPoint(Point.fromLngLat(lon, lat))
            .withIconSize(1.25)
            .withIconOffset(listOf(0.0,0.0))
            .withIconAnchor(IconAnchor.BOTTOM)
            .withIconImage(convertDrawableToBitMap(AppCompatResources.getDrawable(app,R.drawable.ic_manual_place_camera)))
        pointAnnotationManagerr?.create(pointAnnotationOptions)
    }
    fun createMarkerOnMap(repo : List<NewReport>){
        try {
            if (isCameraZoomChanged.value){
                createDotMarkerOnMap(repo)
            }else{
            markerList = ArrayList()
            pointAnnotationManager?.deleteAll()
            for (i in repo){
                var bitmap : Bitmap? = null
                getReportTypeByReportTypeAndSpeedLimit(i.reportType,i.reportSpeedLimit)?.let {
                    bitmap = convertDrawableToBitMap(AppCompatResources.getDrawable(app,it))
                }
                bitmap?.let{
                    val jsonObject = JSONObject()
                    jsonObject.put("report",i.reportId)
                    jsonObject.put("type",i.reportType)
                    jsonObject.put("speed-limit",i.reportSpeedLimit)
                    jsonObject.put("lat", i.geoLocation?.get(0) ?: 0.0)
                    jsonObject.put("lon", i.geoLocation?.get(1) ?: 0.0)
                    val pointAnnotationOptions : PointAnnotationOptions = PointAnnotationOptions()
                        .withGeometry(Point.fromLngLat(i.geoLocation!![1] as Double, i.geoLocation!![0] as Double))
                        .withData(Gson().fromJson(jsonObject.toString(),JsonElement::class.java))
                        .withIconImage(it)
                        .withIconSize(0.75)
                        .withDraggable(draggable = (i.reportType == 1||i.reportType == 2||i.reportType == 3||i.reportType == 4) && currentUserTier.value == UserTiers.ADMIN)
                        .withIconOffset(listOf(0.0,10.0))
                        .withIconAnchor(IconAnchor.BOTTOM)
                    markerList.add(pointAnnotationOptions)
                }
            }
            pointAnnotationManager?.create(markerList)
            }
        }catch (e:Exception){
            Log.d("VIEW_MODEL_LOGS",e.message.toString())
        }
    }
    lateinit var handler: Handler
    private var seconds = 0
    private var pauseOffset: Int = 0
    var showTripDialog =  mutableStateOf(false)
     fun startTimer() {
        handler.post(object : Runnable {
            override fun run() {
                seconds++
                handler.postDelayed(this, 1000)
                timeRemain.value = seconds
            }
        })

        isTimerRunning.value = true
    }
     private fun pauseTimer() {
        handler.removeCallbacksAndMessages(null)
        pauseOffset = seconds
        isTimerRunning.value = false
    }
     private fun resetTimer() {
        if (isTimerRunning.value) {
            pauseTimer()
        }
        seconds = 0
        pauseOffset = 0
        timeRemain.value = seconds
    }


     fun continueTrip() {
        isTripStarted.value = true
        showTripDialog.value = false
    }

     fun tripReset() {
        resetTimer()
        isTripStarted.value = false
         clearLine.value = true
        resetTrip()
    }

     fun pauseTrip() {
        if (isTimerRunning.value){
            pauseTimer()
        }else{
            startTimer()
        }
        MyLocationService.LSS.isTripPused.value = !MyLocationService.LSS.isTripPused.value
    }

     fun startTrip() {
        if (!isTimerRunning.value){
            startTimer()
        }else{
            resetTimer()
            startTimer()
        }
        isTripStarted.value = true
        resetTrip()
        lrouteCoordinates.clear()
        showTripDialog.value = false
    }

    private var tripSize = -1
     suspend fun saveTrip(eTrip:Trip? = null) {
         if (eTrip != null){
             eTrip.let {
                 dataStoreRepository.tripList.collect{trips->
                     if (tripSize+1 != trips.size){
                         tripSize = trips.size
                         val templ = ArrayList<Trip?>()
                         templ.addAll(trips)
                         templ.add(eTrip)
                         saveTrip(templ)
                     }
                 }
             }
         }else{
             isTripStarted.value = false
             trip.value.endTime = Date()
             trip.value.distance = MyLocationService.LSS.TripDistance.value
             trip.value.maxSpeed = MyLocationService.LSS.TripMaxSpeed.value
             trip.value.startTime = MyLocationService.LSS.TripStartTime
             trip.value.speedAverage = MyLocationService.LSS.TripAverageSpeed.value
             trip.value.listOfLatLon.addAll(lrouteCoordinates)
             dataStoreRepository.tripList.collect{trips->
                 if (tripSize+1 != trips.size){
                     tripSize = trips.size
                     val templ = ArrayList<Trip?>()
                     templ.addAll(trips)
                     templ.add(trip.value).let {
                         saveTrip(templ)
                         lrouteCoordinates.clear()
                         resetTrip()
                         resetTimer()
                     }
                 }
             }
         }
    }

    private fun createDotMarkerOnMap(repo : List<NewReport>){
        try {
            markerList = ArrayList()
            pointAnnotationManager?.deleteAll()
            for (i in repo){
                val bitmap = when(i.reportType){
                    1->{ convertDrawableToBitMap(AppCompatResources.getDrawable(app,R.drawable.dot_road_camera))}
                    2->{ convertDrawableToBitMap(AppCompatResources.getDrawable(app,R.drawable.dot_carcrash))}
                    5->{ convertDrawableToBitMap(AppCompatResources.getDrawable(app,R.drawable.dot_road_camera))}
                    6->{ convertDrawableToBitMap(AppCompatResources.getDrawable(app,R.drawable.dot_road_camera))}
                    3-> convertDrawableToBitMap(AppCompatResources.getDrawable(app, R.drawable.dot_police))
                    4-> convertDrawableToBitMap(AppCompatResources.getDrawable(app, R.drawable.dot_construction))
                    7-> convertDrawableToBitMap(AppCompatResources.getDrawable(app,R.drawable.dot_red_light_camera))
                    8-> convertDrawableToBitMap(AppCompatResources.getDrawable(app,R.drawable.dot_road_pathhole))
                    10-> convertDrawableToBitMap(AppCompatResources.getDrawable(app,R.drawable.dot_road_camera))
                    11-> convertDrawableToBitMap(AppCompatResources.getDrawable(app,R.drawable.dot_road_camera))
                    405-> convertDrawableToBitMap(AppCompatResources.getDrawable(app,R.drawable.dot_disable_camera))
                    406-> convertDrawableToBitMap(AppCompatResources.getDrawable(app,R.drawable.dot_disable_camera))
                    else-> convertDrawableToBitMap(AppCompatResources.getDrawable(app,R.drawable.dot_disable_camera))
                }
                val jsonObject = JSONObject()
                jsonObject.put("report",i.reportId)
                jsonObject.put("type",i.reportType)
                jsonObject.put("speed-limit",i.reportSpeedLimit)
                jsonObject.put("lat", i.geoLocation?.get(0) ?: 0.0)
                jsonObject.put("lon", i.geoLocation?.get(1) ?: 0.0)
                val pointAnnotationOptions : PointAnnotationOptions = PointAnnotationOptions()
                    .withPoint(Point.fromLngLat(i.geoLocation!![1] as Double, i.geoLocation!![0] as Double))
                    .withData(Gson().fromJson(jsonObject.toString(),JsonElement::class.java))
                    .withIconImage(bitmap)
                    .withIconSize(1.25)
                    .withIconOffset(listOf(0.0,10.0))
                    .withIconAnchor(IconAnchor.BOTTOM)
                markerList.add(pointAnnotationOptions)
            }

            pointAnnotationManager?.create(markerList)
        }catch (e:Exception){
            Log.d("VIEW_MODEL_LOGS",e.message.toString())
        }

    }
    private fun convertDrawableToBitMap(sourceDrawable : Drawable?) : Bitmap {
        return if (sourceDrawable is BitmapDrawable){
            sourceDrawable.bitmap
        }else{
            val  constatState = sourceDrawable?.constantState
            val drawable = constatState?.newDrawable()?.mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable?.intrinsicWidth ?: 0, drawable?.intrinsicHeight ?: 0,
                Bitmap.Config.ARGB_8888
            )
            val canvas = android.graphics.Canvas(bitmap)
            drawable?.setBounds(0,0,canvas.width,canvas.height)
            drawable?.draw(canvas)
            bitmap
        }
    }
    fun loadUserInfoFromFirebase(): Flow<ResponseDto> {
        return callbackFlow {
            try {
                val uid = auth.currentUser?.uid
                if (uid != null){
                    val docRef = db.collection(DB_REF_USER).document(uid)
                    docRef.get().addOnSuccessListener { task ->
                        if (task.data != null){
                            userInfo.value = task.toObject(UserNew::class.java)!!
                            trySend(ResponseDto(isSuccess = true,""))
                        }else{
                            trySend(ResponseDto(isSuccess = false,"Un error"))
                        }
                    }.addOnFailureListener {
                        trySend(ResponseDto(isSuccess = false,it.message.toString()))
                    }
                }else{
                    trySend(ResponseDto(isSuccess = false,"unauthorized"))
                }
            }catch (e:Exception){
                Log.d("DEBUG_HOME_VIEW_MODEL", "loadUserInfoFromFirebase() : ${e.message.toString()}")
                trySend(ResponseDto(isSuccess = false,"${e.message}"))
            }
            awaitClose{close()}
        }
    }
    fun vibrate(context :Context){
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(VibrationEffect.createOneShot(75, VibrationEffect.EFFECT_TICK))
    }
    private fun handleException(exception : Exception? = null , customMessage: String = "") {
        exception?.printStackTrace()
        val errorMsg = exception?.localizedMessage ?: ""
        val message = if (customMessage=="") errorMsg else "$customMessage : $errorMsg"
        responseMessage.value = Event(message)
    }
    private fun getAddress(lat: Double, lng: Double, context: Context): String {
        return try{
            val geocoder = Geocoder(context)
            val list = geocoder.getFromLocation(lat, lng, 1)
            list?.get(0)?.getAddressLine(0) ?: "-"

        }catch (e:Exception){
            handleException(exception = e,"Error")
            "Unknown"
        }
    }

    suspend fun getSingleReportForSharedLink(reportId: String): NewReport? {
        val docRef = db.collection(
            if (!isDebugMode.value!!) {
                DB_REF_REPORT
            } else {
                DB_REF_REPORT_DEBUG
            }
        ).document(reportId).get().await()
        return docRef.toObject(NewReport::class.java)
    }
    fun getSingleReport(reportId: String) :Flow<SingleCustomReport>  {
        return callbackFlow {
            val docRef = db.collection(if(!isDebugMode.value!!){DB_REF_REPORT}else{ DB_REF_REPORT_DEBUG }).document(reportId)

            val loc = Location("")
            var likeCont = 0
            var disLikeCount = 0
            var isLikedd : Boolean? = null
            try {
                val allFeedbacks = db.collection(if(!isDebugMode.value!!){DB_REF_REPORT}else{ DB_REF_REPORT_DEBUG }).document(reportId).collection("Feedback").get().await().documents
                val alertedCount = db.collection(if(!isDebugMode.value!!){DB_REF_REPORT}else{ DB_REF_REPORT_DEBUG }).document(reportId).collection("Alerted").get().await().documents.size
                allFeedbacks.forEach { feedback->
                    if (feedback.get("feedbackType") as Boolean){
                        likeCont +=1
                    }else{
                        disLikeCount +=1
                    }
                    if (feedback.get("feedbackByUID") as String == auth.currentUser?.uid){
                        isLikedd = feedback.get("feedbackType") as Boolean
                    }
                }
                val source = Source.CACHE
                docRef.get(source).addOnSuccessListener { task ->
                    val geoLocation = task.get("geoLocation") as List<*>?
                    if (geoLocation != null){
                        val lat = geoLocation[0] as Double
                        val lng = geoLocation[1] as Double
                        loc.latitude = lat
                        loc.longitude = lng
                        trySend(
                            SingleCustomReport(
                                reportByUID = task.data?.get("reportByUID") as String,
                                isSuccess = true,
                                reportLocation = loc,
                                isReportOwner = task.data?.get("reportByUID") as String == auth.currentUser?.uid,
                                reportTime = incidentTime(task.data?.get("reportTimeStamp") as Timestamp,app),
                                reportAddress = task.data?.get("reportAddress") as String,
                                reportType = (task.data?.get("reportType") as Long).toInt(),
                                reportSpeedLimit = (task.data?.get("reportSpeedLimit") as Long?)?.toInt(),
                                alertedCount = alertedCount,

                                feedbackLikeCount = likeCont,
                                feedbackDisLikeCount = disLikeCount,
                                isLiked = isLikedd
                            )
                        )
                    }
                }.addOnFailureListener {
                    Log.d("ERROR-55F", "${it.message}")
                    trySend(SingleCustomReport(isSuccess = false))
                }
            }catch (e:Exception){
                Log.d("DEBUG_USER_ID",e.message.toString())
                handleException(exception = e,"Error")
            }
            awaitClose{close()}
        }
    }
    suspend fun getReportOwnerByUid(uid:String):UserDao{
        return try {
            val userRef = db.collection(DB_REF_USER).document(uid).get().await()
            UserDao (
                userName = userRef.get("username") as? String? ?: "",
                userType = (userRef.get("userType") as? Long?)?.toInt() ?: 0
            )
        }catch (e:Exception){
            Log.d("DEBUG_HOME_VIEW_MODEL", "getReportOwnerByUid() : ${e.message.toString()}")
            UserDao()
        }

    }
    fun uploadPhotos(localUri : Uri) : Flow<ResponseDto>{
        return callbackFlow {
            try {
                auth.uid?.let { it1 ->
                    val imageRef = storage.reference.child("profile_images/${it1}/${localUri.lastPathSegment}")
                    val uploadTask  = imageRef.putFile(localUri)
                    uploadTask.addOnSuccessListener {
                        Log.i(ContentValues.TAG, "Image Uploaded $imageRef")
                        val downloadUrl = imageRef.downloadUrl
                        downloadUrl.addOnSuccessListener {
                                remoteUri ->
                            db.collection(DB_REF_USER).document(it1).update("userAvatar",remoteUri.toString()).addOnSuccessListener {
                                trySend(ResponseDto(isSuccess = true, message = "Image updated successfully."))
                            }.addOnFailureListener {
                                trySend(ResponseDto(isSuccess = false, message = "${it.message}"))
                            }
                        }
                    }
                    uploadTask.addOnFailureListener {e->
                        trySend(ResponseDto(isSuccess = false, message = "${e.message}"))
                    }
                }
            }catch (e:Exception){
                trySend(ResponseDto(isSuccess = false, message = "${e.message}"))
                Log.d("DEBUG_HOME_VIEW_MODEL", "updateReportLocation() : ${e.message.toString()}")
            }

            awaitClose{ close()}
        }

    }
    fun checkDeviceLocationSettings(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.isLocationEnabled
        } else {
            val mode: Int = Settings.Secure.getInt(
                context.contentResolver,
                Settings.Secure.LOCATION_MODE,
                Settings.Secure.LOCATION_MODE_OFF
            )
            mode != Settings.Secure.LOCATION_MODE_OFF
        }
    }
//    fun isLogin(): Boolean {
//        if (auth.currentUser != null){
//            return true
//        }
//        return false
//    }
    fun updateUserInfo(fullName: String, phone: String, email: String, gender: String) : Flow<ResponseDto> {
        return callbackFlow {
            try {
                auth.uid?.let {
                    val user : HashMap<String, Any> = HashMap<String, Any>()
                    user["username"] = fullName
                    user["userEmail"] = email
                    user["phoneNumber"] = phone
                    user["userGender"] = gender
                    db.collection(DB_REF_USER).document(it).update(user).addOnSuccessListener {
                        trySend(ResponseDto(isSuccess = true, message = "User Info updated successfully."))
                    }.addOnFailureListener {e->
                        trySend(ResponseDto(isSuccess = false, message = "${e.message}"))
                    }
                }
            }catch (e:Exception){
                trySend(ResponseDto(isSuccess = false, message = "${e.message}"))
                Log.d("DEBUG_HOME_VIEW_MODEL", "updateReportLocation() : ${e.message.toString()}")
            }
            awaitClose{close()}
        }
    }
    fun fetchReports(latt: Double, lonn: Double) {
        try {
            val scope = CoroutineScope(Dispatchers.Default)
            val radius = reportSightRadius.value

            val center =  GeoLocation(latt, lonn)
            val radiusInM = radius * 1000.0
            val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInM)
//            val tasks = arrayListOf<Task<QuerySnapshot>>()
            geofencingClient.removeGeofences(geofencePendingIntent)
            Log.d("BOUNDSSIZE",bounds.size.toString())
            val source = Source.CACHE
            for ((i, b) in bounds.withIndex()) {
                val q = db.collection(if(!isDebugMode.value!!){DB_REF_REPORT}else{ DB_REF_REPORT_DEBUG })
                    .orderBy("g")
                    .startAt(b.startHash)
                    .endAt(b.endHash)
//                tasks.add(q.get(source));
                when (i){
                    0->{
                        snap1?.remove()
                        snap1 = q.addSnapshotListener{ _, _ ->
                            Log.d("TESTGEOQURY_IMPO", "g: 1")
                            tempReports1.clear()
                            tempGeofence1.clear()
                            scope.launch {
                                dataToList(q.get(source),i,latt,lonn,bounds.size)
                            }
                        }
                    }
                    1->{
                        snap2?.remove()
                        snap2 = q.addSnapshotListener{ _, _ ->
                            Log.d("TESTGEOQURY_IMPO", "g: 1")
                            tempReports2.clear()
                            tempGeofence2.clear()
                            scope.launch {
                                dataToList(q.get(source),i,latt,lonn,bounds.size)
                            }
                        }
                    }
                    2->{
                        snap3?.remove()
                        snap3 = q.addSnapshotListener{ _, _ ->
                            Log.d("TESTGEOQURY_IMPO", "g: 1")
                            tempReports3.clear()
                            tempGeofence3.clear()
                            scope.launch {
                                dataToList(q.get(source),i,latt,lonn,bounds.size)
                            }
                        }
                    }
                    3->{
                        snap4?.remove()
                        snap4 = q.addSnapshotListener{ _, _ ->
                            Log.d("TESTGEOQURY_IMPO", "g: 1")
                            tempReports4.clear()
                            tempGeofence4.clear()
                            scope.launch {
                                dataToList(q.get(source),i,latt,lonn,bounds.size)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("LISTSIZE_HSpeedH",e.message.toString())
        }
    }
    private suspend fun dataToList(task : Task<QuerySnapshot>,i:Int, latitude: Double, longitude: Double,size:Int){
        try {
            val radius = reportSightRadius.value
            val center =  GeoLocation(latitude, longitude)
            val radiusInM = radius * 1000.0

            when(i){
                0->{
                    Log.d("TESTGEOQURY_IMPO", "F: 1")
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
                                report?.let { repo ->
                                    tempReports1.add(repo)
                                    if (report.reportType != 405 && report.reportType != 7&& report.reportType != 406) {
                                        tempGeofence1.add(
                                            Geofence.Builder()
                                                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                                .setRequestId("${report.reportId}")
                                                .setCircularRegion(
                                                    report.geoLocation?.get(0) as Double,
                                                    report.geoLocation?.get(1) as Double,
                                                    geofenceRadius.value?.toFloat() ?: 250f
                                                )

                                                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                                                .build()
                                        )
                                    }
                                }
                            }
                        }
                    }
                    isChangeInZone1.value = true
                }
                1->{
                    Log.d("TESTGEOQURY_IMPO", "F: 2")
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
                                report?.let { repo ->
                                    tempReports2.add(repo)
                                    if (report.reportType != 405 && report.reportType != 7&& report.reportType != 406) {
                                        tempGeofence2.add(
                                            Geofence.Builder()
                                                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                                .setRequestId("${report.reportId}")
                                                .setCircularRegion(
                                                    report.geoLocation?.get(0) as Double,
                                                    report.geoLocation?.get(1) as Double,
                                                    geofenceRadius.value?.toFloat() ?: 250f
                                                )
                                                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                                                .build()
                                        )
                                    }
                                }
                            }
                        }
                    }
                    isChangeInZone1.value = true
                }
                2->{
                    Log.d("TESTGEOQURY_IMPO", "F: 3")
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
                                report?.let { repo ->
                                    tempReports3.add(repo)
                                    if (report.reportType != 405 && report.reportType != 7 && report.reportType != 406) {
                                        tempGeofence3.add(
                                            Geofence.Builder()
                                                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                                .setRequestId("${report.reportId}")
                                                .setCircularRegion(
                                                    report.geoLocation?.get(0) as Double,
                                                    report.geoLocation?.get(1) as Double,
                                                    geofenceRadius.value?.toFloat() ?: 250f
                                                )
                                                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                                                .build()
                                        )
                                    }
                                }
                            }
                        }
                    }
                    isChangeInZone1.value = true
                }
                3->{
                    Log.d("TESTGEOQURY_IMPO", "F: 4")
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
                                report?.let { repo ->
                                    tempReports4.add(repo)
                                    if (report.reportType != 405 && report.reportType != 7&& report.reportType != 406) {
                                        tempGeofence4.add(
                                            Geofence.Builder()
                                                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                                .setRequestId("${report.reportId}")
                                                .setCircularRegion(
                                                    report.geoLocation?.get(0) as Double,
                                                    report.geoLocation?.get(1) as Double,
                                                    geofenceRadius.value?.toFloat() ?: 250f
                                                )
                                                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                                                .build()
                                        )
                                    }
                                }
                            }
                        }
                    }
                    isChangeInZone1.value = true
                }
            }
            Log.d("TESTGEOQURY_IMPO", "B: $size  |  $counter")
            if (size >= counter){
                allReports.clear()
                addGeofences(concatenate(tempGeofence1, tempGeofence2, tempGeofence3,tempGeofence4))
                allReports.addAll(concatenate(tempReports1, tempReports2, tempReports3,tempReports4))
                MyLocationService.LSR.allReports.addAll(concatenate(tempReports1, tempReports2, tempReports3,tempReports4))
//        model.createMarkerOnMap(concatenate(tempReports1, tempReports2, tempReports3,tempReports4))
        Log.d("TESTGEOQURY_IMPO","S: "+concatenate(tempReports1, tempReports2, tempReports3,tempReports4).size.toString())
                counter = 0
            }else{
                counter++
            }

        }catch (e:Exception){
            Log.d("ERROR-11232",e.message.toString())
        }
    }
    fun addReport(speedLimit:Int?,reportType: Int = 1) {
        viewModelScope.launch {
            if (ActivityCompat.checkSelfPermission(app, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(app, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener {
                    if (it.isComplete) {
                        if (it.result != null) {
                            if (!Utils.isMockLocationEnabled(it.result)){
                                if(placeReportValidation(reportCountPerOneHour.value, loadTimeOfLastReport.value?:0).isSuccess){
                                    addReport(bearing = it.result.bearing,geoPoint = GeoPoint(it.result.latitude,it.result.longitude), reportType = reportType, speedLimit = speedLimit)
                                }else{
                                    isReachedQuotaDialog.value = true
                                }
                            }else{
                                Toast.makeText(app,"Sorry unable, Please turn off mock location.", Toast.LENGTH_LONG).show()
                            }
                        }else{
                            Toast.makeText(app,"Sorry Unable", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }

        }
    }
    private fun addGeofences(geo: List<Geofence>) {
        if(geo.isNotEmpty()){
            Log.d("GEOBR","${geo.size} added success")
            geofencingClient.removeGeofences(geofencePendingIntent)
            GeofenceBroadcastReceiver.GBRS.add(null)
            if (ActivityCompat.checkSelfPermission(
                    app,
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
    fun getGeofencingRequest(reports: List<Geofence> = ArrayList()): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(reports)
        }.build()
    }
    val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(app, GeofenceBroadcastReceiver::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(app, 0, intent, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT  )
        } else {
            PendingIntent.getBroadcast(app, 0, intent,  PendingIntent.FLAG_UPDATE_CURRENT  )
        }
    }
    @OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("MissingPermission")
    fun getJsonAsync() = GlobalScope.async{
        try {
            if (MetricsUtils.isOnline(app)){
                val respo = URL("https://api.openweathermap.org/data/2.5/weather?lat=${lastLocation.value.latitude}&lon=${lastLocation.value.longitude}&appid=02c2bac30e0194dff2c04877257c322e").readText()
                val data = Gson().fromJson(respo,WeatherDto::class.java)
                temperature.value = (data.main?.temp?.minus(273))?.toInt()
                Log.d("OWAPID",data.name.toString() )
            }else{

            }
        }catch (e:Exception){
            Log.d("HOME_VIEW_MODEL_002",e.message.toString())
        }

    }
    fun getUid():String?{
        return auth.currentUser?.uid
    }

    fun saveAlerts(info : SaveUserInfoInAlerted){
        try {
            auth.currentUser?.let {
                val alert : HashMap<String, Any> = HashMap<String, Any>()
                alert["lastSeen"] = FieldValue.serverTimestamp()
                alert["speedWhenEntered"] = speed.value
                alert["userAvatar"] = info.userAvatar
                alert["userId"] = it.uid
                alert["username"] = info.userName
                db.collection(if(!isDebugMode.value!!){DB_REF_REPORT}else{ DB_REF_REPORT_DEBUG }).document(info.reportId).collection(Constants.DB_REF_ALERTED).document(it.uid).set(alert)
            }
        }catch (e:Exception){
            Log.d("HOME_VIEW_MODEL_002",e.message.toString())
        }

    }

    private suspend fun setUserStatistics(): Flow<ResponseDto> {
        return callbackFlow {
            try {
                ds.retrieveStatistics().collect{statistic->
                    auth.currentUser?.let {user->
                        val statisticHashMap : HashMap<String, Any> = HashMap<String, Any>()
                        statisticHashMap["alertedTime"] = statistic.alertedCount
                        statisticHashMap["maxSpeed"] = statistic.maxSpeed
                        statisticHashMap["traveldDistance"] = statistic.traveledDistance
                        statisticHashMap["latestUpdate"] = FieldValue.serverTimestamp()
                        db.collection(DB_REF_USER)
                            .document(user.uid)
                            .collection("statistic")
                            .document("GeneralStats")
                            .update(statisticHashMap)
                            .addOnSuccessListener {
                                trySend(ResponseDto(isSuccess = true, message = "Success"))
                            }.addOnFailureListener {
                                trySend(ResponseDto(isSuccess = false, message = it.message.toString()))
                            }
                    }
                }
            }catch (e:Exception){
                trySend(ResponseDto(isSuccess = false, message = e.message.toString()))
                Log.d("HOME_VIEW_MODEL_002",e.message.toString())
                throw e
            }
            awaitClose{close()}
        }
    }
    private suspend fun resetStatistics(){
        viewModelScope.launch(Dispatchers.IO) {
            ds.retrieveStatistics().collect{
                ds.saveStatistics(uStatistics = UserStatistics(
                    alertedCount = 0,
                    traveledDistance =0f ,
                    maxSpeed =0
                ))
            }
        }
    }
    suspend fun getUserInformation():UserInformation {
        val userInformation = db.collection(DB_REF_USER).document(auth.currentUser?.uid!!).get().await()
        val username = userInformation.get("username") as? String?
        val userAvatar = userInformation.get("userAvatar") as? String? ?: ""
        val userType = (userInformation.get("userType") as? Long? ?: 0).toInt()
        saveUserType(userType = userType)

        return UserInformation(
            userName = username,
            userAvatar = userAvatar
        )
    }
    fun extendReportTime(reportId: String) {
        val report : HashMap<String, Any> = HashMap<String, Any>()
        report["reportTimeStamp"] = Timestamp.now()
        db.collection(if(!isDebugMode.value!!){DB_REF_REPORT}else{ DB_REF_REPORT_DEBUG }).document(reportId).update(report)
    }
    fun completeUserRegister(userName:String) : Flow<ResponseDto> {
        return callbackFlow {
            try {
                if (userName.length in 2..31){
//                if(containsSpecialCharacters(userName)){
                    if (!startsWithCee(userName)){
                        auth.uid?.let { uID ->
                            val user : HashMap<String, Any> = HashMap<String, Any>()
                            user["username"] = userName
                            db.collection(DB_REF_USER).document(uID).update(user).addOnSuccessListener {
                                trySend(ResponseDto(isSuccess = true, message = "User Info updated successfully."))
                            }.addOnFailureListener {exception->
                                trySend(ResponseDto(isSuccess = false, message = "${exception.message}"))
                            }
                        }
                    }else{
                        trySend(ResponseDto(isSuccess = false, message = "invalid full name"))
                    }
//                }else{
//                    trySend(ResponseDto(isSuccess = false, serverMessage = "invalid full name"))
//                }
                }else{
                    trySend(ResponseDto(isSuccess = false, message = "full name length invalid."))
                }
            }catch (e:Exception){
                trySend(ResponseDto(isSuccess = false, message = "${e.message}"))
                Log.d("DEBUG_HOME_VIEW_MODEL", "updateReportLocation() : ${e.message}")
            }

            awaitClose{close()}
        }
    }
    fun updateReportSpeedLimit(speedLimit: Int?,reportId: String): Flow<ResponseDto> {
        return callbackFlow {
            try {
                if (speedLimit != null && reportId != ""){
                    db.collection(if(!isDebugMode.value!!){DB_REF_REPORT}else{ DB_REF_REPORT_DEBUG }).document(reportId).update("reportSpeedLimit",speedLimit).addOnSuccessListener {
                        trySend(ResponseDto(isSuccess = true, message = "Speed limit updated successfully to $speedLimit."))
                    }.addOnFailureListener {exception->
                        trySend(ResponseDto(isSuccess = false, message = "${exception.message}"))
                    }
                }else{
                    trySend(ResponseDto(isSuccess = false, message = "input Invalid or empty"))
                }
            }catch (e:Exception){
                trySend(ResponseDto(isSuccess = false, message = "${e.message}"))
                Log.d("DEBUG_HOME_VIEW_MODEL", "updateReportLocation() : ${e.message.toString()}")
            }

            awaitClose{close()}
        }
    }

    fun changeDebugMode(isEnable:Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            isDebugMode.postValue(isEnable)
            ds.debugModeSave(isEnable)
        }
    }
    fun changeScreenStatus(isEnable: Boolean? = null,time : Float? = null){
        viewModelScope.launch(Dispatchers.IO) {
            ds.saveAppSetting(isEnable = isEnable,time = time)
        }
    }
    private fun retrieveIsDebugMode(){
        viewModelScope.launch(Dispatchers.IO) {
            ds.retrieveDebugMode().collect{
                isDebugMode.postValue(it)
            }
        }
    }
    fun retrieveIsPreventScreenSleep(){
        viewModelScope.launch(Dispatchers.IO) {
            ds.retrieveAppSetting().collect{
                appSetting.postValue(it)
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.S)
    fun getReportsAndAddGeofences()  {
        if (ActivityCompat.checkSelfPermission(
                app.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                app.applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationProviderClient.lastLocation.addOnCompleteListener{
            if (it.isComplete){
                if (it.result == null) {
                    isLocationNotAvailable.value = true
                }else{
                    if (MetricsUtils.isOnline(app.applicationContext)){
                        fetchReports(it.result.latitude,it.result.longitude)
                    }
                }
            }
        }
        Log.d("TES-gRAAG","set false")
    }
    fun getMyReports() {
//        try {
//            auth.currentUser.let {
//                db.collection(DB_REF_REPORT).whereEqualTo("reportByUID", it?.uid).get().addOnSuccessListener { querySnapshot->
//                    val tempReports = ArrayList<SingleCustomReport>()
//                    querySnapshot.documents.forEach {ducument ->
//                        val likeCont = 0
//                        val disLikeCount = 0
//
////                        val allFeedbacks = db.collection(if(!isDebugMode.value!!){DB_REF_REPORT}else{ DB_REF_REPORT_DEBUG }).document(ducument.id).collection("Feedback").get().result.documents
////                        val alertedCount = db.collection(if(!isDebugMode.value!!){DB_REF_REPORT}else{ DB_REF_REPORT_DEBUG }).document(ducument.id).collection("Alerted").get().result.documents.size
////                        allFeedbacks.forEach { feedback->
////                            if (feedback.get("feedbackType") as Boolean){
////                                likeCont +=1
////                            }else{
////                                disLikeCount +=1
////                            }
////                        }
//
//                        val geoLocation = ducument.get("geoLocation") as List<*>?
//                        if (geoLocation != null) {
//                            val tempReportLocation = Location("")
//                            val lat = geoLocation[0] as Double
//                            val lng = geoLocation[1] as Double
//
//                            tempReportLocation.latitude = lat
//                            tempReportLocation.longitude = lng
//
//                            tempReports.add(SingleCustomReport(
//                                isSuccess = true,
//                                reportLocation = tempReportLocation,
//                                reportTime = incidentTime(ducument.data?.get("reportTimeStamp") as Timestamp,app),
//                                reportAddress = ducument.data?.get("reportAddress") as String,
//                                reportType = (ducument.data?.get("reportType") as Long).toInt(),
//                                reportSpeedLimit = (ducument.data?.get("reportSpeedLimit") as Long?)?.toInt(),
//                                alertedCount = 0,
//
//                                feedbackLikeCount = likeCont,
//                                feedbackDisLikeCount = disLikeCount,
//                                reportId = ducument.id
//                                ))
//
//                        }
//                    }.let {
//                        myReports.addAll(tempReports)
//                    }
//
//                }
//                db.collection(DB_REF_ARCHIVE_REPORT).whereEqualTo("reportByUID", it?.uid).get().addOnSuccessListener {querySnapshott->
//                    val tempReports = ArrayList<SingleCustomReport>()
//                    querySnapshott.documents.forEach {ducument ->
//
//                        val likeCont = 0
//                        val disLikeCount = 0
//
////                        val allFeedbacks = db.collection(if(!isDebugMode.value!!){DB_REF_ARCHIVE_REPORT}else{ DB_REF_REPORT_DEBUG }).document(ducument.id).collection("Feedback").get().result.documents
////                        val alertedCount = db.collection(if(!isDebugMode.value!!){DB_REF_ARCHIVE_REPORT}else{ DB_REF_REPORT_DEBUG }).document(ducument.id).collection("Alerted").get().result.documents.size
////                        allFeedbacks.forEach { feedback->
////                            if (feedback.get("feedbackType") as Boolean){
////                                likeCont +=1
////                            }else{
////                                disLikeCount +=1
////                            }
////                        }
//
//                        val geoLocation = ducument.get("geoLocation") as List<*>?
//                        if (geoLocation != null) {
//                            val tempReportLocation = Location("")
//                            val lat = geoLocation[0] as Double
//                            val lng = geoLocation[1] as Double
//
//                            tempReportLocation.latitude = lat
//                            tempReportLocation.longitude = lng
//
//                            tempReports.add(SingleCustomReport(
//                                isSuccess = true,
//                                isActive = 0,
//                                reportLocation = tempReportLocation,
//                                reportTime = incidentTime(ducument.data?.get("reportTimeStamp") as Timestamp,app),
//                                reportAddress = ducument.data?.get("reportAddress") as String,
//                                reportType = (ducument.data?.get("reportType") as Long).toInt(),
//                                reportSpeedLimit = (ducument.data?.get("reportSpeedLimit") as Long?)?.toInt(),
//                                alertedCount = 0,
//
//                                feedbackLikeCount = likeCont,
//                                feedbackDisLikeCount = disLikeCount,
//                                reportId = ducument.id
//                            ))
//
//                        }
//                    }.let {
//                        myReports.addAll(tempReports)
//                    }
//
//                }.let {
//                    myReports.sortBy { ito -> ito.isActive }
//                    //trySend(ResponseWithData(isSuccess = true, data = myReports, serverMessage = null))
//                }
//
//            }
//
//        } catch (e: Exception) {
//            //trySend(ResponseWithData(isSuccess = false, data = null, serverMessage = e.message))
//        }

    }


//    private fun containsSpecialCharacters(str: String): Boolean {
//        val pp = Pattern.compile("[^A-Za-z]")
//        val mm = pp.matcher(str)
//        val pattern = "[A-Za-z0-9]"
//        Log.d("ISMUTCH_VALID",mm.find().toString())
//        return str.matches(pattern.toRegex())
//    }
    private fun startsWithCee(str: String): Boolean {
        val prefix = "cee"
        return str.startsWith(prefix)
    }
    suspend fun logout(context: Context)  {
        setUserStatistics().collect{
            if (it.isSuccess){
                delay(3000)
                resetStatistics()
                auth.signOut()
                context.startActivity(Intent(context, MainActivity::class.java))
//                trySend(ResponseDto(isSuccess = true,"Success"))
            }
        }

    }
    fun incidentTime(stamp : Timestamp,context: Context) : String{
        val mines = (Timestamp.now().seconds - stamp.seconds)/60
        Log.d("DEBUG_TIME_INCIDENT",mines.toString())
        return try {
//            val sdf = SimpleDateFormat("MMMM dd, yyyy",Locale.US)
//            val netDate = Date((stamp.seconds * 1000))
            if(mines > 60.0){
                if ((mines/1440.0) > 1.0){
                    if ((mines/43200.0) >1.0){
                        "${mines/43200} ${context.getString(R.string.lbl_nearestIncedednts_monthAgo)}"
                    }else{
                        "${mines/1440} ${context.getString(R.string.lbl_nearestIncedents_dayAgo)}"
                    }
                }else{
                    "${mines/60} ${context.getString(R.string.lbl_nearestIncedednts_hourAgo)}"
                }
            }else{
                "$mines ${context.getString(R.string.lbl_nearestIncedednts_minuteAgo)}"
//            "${sdf.format(netDate)} • $mines ${context.getString(R.string.lbl_nearestIncedednts_minuteAgo)}"
            }
        } catch (e: Exception) {
            e.toString()
        }

    }

    fun saveSpeedometerId(id: String) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveSpeedometerId(id)
        }
    fun saveCursorId(id: String) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveCursorId(id)
        }

    fun isGuest(): Boolean {
        return auth.currentUser == null
    }
}
//fun incidentTime(stamp : Timestamp,context: Context) : String{
//    val mines = (Timestamp.now().seconds - stamp.seconds)/60
//    Log.d("DEBUG_TIME_INCIDENT",mines.toString())
//    return try {
//        val sdf = SimpleDateFormat("MMMM dd, yyyy",Locale.US)
//        val netDate = Date((stamp.seconds * 1000))
//        if(mines > 60.0){
//            if ((mines/1440.0) > 1.0){
//                if ((mines/43200.0) >1.0){
//                    "${mines/43200} ${context.getString(R.string.lbl_nearestIncedednts_monthAgo)}"
//                }else{
//                    "${mines/1440} ${context.getString(R.string.lbl_nearestIncedents_dayAgo)}"
//                }
//            }else{
//                "${mines/60} ${context.getString(R.string.lbl_nearestIncedednts_hourAgo)}"
//            }
//        }else{
//            "$mines ${context.getString(R.string.lbl_nearestIncedednts_minuteAgo)}"
////            "${sdf.format(netDate)} • $mines ${context.getString(R.string.lbl_nearestIncedednts_minuteAgo)}"
//        }
//    } catch (e: Exception) {
//        e.toString()
//    }
//
//}
//fun incidentDistance(distance : Float) : String{
//    val df = DecimalFormat("#.##")
//    df.roundingMode = RoundingMode.DOWN
//    return if (distance > 1000){
//        "${df.format(distance/1000)} KM"
//    }else{
//        "${df.format(distance)} M"
//    }
//}


data class UserDao(
    var userName: String = "Cee",
    var userType: Int = 2,
)
data class SaveUserInfoInAlerted(
    var userName: String = "",
    var userAvatar: String = "",
    var reportId: String = "",
)
data class UserInformation(
    var userName: String? = null,
    var userAvatar: String = "",
)




//    private fun getOnlineUserCount(){
//        db.collection(DB_REF_USER).whereEqualTo("status", "online").addSnapshotListener { value, error ->
//            onlineUserCounter.value =  value?.documents?.size ?:
//            0
//            Log.d("DEBUG_ONLINE_USER",onlineUserCounter.value.toString())
//        }
//        //return db.collection(DB_REF_USER).whereEqualTo("status", "online").get().await().documents.size
//    }
//    private fun getOnlineUserCount(){
//    val postListener = object : ValueEventListener {
//        override fun onDataChange(dataSnapshot: DataSnapshot) {
//            onlineUserCounter.value = ((dataSnapshot.value) as? Long?
//                ?: 0L).toInt()
//            Log.d("Debug_realtime_firebase", "Got value ${dataSnapshot.value}")
//        }
//        override fun onCancelled(databaseError: DatabaseError) {
//            Log.d("Debug_realtime_firebase", "loadPost:onCancelled", databaseError.toException())
//        }
//    }
//    rdbRef.child("count").addValueEventListener(postListener)
//    }

//    fun restStatistics() =
//        viewModelScope.launch(Dispatchers.IO) {
//            dataStoreRepository.resetStatistics()
//        }
//suspend fun verifyUserByUid(uid: String) {
//    val user : HashMap<String, Any> = HashMap<String, Any>()
//    user["userType"] = 3
//    db.collection(DB_REF_USER).document(uid).update(user).await()
//}