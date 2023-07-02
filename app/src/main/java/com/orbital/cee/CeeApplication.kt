package com.orbital.cee

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import com.orbital.cee.core.AppLifecycleManager
import com.orbital.cee.core.ExceptionListener
import dagger.hilt.android.HiltAndroidApp
import org.koin.android.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext
import org.koin.core.logger.Level

@HiltAndroidApp
class CeeApplication: Application() ,ExceptionListener
{
    private lateinit var appLifecycleManager: AppLifecycleManager
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate() {
        super.onCreate()
        setupExceptionHandler()
        val channel = NotificationChannel(
            "location",
            "Location",
            NotificationManager.IMPORTANCE_LOW
        )
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        appLifecycleManager = AppLifecycleManager()
        registerActivityLifecycleCallbacks(appLifecycleManager)
        GlobalContext.startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@CeeApplication)
            modules(appModule)
        }
    }
    fun isAppInForeground(): Boolean {
        return appLifecycleManager.isAppInForeground()
    }
    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        // TODO Make sure you are logging this issue some where like Crashlytics.
        // Also indicate that something went wrong to the user like maybe a dialog or an activity.
        throwable.message?.let { Log.d("ExampleApp", it) }
    }
    fun setupExceptionHandler(){
        Handler(Looper.getMainLooper()).post {
            while (true) {
                try {
                    Looper.loop()
                } catch (e: Throwable) {
                    uncaughtException(Looper.getMainLooper().thread, e)
                }
            }
        }
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            uncaughtException(t, e)
        }
    }
}