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
                bearing == 360f -> {"N"}
                bearing == 90f -> {"E"}
                bearing == 180f -> {"S"}
                bearing == 270f -> {"W"}
                bearing > 270.0 && bearing < 360.0 -> {"NW"}
                bearing > 180.0 && bearing < 270.0 -> {"SW"}
                bearing > 90.0 && bearing < 180.0 -> {"SE"}
                bearing > 0 && bearing < 90.0 -> {"NE"}
                else -> {"N/A"}
            }
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