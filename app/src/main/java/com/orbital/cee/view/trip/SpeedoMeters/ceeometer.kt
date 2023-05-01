package com.orbital.cee.view.trip.SpeedoMeters

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.orbital.cee.core.GeofenceBroadcastReceiver

@Composable
fun ceeOMeter(value:Float){
    val conf = LocalConfiguration.current
    Log.d("ScreenDebugDP",conf.screenWidthDp.toString())
    Canvas(modifier = Modifier.size(size = if(conf.screenWidthDp<350){220.dp}else{300.dp})) {
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


}

@Composable
@Preview
fun ceeOMeterPreview(){
    val aa = 0.90f
    ceeOMeter(aa)
}