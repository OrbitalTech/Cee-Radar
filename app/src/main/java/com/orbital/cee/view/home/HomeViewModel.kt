package com.orbital.cee.view.home
import android.app.Application
import android.content.ContentValues
import android.content.Context
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
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.orbital.cee.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Source
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.plugin.LocationPuck2D
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
import com.orbital.cee.core.Constants.DB_REF_ARCHIVE_REPORT
import com.orbital.cee.core.Constants.DB_REF_REPORT
import com.orbital.cee.core.Constants.DB_REF_USER
import com.orbital.cee.data.Event
import com.orbital.cee.data.repository.DataStoreRepository
import com.orbital.cee.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import java.math.RoundingMode
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
    private val dataStoreRepository: DataStoreRepository,
    val storage: FirebaseStorage,
    application: Application,
): AndroidViewModel(application) {
    val app = application
    val readFirstLaunch = dataStoreRepository.readFirstLaunch.asLiveData()
    val lsatAdsWatched = dataStoreRepository.watchTime.asLiveData()
    val readMaxSpeed = dataStoreRepository.readMaxSpeed.asLiveData()
    val readAlertsCount = dataStoreRepository.readAlertsCount.asLiveData()
    val readDistance = dataStoreRepository.readDistance.asLiveData()
    val soundStatus = dataStoreRepository.readSoundStatus.asLiveData()
    val userType = dataStoreRepository.readUserType.asLiveData()
    val trips = dataStoreRepository.tripList.asLiveData()
    val langCode = dataStoreRepository.languageCode.asLiveData()
    val geofenceRadius = dataStoreRepository.readGeofenceRadius.asLiveData()
    var appLaunchTime = mutableStateOf<Date?>(null)
    var userInfo = mutableStateOf(UserNew())
    var trip = mutableStateOf(Trip())
    var isCameraMove = mutableStateOf(true)
    var showCustomDialogWithResult = mutableStateOf(false)

    var lastLocation = mutableStateOf(Location(""))
    var isTripStarted = mutableStateOf(false)
    var isDarkMode = mutableStateOf(false)
//    var isPurchasedAdRemove = mutableStateOf(false)
    var reportClicked = mutableStateOf(false)
    val speed = mutableStateOf(0)
    val onlineUserCounter = mutableStateOf(0)
    val speedPercent = mutableStateOf(0.0f)
    var whichButtonClicked = mutableStateOf(1)
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
    var annotationApi : AnnotationPlugin? = null
    var annotationApii : AnnotationPlugin? = null
    val isLiked = mutableStateOf<Boolean?>(null)
    val allReports = ArrayList<NewReport>()
    var markerList : ArrayList<PointAnnotationOptions> = ArrayList()
    var pointAnnotationManager : PointAnnotationManager? = null
    var pointAnnotationManagerr : PointAnnotationManager? = null
    lateinit var annotationConfig : AnnotationConfig
    lateinit var annotationConfigg : AnnotationConfig
    var mapView = MapView(app)
    var timeRemain = mutableStateOf(0)
    var isTimerRunning = mutableStateOf(false)

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
        getOnlineUserCount()
    }


    var style: Style? = null
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
        //mapView.gestures.addOnMapLongClickListener(onMovedListener)
    }
    fun removeGesturesListener() {
        mapView.gestures.removeOnMoveListener(onMoveListener)
        //mapView.gestures.addOnMapLongClickListener(onMovedListener)
    }
    fun addReport(geoPoint: GeoPoint,reportType:Int,context: Context,time: Timestamp = Timestamp.now(),speedLimit:Int? = null) = viewModelScope.launch {
        try {
            val id = db.collection(DB_REF_REPORT).document().id
            val document = db.collection(DB_REF_REPORT).document(id)
            val idReportInUserDocument = db.collection(DB_REF_USER).document(auth.uid!!).collection("reports").document(id)

            val hash = GeoFireUtils.getGeoHashForLocation(GeoLocation(geoPoint.latitude, geoPoint.longitude))
            val report : HashMap<String, Any> = HashMap<String, Any>()
            val reportt : HashMap<String, Any> = HashMap<String, Any>()

            report["g"] = hash
            report["geoLocation"] = listOf(geoPoint.latitude, geoPoint.longitude)
            report["reportTimeStamp"] = time
            report["reportAddress"] = getAddress(geoPoint.latitude,geoPoint.longitude, context)
            report["reportType"] = reportType
            report["reportId"] = id
            if (speedLimit!=null){
                report["reportSpeedLimit"] = speedLimit
            }
            report["reportByUID"] = auth.currentUser!!.uid
            document.set(report).await()
            idReportInUserDocument.set(reportt).await()


        } catch (e: Exception) {

        }
    }
    fun addReportFeedback(reportId : String,status : Boolean) = viewModelScope.launch{
        try {
            auth.currentUser?.let {
                val document = db.collection(DB_REF_REPORT).document(reportId).collection("Feedback").document(it.uid)
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
        db.collection(DB_REF_REPORT).document(bookId).delete().await()
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
    fun restStatistics() =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.resetStatistics()
        }

    fun updateSoundStatus(soundStatusId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.updateSoundPreferences(soundStatusId)
        }
    suspend fun getMyReportCount() : Int{
        try {
            auth.currentUser?.let {
                var a = 0
                a += db.collection(DB_REF_REPORT).whereEqualTo("reportByUID", it.uid).get().await().documents.size
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
    private fun getOnlineUserCount(){
        db.collection(DB_REF_USER).whereEqualTo("status", "online").addSnapshotListener { value, error ->
            onlineUserCounter.value =  value?.documents?.size ?:
            0
            Log.d("DEBUG_ONLINE_USER",onlineUserCounter.value.toString())
        }
        //return db.collection(DB_REF_USER).whereEqualTo("status", "online").get().await().documents.size
    }
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


    fun createClickedPin(lat: Double,lon:Double){
        pointAnnotationManagerr?.deleteAll()
        val pointAnnotationOptions : PointAnnotationOptions = PointAnnotationOptions()
            .withPoint(Point.fromLngLat(lon, lat))
            .withIconImage(convertDrawableToBitMap(AppCompatResources.getDrawable(app,R.drawable.ic_circle)))
        pointAnnotationManagerr?.create(pointAnnotationOptions)
    }
    fun createMarkerOnMap(repo : List<NewReport>){
        try {
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
        }catch (e:Exception){

        }

    }
    fun onClickReport(annotation: PointAnnotation) {
        reportClicked.value = true
        val reportArray = annotation.getData()?.asJsonObject
        val repo = reportArray?.get("report")
        reportId.value = "${repo?.asString}"
        whichButtonClicked.value = 4
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
    fun vibrate_Alarm(context :Context){
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
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
            val docRef = db.collection(DB_REF_REPORT).document(reportId)

            val loc = Location("")
            var likeCont = 0
            var disLikeCount = 0
            var isLikedd : Boolean? = null
            try {
                auth.currentUser?.let {
                    val allFeedbacks = db.collection(DB_REF_REPORT).document(reportId).collection("Feedback").get().await().documents
                    val alertedCount = db.collection(DB_REF_REPORT).document(reportId).collection("Alerted").get().await().documents.size
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
    //    private fun prepareViewAnnotation(mapView: com.mapbox.maps.MapView) {
//        val viewAnnotationManager = mapView.viewAnnotationManager
//        //val sym = SymbolManager(mapView,mapView.getMapboxMap(),style)
//        viewAnnotationManager.removeAllViewAnnotations()
//        for (i in allReports) {
//            Log.d("FORRR",i.geoLocation!![0].toString())
//            viewAnnotationManager.addViewAnnotation(
//                when(i.reportType){
//                    1-> R.layout.camera_report_annotation
//                    2-> R.layout.carcrash_report_annotation
//                    3-> R.layout.police_report_annotation
//                    4-> R.layout.constraction_report_annotation
//                    5-> R.layout.static_camera_report_annotation
//                    6-> R.layout.point_to_point_report_annotation
//                    else-> R.layout.camera_report_annotation
//                },
//                viewAnnotationOptions {
//                    geometry(Point.fromLngLat(i.geoLocation!![1] as Double,i.geoLocation!![0] as Double))
//                    allowOverlap(true)
//                    anchor(ViewAnnotationAnchor.BOTTOM)
//                    visible(true)
//                }
//            )
//        }
//    }
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

    suspend fun verifyUserByUid(uid: String) {
        val user : HashMap<String, Any> = HashMap<String, Any>()
        user["userType"] = 3
        db.collection(DB_REF_USER).document(uid).update(user).await()
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
