package com.orbital.cee.view.home
import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
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
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point

import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.annotation.AnnotationConfig
import com.mapbox.maps.plugin.annotation.AnnotationPlugin
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
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
import com.orbital.cee.data.Event
import com.orbital.cee.data.repository.DSRepositoryImpl
import com.orbital.cee.data.repository.DataStoreRepository
import com.orbital.cee.data.repository.UserStatistics
import com.orbital.cee.model.*
import com.orbital.cee.utils.MetricsUtils
import com.orbital.cee.utils.Utils
import com.orbital.cee.view.MainActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import java.math.RoundingMode
import java.net.URL
import java.text.DecimalFormat
import java.text.SimpleDateFormat
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
    val readMaxSpeed = dataStoreRepository.readMaxSpeed.asLiveData()
    val readAlertsCount = dataStoreRepository.readAlertsCount.asLiveData()
    val readDistance = dataStoreRepository.readDistance.asLiveData()
    val soundStatus = dataStoreRepository.readSoundStatus.asLiveData()
    val userType = dataStoreRepository.readUserType.asLiveData()
    val reportCountPerOneHour : LiveData<Int> = dataStoreRepository.reportCountPerOneHour.asLiveData()
    val trips = dataStoreRepository.tripList.asLiveData()
    val langCode = dataStoreRepository.languageCode.asLiveData()
    val geofenceRadius = dataStoreRepository.readGeofenceRadius.asLiveData()
    var appLaunchTime = mutableStateOf<Date?>(null)
    var userInfo = mutableStateOf(UserNew())
    var trip = mutableStateOf(Trip())
    var isCameraMove = mutableStateOf(true)
    var showCustomDialogWithResult = mutableStateOf(false)
    var geofencingClient : GeofencingClient = LocationServices.getGeofencingClient(app)

    var userStatistics : MutableLiveData<UserStatistics> = MutableLiveData(UserStatistics(0,0f,0))

    var isReachedQuotaDialog = mutableStateOf(false)
    var isDebugMode :MutableLiveData<Boolean> = MutableLiveData(false)

    var isDeleteReportRequested =  mutableStateOf(false)
    var lastLocation = mutableStateOf(Location(""))
    var isTripStarted = mutableStateOf(false)
    var isDarkMode = mutableStateOf(false)
//    var isPurchasedAdRemove = mutableStateOf(false)
    var reportClicked = mutableStateOf(false)
    var isShowDots = mutableStateOf(false)
    var isCameraZoomChanged = mutableStateOf(false)
    val speed = mutableStateOf(0)
    val onlineUserCounter = mutableStateOf(0)
    val speedPercent = mutableStateOf(0.0f)
    var whichButtonClicked = mutableStateOf(0)
    val slider = mutableStateOf(false)
    val inSideReportToast = mutableStateOf(false)
    val inSideReport = mutableStateOf(false)
    val isDarkModeEnabled = mutableStateOf(false)
    var reportId = mutableStateOf("")
    val myReports = mutableListOf<NewReport>()
    var temperature = mutableStateOf<Int?>(null)
    var tripDurationInSeconds = mutableStateOf<Int?>(0)
    private val responseMessage = mutableStateOf<Event<String>?>(null)
    var isLocationNotAvailable = mutableStateOf(false)
    var fusedLocationProviderClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(app)
    val isLiked = mutableStateOf<Boolean?>(null)
    val allReports = ArrayList<NewReport>()
    var annotationApi : AnnotationPlugin? = null
    var annotationApii : AnnotationPlugin? = null
    var markerList : ArrayList<PointAnnotationOptions> = ArrayList()
    var pointAnnotationManager : PointAnnotationManager? = null
    var pointAnnotationManagerr : PointAnnotationManager? = null
    lateinit var annotationConfig : AnnotationConfig
    lateinit var annotationConfigg : AnnotationConfig

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



//    init {
//        viewModelScope.launch(Dispatchers.IO) {
//            getUserStatistics().collect{
//                Log.d("DEBUG_STATISTICS_VM",it.maxSpeed.toString())
//            }
//        }
//
//    }


    private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener {
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().bearing(it).build())

    }
    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(it).build())
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
    init {
        retrieveIsDebugMode()
    }
    fun saveStatistics(alertedCount:Int, traveledDistance : Float, _maxSpeed: Int){
        viewModelScope.launch(Dispatchers.IO) {
            ds.retrieveStatistics().collect{
                ds.saveStatistics(uStatistics = UserStatistics(
                    alertedCount = it.alertedCount + alertedCount,
                    traveledDistance =it.traveledDistance +traveledDistance ,
                    maxSpeed =it.maxSpeed + _maxSpeed
                ))
            }
        }
    }
    suspend fun retrieveStatistics(){
        viewModelScope.launch(Dispatchers.IO) {
            ds.retrieveStatistics().collect{
                userStatistics.postValue(it)
            }
        }
    }


