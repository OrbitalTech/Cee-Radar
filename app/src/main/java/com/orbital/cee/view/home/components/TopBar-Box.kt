package com.orbital.cee.view.home.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbital.cee.R
import com.orbital.cee.view.trip.advancedShadow
import kotlinx.coroutines.delay

@Composable
fun topBar(onClickMenu : () -> Unit, temp : Int?, onClickAds:()->Unit, isAdLoaded : Boolean, isWatchedRewardVideo:()->Boolean,timeRemain:MutableState<Float>,onlineUser:MutableState<Int>) {

    val progressAnimationValue by animateFloatAsState(
        targetValue = timeRemain.value,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
    )
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

    val superscript = SpanStyle(
        baselineShift = BaselineShift.Superscript,
        fontSize = 10.sp,
        color = Color(0xFF919191)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.Transparent)
            .padding(horizontal = 12.dp, vertical = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = onClickMenu,
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier
                .size(55.dp)
                .advancedShadow(
                    color = Color.Black,
                    alpha = 0.06f,
                    cornersRadius = 21.dp,
                    shadowBlurRadius = 8.dp,
                    offsetX = 0.dp,
                    offsetY = 5.dp
                ),
            elevation =  ButtonDefaults.elevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
                disabledElevation = 0.dp,
                hoveredElevation = 15.dp,
                focusedElevation = 10.dp
            ),
            shape = RoundedCornerShape(21.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.background)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_menu_drawer),
                modifier = Modifier.size(28.dp),
                contentDescription = "",
                tint = Color(0xFF495CE8)
            )
        }
        Row() {
//            Box(
//                modifier = Modifier
//                    .height(55.dp)
//                    .width(100.dp)
//                    .advancedShadow(
//                        color = Color.Black,
//                        alpha = 0.06f,
//                        cornersRadius = 16.dp,
//                        shadowBlurRadius = 8.dp,
//                        offsetX = 0.dp,
//                        offsetY = 5.dp
//                    )
//                    .clip(shape = RoundedCornerShape(21.dp))
//                    .background(color = MaterialTheme.colors.background)
//                , contentAlignment = Alignment.Center
//            ) {
//                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
//                    Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
//                        Icon(
//                            painter = painterResource(id = R.drawable.ic_online_user),
//                            modifier = Modifier.size(16.dp),
//                            contentDescription = "",
//                            tint = Color(0xFF57D654)
//                        )
//                        Spacer(modifier = Modifier.width(5.dp))
//                        Text(modifier = Modifier.width(35.dp),text = "${onlineUser.value}", color = Color(0xFF57D654), fontSize = 12.sp, textAlign = TextAlign.Center)
//                    }
//
//                    Spacer(modifier = Modifier.width(2.dp))
//                    Text(text = "ONLINE USER", color = Color(0xFF848484), fontSize = 10.sp)
//                }
//
//            }
            Spacer(modifier = Modifier.width(8.dp))
            if (isAdLoaded || isWatchedRewardVideo()){
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(
                        onClick = onClickAds,
                        modifier = Modifier
                            .size(54.dp)
                            .advancedShadow(
                                color = Color.Black,
                                alpha = 0.06f,
                                cornersRadius = 21.dp,
                                shadowBlurRadius = 8.dp,
                                offsetX = 0.dp,
                                offsetY = 5.dp
                            ),
                        elevation =  ButtonDefaults.elevation(
                            defaultElevation = 0.dp,
                            pressedElevation = 0.dp,
                            disabledElevation = 0.dp,
                            hoveredElevation = 15.dp,
                            focusedElevation = 10.dp
                        ),
                        contentPadding = PaddingValues(top = 1.dp,bottom = 1.dp, start = 1.dp, end = 1.dp),
                        shape = RoundedCornerShape(21.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.background)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
                            val pathWithProgress by remember {
                                mutableStateOf(Path())
                            }

                            // using path
                            val pathMeasure by remember { mutableStateOf(PathMeasure()) }

                            val path = remember {
                                Path()
                            }
                            if(isWatchedRewardVideo()){
                                Box(contentAlignment = Alignment.Center){
                                    Canvas(modifier = Modifier.size(55.dp)) {

                                        if (path.isEmpty) {
                                            path.addRoundRect(
                                                RoundRect(
                                                    Rect(offset = Offset.Zero, size),
                                                    cornerRadius = CornerRadius(21.dp.toPx())
                                                )
                                            )
                                        }
                                        pathWithProgress.reset()

                                        pathMeasure.setPath(path, forceClosed = false)
                                        pathMeasure.getSegment(
                                            startDistance = 0f,
                                            stopDistance = pathMeasure.length * (progressAnimationValue),
                                            pathWithProgress,
                                            startWithMoveTo = true
                                        )
                                        clipPath(path) {
                                            drawRect(Color.White)
                                        }

                                        drawPath(
                                            path = path,
                                            style = Stroke(
                                                2.dp.toPx()
                                            ),
                                            color = Color.White
                                        )

                                        drawPath(
                                            path = pathWithProgress,
                                            style = Stroke(
                                                2.dp.toPx()
                                            ),
                                            color = Color(0xFF495CE8)
                                        )
                                    }
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_clock),
                                        modifier = Modifier.size(25.dp),
                                        contentDescription = "",
                                        tint = Color.Unspecified
                                    )
                                }

//                                Spacer(modifier = Modifier.height(1.dp))
//                                Text(text = progressAnimationValue.toString(), fontSize = 8.sp, fontWeight = FontWeight.W600, color = Color(0xFF919191))
                            }else{
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_no_ads),
                                    modifier = Modifier.size(25.dp),
                                    contentDescription = "",
                                    tint = Color.Unspecified
                                )
                            }

                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    if(!isWatchedRewardVideo()){
                        Text(text = stringResource(R.string.lbl_home_adFree_sheet_removeAds_title), color = Color(0xFF171729), fontSize = 8.sp, letterSpacing = 0.sp, fontWeight = FontWeight.W600, textAlign = TextAlign.Center)
                    }

                }

                Spacer(modifier = Modifier.width(8.dp))
            }
            Box(
                modifier = Modifier
                    .height(55.dp)
                    .width(165.dp)
                    .advancedShadow(
                        color = Color.Black,
                        alpha = 0.06f,
                        cornersRadius = 16.dp,
                        shadowBlurRadius = 8.dp,
                        offsetX = 0.dp,
                        offsetY = 5.dp
                    )
                    .clip(shape = RoundedCornerShape(21.dp))
                    .background(color = MaterialTheme.colors.background)
                    , contentAlignment = Alignment.Center
            ) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_online_user),
                                modifier = Modifier.size(16.dp),
                                contentDescription = "",
                                tint = Color(0xFF57D654)
                            )
                            Text(modifier = Modifier.width(35.dp),text = "${onlineUser.value}", color = Color(0xFF57D654), fontSize = 12.sp, textAlign = TextAlign.Center)
                        }

                        Spacer(modifier = Modifier.width(1.5.dp))
                        Text(text = "ONLINE USER", color = Color(0xFF848484), fontSize = 9.sp)
                    }
                    Divider(
                        color = Color(0xFFE4E4E4),
                        modifier = Modifier
                            .fillMaxHeight(0.5f)
                            .width(2.dp)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_weather),
                            modifier = Modifier.size(23.dp),
                            contentDescription = "",
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(text = buildAnnotatedString {
                            append("${temp ?: "!!"}")
                            withStyle(superscript){
                                append("o")
                            }
                        }, color = Color(0xFF919191),fontSize = 14.sp)
//                        Text(text = "${temp ?: "!!"}o", color = Color(0xFF919191), fontSize = 16.sp)
                    }

                }


            }
        }

    }
}