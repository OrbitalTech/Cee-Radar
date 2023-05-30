package com.orbital.cee.view.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.orbital.cee.R
import kotlinx.coroutines.delay


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope

//@RequiresApi(Build.VERSION_CODES.O)
//@Composable
//fun llb(){
//    var minSize: Dp = 256.dp
//    Box(modifier = Modifier.size(400.dp), contentAlignment = Alignment.Center) {
//        Canvas(
//            Modifier.size(size = 300.dp)
//        ) {
//            val radius= size.width * .5f
//            val angleDegreeDifference = (360f / 55f)
//            (0..62).forEach {
//                val angleRadDifference = (((angleDegreeDifference * it) - 359f) * (PI / 180f)).toFloat()
//                if (it >19){
//                    val x = (radius - ((radius * .05f) / 2) ) * cos(angleRadDifference) + size.center.x
//                    val y = (radius - ((radius * .05f) / 2) ) * sin(angleRadDifference) + size.center.y
//
//                    val col = Color(red = (234f- (it-19) * 4.025f).toInt(), green = (78f+((it-19) * 0.35f)).toInt(),blue = (52f+((it-19) * 4.5f)).toInt())
//
//                    drawCircle(
//                        color = col,
//                        center = Offset(x,y),
//                        radius = 5f
//                    )
//                }
//            }
//            drawArc(
//                brush = Brush.linearGradient(
//                    colors = listOf(
//                        Color(0xFFECEEFD),
//                        Color(0xFFECEEFD),
//                    )
//                ),
//                startAngle = 127f,
//                sweepAngle = 360 * 0.79f,
//                useCenter = false,
//                style = Stroke(
//                    6.dp.toPx(),
//                    cap = StrokeCap.Round,
//                ),
//                size = size * 0.85f,
//                topLeft = Offset(60f,60f)
//            )
//            val angleInDegrees = (290 * 0.72f) + 60.0
//            val radiuss = (size.height *0.85 / 2)
//            val x = -(radiuss * sin(Math.toRadians(angleInDegrees))).toFloat() + (size.width / 2)
//            val y = (radiuss * cos(Math.toRadians(angleInDegrees))).toFloat() + (size.height / 2)
//            drawArc(
//                brush = Brush.linearGradient(
//                    start = Offset(x,y),
//                    end = Offset(150f ,780f),
//                    colors = listOf(
//                        Color(0xBFEA4E34),
//                        Color(0xFF495CE8),
//                    ),
//                    tileMode = TileMode.Mirror
//                ),
//                startAngle = 127f,
//                sweepAngle = 290 * 0.72f,
//                useCenter = false,
//                style = Stroke(
//                    14.dp.toPx(),
//                    cap = StrokeCap.Round,
//                ),
//                size = size * 0.85f,
//                topLeft = Offset(60f,60f)
//            )
//        }
////        Canvas(
////            Modifier.size(300.dp)
////        ) {
////
////            drawArc(
////                color = Color(0XFFECEEFD),
////                140f,
////                260f,
////                false,
////                style = Stroke(30.dp.toPx(), cap = StrokeCap.Round),
////                size = Size(size.width, size.height)
////            )
////            val rad = 0.71f
////            val angleInDegrees = (290 * rad) + 60.0
////            val radius = (size.height / 2)
////            val x = -(radius * sin(Math.toRadians(angleInDegrees))).toFloat() + (size.width / 2)
////            val y = (radius * cos(Math.toRadians(angleInDegrees))).toFloat() + (size.height / 2)
////            drawArc(
////                brush = Brush.linearGradient(
////                    start = Offset(x,y),
////                    end = Offset(160f ,690f),
////                    colors = listOf(
////                        Color(0xBFEA4E34),
////                        Color(0xFF495CE8),
////                    ),
////                    tileMode = TileMode.Mirror
////                ),
////                startAngle = 140f,
////                sweepAngle = 360 * rad,
////                false,
////                style = Stroke(30.dp.toPx(), cap = StrokeCap.Round),
////                size = Size(size.width, size.height)
////            )
////            val radiuse = size.width * .42f
////            val angleDegreeDifference = (360f / 28f)
////            (0..40).forEach {
////                if (it >19){
////                    val angleRadDifference = (((angleDegreeDifference * it) - 115f) * (PI / 180f)).toFloat()
////                    var lineLength = radiuse * .93f
////                    val lineColour = Color(0XFFC6C6C6)
////                    val startOffsetLine = Offset(
////                        x = lineLength * cos(angleRadDifference) + size.center.x,
////                        y = lineLength * sin(angleRadDifference) + size.center.y
////                    )
////                    val endOffsetLine = Offset(
////                        x = (radiuse - ((radiuse * .05f) / 2) ) * cos(angleRadDifference) + size.center.x,
////                        y = (radiuse - ((radiuse * .05f) / 2) ) * sin(angleRadDifference) + size.center.y
////                    )
////                    drawLine(
////                        color = lineColour,
////                        start = startOffsetLine,
////                        end = endOffsetLine,
////                        strokeWidth = 3f
////                    )
////                }
////            }
////        }
//
//
//
////
////        Canvas(
////            modifier = Modifier
////                .size(width, height)
////        ) {
////            val radius = size.width * .4f
////            val angleDegreeDifference = (360f / 75f)
////
////            (0..80).forEach {
////                if (it >19){
////                    val angleRadDifference = (((angleDegreeDifference * it) - 330f) * (PI / 180f)).toFloat()
////                    var lineLength = if (it % 5 == 0 ) radius * .85f  else radius * .93f
////                    val lineColour = Color(0XFF495CE8)
////                    val startOffsetLine = Offset(
////                        x = lineLength * cos(angleRadDifference) + size.center.x,
////                        y = lineLength * sin(angleRadDifference) + size.center.y
////                    )
////                    val endOffsetLine = Offset(
////                        x = (radius - ((radius * .05f) / 2) ) * cos(angleRadDifference) + size.center.x,
////                        y = (radius - ((radius * .05f) / 2) ) * sin(angleRadDifference) + size.center.y
////                    )
////                    drawLine(
////                        color = lineColour,
////                        start = startOffsetLine,
////                        end = endOffsetLine
////                    )
////                }
////                }
////            drawArc(
////                brush = Brush.radialGradient(
////                    colors = listOf(
////                        Color(0XFF495CE8),
////                        Color(0XFF495CE8),
////                    ), tileMode = TileMode.Repeated
////                ),
////                topLeft = Offset((size.width/2)-29,(size.height/2)-29),
////                size = size * .05f, startAngle = 90f, sweepAngle = 360 * 1f, useCenter = true,
////                style = Stroke(4.dp.toPx(), cap = StrokeCap.Square,),
////            )
////
////            drawLine(
////                color = Color(0XFF495CE8),
////                start = Offset((size.width/2),(size.height/2)),
////                end = Offset(
////                    x = ((radius * .7f) * cos(134.15 + 0.0185) + size.center.x).toFloat(),
////                    y = ((radius * .7f) * sin(134.15+ 0.0185) + size.center.y).toFloat()
////                ),
////                strokeWidth = 4.dp.toPx(),
////                cap = StrokeCap.Round
////            )
////            drawCircle(
////                color = Color.White,
////                radius = radius * .055f,
////                center = size.center
////            )
////        }
//    }
//
//}

