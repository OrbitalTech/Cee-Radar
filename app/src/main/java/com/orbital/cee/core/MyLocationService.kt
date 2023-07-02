package com.orbital.cee.core

import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import androidx.datastore.preferences.core.edit
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.mapbox.geojson.Point
import com.orbital.cee.R
import com.orbital.cee.core.MyLocationService.GlobalStreetSpeed.streetSpeedLimit
import com.orbital.cee.core.MyLocationService.LSR.getReportDataById
import com.orbital.cee.core.MyLocationService.LSR.resetLSR
import com.orbital.cee.core.MyLocationService.LSS.LSSReset
import com.orbital.cee.core.MyLocationService.LSS.calcTrip
import com.orbital.cee.core.MyLocationService.LSS.isChangeDetected
import com.orbital.cee.core.MyLocationService.LSS.isLocationChange
import com.orbital.cee.core.MyLocationService.LSS.lrouteCoordinates
import com.orbital.cee.core.MyLocationService.LSS.resetTrip
import com.orbital.cee.core.MyLocationService.LSS.speed
import com.orbital.cee.data.repository.DataStoreRepository
import com.orbital.cee.data.repository.dataStore
import com.orbital.cee.model.NewReport
import com.orbital.cee.model.OverpassResponse
import com.orbital.cee.utils.MetricsUtils
import com.orbital.cee.view.home.HomeActivity

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.wait
import java.io.IOException

import java.util.*
class MyLocationService: Service() {
    object LSS {
        private var tripLastLocation : Location? = null
        private var inP2PLastLocation : Location? = null
        var inP2PDistance = 0.0f
        var TripDistance = mutableStateOf(0f)
        var TripDuration = mutableStateOf(0L)
        var isTripPused = mutableStateOf(false)
        var isChangeDetected = mutableStateOf(false)
        var isLocationChange = mutableStateOf(false)
        var TripMaxSpeed = mutableStateOf(0)
        var TripAverageSpeed = mutableStateOf(0)
        var inP2PAverageSpeed = mutableStateOf(0)
        var roadMaxSpeed = mutableStateOf(0)
        val speed = mutableStateOf(0)
        var TripStartTime = Date()
        var EnteredP2PTime = Date()
        var isEnteredPointToPointRoad = mutableStateOf(false)
        var lrouteCoordinates = ArrayList<Point>()
        private var listOfSpeedsInTrip = arrayListOf<Int>()
        var listOfSpeedsInTripIterator = arrayListOf<Int>()
        fun LSSReset(){
            tripLastLocation = null
            inP2PLastLocation = null
            inP2PDistance = 0.0f
            TripDistance.value = 0f
            TripDuration.value = 0L
            isTripPused.value = false
             isChangeDetected.value = false
             isLocationChange.value = false
            isEnteredPointToPointRoad.value = false
             TripMaxSpeed.value = 0
             TripAverageSpeed.value = 0
             inP2PAverageSpeed.value = 0
             roadMaxSpeed.value = 0
             speed.value = 0
             TripStartTime = Date()
             EnteredP2PTime = Date()
             lrouteCoordinates = ArrayList<Point>()
             listOfSpeedsInTrip = arrayListOf<Int>()
        }

        suspend fun calcTrip(cloc: Location) {
            if (!isTripPused.value){
                if (tripLastLocation == null) {
                    tripLastLocation = cloc
                }else{
                    addElementAsync(listOfSpeedsInTrip,(cloc.speed * 3.6).toInt())
                    getAverageAsync(listOfSpeedsInTrip)
                    getMaxAsync(listOfSpeedsInTrip)
//                    listOfSpeedsInTrip.add(listOfSpeedsInTrip.size,(cloc.speed * 3.6).toInt())
//                    TripAverageSpeed.value = listOfSpeedsInTrip.average().toInt()
//                    TripMaxSpeed.value = listOfSpeedsInTrip.max().toInt()


                    val tempDistance = tripLastLocation!!.distanceTo(cloc)/1000
                    tripLastLocation = cloc
                    TripDistance.value = TripDistance.value + tempDistance
                }
            }else{
                TripDuration.value = MetricsUtils.getSeconds(TripStartTime, Date())
            }

        }
        private suspend fun addElementAsync(list: MutableList<Int>, element: Int) = coroutineScope {
            val job = async {
                list.add(element)
                println("Element added: $element")
            }
            job.await()
        }
        private suspend fun getAverageAsync(list: MutableList<Int>) = coroutineScope {
            val job = async {
                TripAverageSpeed.value = list.average().toInt()
            }
            job.await()
        }
        private suspend fun getMaxAsync(list: MutableList<Int>) = coroutineScope {
            val job = async {
                TripMaxSpeed.value = list.max().toInt()
            }
            job.await()
        }
//        fun calcAverageSpeed(cLoc:Location){
//            if (inP2PLastLocation == null) {
//                inP2PLastLocation = cLoc
//            }else{
//                val tempDistance = inP2PLastLocation!!.distanceTo(cLoc)/1000
//                inP2PLastLocation = cLoc
//                inP2PDistance += tempDistance
//                val seconds = MetricsUtils.getSeconds(EnteredP2PTime, Date())
//                val hours = (seconds/60.0)/60.0
//                inP2PAverageSpeed.value = (inP2PDistance.div(hours)).toInt()
//            }
//        }
//        fun resetP2PCalc(){
//            inP2PLastLocation = null
//            inP2PDistance = 0f
//            inP2PAverageSpeed.value = 0
//        }
        fun resetTrip(){
            TripStartTime = Date()
            TripDistance.value = 0f
            TripAverageSpeed.value = 0
            TripMaxSpeed.value = 0
            listOfSpeedsInTrip.clear()
            lrouteCoordinates.clear()
        }
    }
    object GlobalStreetSpeed{
        var streetSpeedLimit: MutableState<Double?> = mutableStateOf(null)
    }
    object LSR{
        val allReports = ArrayList<NewReport>()
        val allMutedReports = ArrayList<String>()
        fun resetLSR(){
            allReports.clear()
            allMutedReports.clear()
        }
        fun getReportDataById(reportId:String): NewReport?{
            return allReports.find { report ->
                report.reportId == reportId
            }
        }
        fun isThisReportMuted(reportId:String): Boolean{
            return allMutedReports.contains(reportId)
        }

    }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient
    private lateinit var db : FirebaseFirestore
    var rad = 5L
    private  var previousReportId : String = ""
    var listOfSpeeds = arrayListOf<Int>()