//    var style: Style? = null
    fun initLocationComponent() {
        val locationComponentPlugin =  mapView.location
        locationComponentPlugin.updateSettings {
            this.enabled = true
            this.pulsingEnabled = false
            this.pulsingMaxRadius = 50f
            this.pulsingColor = R.color.primary
            this.locationPuck = LocationPuck2D(
                topImage = AppCompatResources.getDrawable(
                    app,
                    R.drawable.ic_user_puck_new,
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
        mapView.camera.addCameraZoomChangeListener { dd ->
            Log.d("DEBUG_CAMERA_ZOOM", dd.toString())
            isShowDots.value = dd <= 12.0
        }
    }
    fun removeGesturesListener() {
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }
    fun addReport(geoPoint: GeoPoint,
                  reportType:Int,
                  time: Timestamp = Timestamp.now(),
                  speedLimit:Int? = null,
                  address : String? = null
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
                }
                report["reportByUID"] = it.uid
                document.set(report).await()
                idReportInUserDocument.set(reportt).await()
            }



        } catch (e: Exception) {

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

        }
    }
    fun deleteUser() = viewModelScope.launch {
        auth.currentUser?.delete()?.addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("TAG", "User account deleted.")
            }
        }
    }
    fun deleteReport(bookId: String) = viewModelScope.launch {
        db.collection(if(!isDebugMode.value!!){DB_REF_REPORT}else{ DB_REF_REPORT_DEBUG }).document(bookId).delete().await()
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
    fun saveMaxSpeed(speed: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveMaxSpeed(speed)
        }
    fun addAlertCount(alertCount : Int) =
        viewModelScope.launch(Dispatchers.IO) {
            val temp = alertCount.plus(1)
            dataStoreRepository.addAlertCount(temp)
        }
    fun saveUserType(userType : Int) =
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
    fun addDistance(distanceKM: Float) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.addDistance(distanceKM)
        }
    fun updateSoundStatus(soundStatusId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.updateSoundPreferences(soundStatusId)
        }
    suspend fun getMyReportCount() : Int{
        try {
            auth.currentUser?.let {
                var a = 0
                a += db.collection(if(!isDebugMode.value!!){DB_REF_REPORT}else{ DB_REF_REPORT_DEBUG }).whereEqualTo("reportByUID", it.uid).get().await().documents.size
//                a += db.collection(DB_REF_ARCHIVE_REPORT).whereEqualTo("reportByUID", it.uid).get().await().documents.size

                Log.d("PrintGetMyReportCount_Error",a.toString())
                return a
            }
            return -1
        }catch (e:Exception){
            Log.d("PrintGetMyReportCount_Error",e.message.toString())
            return 66
        }
    }


     fun placeReportValidation(reportCounterVal : Int, lastReportPlaceTime : Long ):Boolean{
        return if (userType.value == 2 || userType.value == 1){
             true
        }else{
            if (Timestamp.now().seconds.minus(lastReportPlaceTime) < 360){
                 if(reportCounterVal > 2){
                    Log.d("DEBUG_VALIDATING_REPORT","1: "+reportCounterVal +" "+ loadTimeOfLastReport.value.toString(),)
                    false
                }else{
                    Log.d("DEBUG_VALIDATING_REPORT","2: "+reportCounterVal +" "+ loadTimeOfLastReport.value.toString(),)
                    saveTheTimeOfTheLastReport()
                    incrementReportCountPerOneHour(reportCounterVal)
                    true
                }
            }else{
                Log.d("DEBUG_VALIDATING_REPORT","3: "+reportCounterVal +" "+ loadTimeOfLastReport.value.toString(),)
                saveTheTimeOfTheLastReport()
                resetIncrementedReportCounterPerOneHour()
                 true
            }
        }
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
//        lateinit var viewAnnotation: View
//        pointAnnotationManagerr?.addDragListener(object : OnPointAnnotationDragListener{
//            override fun onAnnotationDrag(annotation: Annotation<*>) {
//                mapView.viewAnnotationManager.updateViewAnnotation(
//                    viewAnnotation,
//                    viewAnnotationOptions {
//                        geometry(annotation.geometry)
//                    }
//                )
//            }
//
//            override fun onAnnotationDragFinished(annotation: Annotation<*>) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onAnnotationDragStarted(annotation: Annotation<*>) {
//                TODO("Not yet implemented")
//            }
//
//        })
        val pointAnnotationOptions : PointAnnotationOptions = PointAnnotationOptions()
            .withPoint(Point.fromLngLat(lon, lat))
            .withIconSize(1.25)
            .withIconOffset(listOf(0.0,0.0))
            .withIconAnchor(IconAnchor.BOTTOM)
//            .withDraggable(true)
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
            pointAnnotationManager?.addClickListener(OnPointAnnotationClickListener {
                    annotation : PointAnnotation ->
                onClickReport(annotation)
                true
            })
            for (i in repo){
                val bitmap = when(i.reportType){

                    2->{ convertDrawableToBitMap(AppCompatResources.getDrawable(app,R.drawable.marker_crash))}
                    3-> {
                        convertDrawableToBitMap(
                            AppCompatResources.getDrawable(
                                app,
                                R.drawable.marker_police
                            )
                        )
                    }
                    4-> {
                        convertDrawableToBitMap(
                            AppCompatResources.getDrawable(
                                app,
                                R.drawable.marker_construction
                            )
                        )
                    }
                    5-> convertDrawableToBitMap(AppCompatResources.getDrawable(app,
                        when(i.reportSpeedLimit){
                            60->{R.drawable.marker_static_camera_60}
                            100->{R.drawable.marker_static_camera_100}
                            50->{R.drawable.marker_static_camera_50}

                            0->{R.drawable.marker_static_camera_0}
                            10->{R.drawable.marker_static_camera_10}
                            15->{R.drawable.marker_static_camera_15}
                            20->{R.drawable.marker_static_camera_20}
                            25->{R.drawable.marker_static_camera_25}
                            30->{R.drawable.marker_static_camera_30}
                            35->{R.drawable.marker_static_camera_35}
                            40->{R.drawable.marker_static_camera_40}
                            45->{R.drawable.marker_static_camera_45}
                            55->{R.drawable.marker_static_camera_55}
                            65->{R.drawable.marker_static_camera_65}
                            70->{R.drawable.marker_static_camera_70}
                            80->{R.drawable.marker_static_camera_80}
                            90->{R.drawable.marker_static_camera_90}
                            110->{R.drawable.marker_static_camera_110}
                            120->{R.drawable.marker_static_camera_120}
                            130->{R.drawable.marker_static_camera_130}
                            140->{R.drawable.marker_static_camera_140}
                            else->{R.drawable.marker_static_camera_0}
                        }

                    ))
                    6-> convertDrawableToBitMap(AppCompatResources.getDrawable(app,
                        when(i.reportSpeedLimit){
                            60->{R.drawable.marker_point_to_point_60}
                            100->{R.drawable.marker_point_to_point_100}
                            50->{R.drawable.marker_point_to_point_50}

                            0->{R.drawable.marker_point_to_point_0}
                            10->{R.drawable.marker_point_to_point_10}
                            15->{R.drawable.marker_point_to_point_15}
                            20->{R.drawable.marker_point_to_point_20}
                            25->{R.drawable.marker_point_to_point_25}
                            30->{R.drawable.marker_point_to_point_30}
                            35->{R.drawable.marker_point_to_point_35}
                            40->{R.drawable.marker_point_to_point_40}
                            45->{R.drawable.marker_point_to_point_45}
                            55->{R.drawable.marker_point_to_point_55}
                            65->{R.drawable.marker_point_to_point_65}
                            70->{R.drawable.marker_point_to_point_70}
                            80->{R.drawable.marker_point_to_point_80}
                            90->{R.drawable.marker_point_to_point_90}
                            110->{R.drawable.marker_point_to_point_110}
                            120->{R.drawable.marker_point_to_point_120}
                            130->{R.drawable.marker_point_to_point_130}
                            140->{R.drawable.marker_point_to_point_140}
                            else->{R.drawable.marker_point_to_point_0}
                        }))
                    7-> convertDrawableToBitMap(AppCompatResources.getDrawable(app,R.drawable.ic_red_traffic_tight))
                    10-> convertDrawableToBitMap(AppCompatResources.getDrawable(app,R.drawable.ic_marker_road_static_camera))
                    11-> convertDrawableToBitMap(AppCompatResources.getDrawable(app,R.drawable.marker_point_to_point_0))
                    405-> convertDrawableToBitMap(AppCompatResources.getDrawable(app,R.drawable.marker_static_camera_not_active))
                    1-> convertDrawableToBitMap(AppCompatResources.getDrawable(app,
                        when(i.reportSpeedLimit){
                            60->{
                                R.drawable.marker_road_camera_60
                            }
                            100->{R.drawable.marker_road_camera_100}
                            50->{R.drawable.marker_road_camera_50}

                            0->{R.drawable.marker_road_camera_0}
                            10->{R.drawable.marker_road_camera_10}
                            15->{R.drawable.marker_road_camera_15}
                            20->{R.drawable.marker_road_camera_20}
                            25->{R.drawable.marker_road_camera_25}
                            30->{R.drawable.marker_road_camera_30}
                            35->{R.drawable.marker_road_camera_35}
                            40->{R.drawable.marker_road_camera_40}
                            45->{R.drawable.marker_road_camera_45}
                            55->{R.drawable.marker_road_camera_55}
                            65->{R.drawable.marker_road_camera_65}
                            70->{R.drawable.marker_road_camera_70}
                            80->{R.drawable.marker_road_camera_80}
                            90->{R.drawable.marker_road_camera_90}
                            110->{R.drawable.marker_road_camera_110}
                            120->{R.drawable.marker_road_camera_120}
                            130->{R.drawable.marker_road_camera_130}
                            140->{R.drawable.marker_road_camera_140}


                            else->{R.drawable.marker_road_camera_0}
                        }

                    ))
                    else-> convertDrawableToBitMap(AppCompatResources.getDrawable(app,R.drawable.ic_cee_one))
                }
                val jsonObject = JSONObject()
                jsonObject.put("report",i.reportId)
                jsonObject.put("lat", i.geoLocation?.get(0) ?: 0.0)
                jsonObject.put("lon", i.geoLocation?.get(1) ?: 0.0)
                val pointAnnotationOptions : PointAnnotationOptions = PointAnnotationOptions()
                    .withPoint(Point.fromLngLat(i.geoLocation!![1] as Double, i.geoLocation!![0] as Double))
                    .withData(Gson().fromJson(jsonObject.toString(),JsonElement::class.java))
                    .withIconImage(bitmap)
                    .withIconSize(0.75)
                    .withIconOffset(listOf(0.0,10.0))
                    .withIconAnchor(IconAnchor.BOTTOM)

                //.withIconImage("marker_point_to_point_01")
                markerList.add(pointAnnotationOptions)
            }

            pointAnnotationManager?.create(markerList)
            }
        }catch (e:Exception){

        }
    }
    private fun createDotMarkerOnMap(repo : List<NewReport>){
        try {
            markerList = ArrayList()
            pointAnnotationManager?.deleteAll()
//            pointAnnotationManager?.addClickListener(OnPointAnnotationClickListener {
//                    annotation : PointAnnotation ->
//                onClickReport(annotation)
//                true
//            })
            for (i in repo){
                val bitmap = when(i.reportType){
                    1->{ convertDrawableToBitMap(AppCompatResources.getDrawable(app,R.drawable.dot_road_camera))}
                    2->{ convertDrawableToBitMap(AppCompatResources.getDrawable(app,R.drawable.dot_carcrash))}
                    5->{ convertDrawableToBitMap(AppCompatResources.getDrawable(app,R.drawable.dot_road_camera))}
                    6->{ convertDrawableToBitMap(AppCompatResources.getDrawable(app,R.drawable.dot_road_camera))}
                    3-> convertDrawableToBitMap(AppCompatResources.getDrawable(app, R.drawable.dot_police))
                    4-> convertDrawableToBitMap(AppCompatResources.getDrawable(app, R.drawable.dot_construction))
                    7-> convertDrawableToBitMap(AppCompatResources.getDrawable(app,R.drawable.dot_red_light_camera))
                    10-> convertDrawableToBitMap(AppCompatResources.getDrawable(app,R.drawable.dot_road_camera))
                    11-> convertDrawableToBitMap(AppCompatResources.getDrawable(app,R.drawable.dot_road_camera))
                    405-> convertDrawableToBitMap(AppCompatResources.getDrawable(app,R.drawable.dot_disable_camera))
                    else-> convertDrawableToBitMap(AppCompatResources.getDrawable(app,R.drawable.dot_disable_camera))
                }
                val jsonObject = JSONObject()
                jsonObject.put("report",i.reportId)
                jsonObject.put("lat", i.geoLocation?.get(0) ?: 0.0)
                jsonObject.put("lon", i.geoLocation?.get(1) ?: 0.0)
                val pointAnnotationOptions : PointAnnotationOptions = PointAnnotationOptions()
                    .withPoint(Point.fromLngLat(i.geoLocation!![1] as Double, i.geoLocation!![0] as Double))
                    .withData(Gson().fromJson(jsonObject.toString(),JsonElement::class.java))
                    .withIconImage(bitmap)
                    .withIconSize(1.25)
                    .withIconOffset(listOf(0.0,10.0))
                    .withIconAnchor(IconAnchor.BOTTOM)

                //.withIconImage("marker_point_to_point_01")
                markerList.add(pointAnnotationOptions)
            }

            pointAnnotationManager?.create(markerList)
        }catch (e:Exception){

        }

    }
    private fun onClickReport(annotation: PointAnnotation) {

        reportClicked.value = true
        val reportArray = annotation.getData()?.asJsonObject
        val repo = reportArray?.get("report")
        val lat = reportArray?.get("lat")?.asDouble
        val lon = reportArray?.get("lon")?.asDouble
        reportId.value = "${repo?.asString}"
        whichButtonClicked.value = 4
        if (lat != null && lon != null){
            onCameraTrackingDismissed()
            mapView.getMapboxMap().cameraState
            val cameraPosition = CameraOptions.Builder()
                .zoom(14.5)
                .center(
                    Point.fromLngLat(
                        lon,lat
                    )
                )
                .build()
            mapView.getMapboxMap().setCamera(cameraPosition)

        }
        Log.d("REPO-R0","${repo?.asString}")
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
            val uid = auth.currentUser?.uid
            if (uid != null){
                val docRef = db.collection(DB_REF_USER).document(uid)
                val source = Source.CACHE
                docRef.get(source).addOnSuccessListener { task ->
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
                trySend(ResponseDto(isSuccess = false,"Un error"))
            }
            awaitClose{close()}
        }
    }
    fun vibrate(context :Context){
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.EFFECT_TICK))
        } else {
            vibrator.vibrate(200)
        }
    }
    private fun handleException(exception : Exception? = null , customMessage: String = "") {
        exception?.printStackTrace()
        val errorMsg = exception?.localizedMessage ?: ""
        val message = if (customMessage.isEmpty()) errorMsg else "$customMessage : $errorMsg"
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
    fun getSingleReport(reportId: String) :Flow<SingleCustomReport>  {
        return callbackFlow {
            val docRef = db.collection(if(!isDebugMode.value!!){DB_REF_REPORT}else{ DB_REF_REPORT_DEBUG }).document(reportId)

            val loc = Location("")
            var likeCont = 0
            var disLikeCount = 0
            var isLikedd : Boolean? = null
            try {
                auth.currentUser?.let {
                    val allFeedbacks = db.collection(if(!isDebugMode.value!!){DB_REF_REPORT}else{ DB_REF_REPORT_DEBUG }).document(reportId).collection("Feedback").get().await().documents
                    val alertedCount = db.collection(if(!isDebugMode.value!!){DB_REF_REPORT}else{ DB_REF_REPORT_DEBUG }).document(reportId).collection("Alerted").get().await().documents.size
                    allFeedbacks.forEach { feedback->
                        if (feedback.get("feedbackType") as Boolean){
                            likeCont +=1
                        }else{
                            disLikeCount +=1
                        }
                        if (feedback.get("feedbackByUID") as String == it.uid){
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
                                    isReportOwner = task.data?.get("reportByUID") as String == auth.currentUser!!.uid,
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
                    }
                        .addOnFailureListener {
                            Log.d("ERROR-55F", "${it.message}")
                            trySend(SingleCustomReport(isSuccess = false))
                        }
                }
            }catch (e:Exception){
                Log.d("DEBUG_USER_ID",e.message.toString())
                handleException(exception = e,"Error")
            }
            awaitClose{close()}
        }
    }
    suspend fun getReportOwnerByUid(uid:String):UserDao{
        val userRef = db.collection(DB_REF_USER).document(uid).get().await()
        Log.d("DEBUG_GET_REPORT_OWNER_INFO",userRef.get("userName").toString())
        return UserDao (
            userName = userRef.get("username") as? String? ?:
            "",
            userType = (userRef.get("userType") as? Long?)?.toInt() ?:
            0
        )
    }
    fun uploadPhotos(localUri : Uri) : Flow<ResponseDto>{
        return callbackFlow {
            auth.uid?.let { it1 ->
                val imageRef = storage.reference.child("profile_images/${it1}/${localUri.lastPathSegment}")
                val uploadTask  = imageRef.putFile(localUri)
                uploadTask.addOnSuccessListener {
                    Log.i(ContentValues.TAG, "Image Uploaded $imageRef")
                    val downloadUrl = imageRef.downloadUrl
                    downloadUrl.addOnSuccessListener {
                            remoteUri ->
                        db.collection(DB_REF_USER).document(it1).update("userAvatar",remoteUri.toString()).addOnSuccessListener {
                            trySend(ResponseDto(isSuccess = true, serverMessage = "Image updated successfully."))
                        }.addOnFailureListener {
                            trySend(ResponseDto(isSuccess = false, serverMessage = "${it.message}"))
                        }
                    }
                }
                uploadTask.addOnFailureListener {
                    trySend(ResponseDto(isSuccess = false, serverMessage = "${it.message}"))
                }
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
    fun isLogin(): Boolean {
        if (auth.currentUser != null){
            return true
        }
        return false
    }
    fun updateUserInfo(fullName: String, phone: String, email: String, gender: String) : Flow<ResponseDto> {
        return callbackFlow {
            auth.uid?.let {
                val user : HashMap<String, Any> = HashMap<String, Any>()
                user["username"] = fullName
                user["userEmail"] = email
                user["phoneNumber"] = phone
                user["userGender"] = gender
                db.collection(DB_REF_USER).document(it).update(user).addOnSuccessListener {
                    trySend(ResponseDto(isSuccess = true, serverMessage = "User Info updated successfully."))
                }.addOnFailureListener {
                    trySend(ResponseDto(isSuccess = false, serverMessage = "${it.message}"))
                }
            }
            awaitClose{close()}
        }
    }
    fun fetchReports(latt: Double, lonn: Double) {
        try {
            val scope = CoroutineScope(Dispatchers.Default)
            val radius = 25

            val center =  GeoLocation(latt, lonn)
            val radiusInM = radius * 1000.0
            val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInM)
            val tasks = arrayListOf<Task<QuerySnapshot>>()
            geofencingClient.removeGeofences(geofencePendingIntent)
            Log.d("BOUNDSSIZE",bounds.size.toString())

            for ((i, b) in bounds.withIndex()) {
                val q = db.collection(if(!isDebugMode.value!!){DB_REF_REPORT}else{ DB_REF_REPORT_DEBUG })
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
    private suspend fun dataToList(task : Task<QuerySnapshot>,i:Int, latitude: Double, longitude: Double,size:Int){
        try {
            val radius = 25
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
                                    createMarkerOnMap(tempReports3)
                                    if (report.reportType != 405 && report.reportType != 7) {
                                        tempGeofence1.add(
                                            Geofence.Builder()
                                                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                                .setRequestId("${report.reportId},${0}")
                                                .setCircularRegion(
                                                    report.geoLocation?.get(0) as Double,
                                                    report.geoLocation?.get(1) as Double,
                                                    geofenceRadius.value?.toFloat() ?: 250f
                                                )

                                                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                                                .build()
                                        )
                                    }
                                    //break
                                }
                                1->{
                                    tempReports2.add(repo)
                                    createMarkerOnMap(tempReports3)
                                    if (report.reportType != 405 && report.reportType != 7) {
                                        tempGeofence2.add(
                                            Geofence.Builder()
                                                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                                .setRequestId("${report.reportId},${0}")
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
                                2->{
                                    tempReports3.add(repo)
                                    createMarkerOnMap(tempReports3)
                                    if (report.reportType != 405 && report.reportType != 7) {
                                        tempGeofence3.add(
                                            Geofence.Builder()
                                                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                                .setRequestId("${report.reportId},${0}")
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
                                3->{
                                    tempReports4.add(repo)
                                    createMarkerOnMap(tempReports3)
                                    if (report.reportType != 405 && report.reportType != 7) {
                                        tempGeofence4.add(
                                            Geofence.Builder()
                                                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                                .setRequestId("${report.reportId},${0}")
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
                }
            }
            Log.d("TESTGEOQURY_IMPO", "B: $size  |  $counter")
            if (size >= counter){
                allReports.clear()
                addGeofences(concatenate(tempGeofence1, tempGeofence2, tempGeofence3,tempGeofence4))
                allReports.addAll(concatenate(tempReports1, tempReports2, tempReports3,tempReports4))
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
    fun addReport(speedLimit:Int?) {
        viewModelScope.launch {
            fusedLocationProviderClient.lastLocation.addOnCompleteListener {
                if (it.isComplete) {
                    if (it.result != null) {
                        if (!Utils.isMockLocationEnabled(it.result)){
                            if(placeReportValidation(reportCountPerOneHour.value!!, loadTimeOfLastReport.value?:0)){
                                addReport(geoPoint = GeoPoint(it.result.latitude,it.result.longitude), reportType = 1, speedLimit = speedLimit)
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
    suspend fun getCurrentLocation():Location?{
        return if (ActivityCompat.checkSelfPermission(
                app.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                app.applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            null
        }else{
            fusedLocationProviderClient.lastLocation.await()
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
    private fun getGeofencingRequest(reports: List<Geofence> = ArrayList()): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(reports)
        }.build()
    }
    private val geofencePendingIntent: PendingIntent by lazy {
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
    fun getJsonAsync(lat: Double, lon: Double) = GlobalScope.async{
        if (MetricsUtils.isOnline(app)){
            val respo = URL("https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${lon}&appid=02c2bac30e0194dff2c04877257c322e").readText()
            val data = Gson().fromJson(respo,WeatherDto::class.java)
            temperature.value = (data.main?.temp?.minus(273))?.toInt()
            Log.d("OWAPID",data.name.toString() )
        }
    }
    fun getUid():String?{
        return auth.currentUser?.uid
    }
    fun saveAlerts(info : SaveUserInfoInAlerted){
        auth.currentUser?.let {
            val alert : HashMap<String, Any> = HashMap<String, Any>()
            alert["lastSeen"] = FieldValue.serverTimestamp()
            alert["speedWhenEntered"] = speed.value
            alert["userAvatar"] = info.userAvatar
            alert["userId"] = it.uid
            alert["username"] = info.userName
            db.collection(if(!isDebugMode.value!!){DB_REF_REPORT}else{ DB_REF_REPORT_DEBUG }).document(info.reportId).collection(Constants.DB_REF_ALERTED).document(it.uid).set(alert)
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
                                trySend(ResponseDto(isSuccess = true, serverMessage = "Success"))
                            }.addOnFailureListener {
                                trySend(ResponseDto(isSuccess = false, serverMessage = it.message.toString()))
                            }
                    }
                }
//                retrieveStatistics()
//                userStatistics.value?.let {statistic->
//
//                }
            }catch (e:Exception){
                trySend(ResponseDto(isSuccess = false, serverMessage = e.message.toString()))
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
    fun setOnlineStatus(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            val token = task.result
            val user : HashMap<String, Any> = HashMap<String, Any>()
            user["pushId"] = token
            user["status"] = "online"
//            user["uniqueDeviceID"] = Utils.getDeviceUniqueID(app)
            db.collection(DB_REF_USER).document(auth.currentUser?.uid!!).update(user)
        })
    }
    fun extendReportTime(reportId: String) {
        val report : HashMap<String, Any> = HashMap<String, Any>()
        report["reportTimeStamp"] = Timestamp.now()
        db.collection(if(!isDebugMode.value!!){DB_REF_REPORT}else{ DB_REF_REPORT_DEBUG }).document(reportId).update(report)
    }
    fun completeUserRegister(userName:String) : Flow<ResponseDto> {
        return callbackFlow {
            if (userName.length in 2..31){
                if(containsSpecialCharacters(userName)){
                    if (!startsWithCee(userName)){
                        auth.uid?.let { uID ->
                            val user : HashMap<String, Any> = HashMap<String, Any>()
                            user["username"] = userName
                            db.collection(DB_REF_USER).document(uID).update(user).addOnSuccessListener {
                                trySend(ResponseDto(isSuccess = true, serverMessage = "User Info updated successfully."))
                            }.addOnFailureListener {exception->
                                trySend(ResponseDto(isSuccess = false, serverMessage = "${exception.message}"))
                            }
                        }
                    }else{
                        trySend(ResponseDto(isSuccess = false, serverMessage = "invalid full name"))
                    }
                }else{
                    trySend(ResponseDto(isSuccess = false, serverMessage = "invalid full name"))
                }
            }else{
                trySend(ResponseDto(isSuccess = false, serverMessage = "full name length invalid."))
            }
            awaitClose{close()}
        }
    }
    fun updateReportSpeedLimit(speedLimit: Int?,reportId: String): Flow<ResponseDto> {
        return callbackFlow {
            if (speedLimit != null && reportId != ""){
                db.collection(if(!isDebugMode.value!!){DB_REF_REPORT}else{ DB_REF_REPORT_DEBUG }).document(reportId).update("reportSpeedLimit",speedLimit).addOnSuccessListener {
                    trySend(ResponseDto(isSuccess = true, serverMessage = "Speed limit updated successfully to $speedLimit."))
                }.addOnFailureListener {exception->
                    trySend(ResponseDto(isSuccess = false, serverMessage = "${exception.message}"))
                }
            }else{
                trySend(ResponseDto(isSuccess = false, serverMessage = "input Invalid or empty"))
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
    private fun retrieveIsDebugMode(){
        viewModelScope.launch(Dispatchers.IO) {
            ds.retrieveDebugMode().collect{
                isDebugMode.postValue(it)
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
    private fun containsSpecialCharacters(str: String): Boolean {
        val pattern = "[^A-Za-z0-9 ]"
        return str.matches(pattern.toRegex())
    }
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
}





fun incidentTime(stamp : Timestamp,context: Context) : String{
    val mines = (Timestamp.now().seconds - stamp.seconds)/60
    Log.d("DEBUG_TIME_INCIDENT",mines.toString())
    return try {
        val sdf = SimpleDateFormat("MMMM dd, yyyy",Locale.US)
        val netDate = Date((stamp.seconds * 1000))
        if(mines > 60.0){
            if ((mines/1440.0) > 1.0){
                if ((mines/43200.0) >1.0){
                    "${sdf.format(netDate)} • ${mines/43200} ${context.getString(R.string.lbl_nearestIncedednts_monthAgo)}"
                }else{
                    "${sdf.format(netDate)} • ${mines/1440} ${context.getString(R.string.lbl_nearestIncedents_dayAgo)}"
                }
            }else{
                "${sdf.format(netDate)} • ${mines/60} ${context.getString(R.string.lbl_nearestIncedednts_hourAgo)}"
            }
        }else{
            "${sdf.format(netDate)} • $mines ${context.getString(R.string.lbl_nearestIncedednts_minuteAgo)}"
        }
    } catch (e: Exception) {
        e.toString()
    }
}
fun incidentDistance(distance : Float) : String{
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.DOWN
    return if (distance > 1000){
        "${df.format(distance/1000)} KM"
    }else{
        "${df.format(distance)} M"
    }
}


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
//    fun getMyReports() {
//
//        try {
//            auth.currentUser.let {
//                db.collection(DB_REF_REPORT).whereEqualTo("reportByUID", it?.uid).get().addOnSuccessListener { querySnapshot->
//                    val tempReports = ArrayList<NewReport>()
//                    querySnapshot.documents.forEach {ducument ->
//                        tempReports.add(NewReport(
//                            isActive = true,
//                            geoLocation = ducument.data?.get("geoLocation") as List<*>?,
//                            reportByUID = it?.uid,
//                            reportAddress = ducument.data?.get("reportAddress") as String,
//                            reportType = (ducument.data?.get("reportType") as Long).toInt(),
//                           // reportTimeStamp = ducument.data?.get("reportTimeStamp") as Timestamp,
//                        ))
//                    }.let {
//                        myReports.addAll(tempReports)
//                    }
//
//                }
//                db.collection(DB_REF_ARCHIVE_REPORT).whereEqualTo("reportByUID", it?.uid).get().addOnSuccessListener {querySnapshott->
//                    val tempReports = ArrayList<NewReport>()
//                    querySnapshott.documents.forEach {ducument ->
//                        Log.d("MYREPORTS",ducument.data?.get("reportAddress").toString())
//                        tempReports.add(NewReport(
//                            isActive = false,
//                            geoLocation = ducument.data?.get("geoLocation") as List<*>?,
//                            reportByUID = it?.uid,
//                            reportAddress = ducument.data?.get("reportAddress") as String,
//                            reportType = (ducument.data?.get("reportType") as Long).toInt(),
//                            //reportTimeStamp = ducument.data?.get("reportTimeStamp") as Timestamp,
//                        ))
//                    }.let {
//                        myReports.addAll(tempReports)
//                    }
//
//                }.let {
//                    myReports.sortBy { it -> it.isActive }
//                    //trySend(ResponseWithData(isSuccess = true, data = myReports, serverMessage = null))
//                }
//
//            }
//
//        } catch (e: Exception) {
//            //trySend(ResponseWithData(isSuccess = false, data = null, serverMessage = e.message))
//        }
//
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