package com.orbital.cee.view.home.components

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.orbital.cee.R
import com.orbital.cee.core.Constants
import com.orbital.cee.core.GeofenceBroadcastReceiver
import com.orbital.cee.core.MyLocationService.LSS.speed
import com.orbital.cee.utils.Utils.pxToDp
import com.orbital.cee.view.home.HomeViewModel
import com.orbital.cee.view.trip.Speed
import java.math.RoundingMode
import java.text.DecimalFormat

@OptIn(ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun bottomBar(model:HomeViewModel,
              onButtonClicked: (bId : Int) -> Unit,
              onClickSpeed: (o : Offset) -> Unit,
              speedLimit:Int,bottomSheetState :BottomSheetState,
              navigationBarHeight:Int = 0,startTrip:()->Unit,saveTrip:()->Unit,continueTrip:()->Unit,tripReset:()->Unit,pauseTrip:()->Unit,isPurchasedAdRemove:MutableState<Boolean>) {
    val conf = LocalConfiguration.current
    val df = DecimalFormat("#.###")
    df.roundingMode = RoundingMode.DOWN
    val soundSta = model.soundStatus.observeAsState()
    val progressAnimationValue by animateFloatAsState(
        targetValue = ((speed.value.toFloat() / Constants.MAX_SPEED.toFloat()) * 0.8).toFloat(),
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
    )

//    Log.d("DEBUG_MODAL_BOTTOM_SHEET", "ScrHeight: ${conf.screenHeightDp}")
    val composition = when (speed.value) {
        in 0..29 -> { rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lottie_speedface_0to30km)) }
        in 30..59 -> { rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lottie_speedface_30to60km)) }
        in 60..79 -> { rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lottie_speedface_60to80km)) }
        in 80..99 -> { rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lottie_speedface_80to100km)) }
        in 100..109 -> { rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lottie_speedface_100to110km)) }
        in 110..119 -> { rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lottie_speedface_110to120km)) }
        else -> { rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lottie_speedface_120todeadkm)) }
    }
    val progress by animateLottieCompositionAsState(
        composition.value,
        iterations = LottieConstants.IterateForever,
        isPlaying = true,
        speed = 1f,
        restartOnPlay = false
    )

    var isToExpand = false
    var isToCollapse = false
    var a = 0f


//    if (bottomSheetState.progress < 1f){
//        isToExpand = bottomSheetState.requireOffset() < 20f
//        isToCollapse = bottomSheetState.requireOffset() > 1950f
//        a = 1 - (pxToDp(bottomSheetState.requireOffset().toInt()).toFloat() / conf.screenHeightDp.toFloat())
////        Log.d("DEBUG_MODAL_BOTTOM_SHEET", "isToEx: $isToExpand")
//        Log.d("DEBUG_MODAL_BOTTOM_SHEET", "ScrHeightOF: ${pxToDp(bottomSheetState.requireOffset().toInt())}")
//    }
//
//    if (isToExpand){
//        a = 1f
//    }
//    if (isToCollapse){
//        a = 0f
//    }



