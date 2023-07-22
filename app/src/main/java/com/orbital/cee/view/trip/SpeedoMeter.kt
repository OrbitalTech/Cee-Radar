package com.orbital.cee.view.trip


import android.location.Location
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.airbnb.lottie.compose.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.orbital.cee.R
import com.orbital.cee.core.Constants.MAX_SPEED
import com.orbital.cee.core.GeofenceBroadcastReceiver
import com.orbital.cee.core.MyLocationService
import com.orbital.cee.core.MyLocationService.LSS.TripAverageSpeed
import com.orbital.cee.core.MyLocationService.LSS.TripDistance
import com.orbital.cee.core.MyLocationService.LSS.TripMaxSpeed
import com.orbital.cee.core.MyLocationService.LSS.speed
import com.orbital.cee.model.Trip
import com.orbital.cee.ui.theme.white
import com.orbital.cee.utils.MetricsUtils.Companion.bearingToCoordinate
import com.orbital.cee.utils.MetricsUtils.Companion.getRemainSeconds
import com.orbital.cee.utils.MetricsUtils.Companion.getReportUiByReportType
import com.orbital.cee.view.home.BottomSheets.incidentDistance
import com.orbital.cee.view.home.HomeViewModel
import com.orbital.cee.view.home.appMenu.componenets.advancedShadow
import com.orbital.cee.view.home.components.startTripDialog
import com.orbital.cee.view.trip.SpeedoMeters.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun Speed(
    model : HomeViewModel,
    onClickBack: ()-> Unit,
    showBottomModalSheet: ()-> Unit,
    onClickContinue:()->Unit,
    onClickStart: ()-> Unit,
    onClickResetTrip : () -> Unit,
    onClickPause:() -> Unit,
    onClickFinish: ()-> Unit,
    isPurchasedAdRemove : MutableState<Boolean>){

    val timer by remember { mutableStateOf("00:00:00") }
    var showTripDialog by remember { mutableStateOf(false) }
    val trips = model.trips.observeAsState()
    val speedometerId = model.speedometerId.observeAsState()
    val rotate = if (LocalConfiguration.current.layoutDirection == LayoutDirection.Rtl.ordinal){180f}else{0f}
    val context = LocalContext.current
    val composition by rememberLottieComposition(
        LottieCompositionSpec
            .RawRes(R.raw.lottie_cee_drive_car)
    )
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = model.isTripStarted.value && model.isTimerRunning.value,
        speed = 1f,
        restartOnPlay = false
    )

    var isShowTripHistory by remember { mutableStateOf(false) }
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.DOWN

    val progressAnimationValue by animateFloatAsState(
        targetValue = ((speed.value.toFloat() / MAX_SPEED.toFloat()) * 0.8).toFloat(),
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
    )
    val conf = LocalConfiguration.current
    val color = remember { mutableStateOf(Color.White) }
    val icon = remember { mutableStateOf(R.drawable.cee) }
    val title = remember { mutableStateOf("") }
    val title1 = remember { mutableStateOf("") }

