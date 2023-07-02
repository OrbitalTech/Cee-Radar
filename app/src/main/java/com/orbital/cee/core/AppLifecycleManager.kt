package com.orbital.cee.core

import android.app.Activity
import android.app.Application
import android.os.Bundle

class AppLifecycleManager : Application.ActivityLifecycleCallbacks {

    private var foregroundActivityCount = 0

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        // Not used in this example
    }

    override fun onActivityStarted(activity: Activity) {
        // Not used in this example
    }

    override fun onActivityResumed(activity: Activity) {
        foregroundActivityCount++
    }

    override fun onActivityPaused(activity: Activity) {
        foregroundActivityCount--
    }

    override fun onActivityStopped(activity: Activity) {
        // Not used in this example
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        // Not used in this example
    }

    override fun onActivityDestroyed(activity: Activity) {
        // Not used in this example
    }

    fun isAppInForeground(): Boolean {
        return foregroundActivityCount > 0
    }
}