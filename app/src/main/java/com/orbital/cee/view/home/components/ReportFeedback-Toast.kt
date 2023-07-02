package com.orbital.cee.view.home.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbital.cee.R
import com.orbital.cee.utils.MetricsUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FeedbackToast(reportType: Int,
                  onLike:()->Unit,
                  onUnlike:()->Unit,
                  onClose:()->Unit,
                  isLiked: MutableState<Boolean?>,
                  onDrag :(po: PointerInputChange, offset: Offset)->Unit,
                  bottomNavigationHeight:Int
){
    val timer = remember { mutableStateOf(0) }
    val context = LocalContext.current
    val reportUI = MetricsUtils.getReportUiByReportType(reportType = reportType, context = context)
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
    Box(modifier = Modifier
        .wrapContentSize()
        .pointerInput(Unit) {
            detectDragGestures(onDrag = onDrag)
        }
        .background(color = Color.Transparent), contentAlignment = Alignment.Center) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(20.dp)
                    )
//                    .border(
//                        color = reportUI.color1,
//                        width = 1.5.dp,
//                        shape = RoundedCornerShape(15.dp)
//                    )
            ){

                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

                        Box(modifier = Modifier
                            .size(52.dp)
                            .clickable (
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                                onClick = {}),contentAlignment = Alignment.Center
                        ) {
                            Icon(painter = painterResource(id = R.drawable.bg_btn_place_cam_fab_main_scr), contentDescription = "", tint = reportUI.color1)
                            Icon(
                                painter = painterResource(id = reportUI.icon),
                                modifier = Modifier.size(27.dp,30.dp),
                                tint = Color.White,
                                contentDescription = ""
                            )
                        }
                        Text(
                            text = stringResource(id = R.string.lbl_feedback_feedbackToast),
                            modifier = Modifier.fillMaxWidth(0.8f),
                            fontSize = 16.sp
                        )
                        Box(modifier = Modifier.size(30.dp), contentAlignment = Alignment.Center){
                            Canvas(modifier = Modifier.size(25.dp)) {
                                drawArc(
                                    color = reportUI.color1,
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
                                tint = reportUI.color1 )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Button(onClick = onLike,
                            enabled = !((reportType == 5) || (reportType == 6))  ,
                            border = if (isLiked.value == true){ BorderStroke(2.dp, color = Color(0XFF57D654)) }else{null},
                            elevation  = ButtonDefaults.elevation(
                                defaultElevation = 0.dp,
                                pressedElevation = 0.dp
                            ),
                            colors = ButtonDefaults.buttonColors(backgroundColor =if (isLiked.value == false){ Color(0XFFE4E4E4) }else{ Color(0XFFEEFBEE)}),
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .height(48.dp), shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(modifier = Modifier.size(24.dp),
                                    painter = painterResource(id = R.drawable.ic_like),
                                    contentDescription = "",
                                    tint = if (isLiked.value == false){ Color(0XFF848484) }else{ Color(0XFF57D654) }
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(text = stringResource(id = R.string.lbl_confirm_report_location),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.W600,
                                    color =if (isLiked.value == false){ Color(0XFF848484) }else{ Color(0XFF57D654) }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Button(onClick = onUnlike,
                            enabled = !((reportType == 5) || (reportType == 6)),
                            border = if (isLiked.value == false){ BorderStroke(2.dp, color = Color(0XFFEA4E34)) }else{ null},
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
                                .height(48.dp), shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(modifier = Modifier.size(24.dp),painter = painterResource(id = R.drawable.ic_dislike), contentDescription = "", tint =if (isLiked.value == true){
                                    Color(0XFF848484)
                                }else{
                                    Color(0XFFEA4E34)
                                } )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(text = stringResource(id =R.string.lbl_disapprove_report_location),fontSize = 14.sp,fontWeight = FontWeight.W600,fontFamily = FontFamily(
                                    Font(R.font.work_sans_medium)
                                ), color = if (isLiked.value == true){
                                    Color(0XFF848484)
                                }else{
                                    Color(0XFFEA4E34)
                                })
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height((bottomNavigationHeight+ 15) .dp))
        }
    }
}