//    model.allReports.sortBy {report->
//        report.geoLocation?.let {location->
//            val thisIncLocation = Location("")
//            thisIncLocation.latitude = location[0] as Double
//            thisIncLocation.longitude = location[1] as Double
//            model.lastLocation.value.distanceTo(thisIncLocation)
//        }
//    }
//    val reportUI = getReportUiByReportType(reportType = model.allReports.firstOrNull()?.reportType ?: 1, context = context)

    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = MaterialTheme.colors.background)
        , contentAlignment = Alignment.TopCenter){
        Column(modifier = Modifier.fillMaxSize()) {
            if(!isPurchasedAdRemove.value){
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                    .height(80.dp), contentAlignment = Alignment.Center) {
                    Box(modifier = Modifier
                        .background(
                            color = Color.Transparent,
                            shape = RoundedCornerShape(
                                bottomEnd = 18.dp,
                                bottomStart = 18.dp
                            )
                        )
                        .fillMaxWidth()
                        .padding(
                            8.dp
                        ), contentAlignment = Alignment.Center) {
                        AndroidView(factory ={
                            AdView(it).apply {
                                this.setAdSize(AdSize.BANNER)
                                adUnitId =resources.getString(R.string.speedometer_screen_ad_banner_id)
                                loadAd(AdRequest.Builder().build())
                            }
                        })
                    }

                }
            }
            Column(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(horizontal = 24.dp)
                //.verticalScroll(rememberScrollState())
                .background(color = MaterialTheme.colors.background)) {

//                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(50.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onClickBack) {
                        Icon(painter = painterResource(id = R.drawable.ic_arrow_left), tint = Color(0XFF848484),modifier = Modifier
                            .size(22.dp)
                            .rotate(rotate), contentDescription = "" )
                    }
                    Box(modifier = Modifier
                        .clickable(onClick = {
                            if (model.isGuest()) {
                                model.whichButtonClicked.value = 8
                                showBottomModalSheet()
                            } else {
                                isShowTripHistory = true
                            }
                        }, interactionSource = remember {MutableInteractionSource() }, indication = null)
                        .advancedShadow(
                            color = Color.Black,
                            alpha = 0.03f,
                            cornersRadius = 10.dp,
                            shadowBlurRadius = 6.dp,
                            offsetX = 0.dp,
                            offsetY = 3.dp
                        ), contentAlignment = Alignment.Center) {
                        Icon(modifier = Modifier
                            .size(46.dp),painter = painterResource(id =R.drawable.bg_btn_place_cam_fab_main_scr ), contentDescription ="", tint = white )
                        Icon(painter = painterResource(id = R.drawable.ic_trip_history), tint = Color.Unspecified, contentDescription = "", modifier = Modifier.size(23.dp))
                    }
                }
                Column(modifier = Modifier
                    .fillMaxHeight(),verticalArrangement = Arrangement.SpaceBetween, horizontalAlignment = Alignment.CenterHorizontally) {
                    Box{
                        when (speedometerId.value) {
                            "HITEX" -> {
                                SpeedometerHITEX(
                                    value = progressAnimationValue,
                                    speed = speed.value,
                                    bearing = model.lastLocation.value.bearing,
                                    isNearReport = model.isNearReport.value,
                                    reportType = model.nearReportType.value,
                                    distance = model.distanceAway.value
                                )
                            }
                            "Analog" -> { classicSpeedometer(progressAnimationValue) }
                            "ProPlus" -> { speedometerUnknown_1(progressAnimationValue) }
                            else -> {
                                CeeOMeter(
                                    value = progressAnimationValue,
                                    speed = speed.value,
                                    bearing = model.lastLocation.value.bearing,
                                    isNearReport = model.isNearReport.value,
                                    reportType = model.nearReportType.value,
                                    distance = model.distanceAway.value
                                )
                            }
                        }
                    }


//                    when (speedoMeterID) {
//                        2 -> {
//                            Box(modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(top = 10.dp)
//                                .pointerInput(Unit) {
//                                    detectTapGestures(onTap = {
//                                        speedoMeterID = 3
//                                    })
//                                }, contentAlignment = Alignment.Center){
//                                speedometerUnknown_3(progressAnimationValue)
//                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                                    Text(text = df.format(speed.value), fontWeight = FontWeight.Bold, fontSize = 42.sp, color = Color(0XFF495CE8))
//                                    Text(text = "Km/h", color = Color(0XFF495CE8), fontSize = 25.sp)
//                                }
//                            }
//                        }
//                        1 -> {
//                            Box(modifier =
//                            Modifier
//                                .size((conf.screenWidthDp - 80).dp)
//                                .border(
//                                    width = 0.dp,
//                                    color = Color(0x14FFFFFF),
//                                    shape = CircleShape
//                                )){
//                                    Box(
//                                        Modifier
//                                            .padding(18.dp)
//                                    ) {
////                                        ceeOMeter(value = progressAnimationValue)
////                                        speedometerHITEX(value = progressAnimationValue)
//                                        speedometerUnknown_1(progressAnimationValue)
//                                        SpeedDigit(currentLocation= model.lastLocation, speed = speed.value, nearestReport = model.allReports.firstOrNull(),hitex = false)
//                                    }
//                            }
//                        }
//                        4 -> {
//                            Box(modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(top = 10.dp)
//                                .pointerInput(Unit) {
//                                    detectTapGestures(onTap = {
//                                        speedoMeterID = 1
//                                    })
//                                }, contentAlignment = Alignment.Center) {
//                                classicSpeedometer(progressAnimationValue)
//                                Column(modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(top = 20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
////                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
////                                        Icon(painter = painterResource(id = R.drawable.ic_triangle), contentDescription = "", tint = Color.Unspecified, modifier = Modifier.rotate(180f))
////                                        Text(bearingToCoordinate(model.lastLocation.value.bearing), fontWeight = FontWeight.Bold, fontSize = 16.sp,color = Color(0xFF495CE8))
////                                        Spacer(modifier = Modifier.height(10.dp))
////                                        AnimatedVisibility(
////                                            modifier = Modifier.fillMaxWidth(),
////                                            visible = GeofenceBroadcastReceiver.GBRS.GeoId.value != null && !reportListIsEmpty,
////                                            enter =  fadeIn(),
////                                            exit = fadeOut()
////                                        ) {
////                                            Row(
////                                                modifier = Modifier
////                                                    .fillMaxWidth()
////                                                    .height(35.dp),
////                                                horizontalArrangement = Arrangement.Center,
////                                                verticalAlignment = Alignment.CenterVertically
////                                            ) {
////                                                Box(
////                                                    modifier = Modifier
////                                                        .size(35.dp)
////                                                        .clip(shape = RoundedCornerShape(12.dp))
////                                                        .background(color = color1.value), contentAlignment = Alignment.Center
////                                                ) {
////                                                    Icon(painter = painterResource(id = icon.value), modifier = Modifier.size(18.dp), tint = color.value, contentDescription = "")
////                                                }
////                                                Spacer(modifier = Modifier.width(5.dp))
////                                                Text(incidentDistance(nearestReportDistance), fontWeight = FontWeight.Bold, fontSize = 16.sp,color = Color(0xFF495CE8))
////
////                                            }
////                                        }
////                                    }
//                                    Spacer(modifier = Modifier.height(70.dp))
//                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                                        Text(df.format(speed.value), fontWeight = FontWeight.ExtraBold, fontSize = 35.sp,color = Color(0xFF495CE8))
//                                        Text("Km/h", fontWeight = FontWeight.Bold, fontSize = 16.sp,color = Color(0xFF495CE8))
//                                    }
//
//                //
//                                }
//
//
//                            }
//                        }
//                        5 -> {
//                            Box(modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(top = 10.dp)
//                                .pointerInput(Unit) {
//                                    detectTapGestures(onTap = {
//                                        speedoMeterID = 4
//                                    })
//                                }, contentAlignment = Alignment.Center) {
//                                speedometerUnknown(progressAnimationValue)
//                                SpeedDigit(currentLocation= model.lastLocation, speed = speed.value, nearestReport = model.allReports.firstOrNull())
//                            }
//                        }
//                        6 -> {
//                            Box(modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(top = 10.dp)
//                                .pointerInput(Unit) {
//                                    detectTapGestures(onTap = {
//                                        speedoMeterID = 5
//                                    })
//                                }, contentAlignment = Alignment.Center) {
//                                speedometerUnknown_2(progressAnimationValue)
//                                SpeedDigit(currentLocation= model.lastLocation, speed = speed.value, nearestReport = model.allReports.firstOrNull())
//
//                            }
//                        }
//                        7 -> {
//                            Box(modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(top = 10.dp)
////                                .pointerInput(Unit) {
////                                    detectTapGestures(onTap = {
////                                        speedoMeterID = 6
////                                    })
////                                }
//                                ,contentAlignment = Alignment.Center) {
//                                speedometerUnknown_1(progressAnimationValue)
//                                SpeedDigit(currentLocation= model.lastLocation, speed = speed.value, nearestReport = model.allReports.firstOrNull())
//
//                            }
//                        }
//                        else -> {
//                            Box(modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(top = 15.dp)
//                                .pointerInput(Unit) {
//                                    detectTapGestures(onTap = {
//                                        speedoMeterID = 7
//                                    })
//                                }
//                                .height(260.dp), contentAlignment = Alignment.Center) {
//                                Box(modifier = Modifier
//                                    .size(corR.dp)
//                                    .clip(shape = RoundedCornerShape(300.dp))
//                                    .background(color = Color(0XFFECEEFD)),
//                                    contentAlignment = Alignment.Center) {
//                                }
//                                Box(modifier = Modifier
//                                    .clip(shape = RoundedCornerShape(300.dp))
//                                    .size(220.dp)
//                                    .background(color = Color(0XFF495CE8))) {
//                                    Column(modifier = Modifier
//                                        .fillMaxWidth()
//                                        .fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
//                                        Text(text = df.format(speed.value), fontSize = 42.sp, fontWeight = FontWeight.Bold, color = Color.White)
//                                        Text(text = "km/h",color = Color.White, fontSize = 25.sp)
//                                    }
//                                }
//                            }
//                        }
//                    }
                    Column(modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .padding(bottom = 15.dp, top = 5.dp), verticalArrangement = Arrangement.Bottom) {
                        Column(horizontalAlignment = Alignment.End) {
                            if (false){
                                SpeedOMeterOnBoard(
                                    timer = timer,
                                    icon = icon.value,
                                    speedLimit = model.allReports.firstOrNull()?.reportSpeedLimit,
                                    color = color.value,
                                    text1 = title.value,
                                    text2 = title1.value
                                )
                            }else{
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                    .height(
                                        height = if (conf.screenWidthDp < 350) {
                                            85.dp
                                        } else {
                                            100.dp
                                        }
                                    )
                                    .background(
                                        color = Color(0XFFF7F7F7),
                                        shape = RoundedCornerShape(
                                            topStart = 10.dp,
                                            topEnd = 10.dp,
                                            bottomStart = 10.dp,
                                            bottomEnd = 10.dp
                                        )
                                    ), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(0.5f), contentAlignment = Alignment.Center){
                                        LottieAnimation(
                                            composition,
                                            progress,
                                            modifier = Modifier.fillMaxSize(0.8f)
                                        )
//                                        if(model.isTripStarted.value){
//
//                                        }else{
//                                            Icon(painter = painterResource(id = R.drawable.ic_cee_trip), tint = Color.Unspecified, contentDescription ="",modifier = Modifier.fillMaxSize(0.8f) )
//                                        }

                                    }
                                    Box(modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(), contentAlignment = Alignment.Center){
                                        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center) {
                                            Text(text = stringResource(R.string.lbl_speedometer_trip_duration))

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(text = getRemainSeconds(model.timeRemain.value.toLong()), fontSize =if(conf.screenWidthDp<350){20.sp}else{26.sp} , fontWeight = FontWeight.Bold)
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Text(text =  stringResource(R.string.lbl_speedometer_trip_duration_hour))

                                            }
                                        }
                                    }

                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .height(
                                    height = if (conf.screenWidthDp < 350) {
                                        85.dp
                                    } else {
                                        100.dp
                                    }
                                ), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(modifier = Modifier
                                    .fillMaxWidth(0.32f)
                                    .fillMaxHeight()
                                    .background(
                                        color = Color(0XFFF7F7F7),
                                        shape = RoundedCornerShape(10.dp)
                                    ), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                    Icon(modifier = Modifier.size(25.dp),painter = painterResource(id = R.drawable.ic_flash), tint = Color.Unspecified, contentDescription = "")
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Text(text = stringResource(R.string.lbl_trip_history_detail_avg_speed), fontWeight = FontWeight.W700, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Row(verticalAlignment = Alignment.Bottom) {
                                        Text(text =if(model.isTripStarted.value){df.format(TripAverageSpeed.value)}else{"0"} , fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(text = "Km/h", fontSize = 13.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier
                                    .fillMaxWidth(0.5f)
                                    .fillMaxHeight()
                                    .background(
                                        color = Color(0XFFF7F7F7),
                                        shape = RoundedCornerShape(10.dp)
                                    ), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                    Icon(modifier = Modifier.size(25.dp),painter = painterResource(id = R.drawable.ic_speed), tint = Color.Unspecified, contentDescription = "")
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Text(text = stringResource(R.string.lbl_speedometer_trip_max_speed), fontWeight = FontWeight.W700, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Row(verticalAlignment = Alignment.Bottom) {
                                        Text(text =if(model.isTripStarted.value){"${TripMaxSpeed.value}"}else{"0"} , fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(text = "Km/h", fontSize = 13.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight()
                                    .background(
                                        color = Color(0XFFF7F7F7),
                                        shape = RoundedCornerShape(10.dp)
                                    ), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                    Icon(modifier = Modifier.size(25.dp),painter = painterResource(id = R.drawable.ic_point_to_point_camera), tint = Color.Unspecified, contentDescription = "")
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Text(text = stringResource(R.string.lbl_speedometer_trip_distance), fontWeight = FontWeight.W700, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Row(verticalAlignment = Alignment.Bottom) {
                                        Text(text = if(model.isTripStarted.value){df.format(TripDistance.value)}else{"0"} , fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(text = "KM", fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(modifier = Modifier
                            .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            if(!model.isTripStarted.value){
                                Button(onClick = {
                                    if (model.isGuest()){
                                        model.whichButtonClicked.value = 8
                                        showBottomModalSheet()
                                    }else{
                                        if (model.isTimerRunning.value){
                                            showTripDialog = true
                                        }else{
                                            onClickStart()
                                        }
                                    }


                                }, modifier = Modifier
                                    .fillMaxWidth()
                                    .height(55.dp)
                                    .clip(shape = RoundedCornerShape(10.dp)),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = Color(0XFF495CE8),
                                        contentColor = Color.White)) {
                                    Text(text = stringResource(R.string.btn_speedometer_start_trip), fontWeight = FontWeight.Bold)
                                }

                            }else{
                                Button(onClick = onClickFinish, modifier = Modifier
                                    .fillMaxWidth(0.65f)
                                    .height(55.dp)
                                    .border(
                                        border = BorderStroke(
                                            width = 2.dp,
                                            color = Color.Red
                                        ),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clip(shape = RoundedCornerShape(10.dp)),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = Color.White,
                                        contentColor = Color.Red)) {
                                    Text(text = stringResource(R.string.btn_speedometer_end_trip), fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Button(onClick =onClickPause,
                                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondaryVariant),
                                    modifier = Modifier
                                        .fillMaxWidth(0.45f)
                                        .height(55.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                ) {
                                    Icon(painter = painterResource(id =if(MyLocationService.LSS.isTripPused.value) R.drawable.ic_play else R.drawable.ic_pause), contentDescription ="", tint = MaterialTheme.colors.primaryVariant)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Button(onClick =onClickResetTrip,
                                    modifier = Modifier
                                        .fillMaxWidth(1f)
                                        .height(55.dp)
                                        .clip(RoundedCornerShape(10.dp)),
                                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.onError)
                                ) {
                                    Icon(painter = painterResource(id = R.drawable.close_circle), contentDescription ="", tint = MaterialTheme.colors.error)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(45.dp))
                    }
                }
            }
        }

        if(showTripDialog){
            startTripDialog(
                onClickStart ={
                    showTripDialog = false
                    onClickStart.invoke()
                },
                onClickContinue ={
                    showTripDialog = false
                    onClickContinue.invoke()
                } ,
                onDismiss = {
                    showTripDialog = false
                })
        }
        AnimatedVisibility(
            modifier = Modifier.fillMaxSize(),
            visible = isShowTripHistory,
            enter =  fadeIn(),
            exit = fadeOut()
        ) {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White), contentAlignment = Alignment.Center) {
                TripHistory(tripList = trips.value,onClickBack = {
                    isShowTripHistory = false
                },onDeleteTrip = { trip->
                    if (trip == Trip()){
                        Toast.makeText(context,"already removed",Toast.LENGTH_LONG).show()
                    }else{
                        trips.value?.remove(trip).let {
                            if (it == true){
                                trips.value?.let { it1 -> model.saveTrip(it1) }
                                //isShowTripHistory = false
                            }
                        }
                    }

                })
            }
        }

    }

}
@Composable
fun SpeedDigit(speed:Int, nearestReportType:Int, currentLocation:MutableState<Location>, hitex:Boolean = false) {
    val reportListIsEmpty by remember { mutableStateOf(false) }
    val nearestReportDistance by remember { mutableStateOf(0f) }
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.DOWN
    val context = LocalContext.current
    val reportUI = getReportUiByReportType(nearestReportType,context)

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(top = 24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(painter = painterResource(id = R.drawable.ic_triangle), contentDescription = "", tint = Color.Unspecified, modifier = Modifier.rotate(180f))
        Text(bearingToCoordinate(currentLocation.value.bearing), fontWeight = FontWeight.Bold, fontSize = 16.sp,color = Color(0xFF495CE8))
        Spacer(modifier = Modifier.height(10.dp))
        Column(modifier = Modifier.height(36.dp)){
            AnimatedVisibility(
                modifier = Modifier.fillMaxWidth(),
                visible = GeofenceBroadcastReceiver.GBRS.GeoId.value != null && !reportListIsEmpty,
                enter =  fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(35.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(35.dp)
                            .clip(shape = RoundedCornerShape(12.dp))
                            .background(color = reportUI.color1), contentAlignment = Alignment.Center
                    ) {
                        Icon(painter = painterResource(id = reportUI.icon), modifier = Modifier.size(18.dp), tint = reportUI.color1, contentDescription = "")
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(incidentDistance(nearestReportDistance), fontWeight = FontWeight.Bold, fontSize = 16.sp,color = Color(0xFF495CE8))

                }
            }
        }
        Text(df.format(speed), fontWeight = FontWeight.ExtraBold, fontSize = 75.sp,color = Color(0xFF495CE8), fontFamily = if (hitex){
           FontFamily(Font(R.font.off_bit_trial_bold_t))
        }else{null})
//        Text("Km/h", fontWeight = FontWeight.Bold, fontSize = 16.sp,color = Color(0xFF495CE8))
        Spacer(modifier = Modifier.height(35.dp))
        Text("Km/h", fontWeight = FontWeight.Bold, fontSize = 16.sp,color = Color(0xFF495CE8))
//        Text("40 km/h", fontWeight = FontWeight.Bold, fontSize = 14.sp,color = Color(0xFF495CE8))
    }
}
//
//
//fun incidentDistance(distance : Float) : String{
//    val df = DecimalFormat("#.##")
//    df.roundingMode = RoundingMode.DOWN
//    return if (distance > 1000){
//        "${df.format((distance/1000))} KM"
//    }else{
//        "${df.format(distance)} M"
//    }
//}
