package com.orbital.cee.core

import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import kotlinx.serialization.json.Json
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.orbital.cee.R
import com.orbital.cee.core.MyLocationService.GlobalStreetSpeed.streetSpeedLimit
import com.orbital.cee.core.MyLocationService.LSS.resetTrip
import com.orbital.cee.model.OverpassResponse
import com.orbital.cee.model.WeatherDto
import com.orbital.cee.utils.MetricsUtils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.Serializable
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
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
        var TripMaxSpeed = mutableStateOf(0)
        var TripAverageSpeed = mutableStateOf(0)
        var inP2PAverageSpeed = mutableStateOf(0)
        var TripStartTime = Date()
        var EnteredP2PTime = Date()
        fun calcTrip(cloc: Location) {
            if (!isTripPused.value){
                if (tripLastLocation == null) {
                    tripLastLocation = cloc
                }else{
                    val tempDistance = tripLastLocation!!.distanceTo(cloc)/1000
                    tripLastLocation = cloc
                    TripDistance.value = TripDistance.value + tempDistance

                    val seconds = MetricsUtils.getSeconds(TripStartTime, Date())
                    val hours = (seconds/60.0)/60.0
                    TripAverageSpeed.value = (TripDistance.value.div(hours)).toInt()
                }
                if ((cloc.speed * 3.6).toInt() > TripMaxSpeed.value){
                    TripMaxSpeed.value = (cloc.speed * 3.6).toInt()
                }
            }else{
                TripDuration.value = MetricsUtils.getSeconds(TripStartTime, Date())
            }

        }
        fun calcAverageSpeed(cLoc:Location){
            if (inP2PLastLocation == null) {
                inP2PLastLocation = cLoc
            }else{
                val tempDistance = inP2PLastLocation!!.distanceTo(cLoc)/1000
                inP2PLastLocation = cLoc
                inP2PDistance += tempDistance
                val seconds = MetricsUtils.getSeconds(EnteredP2PTime, Date())
                val hours = (seconds/60.0)/60.0
                inP2PAverageSpeed.value = (inP2PDistance.div(hours)).toInt()
            }
        }
        fun resetP2PCalc(){
            inP2PLastLocation = null
            inP2PDistance = 0f
            inP2PAverageSpeed.value = 0
        }
        fun resetTrip(){
            TripStartTime = Date()
            TripDistance.value = 0f
            TripAverageSpeed.value = 0
            TripMaxSpeed.value = 0
        }
    }
    object GlobalStreetSpeed{
        var streetSpeedLimit: MutableState<Double?> = mutableStateOf(null)
    }
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient
    private lateinit var db : FirebaseFirestore
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        db = FirebaseFirestore.getInstance()
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
        resetTrip()


    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun start() {
        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Cee uses location")
            .setContentText("Please don't close cee to take care of you")
            .setSmallIcon(R.drawable.cee)
            .setOngoing(true)
        locationClient
            .getLocationUpdates(500L)
            .catch { e -> e.printStackTrace() }
            .onEach {
                if (GeofenceBroadcastReceiver.GBRS.GeoId.value != null){
                    GeofenceBroadcastReceiver.GBRS.GeoId.value?.let {rId->
                        val report = db.collection("ReportsDebug").document(rId).get().await()
                        val rA = report.get("reportAddress") as? String?
                        Log.d("DEBUG_VALIDATING_REPORT","$rA")
                    }
                }
                val req = Request.Builder()
                    .url("https://overpass-api.de/api/interpreter?data=[out:json];way(around:10,35.579388,45.458819)['maxspeed'];out;").get().build()

                val client = OkHttpClient()
                client.newCall(req).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.d("DEBUG_SPEED_LIMIT_STREET",e.message.toString())
                    }
                    override fun onResponse(call: Call, response: Response) {

                        response.body?.string()?.let { responseBody->
                            val data = Gson().fromJson(responseBody, OverpassResponse::class.java)
                            data.elements.firstOrNull()?.tags?.maxspeed?.let {maxSpeed ->
                                val digits = maxSpeed.filter { it.isDigit() }
                                val mphSpeedLimit = digits.toDoubleOrNull()

                                mphSpeedLimit?.let {
                                    streetSpeedLimit.value = it
                                } ?: run {
                                    streetSpeedLimit.value = null
                                }

                            }
                            Log.d("DEBUG_SPEED_LIMIT_STREET", "SUC: ${data.elements.firstOrNull()?.tags?.maxspeed}")
                        }



                    }
                })
            }
            .launchIn(serviceScope)

        startForeground(1, notification.build())
    }

    private fun stop() {
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}