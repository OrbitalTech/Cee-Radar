package com.orbital.cee.view.trip.SpeedoMeters

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.orbital.cee.core.GeofenceBroadcastReceiver
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun speedometerUnknown(value:Float){
    val conf = LocalConfiguration.current


    val a = remember {
        mutableStateOf("")
    }

    Canvas(
        Modifier.fillMaxSize()
    ) {
        drawArc(
            color = Color(0XFFECEEFD),
            127.7f,
            360 * 0.79f,
            false,
            style = Stroke(20.dp.toPx(), cap = StrokeCap.Round),
            size = size *0.85f,
            topLeft = Offset(size.width * 0.075f, size.width * 0.075f)
        )

        drawArc(
            color = Color(0XFF495CE8),
            startAngle = 127.7f,
            sweepAngle = 285 * value,
            false,
            style = Stroke(20.dp.toPx(), cap = StrokeCap.Round),
            size = size *0.85f,
            topLeft = Offset(size.width * 0.075f, size.width * 0.075f)
        )
        drawArc(
            color = Color(0x34ECEEFD),
            startAngle = 127.7f,
            sweepAngle = 285 * value,
            false,
            style = Stroke(2.dp.toPx(), cap = StrokeCap.Round),
            size = size *0.85f,
            topLeft = Offset(size.width * 0.075f, size.width * 0.075f)
        )


        val angleInDegrees = (285 * value) + 37.0
        val radius = (size.height*0.848 / 2)
        val x = -(radius * sin(Math.toRadians(angleInDegrees))).toFloat() + (size.width / 2)
        val y = (radius * cos(Math.toRadians(angleInDegrees))).toFloat() + (size.height / 2)

        drawCircle(
            color = Color.White,
            radius = 10f,
            center = Offset(x,  y)
        )
        drawArc(
            color = Color(0x0D000000),
            startAngle = 127f,
            sweepAngle = 360f,
            false,
            style = Stroke(1.dp.toPx(), cap = StrokeCap.Round),
            size = Size(size.width, size.height)
        )
    }
}

@Composable
@Preview
fun speedometerUnknownPreview(){
    val aa = 0.99f
    speedometerUnknown(aa)
}