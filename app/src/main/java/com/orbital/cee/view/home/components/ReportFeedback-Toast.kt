package com.orbital.cee.view.home.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbital.cee.R
import com.orbital.cee.core.GeofenceBroadcastReceiver
import kotlinx.coroutines.delay

@Composable
fun FeedbackToast(reportType: Int,onLike:()->Unit,onUnlike:()->Unit,onClose:()->Unit,isLiked: MutableState<Boolean?>,onDrag :(po: PointerInputChange, offset: Offset)->Unit){
    val color = remember { mutableStateOf(Color.White) }
    val color1 = remember { mutableStateOf(Color.White) }
    val icon = remember { mutableStateOf(R.drawable.cee) }
    val title = remember { mutableStateOf("") }
    val timer = remember { mutableStateOf(0) }
    LaunchedEffect(Unit){
        while (true){
            timer.value++
            delay(1000)
        }
    }
    val progressAnimationValue by animateFloatAsState(
        targetValue = timer.value * 24f,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
    )
    if (timer.value > 14){
        onClose.invoke()
    }
    when(reportType){
        1 ->{
            color.value = Color(0XFF495CE8)
            color1.value = Color(0XFFAAAAAA)
            icon.value = R.drawable.ic_camera_fab
            title.value = stringResource(R.string.btn_home_report_action_sheet_roadCam)
        }
        2->{
            color.value = Color(0XFFF27D28)
            color1.value = Color(0XFFAAAAAA)
            icon.value = R.drawable.ic_car_crash
            title.value = stringResource(R.string.btn_home_report_action_sheet_carCrash)
        }
        3->{
            color.value = Color(0XFF36B5FF)
            color1.value = Color(0XFFAAAAAA)
            icon.value = R.drawable.ic_police
            title.value = stringResource(R.string.btn_home_report_action_sheet_police)
        }
        4->{
            color.value = Color(0XFF1ED2AF)
            color1.value = Color(0XFFAAAAAA)
            icon.value = R.drawable.ic_construction
            title.value = stringResource(R.string.btn_home_report_action_sheet_construction)
        }
        5 ->{
            color.value = Color(0XFF495CE8)
            color1.value = Color(0XFFAAAAAA)
            icon.value = R.drawable.ic_static_camera
            title.value = stringResource(R.string.btn_home_report_action_sheet_staticCam)
        }
        6 ->{
            color.value = Color(0XFF495CE8)
            color1.value = Color(0XFFAAAAAA)
            icon.value = R.drawable.ic_point_to_point_camera
            title.value =  stringResource(R.string.btn_home_report_action_sheet_p2pCam)
        }
    }
    Box(modifier = Modifier
        .wrapContentSize()
        .pointerInput(Unit) {
            detectDragGestures(onDrag = onDrag)
        }
        .background(color = Color.Transparent), contentAlignment = Alignment.Center) {
        Column() {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(15.dp)
                    )
                    .border(
                        color = color.value,
                        width = 1.5.dp,
                        shape = RoundedCornerShape(15.dp)
                    )){

                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Button(
                            contentPadding = PaddingValues(0.dp),
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(backgroundColor =  color.value),
                            modifier = Modifier
                                .size(40.dp)
//                                .shadow(
//                                    elevation = 30.dp,
//                                    shape = RoundedCornerShape(14.dp),
//                                    clip = true
//                                )
                            ,
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = icon.value,),
                                modifier = Modifier.size(20.dp),
                                tint = Color.White,
                                contentDescription = ""
                            )
                        }
                        Text(
                            text = stringResource(id = R.string.lbl_feedback_feedbackToast),
                            modifier = Modifier.fillMaxWidth(0.8f),
                            fontSize = 12.sp
                        )
                        Box(modifier = Modifier.size(30.dp), contentAlignment = Alignment.Center){
                            Canvas(modifier = Modifier.size(25.dp)) {
                                drawArc(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            color.value,
                                            color.value,
                                        )
                                    ),
                                    startAngle = 270f,
                                    sweepAngle = (360 - progressAnimationValue),
                                    useCenter = false,
                                    style = Stroke(
                                        2.dp.toPx(),
                                        cap = StrokeCap.Round,
                                    ),
                                )
                            }
                            Icon(
                                modifier = Modifier
                                    .clickable(onClick = onClose)
                                    .padding(5.dp),
                                painter = painterResource(id = R.drawable.ic_close),
                                contentDescription ="",
                                tint = color.value )
                        }


                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Button(onClick = onLike,
                            enabled = reportType != 5 || reportType != 6,
                            border = if (isLiked.value == true){
                                BorderStroke(2.dp, color = Color(0XFF57D654))
                            }else{null},
                            elevation  = ButtonDefaults.elevation(
                                defaultElevation = 0.dp,
                                pressedElevation = 0.dp
                            ),
                            colors = ButtonDefaults.buttonColors(backgroundColor =if (isLiked.value == false){
                                Color(0XFFE4E4E4)
                            }else{
                                Color(0XFFEEFBEE)
                            } ),
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .height(37.dp), shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(modifier = Modifier.size(18.dp),painter = painterResource(id = R.drawable.ic_like), contentDescription = "",tint = if (isLiked.value == false){
                                    Color(0XFF848484)
                                }else{
                                    Color(0XFF57D654)
                                })
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(text = stringResource(id = R.string.lbl_confirm_report_location), fontSize = 11.sp,fontWeight = FontWeight.W600,fontFamily = FontFamily(
                                    Font(R.font.work_sans_medium,)
                                ),color =if (isLiked.value == false){
                                    Color(0XFF848484)
                                }else{
                                    Color(0XFF57D654)
                                }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Button(onClick = onUnlike,
                            enabled = reportType != 5 || reportType != 6,
                            border = if (isLiked.value == false){
                                BorderStroke(2.dp, color = Color(0XFFEA4E34))
                            }else{ null},
                            elevation  = ButtonDefaults.elevation(
                                defaultElevation = 0.dp,
                                pressedElevation = 0.dp
                            ),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor =if (isLiked.value == true){
                                    Color(0XFFE4E4E4)
                                }else{
                                    Color(0XFFFDEDEB)
                                } ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(37.dp), shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(modifier = Modifier.size(18.dp),painter = painterResource(id = R.drawable.ic_dislike), contentDescription = "", tint =if (isLiked.value == true){
                                    Color(0XFF848484)
                                }else{
                                    Color(0XFFEA4E34)
                                } )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(text = stringResource(id =R.string.lbl_disapprove_report_location),fontSize = 11.sp,fontWeight = FontWeight.W600,fontFamily = FontFamily(
                                    Font(R.font.work_sans_medium)
                                ), color = if (isLiked.value == true){
                                    Color(0XFF848484)
                                }else{
                                    Color(0XFFEA4E34)
                                })
                            }

                        }
                    }

