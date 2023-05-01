package com.orbital.cee.transitions

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionEvent
import com.google.android.gms.location.ActivityTransitionResult
import com.orbital.cee.core.SupportedActivity

const val TRANSITIONS_RECEIVER_ACTION = "com.orbital.cee_transitions_receiver_action"
private const val TRANSITION_PENDING_INTENT_REQUEST_CODE = 200

class TransitionsReceiver: BroadcastReceiver() {

  var action: ((SupportedActivity) -> Unit)? = null

  companion object {

    fun getPendingIntent(context: Context): PendingIntent {
      val intent = Intent(TRANSITIONS_RECEIVER_ACTION)
      return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT  )
      } else {
        PendingIntent.getBroadcast(context, 0, intent,  PendingIntent.FLAG_UPDATE_CURRENT  )
      }
    }
  }

  override fun onReceive(context: Context, intent: Intent) {
    // 1
    if (ActivityTransitionResult.hasResult(intent)) {
      // 2
      val result = ActivityTransitionResult.extractResult(intent)
      result?.let { handleTransitionEvents(it.transitionEvents) }
    }
  }

  private fun handleTransitionEvents(transitionEvents: List<ActivityTransitionEvent>) {
    transitionEvents
        // 3
        .filter { it.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER }
        // 4
        .forEach {  action?.invoke(SupportedActivity.fromActivityType(it.activityType)) }
  }
}
