package com.orbital.cee.core

import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.orbital.cee.core.Constants.PERMISSION_BACKGROUND_LOCATION_REQUEST_CODE
import com.orbital.cee.core.Constants.PERMISSION_LOCATION_REQUEST_CODE
import pub.devrel.easypermissions.EasyPermissions


object Permissions {

    fun hasLocationPermission(context: Context) =
        EasyPermissions.hasPermissions(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    @RequiresApi(Build.VERSION_CODES.Q)
    fun hasActivityRecognitionPermission(context: Context) =if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        EasyPermissions.hasPermissions(
            context,
            Manifest.permission.ACTIVITY_RECOGNITION
        )}else{
            false
    }
    fun requestsLocationPermission(activity: Activity) {
        EasyPermissions.requestPermissions(
            activity,
            "This application cannot work without Location Permission.",
            PERMISSION_LOCATION_REQUEST_CODE,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    fun requestsActivityRecognitionPermission(activity: Activity) {
        EasyPermissions.requestPermissions(
            activity,
            "Cee needs Activity-Recognition Permission. For a better job",
            PERMISSION_LOCATION_REQUEST_CODE,
            Manifest.permission.ACTIVITY_RECOGNITION
        )
    }

    fun hasBackgroundLocationPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
        return true
    }

    fun requestsBackgroundLocationPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                activity,
                "Cee needed always in use location data to enable finding reports near your location & geofencing even when the app is closed or not in use.",
                PERMISSION_BACKGROUND_LOCATION_REQUEST_CODE,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

}