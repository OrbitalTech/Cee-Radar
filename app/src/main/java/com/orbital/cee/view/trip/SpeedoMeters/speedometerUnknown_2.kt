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
fun speedometerUnknown_2(value:Float){
    val conf = LocalConfiguration.current
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawArc(
            color = Color(0XFFECEEFD),
            127.7f,
            285f,
            false,
            style = Stroke(18.dp.toPx(), cap = StrokeCap.Round),
            size = Size(size.width, size.height)
        )
        val angleInDegrees = (285 * value) + (55.0 - (value * 20))
        val radiuss = (size.height *0.85 / 2)
        val x = -(radiuss * sin(Math.toRadians(angleInDegrees))).toFloat() + (size.width / 2)
        val y = (radiuss * cos(Math.toRadians(angleInDegrees))).toFloat() + (size.height / 2)
        drawArc(
            brush = Brush.linearGradient(
                start = Offset(x,y),
                end = Offset(180f ,730f),
                colors = listOf(
                    Color(0xFFEA4E34),
                    Color(0xFF495CE8),
                ),
                tileMode = TileMode.Mirror
            ),
            startAngle = 127.7f,
            sweepAngle = 285 * value,
            false,
            style = Stroke(18.dp.toPx(), cap = StrokeCap.Round),
            size = Size(size.width, size.height)
        )
        val radiuse = size.width * .42f
        val angleDegreeDifference = (360f / 28f)
        (0..40).forEach {
            if (it >19){
                val angleRadDifference = (((angleDegreeDifference * it) - 115f) * (Math.PI / 180f)).toFloat()
                var lineLength = radiuse * .93f
                val lineColour = Color(0XFFC6C6C6)
                val startOffsetLine = Offset(
                    x = lineLength * cos(angleRadDifference) + size.center.x,
                    y = lineLength * sin(angleRadDifference) + size.center.y
                )
                val endOffsetLine = Offset(
                    x = (radiuse - ((radiuse * .05f) / 2) ) * cos(angleRadDifference) + size.center.x,
                    y = (radiuse - ((radiuse * .05f) / 2) ) * sin(angleRadDifference) + size.center.y
                )
                drawLine(
                    color = lineColour,
                    start = startOffsetLine,
                    end = endOffsetLine,
                    strokeWidth = 3f
                )
            }
        }
    }
}

@Composable
@Preview
fun speedometerUnknown_2Preview(){
    val aa = 0.82f
    speedometerUnknown_2(aa)
}