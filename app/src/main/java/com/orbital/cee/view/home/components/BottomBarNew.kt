package com.orbital.cee.view.home.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.orbital.cee.R
import com.orbital.cee.core.Constants
import com.orbital.cee.core.MyLocationService
import com.orbital.cee.core.MyLocationService.LSS.speed
import com.orbital.cee.model.UserTiers
import com.orbital.cee.ui.theme.black
import com.orbital.cee.ui.theme.light_gray
import com.orbital.cee.ui.theme.red
import com.orbital.cee.ui.theme.white
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun NewBottomBar(
    navigationBarHeight:Int,
    onClickSpeedometer:()->Unit,
    onClickAddNewReport:()->Unit,
    onClickSound:()->Unit,
    onClickIndicator:()->Unit,
    isCameraMove: MutableState<Boolean>,
    soundStatus: LiveData<Int>,
    userType: MutableLiveData<UserTiers>,
    isPointClicked:MutableState<Boolean>,
    onClickReport: ()-> Unit,
    onClickReportAddManually: ()-> Unit,
    isToastAppeared:MutableState<Boolean>
){
    val streetSpeed = remember { mutableStateOf(-1) }
    val soundSta = soundStatus.observeAsState()
    val heightNabBottom = remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    val progressAnimationValue by animateFloatAsState(
        targetValue = ((speed.value.toFloat() / Constants.MAX_SPEED.toFloat()) * 0.8).toFloat(),
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
    )
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
        .background(color = Color.Transparent),
        horizontalAlignment = Alignment.End) {
        MyFab(
            userType = userType,
            isPointClicked = isPointClicked,
            onClickReport = onClickReport,
            onClickReportAddManually = onClickReportAddManually
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Box{
                this@Row.AnimatedVisibility(visible = isCameraMove.value, enter = fadeIn(), exit =  fadeOut()) {
                    Box(modifier = Modifier.size(105.dp), contentAlignment = Alignment.BottomStart ){
                        if (streetSpeed.value != speed.value){
                            Box(modifier = Modifier
                                .clickable(
                                    onClick = onClickSpeedometer,
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() })
                                .size(89.dp)
                                .border(width = 1.dp, color = light_gray, shape = CircleShape)
                                .background(color = Color.White, shape = CircleShape), contentAlignment = Alignment.Center){
                                Speedometer(progressAnimationValue)
                                Text(text = "${speed.value}", fontSize = 35.sp, fontWeight = FontWeight.Bold, color = black)
                            }
                            MyLocationService.GlobalStreetSpeed.streetSpeedLimit.value?.let {
                                streetSpeed.value = it.toInt()
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopEnd){
                                    Box(modifier = Modifier
                                        .size(52.dp)
                                        .border(width = 4.dp, color = red, shape = CircleShape)
                                        .background(color = Color.White, shape = CircleShape), contentAlignment = Alignment.Center){
                                        Text(text = "${streetSpeed.value}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = red)
                                    }
                                }
                            }
                        }else{
                            Box(modifier = Modifier
                                .clickable(
                                    onClick = onClickSpeedometer,
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() })
                                .size(89.dp)
                                .border(width = 6.dp, color = white, shape = CircleShape)
                                .background(color = red, shape = CircleShape), contentAlignment = Alignment.Center){
                                Text(text = "${speed.value}", fontSize = 35.sp, fontWeight = FontWeight.Bold, color = white, fontFamily = FontFamily(
                                    Font(R.font.bebas_neue_pro_bold)
                                )
                                )
                            }
                        }
                    }
                }
                Box(modifier = Modifier
                    .size(105.dp)
                    .padding(start = 15.dp, bottom = 15.dp), contentAlignment = Alignment.BottomStart ){
                    this@Row.AnimatedVisibility(visible = !isCameraMove.value, enter = fadeIn(
                        animationSpec = tween(1500)
                    ), exit =  fadeOut()) {
                        Box(modifier = Modifier
                            .size(65.dp)
                            .background(color = Color.White, shape = CircleShape)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                                onClick = {
                                    onClickIndicator()
                                }),contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_user_puck),
                                modifier = Modifier.size(28.dp),
                                contentDescription = "",
                                tint =if (isCameraMove.value) Color(0xFF495CE8) else Color.Unspecified
                            )
                        }
                    }
                }
            }
            Row(modifier = Modifier
                .width(119.dp)
                .height(68.dp)
                .background(color = Color.White, shape = RoundedCornerShape(34.dp))){
                Box(modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .fillMaxHeight()
                    .clickable(
                        onClick = onClickAddNewReport,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() })
                    .size(28.dp)
                    .clip(shape = RoundedCornerShape(topStart = 34.dp, bottomStart = 34.dp)), contentAlignment = Alignment.Center){
                    Icon(painter = painterResource(id = R.drawable.ic_plus), contentDescription = "",tint = Color(0xFF495CE8))
                }
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .clickable(
                        onClick = onClickSound,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() })
                    .size(25.dp)
                    .clip(shape = RoundedCornerShape(topEnd = 34.dp, bottomEnd = 34.dp)), contentAlignment = Alignment.Center){
                    Icon(painter = painterResource(id = if(soundSta.value == 1){R.drawable.volume_high}else if(soundSta.value == 2){R.drawable.ic_sound_vibrate}else{R.drawable.ic_volume_slash}), contentDescription = "", tint = Color(0xFF495CE8))
                }
            }
        }
        if (isToastAppeared.value){
            heightNabBottom.value = 0
        }else{
            LaunchedEffect(Unit){
                coroutineScope.launch {
                    delay(300)
                    heightNabBottom.value = navigationBarHeight
                }
            }
        }
        val bottomSpaceValue by animateFloatAsState(
            targetValue = (heightNabBottom.value+10).toFloat(),
            animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing)
        )
        Spacer(modifier = Modifier.height(bottomSpaceValue.dp))
    }
}

@Composable
fun Speedometer(value : Float){
    Canvas(modifier = Modifier
        .fillMaxSize()
        .padding(10.dp)) {
        drawArc(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFD6D6D6),
                    Color(0xFFD6D6D6),
                )
            ),
            startAngle = 134.5f,
            sweepAngle = 360 * 0.75f,
            useCenter = false,
            style = Stroke(
                5.dp.toPx(),
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
            startAngle = 135f,
            sweepAngle = 330 * value,
            useCenter = false,
            style = Stroke(
                5.dp.toPx(),
                cap = StrokeCap.Round,
            ),
        )
    }
}