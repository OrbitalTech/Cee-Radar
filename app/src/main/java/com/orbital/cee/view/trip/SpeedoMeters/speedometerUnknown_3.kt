package com.orbital.cee.view.trip.SpeedoMeters

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.orbital.cee.core.GeofenceBroadcastReceiver
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun speedometerUnknown_3(value:Float){
    val conf = LocalConfiguration.current
    Canvas(modifier = Modifier.size(size = if(conf.screenWidthDp<350){220.dp}else{280.dp})) {
        drawArc(
            brush = Brush.radialGradient(
                center = center,
                radius = (size.width*0.735).toFloat(),
                colors = listOf(
                    Color(0x39C8CEFF),
                    Color(0xFFFFFFFF),
                ), tileMode = TileMode.Repeated
            ),
            topLeft =  Offset(25f, 25f),
            size = Size(size.width * 0.94f,size.height *0.96f),
            startAngle = 90f,
            sweepAngle = 360 * 1f,
            useCenter = true,
            style = Stroke(
                15.dp.toPx(),
                cap = StrokeCap.Square,
            ),
        )
        drawArc(
            brush = Brush.radialGradient(
                center = center,
                radius = size.height*0.2f,
                colors = listOf(
                    Color(0x8DC8CEFF),
                    Color(0xFFF4F5FF),
                    Color(0xFFF3F5FF),
                    Color(0x8DC8CEFF)
                ), tileMode = TileMode.Repeated
            ),
            topLeft =  Offset(size.width * 0.075f, size.width * 0.075f),
            size = size*0.85f, startAngle = 90f, sweepAngle = 360 * 1f, useCenter = true,
            style = Stroke(30.dp.toPx(), cap = StrokeCap.Square,),
        )
        drawArc(
            brush = Brush.radialGradient(
                center = center,
                radius = size.height*0.36f,
                colors = listOf(
                    Color(0xFFEDEFFD),
                    Color(0xFFFFFFFF),
                    Color(0xFFFFFFFF),
                    Color(0xFFFFFFFF),
                    Color(0xFFEDEFFD)
                ),
                tileMode = TileMode.Mirror
            ),
            size = size*0.85f,
            startAngle = 45f,
            sweepAngle = 360 * 0.25f,
            useCenter = false,
            topLeft =  Offset(size.width * 0.075f, size.width * 0.075f),
            style = Stroke(
                30.dp.toPx(),
                cap = StrokeCap.Butt,
            ),
        )
        drawArc(
            brush = Brush.radialGradient(
                center = center,
                radius = size.height*0.2f,
                colors = listOf(
                    Color(0XFF495CE8),
                    Color(0xFF5C6CDF),
                    Color(0xFF7A84DF),
                    Color(0xFFCED1EB),
                ),
                tileMode = TileMode.Mirror
            ),
            size = size*0.85f,
            startAngle = 135f,
            sweepAngle = 290 * value,
            useCenter = false,
            topLeft =  Offset(size.width * 0.075f, size.width * 0.075f),
            style = Stroke(
                30.dp.toPx(),
                cap = StrokeCap.Butt,
            ),
        )
    }
}

@Composable
@Preview
fun speedometerUnknown_3Preview(){
    val aa = 0.22f
    speedometerUnknown_3(aa)
}