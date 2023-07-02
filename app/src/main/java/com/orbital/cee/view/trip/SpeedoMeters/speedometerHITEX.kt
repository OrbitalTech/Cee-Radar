package com.orbital.cee.view.trip.SpeedoMeters

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbital.cee.R
import com.orbital.cee.core.GeofenceBroadcastReceiver
import com.orbital.cee.core.MyLocationService
import com.orbital.cee.ui.theme.red
import com.orbital.cee.ui.theme.type_gray
import com.orbital.cee.utils.MetricsUtils
import com.orbital.cee.view.home.BottomSheets.incidentDistance
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SpeedometerHITEX(value:Float,speed:Int,bearing:Float,isNearReport:Boolean,reportType:Int,distance:Double){
    val conf = LocalConfiguration.current
    val context = LocalContext.current
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.DOWN

    val reportUI = MetricsUtils.getReportUiByReportType(reportType , context)
    Box(modifier = Modifier
        .size((415f.coerceAtMost(conf.screenWidthDp.toFloat())-60).dp)
        .border(
            width = 1.dp,
            color = Color.Black.copy(alpha = 0.08f),
            shape = CircleShape
        )
        .padding(10.dp),contentAlignment = Alignment.Center){
        Canvas(
            Modifier.fillMaxSize()
        ) {
            val radius = size.width/2
            val angleDegreeDifference = 8   // /10
            val speedVal = value * 36
            val angleRadDifference1 = (((angleDegreeDifference * speedVal) - 230f) * (Math.PI / 180f)).toFloat()
            val lineLength = radius * 0.90f
            (0..35).forEach {
                val angleRadDifference = (((angleDegreeDifference * it) - 230f) * (Math.PI / 180f)).toFloat()
                val lineColour = if (angleRadDifference1>=angleRadDifference){
                    Color(0XFF495CE8)
                }else{
                    Color(0x14000000)
                }
                val startOffsetLine = Offset(
                    x = lineLength * cos(angleRadDifference) + size.center.x,
                    y = lineLength * sin(angleRadDifference) + size.center.y
                )
                val endOffsetLine = Offset(
                    x = (radius - ((radius * .05f) / 2) ) * cos(angleRadDifference) + size.center.x,
                    y = (radius - ((radius * .05f) / 2) ) * sin(angleRadDifference) + size.center.y
                )
                drawLine(
                    color = lineColour,
                    start = startOffsetLine,
                    end = endOffsetLine,
                    strokeWidth = 30f
                )
            }
        }
        Column(modifier = Modifier
            .fillMaxHeight()
            .padding(top = 35.dp, bottom = 20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(painter = painterResource(id = R.drawable.ic_triangle), contentDescription = "", tint = Color.Unspecified, modifier = Modifier.rotate(180f))
                androidx.compose.material.Text(
                    MetricsUtils.bearingToCoordinate(bearing),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF495CE8)
                )
                Spacer(modifier = Modifier.height(10.dp))
                AnimatedVisibility(
                    modifier = Modifier.fillMaxWidth(),
                    visible = isNearReport,
                    enter =  fadeIn(),
                    exit = fadeOut()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(35.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(modifier = Modifier.size(35.dp),painter = painterResource(id = R.drawable.bg_btn_place_cam_fab_main_scr), contentDescription = "", tint = reportUI.color1.copy(alpha = 0.1f))
                            Icon(painter = painterResource(id = reportUI.icon), modifier = Modifier.size(18.dp), tint = reportUI.color1, contentDescription = "")
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                        androidx.compose.material.Text(incidentDistance(distance.toFloat()), fontWeight = FontWeight.Bold, fontSize = 16.sp,color = Color(0xFF495CE8))

                    }
                }
            }
            MyLocationService.GlobalStreetSpeed.streetSpeedLimit.value?.let {
                Text("${it.toInt()} km/h", fontWeight = FontWeight.Bold, fontSize = 14.sp,color = Color(0xFF495CE8))
            }
        }
        Column(modifier = Modifier
            .fillMaxHeight()
            .padding(top = 45.dp, bottom = 20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center ) {
            androidx.compose.material.Text(
                df.format(speed),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 75.sp,
                color = Color(0xFF495CE8),
                fontFamily = FontFamily(Font(R.font.off_bit_trial_bold_t))
            )

            androidx.compose.material.Text(
                "Km/h",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF495CE8)
            )
        }
    }

}

@Composable
@Preview
fun speedometerHITEXPreview(){
    val aa = 0.19f
    SpeedometerHITEX(aa,63,45f,true,3,45.0)
}