//                                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
//                                                        Box(modifier  = Modifier
//                                                            .size(40.dp)
//                                                            .clip(shape = RoundedCornerShape(12.dp))
//                                                            .background(
//                                                                color = Color(
//                                                                    0xFFF7F7F7
//                                                                )
//                                                            )
//                                                            .clickable {
//                                                                coroutineScope.launch {
//                                                                    delay(1000)
//                                                                    model.slider.value = false
//                                                                }
//                                                            }
//                                                            , contentAlignment = Alignment.Center){
//                                                            Icon(painter = painterResource(id = R.drawable.ic_like),modifier = Modifier.size(24.dp), tint = Color(0xFF989898), contentDescription ="cons" )
//                                                        }
//                                                        Spacer(modifier = Modifier.width(10.dp))
//                                                        Box(modifier  = Modifier
//                                                            .size(40.dp)
//                                                            .clip(shape = RoundedCornerShape(12.dp))
//                                                            .background(color = Color(0xFFF7F7F7))
//                                                            .clickable {
//                                                                coroutineScope.launch {
//                                                                    delay(1000)
//                                                                    model.slider.value = false
//                                                                }
//                                                            }
//                                                            , contentAlignment = Alignment.Center){
//                                                            Icon(painter = painterResource(id = R.drawable.ic_dislike),modifier = Modifier.size(24.dp), tint =  Color(0xFF989898), contentDescription ="cons" )
//                                                        }
//                                                        Spacer(modifier = Modifier.width(10.dp))
//                                                        Box(modifier  = Modifier
//                                                            .size(40.dp)
//                                                            .clip(shape = RoundedCornerShape(12.dp))
//                                                            .background(color = Color(0xFFF7F7F7))
//                                                            .clickable {
//
//                                                            }
//                                                            , contentAlignment = Alignment.Center){
//                                                            Icon(painter = painterResource(id = R.drawable.ic_share),modifier = Modifier.size(24.dp), tint =  Color(0xFF989898), contentDescription ="cons" )
//                                                        }
//                                                    }

                }
            }
            Spacer(modifier = Modifier.height(155.dp))
        }
    }
}