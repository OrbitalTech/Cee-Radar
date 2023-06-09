
package com.orbital.cee.detectedactivity

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity
import com.orbital.cee.R
import com.orbital.cee.core.SupportedActivity
import com.orbital.cee.view.MainActivity

private const val DETECTED_PENDING_INTENT_REQUEST_CODE = 100
private const val RELIABLE_CONFIDENCE = 75

private const val DETECTED_ACTIVITY_CHANNEL_ID = "detected_activity_channel_id"
const val DETECTED_ACTIVITY_NOTIFICATION_ID = 10
const val SUPPORTED_ACTIVITY_KEY = "activity_key"
class DetectedActivityReceiver : BroadcastReceiver() {

  companion object {

    fun getPendingIntent(context: Context): PendingIntent {
      val intent = Intent(context, DetectedActivityReceiver::class.java)
      return PendingIntent.getBroadcast(context, DETECTED_PENDING_INTENT_REQUEST_CODE, intent,
          PendingIntent.FLAG_UPDATE_CURRENT)
    }
  }

  override fun onReceive(context: Context, intent: Intent) {
    if (ActivityRecognitionResult.hasResult(intent)) {
      val result = ActivityRecognitionResult.extractResult(intent)
      result?.let { handleDetectedActivities(it.probableActivities, context) }
    }
  }

  private fun handleDetectedActivities(detectedActivities: List<DetectedActivity>,
      context: Context) {
    detectedActivities
        .filter {
          it.type == DetectedActivity.STILL ||
              it.type == DetectedActivity.WALKING ||
              it.type == DetectedActivity.RUNNING
        }
        .filter { it.confidence > RELIABLE_CONFIDENCE }
        .run {
          if (isNotEmpty()) {
            showNotification(this[0], context)
          }
        }
  }

  private fun showNotification(detectedActivity: DetectedActivity, context: Context) {
    createNotificationChannel(context)
    val intent = Intent(context, MainActivity::class.java).apply {
      putExtra(SUPPORTED_ACTIVITY_KEY, detectedActivity.type)
    }
    val pendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT  )
    } else {
        PendingIntent.getBroadcast(context, 0, intent,  PendingIntent.FLAG_UPDATE_CURRENT  )
    }

    val activity = SupportedActivity.fromActivityType(detectedActivity.type)

    val builder = NotificationCompat.Builder(context, DETECTED_ACTIVITY_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_cee_select_lang)
        .setContentTitle(activity.activityText+" test..")
        .setContentText("Your pet is ${detectedActivity.confidence}% sure of it")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)
        .setOnlyAlertOnce(true)
        .setAutoCancel(true)

    with(NotificationManagerCompat.from(context)) {
      if (ActivityCompat.checkSelfPermission(
              context,
              Manifest.permission.POST_NOTIFICATIONS
          ) != PackageManager.PERMISSION_GRANTED
      ) {
          return
      }
        notify(DETECTED_ACTIVITY_NOTIFICATION_ID, builder.build())
    }
  }

  private fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val name = "detected_activity_channel_name"
      val descriptionText = "detected_activity_channel_description"
      val importance = NotificationManager.IMPORTANCE_DEFAULT
      val channel = NotificationChannel(DETECTED_ACTIVITY_CHANNEL_ID, name, importance).apply {
        description = descriptionText
        enableVibration(false)
      }
      // Register the channel with the system
      val notificationManager: NotificationManager =
          context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.createNotificationChannel(channel)
    }
  }
}