@Composable
private fun BorderProgressBar() {

    val startDurationInSeconds = 10
    var currentTime by remember {
        mutableStateOf(startDurationInSeconds)
    }

    var targetValue by remember {
        mutableStateOf(100f)
    }

    var timerStarted by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = timerStarted) {
        if (timerStarted) {
            while (currentTime > 0) {
                delay(1000)
                currentTime--
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .padding( horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawLine(
                color = Color(0XFFECEEFD),
                Offset(0.0f,-40.0f),
                Offset(size.width,-40f),
                strokeWidth = 75f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color(0xFF3F51B5),
                Offset(0.0f,-40.0f),
                Offset(650.0f,-40f),
                strokeWidth = 75f,
                cap = StrokeCap.Round
            )
        }
    }
}

@Preview
@Composable
fun HorizontalProgress(percent : Float = 0.6f){
    Column(
        modifier = Modifier.height(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
//        TripInfoContainer(cornerRadius = 8f, tabWidth =250f){
//        }

        Canvas(modifier = Modifier.fillMaxHeight().fillMaxWidth(0.6f)) {
            drawLine(
                color = Color(0XFFECEEFD),
                Offset(0.0f,5.0f),
                Offset(size.width,5f),
                strokeWidth = 14.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color(0xFF3F51B5),
                Offset(0.0f,5.0f),
                Offset(size.width * percent,5f),
                strokeWidth = 14.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }

}


class NavShape(
    private val widthOffset: Dp,
    private val scale: Float
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Rectangle(
            Rect(
                Offset.Zero,
                Offset(
                    size.width * scale + with(density) { widthOffset.toPx() },
                    size.height
                )
            )
        )
    }
}




@Composable
fun TripInfoContainer(
    modifier: Modifier = Modifier,
    cornerRadius: Float,
    tabWidth: Float,
    content: @Composable () -> Unit
) {
//    val path = rememberPath(cornerRadius, tabWidth)

    Canvas(modifier = modifier) {
        val path = Path()
        val rect = Rect(Offset.Zero, size)

        path.moveTo(cornerRadius, rect.height)
        path.arcTo(
            rect = Rect(
                Offset(rect.width - cornerRadius, rect.height - cornerRadius),
                Size(cornerRadius * 2, cornerRadius * 2)
            ),
            startAngleDegrees = 90f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )
        path.arcTo(
            rect = Rect(Offset(rect.width - cornerRadius, 0f), Size(cornerRadius * 2, cornerRadius * 2)),
            startAngleDegrees = 0f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )
        path.arcTo(
            rect = Rect(
                Offset(rect.width - tabWidth - cornerRadius, cornerRadius),
                Size(cornerRadius * 2, cornerRadius * 2)
            ),
            startAngleDegrees = -90f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )
        path.arcTo(
            rect = Rect(
                Offset(rect.width - tabWidth - cornerRadius, cornerRadius * 3),
                Size(cornerRadius * 2, cornerRadius * 2)
            ),
            startAngleDegrees = 180f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )
        path.arcTo(
            rect = Rect(Offset(0f, rect.height - cornerRadius * 2), Size(cornerRadius * 2, cornerRadius * 2)),
            startAngleDegrees = 90f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )
        path.arcTo(
            rect = Rect(Offset(0f, rect.height - cornerRadius), Size(cornerRadius * 2, cornerRadius * 2)),
            startAngleDegrees = 180f,
            sweepAngleDegrees = -90f,
            forceMoveTo = false
        )
        drawPath(path, color = Color.Red)
    }

    content()
}

//private fun rememberPath(cornerRadius: Float, tabWidth: Float): Path {
//
//
//    return path
//}



@Composable
fun Loading(size : Int){
    val composition by rememberLottieComposition(
        LottieCompositionSpec
            .RawRes(R.raw.lottie_three_dot_loading_gray)

    )
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true,
        speed = 1f,
        restartOnPlay = false
    )
    LottieAnimation(
        composition,
        progress,
        modifier = Modifier.size(size.dp).background(color = Color(0xFF848484))
    )

}


//@RequiresApi(Build.VERSION_CODES.O)
//@Preview(showBackground = true)
//@Composable
//fun llbp(){
//    llb()
//}









