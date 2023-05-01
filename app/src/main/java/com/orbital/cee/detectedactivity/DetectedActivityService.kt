
package com.orbital.cee.detectedactivity

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.ActivityRecognitionClient

const val ACTIVITY_UPDATES_INTERVAL = 1000L

class DetectedActivityService : Service() {

  inner class LocalBinder : Binder() {

    val serverInstance: DetectedActivityService
      get() = this@DetectedActivityService
  }

  override fun onBind(p0: Intent?): IBinder = LocalBinder()

//  override fun onCreate() {
//    super.onCreate()
////    requestActivityUpdates()
//  }

//  private fun requestActivityUpdates() {
//     if (ActivityCompat.checkSelfPermission(
//        this,
//        Manifest.permission.ACTIVITY_RECOGNITION
//      ) != PackageManager.PERMISSION_GRANTED
//    ) {
//      return
//    }
//    val task =  ActivityRecognitionClient(this).requestActivityUpdates(ACTIVITY_UPDATES_INTERVAL,
//        DetectedActivityReceiver.getPendingIntent(this))
//
//    task.run {
//      addOnSuccessListener {
//        Log.d("TRANSITION_ACTIVITY", "Success")
//      }
//      addOnFailureListener {
//        Log.d("TRANSITION_ACTIVITY","Failed")
//      }
//    }
//  }

//  private fun removeActivityUpdates() {
//    if (ActivityCompat.checkSelfPermission(
//        this,
//        Manifest.permission.ACTIVITY_RECOGNITION
//      ) != PackageManager.PERMISSION_GRANTED
//    ) {
//      return
//    }
//    val task = ActivityRecognitionClient(this).removeActivityUpdates(
//        DetectedActivityReceiver.getPendingIntent(this))
//
//    task.run {
//      addOnSuccessListener {
//        Log.d("TRANSITION_ACTIVITY", "Success")
//      }
//      addOnFailureListener {
//        Log.d("TRANSITION_ACTIVITY", "Failed")
//      }
//    }
//  }

//  override fun onDestroy() {
//    super.onDestroy()
//    removeActivityUpdates()
//    NotificationManagerCompat.from(this).cancel(DETECTED_ACTIVITY_NOTIFICATION_ID)
//  }
}