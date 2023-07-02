package com.orbital.cee.view.trip.SpeedoMeters

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
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
import com.orbital.cee.utils.MetricsUtils
import com.orbital.cee.view.home.BottomSheets.incidentDistance
import java.math.RoundingMode
import java.text.DecimalFormat

@Composable
fun CeeOMeter(value:Float,speed:Int,bearing:Float,isNearReport:Boolean,reportType:Int,distance:Double){
    val conf = LocalConfiguration.current
    val context = LocalContext.current
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.DOWN
    Log.d("DEBUG_SCREEN_WIDITH_IN_SPEEDO",conf.screenWidthDp.toString())
    val reportUI = MetricsUtils.getReportUiByReportType(reportType , context)
    Box(modifier = Modifier.size((415f.coerceAtMost(conf.screenWidthDp.toFloat())-60).dp), contentAlignment = Alignment.Center){
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFECEEFD),
                        Color(0xFFECEEFD),
                    )
                ),
                startAngle = 127f,
                sweepAngle = 360 * 0.79f,
                useCenter = false,
                style = Stroke(
                    6.dp.toPx(),
                    cap = StrokeCap.Round,
                ),
            )
            drawArc(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF495CE8) ,
                        Color(0xFF495CE8) ,
                    )
                ),
                startAngle = 127f,
                sweepAngle = 290 * value,
                useCenter = false,
                style = Stroke(
                    14.dp.toPx(),
                    cap = StrokeCap.Round,
                ),
            )
        }

        Column(modifier = Modifier
            .fillMaxHeight()
            .padding(top = 35.dp, bottom = 20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(painter = painterResource(id = R.drawable.ic_triangle), contentDescription = "", tint = Color.Unspecified, modifier = Modifier.rotate(180f))
                Text(
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
                        Text(incidentDistance(distance.toFloat()), fontWeight = FontWeight.Bold, fontSize = 16.sp,color = Color(0xFF495CE8))

                    }
                }
            }
            MyLocationService.GlobalStreetSpeed.streetSpeedLimit.value?.let {
                Text("${it.toInt()} km/h", fontWeight = FontWeight.Bold, fontSize = 14.sp,color = Color(0xFF495CE8))
            }
//            Text("40 km/h", fontWeight = FontWeight.Bold, fontSize = 14.sp,color = Color(0xFF495CE8))
        }
        Column(modifier = Modifier
            .fillMaxHeight()
            .padding(top = 35.dp, bottom = 20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center ) {
            Text(
                df.format(speed),
                fontWeight = FontWeight.SemiBold,
                fontSize = 80.sp,
                color = Color(0xFF495CE8),
            )

            Text(
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
fun ceeOMeterPreview(){
    val aa = 0.90f
    CeeOMeter(aa,63,45f,true,3,45.0)
}