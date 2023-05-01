package com.orbital.cee.view.trip.SpeedoMeters

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
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
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun classicSpeedometer(value:Float){
    val conf = LocalConfiguration.current
    Canvas(modifier = Modifier.size(size = if(conf.screenWidthDp<350){220.dp}else{280.dp})) {
        val radius = size.width * .50f
        val angleDegreeDifference = (360f / 75f)

        (0..80).forEach {
            if (it >19){
                val angleRadDifference = (((angleDegreeDifference * it) - 330f) * (Math.PI / 180f)).toFloat()
                var lineLength = if (it % 5 == 0 ) radius * .85f  else radius * .93f
                val lineColour = Color(0XFF495CE8)
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
                    strokeWidth = 4f
                )
            }
        }

        drawCircle(
            color = Color(0XFF495CE8),
            radius = radius * .075f,
            center = size.center
        )

        drawLine(
            color = Color(0XFF495CE8),
            start = Offset((size.width/2),(size.height/2)),
            end = Offset(
                x = ((radius * .7f) * cos(134.15 + (5 * value)) + size.center.x).toFloat(),
                y = ((radius * .7f) * sin(134.15+ (5 * value)) + size.center.y).toFloat()
            ),
            strokeWidth = 4.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawCircle(
            color = Color.White,
            radius = radius * .055f,
            center = size.center
        )
    }
}

@Composable
@Preview
fun classicSpeedometerPreview(){
    val aa = 0.25f
    classicSpeedometer(aa)
}