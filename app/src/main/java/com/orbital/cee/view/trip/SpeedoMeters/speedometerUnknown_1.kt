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
fun speedometerUnknown_1(value:Float){
    Canvas(modifier = Modifier.fillMaxSize()) {
        val radius= size.width * .5f
        val angleDegreeDifference = (360f / 55f)
        (0..62).forEach {
            val angleRadDifference = (((angleDegreeDifference * it) - 359f) * (Math.PI / 180f)).toFloat()
            if (it >19){
                val x = (radius - ((radius * .05f) / 2) ) * cos(angleRadDifference) + size.center.x
                val y = (radius - ((radius * .05f) / 2) ) * sin(angleRadDifference) + size.center.y

                val col = Color(red = (234f- (it-19) * 4.025f).toInt(), green = (78f+((it-19) * 0.35f)).toInt(),blue = (52f+((it-19) * 4.5f)).toInt())

                drawCircle(
                    color = col,
                    center = Offset(x,y),
                    radius = 5f
                )
            }
        }
        drawArc(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFECEEFD),
                    Color(0xFFECEEFD),
                )
            ),
            startAngle = 127.7f,
            sweepAngle = 285 * 1f,
            useCenter = false,
            style = Stroke(
                6.dp.toPx(),
                cap = StrokeCap.Round,
            ),
            size = size * 0.85f,
            topLeft = Offset(size.width * 0.075f, size.width * 0.075f)
        )
        val angleInDegrees = (285 * value) + (55.0 - (value * 20))
        val radiuss = (size.height *0.85 / 2)
        val x = -(radiuss * sin(Math.toRadians(angleInDegrees))).toFloat() + (size.width / 2)
        val y = (radiuss * cos(Math.toRadians(angleInDegrees))).toFloat() + (size.height / 2)
        drawArc(
            brush = Brush.linearGradient(
                start = Offset(x,y),
                end = Offset(180f ,690f),
                colors = listOf(
                    Color(0xFFEA4E34),
                    Color(0xFF495CE8),
                ),
                tileMode = TileMode.Mirror
            ),
            startAngle = 127.7f,
            sweepAngle = 285 * value,
            useCenter = false,
            style = Stroke(
                14.dp.toPx(),
                cap = StrokeCap.Round,
            ),
            size = size * 0.85f,
            topLeft = Offset(size.width * 0.075f, size.width * 0.075f)
        )
    }
}

@Composable
@Preview
fun speedometerUnknown_1Preview(){
    val aa = 0.22f
    speedometerUnknown_1(aa)
}