package com.orbital.cee.utils

import android.content.Context
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.compose.ui.graphics.Color
import com.orbital.cee.R
import com.orbital.cee.model.UserPermission
import com.orbital.cee.model.UserTiers
import com.orbital.cee.ui.theme.blue
import com.orbital.cee.ui.theme.blurple
import com.orbital.cee.ui.theme.light_blue
import com.orbital.cee.ui.theme.light_gray
import com.orbital.cee.ui.theme.light_orange
import com.orbital.cee.ui.theme.light_turquoise
import com.orbital.cee.ui.theme.orange
import com.orbital.cee.ui.theme.turquoise
import com.orbital.cee.ui.theme.type_gray
import com.orbital.cee.ui.theme.white
import com.orbital.cee.view.home.BottomSheets.SpeedLimits
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
        fun getRandomString(length: Int) : String {
            val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
            return (1..length)
                .map { allowedChars.random() }
                .joinToString("")
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
            }else if(currentPoint>0){
                0f
            }else{
                1f.minus((100f.minus(currentPoint)).div(100f))
            }
        }
        fun getNextLevelRequiredPoints(currentPoint:Int):Int{
            return if (currentPoint < 100){
                100
            }else if (currentPoint < 200){
                200
            }else if (currentPoint < 600){
                600
            }else if ( currentPoint < 1200){
                1200
            }else if(currentPoint < 2800){
                2800
            }else if(currentPoint < 5600){
                5600
            } else if(currentPoint < 11200){
                11200
            }else{
                100
            }
        }
        fun userTypeToUserTier(userType:Int?): UserTiers {
            return when(userType){
                2->{ UserTiers.PLEB }
                3->{ UserTiers.BASIC }
                4->{ UserTiers.MID_LEVEL }
                5->{ UserTiers.CEEKER }
                6->{ UserTiers.MODERATOR }
                7->{ UserTiers.ADMIN }
                else->{ UserTiers.GUEST }
            }
        }
        fun getPermissionsByUserTier(userTiers:UserTiers): UserPermission {
            return when(userTiers){
                UserTiers.GUEST ->{ UserPermission(alertLimit = 5)}
                UserTiers.PLEB ->{
                    UserPermission(
                        isCanRecordTrip = true,
                        isCanFeedback = true,
                        tripRecordLimit = 5,
                        alertLimit = -1
                    )
                }
                UserTiers.BASIC ->{
                    UserPermission(
                        isCanAddReport = true,
                        isCanRecordTrip = true,
                        isCanFeedback = true,
                        tripRecordLimit = 10,
                        reportLimitPerHour = 2,
                        alertLimit = -1
                    )
                }
                UserTiers.MID_LEVEL ->{
                    UserPermission(
                        isCanAddReport = true,
                        isCanRecordTrip = true,
                        isCanFeedback = true,
                        tripRecordLimit = 20,
                        reportLimitPerHour = 3,
                        alertLimit = -1
                    )
                }
                UserTiers.CEEKER ->{
                    UserPermission(
                        isCanAddReport = true,
                        isCanRecordTrip = true,
                        isCanFeedback = true,
                        tripRecordLimit = -1,
                        reportLimitPerHour = 5,
                        alertLimit = -1
                    )
                }
                UserTiers.MODERATOR ->{
                    UserPermission(
                        isCanAddReport = true,
                        isCanRecordTrip = true,
                        isCanFeedback = true,
                        tripRecordLimit = -1,
                        reportLimitPerHour = -1,
                        alertLimit = -1
                    )
                }
                UserTiers.ADMIN ->{
                    UserPermission(
                        isCanAddReport = true,
                        isCanRecordTrip = true,
                        isCanFeedback = true,
                        tripRecordLimit = -1,
                        reportLimitPerHour = -1,
                        alertLimit = -1
                    )
                }
                else->{ UserPermission() }
            }
        }

        fun getReportTypeByReportTypeAndSpeedLimit(reportType: Int,speedLimits: Int?): Int?{
            return when(reportType){
                2->R.drawable.marker_crash
                3->R.drawable.marker_police
                4->R.drawable.marker_construction
                8->R.drawable.ic_marker_road_hazard
                1->{
                    if (speedLimits == null){
                        R.drawable.marker_road_camera_0
                    }else{
                        if (speedLimits>55){
                            if (speedLimits>80){
                                when(speedLimits){
                                    100->{ R.drawable.marker_road_camera_100 }
                                    110->{ R.drawable.marker_road_camera_110 }
                                    120->{ R.drawable.marker_road_camera_120 }
                                    90->{ R.drawable.marker_road_camera_90 }
                                    130->{ R.drawable.marker_road_camera_130 }
                                    140->{ R.drawable.marker_road_camera_140 }
                                    else->null
                                }
                            }else{
                                when(speedLimits){
                                    60->{ R.drawable.marker_road_camera_60 }
                                    80->{ R.drawable.marker_road_camera_80 }
                                    70->{ R.drawable.marker_road_camera_70 }
                                    else->null
                                }
                            }
                        }else{
                            when(speedLimits){
                                0->{ R.drawable.marker_road_camera_0 }
                                50->{ R.drawable.marker_road_camera_50 }
                                40->{ R.drawable.marker_road_camera_40 }
                                55->{ R.drawable.marker_road_camera_55 }
                                45->{ R.drawable.marker_road_camera_45 }
                                35->{ R.drawable.marker_road_camera_35 }
                                30->{ R.drawable.marker_road_camera_30 }
                                25->{ R.drawable.marker_road_camera_25 }
                                20->{ R.drawable.marker_road_camera_20 }
                                15->{ R.drawable.marker_road_camera_15 }
                                10->{ R.drawable.marker_road_camera_10 }
                                else->null
                            }
                        }
                    }
                }
                5->{
                    if (speedLimits == null){
                        R.drawable.marker_road_camera_0
                    }else {
                        if (speedLimits > 55) {
                            if (speedLimits > 80) {
                                when (speedLimits) {
                                    100 -> { R.drawable.marker_static_camera_100 }
                                    110 -> { R.drawable.marker_static_camera_110 }
                                    120 -> { R.drawable.marker_static_camera_120 }
                                    90 -> { R.drawable.marker_static_camera_90 }
                                    130 -> { R.drawable.marker_static_camera_130 }
                                    140 -> { R.drawable.marker_static_camera_140 }
                                    else -> null
                                }
                            } else {
                                when (speedLimits) {
                                    60 -> { R.drawable.marker_static_camera_60 }
                                    80 -> { R.drawable.marker_static_camera_80 }
                                    70 -> { R.drawable.marker_static_camera_70 }
                                    else -> null
                                }
                            }
                        } else {
                            when (speedLimits) {
                                0 -> { R.drawable.marker_static_camera_0 }
                                50 -> { R.drawable.marker_static_camera_50 }
                                40 -> { R.drawable.marker_static_camera_40 }
                                55 -> { R.drawable.marker_static_camera_55 }
                                45 -> { R.drawable.marker_static_camera_45 }
                                35 -> { R.drawable.marker_static_camera_35 }
                                30 -> { R.drawable.marker_static_camera_30 }
                                25 -> { R.drawable.marker_static_camera_25 }
                                20 -> { R.drawable.marker_static_camera_20 }
                                15 -> { R.drawable.marker_static_camera_15 }
                                10 -> { R.drawable.marker_static_camera_10 }
                                else -> null
                            }
                        }
                    }
                }
                6->{
                    if (speedLimits == null){
                        R.drawable.marker_road_camera_0
                    }else {
                        if (speedLimits > 55) {
                            if (speedLimits > 80) {
                                when (speedLimits) {
                                    100 -> { R.drawable.marker_point_to_point_100 }
                                    110 -> { R.drawable.marker_point_to_point_110 }
                                    120 -> { R.drawable.marker_point_to_point_120 }
                                    90 -> { R.drawable.marker_point_to_point_90 }
                                    130 -> { R.drawable.marker_point_to_point_130 }
                                    140 -> { R.drawable.marker_point_to_point_140 }
                                    else -> null
                                }
                            } else {
                                when (speedLimits) {
                                    60 -> { R.drawable.marker_point_to_point_60 }
                                    80 -> { R.drawable.marker_point_to_point_80 }
                                    70 -> { R.drawable.marker_point_to_point_70 }
                                    else -> null
                                }
                            }
                        } else {
                            when (speedLimits) {
                                0 -> { R.drawable.marker_point_to_point_0 }
                                50 -> { R.drawable.marker_point_to_point_50 }
                                40 -> { R.drawable.marker_point_to_point_40 }
                                55 -> { R.drawable.marker_point_to_point_55 }
                                45 -> { R.drawable.marker_point_to_point_45 }
                                35 -> { R.drawable.marker_point_to_point_35 }
                                30 -> { R.drawable.marker_point_to_point_30 }
                                25 -> { R.drawable.marker_point_to_point_25 }
                                20 -> { R.drawable.marker_point_to_point_20 }
                                15 -> { R.drawable.marker_point_to_point_15 }
                                10 -> { R.drawable.marker_point_to_point_10 }
                                else -> null
                            }
                        }
                    }
                }
                7->R.drawable.ic_red_traffic_tight
                10-> R.drawable.ic_marker_road_static_camera
                11->R.drawable.marker_point_to_point_0
                405->R.drawable.marker_static_camera_not_active
                406->R.drawable.ic_disabled_marker_point_to_point_camera
                else-> null
            }
        }



        fun getReportUiByReportType(reportType:Int,context: Context):ReportTheme{
            return when(reportType){
                1->{ReportTheme(
                    color1 = blurple,
                    color2 = Color(0xFFEDEFFD),
                    title = context.getString(R.string.btn_home_report_action_sheet_roadCam),
                    icon = R.drawable.ic_camera
                )}
                2->{ReportTheme(
                    color1 = orange,
                    color2 = light_orange,
                    title = context.getString(R.string.btn_home_report_action_sheet_carCrash),
                    icon = R.drawable.ic_carcrash
                )}
                3->{ReportTheme(
                    color1 = blue,
                    color2 = light_blue,
                    title = context.getString(R.string.btn_home_report_action_sheet_police),
                    icon = R.drawable.ic_new_police
                )}
                4->{ReportTheme(
                    color1 = turquoise,
                    color2 = light_turquoise,
                    title = context.getString(R.string.btn_home_report_action_sheet_construction),
                    icon = R.drawable.ic_new_construction
                )}
                5->{ReportTheme(
                    color1 = blurple,
                    color2 = Color(0xFFEDEFFD),
                    title = context.getString(R.string.btn_home_report_action_sheet_staticCam),
                    icon = R.drawable.ic_static_camera
                )}
                6->{ReportTheme(
                    color1 = blurple,
                    color2 = Color(0xFFEDEFFD),
                    title = context.getString(R.string.btn_home_report_action_sheet_p2pCam),
                    icon = R.drawable.ic_point_to_point_camera
                )}
                7->{ReportTheme(
                    color1 = white,
                    color2 = Color(0xFFE84949),
                    title = context.getString(R.string.lbl_home_report_feedback_sheet_report_type_redlight),
                    icon = R.drawable.ic_trafic_light
                )}
                405->{ReportTheme(
                    color1 = type_gray,
                    color2 = light_gray,
                    title = context.getString(R.string.btn_home_report_action_sheet_staticCam_not_active),
                    icon = R.drawable.ic_static_camera
                )}
                406->{ReportTheme(
                    color1 = type_gray,
                    color2 = light_gray,
                    title = context.getString(R.string.btn_home_report_action_sheet_staticCam_not_active),
                    icon = R.drawable.ic_point_to_point_camera
                )}

                else->{
                    ReportTheme(
                        color1 = blurple,
                        color2 = Color(0xFFEDEFFD),
                        title = context.getString(R.string.btn_home_report_action_sheet_roadCam),
                        icon = R.drawable.ic_camera)
                }
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