//    a = if(bottomSheetState.isCollapsed){
//        if (bottomSheetState.progress == 1f){
//            if (isToExpand){
//                1f
//            }else{
//                0f
//            }
//        }else{
//            Log.d("DEBUG_MODAL_BOTTOM_SHEET","Off: ${bottomSheetState.requireOffset()}")
//            bottomSheetState.progress
//        }
//    }else{
//        if(bottomSheetState.progress == 1f){
//            1f
//        }else{
//            Log.d("DEBUG_MODAL_BOTTOM_SHEET","Off: ${bottomSheetState.requireOffset()}")
//            1 - bottomSheetState.progress
//
//        }
//
//    }
    Row(
        modifier = Modifier
            .fillMaxWidth()

            //.navigationBarsPadding()
            .height(
                height = if (conf.screenWidthDp < 350) {
                    130.dp
                } else {
                    (90 + navigationBarHeight).dp
                }
            )
            .background(
                color = MaterialTheme.colors.background, shape = RoundedCornerShape(
                    topStart = 18.dp,
                    topEnd = 18.dp
                )
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column() {
            Spacer(modifier = Modifier.height(13.dp))
            Row(Modifier
//                        .pointerInput(Unit){
//                            detectDragGestures { change, dragAmount ->
////                            change.consume()
//                                val (x,y) = dragAmount
//                                when {
//                                    x > 0 ->{ }
//                                    x < 0 -> {}
//                                }
//                                Log.d("PRINT_Y_BOTTOM_BAR",change.position.y.toString())
//
//                            }
//                        }
                .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(modifier = Modifier
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = onClickSpeed
                        )
                    }
                    .padding(start = 12.dp)
//                    .clickable(onClick = onClickSpeed)
                    ,
                    verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .width(70.dp), contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(size = if(conf.screenWidthDp<350){50.dp}else{60.dp}),) {
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
                                sweepAngle = 330 * progressAnimationValue,
                                useCenter = false,
                                style = Stroke(
                                    5.dp.toPx(),
                                    cap = StrokeCap.Round,
                                ),
                            )
                        }
                        if (GeofenceBroadcastReceiver.GBRS.GeoId.value != null){
                            if (speedLimit >0){
                                Text(text = speedLimit.toString(), color = Color(0xFF495CE8), fontSize = 18.sp, fontWeight = FontWeight.W700)
                            }else{
                                LottieAnimation(
                                    composition.value,
                                    progress,
                                    modifier = Modifier
                                        .size(34.dp)
                                )
//                                Icon( modifier =Modifier.size(30.dp),painter = painterResource(id = R.drawable.ic_cee_one), contentDescription = "", tint = Color.Unspecified )
                            }
                        }else{
//                            Icon( modifier =Modifier.size(30.dp),painter = painterResource(id = R.drawable.ic_cee_one), contentDescription = "", tint = Color.Unspecified )
                            LottieAnimation(
                                composition.value,
                                progress,
                                modifier = Modifier
                                    .size(34.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.padding(start = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = df.format(speed.value), fontSize =  if(conf.screenWidthDp<350){25.sp}else{35.sp}, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(text = "km/h")
                    }
                }
                Row(modifier = Modifier.padding(end = 12.dp)) {
                    OutlinedButton(
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.size(size = if(conf.screenWidthDp<350){40.dp}else{50.dp}),
                        onClick = { onButtonClicked(1) },
                        border = BorderStroke(1.dp, Color(0xFFE4E4E4)),
                        shape = RoundedCornerShape(20)
                    ) {
                        Icon(
                            tint = Color(0xFF495CE8),
                            painter = painterResource(id = R.drawable.ic_add),
                            contentDescription = "",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    OutlinedButton(
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.size(size = if(conf.screenWidthDp<350){40.dp}else{50.dp}),
                        onClick = { onButtonClicked(2) },
                        border = BorderStroke(1.dp, Color(0xFFE4E4E4)),
                        shape = RoundedCornerShape(20)
                    ) {
                        Icon(
                            tint = Color(0xFF495CE8),
                            painter = painterResource(id = R.drawable.ic_menu),
                            contentDescription = "",
                            modifier = Modifier.size(25.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    OutlinedButton(
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier
                            .size(
                                size = if (conf.screenWidthDp < 350) {
                                    40.dp
                                } else {
                                    50.dp
                                }
                            )
                            .align(Alignment.CenterVertically),
                        onClick = { onButtonClicked(3) },
                        border = BorderStroke(1.dp, Color(0xFFE4E4E4)),
                        shape = RoundedCornerShape(20)
                    ) {
                        Icon(
                            tint = Color(0xFF495CE8),
                            painter = if(soundSta.value == 1) painterResource(id = R.drawable.volume_high) else if (soundSta.value == 2) painterResource(id = R.drawable.ic_sound_vibrate) else painterResource(id = R.drawable.ic_volume_slash)  ,
                            contentDescription = "",
                            modifier = Modifier.size(25.dp)
                        )
                    }
                }
            }
        }

    }
//    Box(Modifier.fillMaxSize(),contentAlignment = Alignment.TopCenter){
//
//        Surface(modifier = Modifier.fillMaxSize(), color = Color.Red.copy(alpha =a)) {
//
//            Speed(
//                model = model,onClickStart = startTrip,onClickFinish = saveTrip,
//                onClickContinue = continueTrip,onClickBack ={} ,
//                onClickResetTrip = tripReset,onClickPause = pauseTrip,isPurchasedAdRemove = isPurchasedAdRemove
//            )
//        }
//    }

//    AnimatedContent(
//        targetState = bottomSheetState.progress,
//        modifier = Modifier.fillMaxSize(),
//        transitionSpec = {
//            fadeIn() + slideInVertically(animationSpec = tween(400),
//                initialOffsetY = { fullHeight -> fullHeight }) with
//                    fadeOut(animationSpec = tween(200))
//        }
//    ) { targetState ->
//
//
//
//
////        if (targetState >= 0.9f){
////
////        }else{
////            Speed(
////                model = model,onClickStart = startTrip,onClickFinish = saveTrip,
////                onClickContinue = continueTrip,onClickBack ={} ,
////                onClickResetTrip = tripReset,onClickPause = pauseTrip,isPurchasedAdRemove = isPurchasedAdRemove
////            )
////        }
//    }






}