package com.orbital.cee.core

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.stringResource
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.StreamType
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.orbital.cee.R
import com.orbital.cee.core.Constants.NOTIFICATION_CHANNEL_ID
import com.orbital.cee.core.Constants.NOTIFICATION_CHANNEL_NAME
import com.orbital.cee.core.Constants.NOTIFICATION_ID
import com.orbital.cee.core.GeofenceBroadcastReceiver.GBRS.playAlertSound
import com.orbital.cee.view.home.HomeActivity
import kotlinx.coroutines.delay

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    //@RequiresApi(Build.VERSION_CODES.S)
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent == null){
            Log.e("BroadcastReceiver", "NULL EXCEPTION")
        }else{
            if ( geofencingEvent.hasError()) {
                val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
                Log.e("BroadcastReceiver", errorMessage)
                return
            }
            when (geofencingEvent.geofenceTransition) {
                Geofence.GEOFENCE_TRANSITION_ENTER -> {
                    displayNotification(context)
                    val triggeringGeofences = geofencingEvent.triggeringGeofences
                    if (triggeringGeofences != null) {
                        for (i in triggeringGeofences){
                            val aa = i.requestId.split(",").toTypedArray()
                            playAlertSound(true,context, bearing = aa[1].toInt())
                            Log.d("BERING_TEST","REPOID-"+aa[0])
                            Log.d("BERING_TEST","REPOBER-"+aa[1])

                            Log.d("GEOBR",i.requestId)

                            GBRS.add(aa[0])
                        }
                    }
                    Log.d("GEOBR", "Geofence Enter ")
                }
                Geofence.GEOFENCE_TRANSITION_EXIT -> {
                    GBRS.add(null)
                    Log.d("GEOBR", "Geofence Exit")
                }
            }
        }


    }

    private fun displayNotification(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel(notificationManager)

        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.cee)
            .setContentTitle(context.getString(R.string.inside_geofence_notification_title))
            .setContentText(context.getString(R.string.inside_geofence_notification_description))
        notificationManager.notify(NOTIFICATION_ID, notification.build())
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
    }
    object GBRS {
        var GeoId = mutableStateOf<String?>(null)
        var secondGeo :String? = null
        fun add(geoId: String?) {
            if (geoId != null){
                if (GeoId.value == null){
                    GeoId.value = geoId
                    //playAlertSound(true)
                }else{
                    if (GeoId.value != geoId){
                        secondGeo = geoId
                    }else{
                        GeoId.value = geoId
                    }
                }
            }else{
                if (secondGeo != null){
                    GeoId.value = secondGeo
                    secondGeo = null
                }else{
                    GeoId.value = null
                    //playAlertSound(false)
                }
            }
        }

    @RequiresApi(Build.VERSION_CODES.S)
    fun playAlertSound(boolean: Boolean, context:Context, bearing:Int) {
        var seconds = 0 // initial time in seconds
        var handler: Handler = Handler()

        val mMediaPlayerr = MediaPlayer.create(context, R.raw.sound_geofence_alert)
        //mMediaPlayerr.start()
        if (boolean){
            if (HomeActivity.Singlt.SoundSta.value == 1){
                val audio : AudioManager =  context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                val stVolLev = audio.getStreamVolume(AudioManager.STREAM_MUSIC)
                audio.setStreamVolume(AudioManager.STREAM_MUSIC,15,0)
                mMediaPlayerr.start()
                handler.post(object : Runnable {
                    override fun run() {
                        seconds++
                        handler.postDelayed(this, 1000)

                        if (seconds > 5){
                            audio.setStreamVolume(AudioManager.STREAM_MUSIC,stVolLev,0)
                            handler.removeCallbacksAndMessages(null)
                        }
                    }
                })



//                if(bearing == 0){
//                    val audio : AudioManager =  context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
//                    audio.setStreamVolume(AudioManager.STREAM_MUSIC,20,0)
//                    mMediaPlayerr.start()
//                }else{
//                    if (bearing-10 < HomeActivity.Singlt.bearingLoc.value && bearing+10 < HomeActivity.Singlt.bearingLoc.value){
//                        val audio : AudioManager =  context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
//                        audio.setStreamVolume(AudioManager.STREAM_MUSIC,20,0)
//                        mMediaPlayerr.start()
//                    }
//                }
            }else if (HomeActivity.Singlt.SoundSta.value == 2){
                handler.post(object : Runnable {
                    override fun run() {
                        seconds++
                        handler.postDelayed(this, 1000)
                        if (seconds % 2 == 1){
                            vibrate(context)

                        }
                        if (seconds >10){
                            handler.removeCallbacksAndMessages(null)
                        }
                    }
                })

            }
        }else{

        }
    }
    fun vibrate(context :Context){
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(50)
        }
    }
    }
}