    private lateinit var mMediaPlayer : MediaPlayer

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        db = FirebaseFirestore.getInstance()
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        val defaultValue = HashMap<String,Any>()
        defaultValue["reportSightRadius"] = 5

        remoteConfig.setDefaultsAsync(defaultValue)
        remoteConfig.fetch(0)
        remoteConfig.fetchAndActivate()
        rad = remoteConfig.getLong("reportSightRadius")
        Log.d("debug_reportSightRadius" , rad.toString())

        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
        resetTrip()
        mMediaPlayer = MediaPlayer.create(this, R.raw.sound_geofence_alert)

    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    var prevLocation = Location("")
    private var lLocation = Location("")
    var counter = 0
    var counter1 = 0
    var i = 0
    @RequiresApi(Build.VERSION_CODES.S)
    private fun start() {
        val loc = Location("")
        loc.latitude = 0.00
        loc.latitude = 0.00
        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Cee uses location")
            .setContentText("Please don't close cee to take care of you")
            .setSmallIcon(R.drawable.cee)
            .setOngoing(true)
        locationClient
            .getLocationUpdates(500L)
            .catch { e -> e.printStackTrace() }
            .onEach { it ->
                Log.d("DEBUG_LAT_LON_SERV","${it.longitude},${it.latitude}")
                lrouteCoordinates.add(Point.fromLngLat(it.longitude,it.latitude))
                prevLocation = if (prevLocation == Location("")){ it }else{
                    val dis = prevLocation.distanceTo(it)
                    addDistance(dis.div(1000f))
                    it
                }
                counter++
                if (counter > 4){
                    calcTrip(it)
                    isLocationChanged(it)
                    counter = 0
                }
                speed.value = (it.speed * 3.6).toInt()
                isChangeDetected.value = true

                GeofenceBroadcastReceiver.GBRS.GeoId.value?.let {rId->
                    val report = findReport(rId)
                    report?.let {repo->
                        var reportDirection = "-1"
                        repo.reportDirection?.let {
                            reportDirection = it.toString()
                        }
                        val reportType = repo.reportType
                        if(reportDirection != ""){
                            val reportDirectionAsFloat : Double = reportDirection.toDouble()
                            if (reportDirectionAsFloat > 0){
                                if(it.bearing.toInt() in reportDirectionAsFloat.minus(22.5).toInt() .. reportDirectionAsFloat.plus(22.5).toInt()){
                                    if(previousReportId != repo.reportId){
                                        if (HomeActivity.Singlt.SoundSta.value == 1){
                                            if (!mMediaPlayer.isPlaying){
                                                if (!LSR.isThisReportMuted(reportId = rId)){
                                                    val audio : AudioManager =  applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                                                    val stVolLev = audio.getStreamVolume(AudioManager.STREAM_MUSIC)
                                                    audio.setStreamVolume(AudioManager.STREAM_MUSIC,15,0)
                                                    mMediaPlayer.start()
                                                    mMediaPlayer.setOnCompletionListener {
                                                        audio.setStreamVolume(AudioManager.STREAM_MUSIC,stVolLev,0)
                                                    }
                                                }
                                            }
                                        }else if (HomeActivity.Singlt.SoundSta.value == 2){
                                            serviceScope.launch {
                                                vibrate(applicationContext)
                                                var seconds = 0
                                                while (seconds <= 10){
                                                    if (seconds % 2 == 1){
                                                        vibrate(applicationContext)
                                                    }
                                                    seconds++
                                                }
                                            }
                                        }
                                        previousReportId = repo.reportId.toString()
                                    }
                                }
                            }else{
                                Log.d("DEBUG_PLAY_SOUND_ITR","hey2")
                                if(previousReportId != repo.reportId){
                                    if (HomeActivity.Singlt.SoundSta.value == 1){
                                        if (!mMediaPlayer.isPlaying){
                                            if (!LSR.isThisReportMuted(reportId = rId)){
                                                val audio : AudioManager =  applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                                                val stVolLev = audio.getStreamVolume(AudioManager.STREAM_MUSIC)
                                                audio.setStreamVolume(AudioManager.STREAM_MUSIC,15,0)
                                                mMediaPlayer.start()
                                                mMediaPlayer.setOnCompletionListener {
                                                    audio.setStreamVolume(AudioManager.STREAM_MUSIC,stVolLev,0)
                                                }
                                            }
                                        }
                                    }else if (HomeActivity.Singlt.SoundSta.value == 2){
                                        serviceScope.launch {
                                            vibrate(applicationContext)
                                            var seconds = 0
                                            while (seconds <= 10){
                                                if (seconds % 2 == 1){
                                                    vibrate(applicationContext)
                                                }
                                                seconds++
                                            }
                                        }
                                    }
                                    previousReportId = repo.reportId.toString()
                                }
                            }
                        }
                        if (reportType == 6){
                            LSS.isEnteredPointToPointRoad.value = true
                            LSS.roadMaxSpeed.value = repo.reportSpeedLimit ?: 0
                        }
                    }
                }
                if (LSS.isEnteredPointToPointRoad.value){
                    i++
                    if (i%2 == 0){
                        LSS.inP2PAverageSpeed.value = if(listOfSpeeds.isNotEmpty()){
                            listOfSpeeds.average().toInt()
                        }else{
                            0
                        }
                    }else{
                        listOfSpeeds.add((it.speed * 3.6).toInt())
                    }
                }
                counter1++
                if (counter1 >15){
                val req = Request.Builder()
                    .url("https://overpass-api.de/api/interpreter?data=[out:json];way(around:10,${it.latitude},${it.longitude})['maxspeed'];out;").get().build()
                val client = OkHttpClient()
                client.newCall(req).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.d("DEBUG_SPEED_LIMIT_STREET",e.message.toString())
                    }

                    private val myJson = Json { ignoreUnknownKeys = true }

                    override fun onResponse(call: Call, response: Response) {
                        response.body?.string()?.let { responseBody->
//                            val data = Gson().fromJson(responseBody, OverpassResponse::class.java)
                            val data = myJson.decodeFromString<OverpassResponse>(responseBody)
                            data.elements.firstOrNull()?.tags?.maxspeed?.let {maxSpeed ->
                                val digits = maxSpeed.filter { it.isDigit() }
                                val mphSpeedLimit = digits.toDoubleOrNull()

                                mphSpeedLimit?.let {_maxSpeed->
                                    streetSpeedLimit.value = _maxSpeed
                                } ?: run {
                                    streetSpeedLimit.value = null
                                }
                            }
                            Log.d("DEBUG_SPEED_LIMIT_STREET", "SUC: $responseBody")
                        }
                    }
                })
                    counter1 = 0
                }

            }
            .launchIn(serviceScope)
        startForeground(1, notification.build())
    }

    var prevId = ""
    var report : NewReport? = null
    private suspend fun findReport(rId: String) : NewReport? {
        return if (prevId!=rId){
            val localReport = getReportDataById(rId)
            report = if (localReport != null){
                getReportDataById(rId)
            }else{
                db.collection("ReportsDebug").document(rId).get().await().toObject(NewReport::class.java)
            }
            prevId = rId
            report
        }else{
            report
        }
    }

    private fun stop() {
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        LSSReset()
        resetLSR()
        serviceScope.cancel()

    }


    @RequiresApi(Build.VERSION_CODES.S)
    private fun isLocationChanged(location: Location?) {
        if (location != null){
            if (lLocation == Location("")){
                isLocationChange.value = true
                lLocation = location
            }else{
                if (location.distanceTo(lLocation) > (rad*1000)-500){
                    isLocationChange.value = true
                    lLocation = location
                }
            }
        }
    }
    suspend fun addDistance(distance : Float) {
        dataStore.edit { preference ->
            Log.d("DEBUG_DSRPKD_PRINTO_LOS",preference[DataStoreRepository.PreferenceKey.distance].toString())
            preference[DataStoreRepository.PreferenceKey.distance] =
                if(preference[DataStoreRepository.PreferenceKey.distance] == null){
                    distance
                }else{
                    preference[DataStoreRepository.PreferenceKey.distance]!! + distance
                }
        }
    }
    private fun vibrate(context :Context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            val vibrator = vibratorManager.defaultVibrator
            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
        }else{
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(100)
        }
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}