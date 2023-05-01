package com.orbital.cee.view.splash.component
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import kotlinx.coroutines.launch


@Composable
fun RelaxCee() {
    val animationScope = rememberCoroutineScope()

    val animatableX = remember { Animatable(initialValue = 0f) }
    val animatableY = remember { Animatable(initialValue = 0f) }

    Canvas(modifier = Modifier.fillMaxSize()) {


        val canvasWidth = size.width
        val canvasHeight = size.height

        val unitWidth = size.width / 24
        val unitHeight = size.width / 24

        val eyeWidth = size.width / 100
        val eyeHeight = size.height / 100


        val corneaLX = (unitWidth * 3.3).toFloat()
        val corneaRX = (unitWidth * 10).toFloat()


        animationScope.launch {
            launch {
                animatableX.animateTo(
                    targetValue = corneaLX - 10,
                    animationSpec = tween(durationMillis = 100, delayMillis = 60)
                )
                animatableX.animateTo(
                    targetValue = corneaLX + 20,
                    animationSpec = tween(durationMillis = 100, delayMillis = 50)
                )
            }

            launch {
                animatableY.animateTo(
                    targetValue = corneaRX - 10,
                    animationSpec = tween(durationMillis = 100, delayMillis = 60)
                )
                animatableY.animateTo(
                    targetValue = corneaRX + 20,
                    animationSpec = tween(durationMillis = 100, delayMillis = 50)
                )
            }
        }


        drawCircle(
            color = Color.White,
            center = Offset(x = canvasWidth / 2, y = canvasHeight / 2),
            radius = size.minDimension/2,
            style = Stroke(8F)
        )
        drawOval(
            color = Color.White,
            topLeft = Offset(x = (unitWidth * 7.5).toFloat(), y = unitHeight * 5 ),
            size = Size(width = eyeWidth * 25, height = eyeHeight * 55)
        )
        rotate(degrees = -5f) {
            drawOval(
                color = Color.White,
                topLeft = Offset(x = (unitWidth * 1.5).toFloat(), y = unitHeight * 7 ),
                size = Size(width = eyeWidth * 19, height = eyeHeight * 44)
            )
        }

        drawOval(
            color = Color.Black,
            topLeft = Offset(x = animatableY.value, y = unitHeight * 8 ),
            size = Size(width = eyeWidth * 12, height = eyeHeight * 30)
        )
        rotate(degrees = -5f) {
            drawOval(
                color = Color.Black,
                topLeft = Offset(x = animatableX.value, y = (unitHeight * 9.5).toFloat()),
                size = Size(width = eyeWidth * 8, height = eyeHeight * 25)
            )
        }

    }
}