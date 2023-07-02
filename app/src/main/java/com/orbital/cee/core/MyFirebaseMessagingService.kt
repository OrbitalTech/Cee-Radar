package com.orbital.cee.core
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.orbital.cee.CeeApplication
import com.orbital.cee.R
import com.orbital.cee.core.MyFirebaseMessagingService.NotificationService.body
import com.orbital.cee.core.MyFirebaseMessagingService.NotificationService.title

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val notification = remoteMessage.notification
        Log.d("DEBUG_NOTIFICATION_SERVICE_ON_RECEIVE",notification?.body.toString())
        val isAppInForeground = (applicationContext as CeeApplication).isAppInForeground()
        if (isAppInForeground) {
            notification?.let {
                title.value = it.title.toString()
                body.value = it.body.toString()
            }
        }


    }

    override fun onNewToken(token: String) {
        // Handle token refresh
        // Send the token to your server or perform any necessary actions
    }

    object NotificationService {
        var title = mutableStateOf<String?>(null)
        var body = mutableStateOf<String?>(null)
    }
}