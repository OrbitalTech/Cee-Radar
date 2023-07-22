package com.orbital.cee.view.home.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.orbital.cee.R
import com.orbital.cee.model.SingleCustomReport
import com.orbital.cee.view.home.HomeViewModel
import kotlinx.coroutines.*
import java.math.RoundingMode
import java.text.DecimalFormat

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun ReportViewDetail(model: HomeViewModel = viewModel(),reportId : String = "",onCloseClick: (id:String) -> Unit ) {
    var isLoading = remember { mutableStateOf(true) }
    var report = remember {
        mutableStateOf(SingleCustomReport(isSuccess = false))
    }
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.DOWN
//    var cLoc = Location("")
//    cLoc.latitude = location?.LatLon?.latitude ?: 0.00
//    cLoc.longitude = location?.LatLon?.longitude ?: 0.00
    val coroutineScope = rememberCoroutineScope()
    coroutineScope.launch {
        model.getSingleReport(reportId = reportId).collect{
            if (it.isSuccess){
                report.value = it
                isLoading.value = false
            }
        }
    }

    val color = remember {mutableStateOf(Color.White)}
    val color1 = remember {mutableStateOf(Color.White)}
    val icon = remember {mutableStateOf(R.drawable.cee)}
    val title = remember {mutableStateOf("")}

    val isApprove = remember {
        mutableStateOf(false)
    }
    val isDisapprove = remember {
        mutableStateOf(false)
    }
    val totalApprove = remember {
        mutableStateOf(4)
    }
    val totalDisapprove = remember {
        mutableStateOf(8)
    }
    var isVoted = remember {
        mutableStateOf(false)
    }

    when(report.value.reportType){
        1 ->{
            color.value = Color(0XFF495CE8)
            color1.value = Color(0XFFAAAAAA)
            icon.value = R.drawable.ic_camera_fab
            title.value = stringResource(R.string.btn_home_report_action_sheet_roadCam)
        }
        2->{
            color.value = Color(0XFFEA4E34)
            color1.value = Color(0XFFAAAAAA)
            icon.value = R.drawable.ic_carcrash
            title.value = stringResource(R.string.btn_home_report_action_sheet_carCrash)
        }
        3->{
            color.value = Color(0XFF57D654)
            color1.value = Color(0XFFAAAAAA)
            icon.value = R.drawable.ic_new_police
            title.value = stringResource(R.string.btn_home_report_action_sheet_police)
        }
        4->{
            color.value = Color(0XFFFECD5C)
            color1.value = Color(0XFFAAAAAA)
            icon.value = R.drawable.ic_new_construction
            title.value = "Road under Construction"
        }
    }
    Box(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.40f)
        .clip(shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp))
        .background(color = Color.White), contentAlignment = Alignment.Center){

        if (isLoading.value){
            CircularProgressIndicator()
        }else{
            Column(modifier = Modifier.padding(vertical = 25.dp, horizontal = 15.dp)) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.25f)) {
                    Box(modifier  = Modifier
                        .size(60.dp)
                        .clip(shape = RoundedCornerShape(20.dp))
                        .background(color = color.value), contentAlignment = Alignment.Center){
                        Icon(painter = painterResource(id = icon.value),modifier = Modifier.size(30.dp), tint = Color(
                            0xFFFFFFFF
                        ), contentDescription ="cons" )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.fillMaxWidth(0.7f)) {
                        Text(text = title.value, fontSize = 12.sp)

                        Text(text = "" +report.value.reportLocation?.let { df.format(it.distanceTo(
                            model.lastLocation.value).div(1000)) }+"KM away", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(painter = painterResource(id = R.drawable.ic_bold_location), contentDescription = "",modifier = Modifier.size(10.dp),tint = Color(
                                0xFF3C3C3C))
                            Text(text = " ${report.value.reportAddress}",fontSize = 12.sp)
                        }
                    }
                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(painter = painterResource(id = R.drawable.ic_bold_clock), contentDescription = "",modifier = Modifier.size(10.dp),tint = Color(
                                0xFF989898
                            )
                            )
                            Text(text = " ${report.value.reportTime}",fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        if (report.value.isReportOwner){
                            Box(modifier = Modifier
                                .background(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.Transparent
                                )
                                .padding(horizontal = 5.dp, vertical = 10.dp)
                                .clickable { onCloseClick(reportId) }) {
                                Text(
                                    text = "DELETE REPORT",
                                    color = Color(0XFFEA4E34),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                            //Button(onClick = { onCloseClick(reportId) }, colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)) {

                            //}
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.45f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = "Approve camera location ?")
                    Text(text = "Your rating helps us improve")
                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(bottom = 30.dp),
                    Arrangement.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(modifier  = Modifier
                            .size(50.dp)
                            .clip(shape = RoundedCornerShape(20.dp))
                            .background(
                                color = if (isDisapprove.value) color.value else Color(
                                    0xFFF7F7F7
                                )
                            )
                            .clickable {
                                isApprove.value = false
                                isDisapprove.value = true
                                totalDisapprove.value += 1
                            }
                            , contentAlignment = Alignment.Center){
                            Icon(painter = painterResource(id = R.drawable.ic_dislike),modifier = Modifier.size(28.dp), tint = if(isDisapprove.value) Color.White else Color(0xFF989898), contentDescription ="cons" )
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(text = "${totalDisapprove.value}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0XFF989898))
                    }
                    Box(modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .fillMaxHeight(0.5f), contentAlignment = Alignment.Center) {

                        CircularProgressBare(percentage = ((totalApprove.value/(totalApprove.value+totalDisapprove.value).toFloat()) * 100).toFloat(),color = color.value)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(modifier  = Modifier
                            .size(50.dp)
                            .clip(shape = RoundedCornerShape(20.dp))
                            .background(
                                color = if (isApprove.value) color.value else Color(
                                    0xFFF7F7F7
                                )
                            )
                            .clickable {
                                isApprove.value = true
                                isDisapprove.value = false
                                totalApprove.value += 1
                            }
                            , contentAlignment = Alignment.Center){
                            Icon(painter = painterResource(id = R.drawable.ic_like),modifier = Modifier.size(28.dp), tint = if(isApprove.value) Color.White else Color(0xFF989898), contentDescription ="cons" )
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(text = "${totalApprove.value}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0XFF989898))
                    }
                }
            }
        }

    }

//    if (model.isSuccess.value){
//
//    }else{
//        Box(modifier = Modifier
//            .fillMaxWidth()
//            .fillMaxHeight(), contentAlignment = Alignment.Center){
//            LinearProgressIndicator()
//        }
//    }

}
@RequiresApi(Build.VERSION_CODES.S)
@Preview(showBackground = true)
@Composable
private fun defultPreview(){
    ReportViewDetail(){

    }

}

@Composable
fun CircularProgressBare(
    percentage: Float,
    color: Color
) {
    val progressAnimationValue by animateFloatAsState(
        targetValue = percentage,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .height(20.dp)
    ) {
        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(20.dp)) {
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFABAAAC),
                        Color(0xFFABAAAC),
                    )
                ),
                Offset(0f,40f),
                Offset(380f,40f),
                cap = StrokeCap.Round,
                strokeWidth = 25f
            )
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(
                        color,
                        color,
                    )
                ),
                Offset(0f,40f),
                Offset((progressAnimationValue * 4.6).toFloat(),40f),
                cap = StrokeCap.Round,
                strokeWidth = 25f
            )
        }

    }
}