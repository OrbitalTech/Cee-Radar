package com.orbital.cee.view.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbital.cee.R

@Composable
fun InsideReportToast(reportType:Int,speedLimit: Int?,onClose:()->Unit){
    val color = remember { mutableStateOf(Color.White) }
    val color1 = remember { mutableStateOf(Color.White) }
    val icon = remember { mutableStateOf(R.drawable.cee) }
    val title = remember { mutableStateOf("") }

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
            icon.value = R.drawable.ic_carcrash
            title.value = stringResource(R.string.btn_home_report_action_sheet_carCrash)
        }
        3->{
            color.value = Color(0XFF36B5FF)
            color1.value = Color(0XFFAAAAAA)
            icon.value = R.drawable.ic_new_police
            title.value = stringResource(R.string.btn_home_report_action_sheet_police)
        }
        4->{
            color.value = Color(0XFF1ED2AF)
            color1.value = Color(0XFFAAAAAA)
            icon.value = R.drawable.ic_new_construction
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
            title.value = stringResource(R.string.btn_home_report_action_sheet_p2pCam)
        }
    }
    Box(modifier = Modifier
        .wrapContentSize()
        .pointerInput(Unit) {
            detectDragGestures(onDrag = { _, offset ->
                if (offset.x < -30) {
                    onClose.invoke()
                }
            })
        }
        .background(color = Color.Transparent), contentAlignment = Alignment.Center) {

        Column() {
            Row(modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(62.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(15.dp)
                )
                .border(
                    color = Color(0xFF495CE8),
                    width = 1.5.dp,
                    shape = RoundedCornerShape(15.dp)
                )
                .padding(horizontal = 10.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier
                    .fillMaxWidth(0.18f)
                    .fillMaxHeight(), contentAlignment = Alignment.Center){

                    ReportIconWithSpeedLimit(icon = icon.value, speedLimit = speedLimit, color = color.value)
//                    Button(
//                        contentPadding = PaddingValues(0.dp),
//                        onClick = {},
//                        colors = ButtonDefaults.buttonColors(backgroundColor =  Color(0xFF495CE8)),
//                        modifier = Modifier
//                            .size(40.dp)
////                            .shadow(
////                                elevation = 30.dp,
////                                shape = RoundedCornerShape(12.dp),
////                                clip = true
////                            )
//                        ,
//                        shape = RoundedCornerShape(12.dp)
//                    ) {
//                        Icon(
//                            painter = painterResource(id = R.drawable.ic_gps,),
//                            modifier = Modifier.size(22.dp),
//                            tint = Color.White,
//                            contentDescription = ""
//                        )
//                    }
                }
                Box(modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight()
                    .padding(horizontal = 10.dp), contentAlignment = Alignment.CenterStart){
                    Text(
                        text = stringResource(id = R.string.lbl_home_bottom_toast_inside_zone),
                        modifier = Modifier.fillMaxWidth(0.85f),
                        fontSize = 14.sp,fontWeight = FontWeight.W400,
                    )
                }
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(), contentAlignment = Alignment.Center){
                    Icon(
                        modifier = Modifier
                            .clickable(onClick = onClose)
                            .padding(5.dp),
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription ="",
                        tint = Color(0xFF495CE8) )
                }

            }
            Spacer(modifier = Modifier.height(155.dp))
        }
    }
}