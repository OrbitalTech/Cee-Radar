package com.orbital.cee.transitions

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.DetectedActivity

//fun Activity.requestActivityTransitionUpdates() {
//  val request = ActivityTransitionRequest(getActivitiesToTrack())
//    if (ActivityCompat.checkSelfPermission(
//            this,
//            Manifest.permission.ACTIVITY_RECOGNITION
//        ) != PackageManager.PERMISSION_GRANTED
//    ) {
//        return
//    }
//  val task = ActivityRecognitionClient(this).requestActivityTransitionUpdates(request,
//      TransitionsReceiver.getPendingIntent(this))
//
//  task.run {
//    addOnSuccessListener {
//      Log.d("TRANSITION_ACTIVITY", "success")
//    }
//    addOnFailureListener {
//      Log.d("TRANSITION_ACTIVITY", "failed")
//    }
//  }
//}

//fun Activity.removeActivityTransitionUpdates() {
//   if (ActivityCompat.checkSelfPermission(
//          this,
//          Manifest.permission.ACTIVITY_RECOGNITION
//      ) != PackageManager.PERMISSION_GRANTED
//  ) {
//      return
//  }
//    val task = ActivityRecognitionClient(this).removeActivityTransitionUpdates(
//      TransitionsReceiver.getPendingIntent(this))
//
//  task.run {
//    addOnSuccessListener {
//      Log.d("TRANSITION_ACTIVITY", "Success")
//    }
//    addOnFailureListener {
//      Log.d("TRANSITION_ACTIVITY","Failed")
//    }
//  }
//}

private fun getActivitiesToTrack(): List<ActivityTransition> =
    mutableListOf<ActivityTransition>()
        .apply {
          add(ActivityTransition.Builder()
              .setActivityType(DetectedActivity.STILL)
              .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
              .build())
          add(ActivityTransition.Builder()
              .setActivityType(DetectedActivity.STILL)
              .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
              .build())
          add(ActivityTransition.Builder()
              .setActivityType(DetectedActivity.WALKING)
              .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
              .build())
          add(ActivityTransition.Builder()
              .setActivityType(DetectedActivity.WALKING)
              .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
              .build())
          add(ActivityTransition.Builder()
              .setActivityType(DetectedActivity.RUNNING)
              .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
              .build())
          add(ActivityTransition.Builder()
              .setActivityType(DetectedActivity.RUNNING)
              .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
              .build())
        }