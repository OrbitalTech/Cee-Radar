package com.orbital.cee.view.trip.SpeedoMeters

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.orbital.cee.core.GeofenceBroadcastReceiver
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun speedometerHITEX(value:Float){
    val conf = LocalConfiguration.current
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
}

@Composable
@Preview
fun speedometerHITEXPreview(){
    val aa = 0.19f
    speedometerHITEX(aa)
}