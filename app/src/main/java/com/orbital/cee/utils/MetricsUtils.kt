package com.orbital.cee.utils

import android.content.ClipDescription
import android.content.ContentValues.TAG
import android.content.Context
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.telephony.TelephonyManager
import android.util.Log
import androidx.compose.ui.graphics.Color
import com.google.firebase.Timestamp
import com.orbital.cee.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MetricsUtils {
    companion object {
        fun getDuration(startDate: Date?, finishDate: Date?): String {
            return if (startDate != null && finishDate != null) {
                var seconds: Long = TimeUnit.MILLISECONDS.toSeconds(finishDate.time - startDate.time)
                var minute = seconds / 60
                seconds %= 60
                var hour = minute / 60
                minute %= 60
                var days = hour / 24
                hour %= 24
                String.format("%02d", hour) + ":" + String.format("%02d", minute) + ":" + String.format("%02d", seconds)
            } else {
                "-"
            }
        }
        fun getRemain(watchTime: Long?,dateTimeNow : Long?): String {
            return if (watchTime != null && dateTimeNow != null) {
                var tmpSeconds: Long = dateTimeNow - watchTime
                var seconds =  1800 - tmpSeconds
                var minute = seconds / 60
                seconds %= 60
                var hour = minute / 60
                minute %= 60
                var days = hour / 24
                hour %= 24
                /* String.format( "%02d", hour) + ":" +*/ String.format("%02d", minute) + ":" + String.format("%02d", seconds)
            } else {
                "-"
            }
        }
        fun getRemainSeconds(seconds : Long?): String {
            var _seconds = seconds
            return if (_seconds != null) {
                var minute = _seconds / 60
                _seconds %= 60
                var hour = minute / 60
                minute %= 60
                var days = hour / 24
                hour %= 24
                String.format("%02d", hour) + ":" + String.format("%02d", minute) + ":" + String.format("%02d", _seconds)
            } else {
                "-"
            }
        }
        fun getRemainInt(watchTime: Long?,dateTimeNow : Long?): Float {
            return if (watchTime != null && dateTimeNow != null) {
                val tmpSeconds: Long = dateTimeNow - watchTime
                val seconds =  1800 - tmpSeconds
                seconds/1800f
            } else {
                0f
            }
        }
        fun getSeconds(startDate: Date?, finishDate: Date?): Long {
            return if (startDate != null && finishDate != null) {
                var seconds: Long = TimeUnit.MILLISECONDS.toSeconds(finishDate.time - startDate.time)
                seconds
            } else {
               0
            }
        }
        fun getAddress(lat: Double, lng: Double, context: Context): String {
            return try{
                val geocoder = Geocoder(context)
                val list = geocoder.getFromLocation(lat, lng, 1)
                list?.get(0)?.getAddressLine(0) ?: "-"

            }catch (e:Exception){
                "Unknown"
            }
        }
        fun isOnline(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
            return false
        }
        fun bearingToCoordinate(bearing: Float):String{
            return when {
                bearing > 350f && bearing <= 360 -> {"N"}
                bearing > 80f && bearing <= 100 -> {"E"}
                bearing > 170f && bearing <= 190 -> {"S"}
                bearing > 260f && bearing <= 280 -> {"W"}
                bearing > 260.0 && bearing < 360.0 -> {"NW"}
                bearing > 170.0 && bearing < 260.0 -> {"SW"}
                bearing > 80.0 && bearing < 170.0 -> {"SE"}
                bearing > 0 && bearing < 90.0 -> {"NE"}
                else -> {"N/A"}
            }
        }

        fun getDeviceId(context: Context): String? {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            var deviceId: String? = null

            deviceId = if (telephonyManager.deviceId != null) {
                telephonyManager.deviceId
            } else {
                android.provider.Settings.Secure.getString(context.contentResolver, android.provider.Settings.Secure.ANDROID_ID)
            }
            return deviceId
        }
        fun reportTypeToReport(reportType:Int):ReportTheme{

            return ReportTheme(color1 = Color.White, color2 = Color.White, title = "", icon = 0)
        }
        fun convertDateToLong(date: String): Long {
            val df = SimpleDateFormat("yyyy.MM.dd HH:mm")
            return df.parse(date).time
        }
        fun convertLongToTime(time: Long): String {
            val date = Date(time)
            val format = SimpleDateFormat("yyyy.MM.dd HH:mm")
            return format.format(date)
        }
        fun calculatePointRemainToNextLevel(currentPoint:Int):Int{
            return if (currentPoint >= 2800){
                1000000 - currentPoint
            }else if (currentPoint >= 1200){
                2800 - currentPoint
            }else if (currentPoint >= 600){
                1200 - currentPoint
            }else if ( currentPoint >= 200){
                600 - currentPoint
            }else if(currentPoint >= 100){
                200 - currentPoint
            }else{
                100 - currentPoint
            }
        }
        fun calculatePointRemainToNextLevelPersint(currentPoint:Int):Float{
            return if (currentPoint >= 2800){
                1f.minus((1000000f.minus(currentPoint)).div(1000000f))
            }else if (currentPoint >= 1200){
                1f.minus((2800f.minus(currentPoint)).div(2800f))
            }else if (currentPoint >= 600){
                1f.minus( (1200f.minus(currentPoint)).div(1200f))
            }else if ( currentPoint >= 200){
                1f.minus((600f.minus(currentPoint)).div(600f))
            }else if(currentPoint >= 100){
                1f.minus((200f.minus(currentPoint)).div(200f))
            }else{
                1f.minus((100f.minus(currentPoint)).div(200f))
            }
        }
    }

}
data class ReportTheme(
    var color1: Color,
    var color2: Color,
    var color3: Color? = null,
    var title: String,
    var description: String? = null,
    var icon : Int = R.drawable.ic_cee_select_lang,
)