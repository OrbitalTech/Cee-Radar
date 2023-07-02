package com.orbital.cee.view.home.BottomSheets

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbital.cee.R
import com.orbital.cee.model.UserTiers
import com.orbital.cee.utils.MetricsUtils.Companion.getReportUiByReportType

@Composable
fun UpdateCeeMap(onCloseClick: () -> Unit , onButtonClicked:(type:Int) -> Unit,userType:UserTiers? = UserTiers.GUEST) {
    val context = LocalContext.current
    val items = arrayListOf(1,2,3,4)
    val items2 = arrayListOf(5,6,405,406)

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(
            if (userType == UserTiers.ADMIN) {
                450.dp
            } else {
                350.dp
            }
        )
        .clip(shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp))
        .background(color = Color.White)){
        Column() {
            IconButton(onClick = onCloseClick ) {
                Icon(Icons.Filled.Close, contentDescription ="" )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ){
                Text(text = stringResource(id = R.string.lbl_home_report_action_sheet_title), fontWeight = FontWeight.Bold, fontSize = 22.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(horizontal = 20.dp)) {

                items.forEach { item ->
                    val reportUI = getReportUiByReportType(reportType = item, context = context)
                    Box(modifier = Modifier.pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {onButtonClicked(item)}
                            )
                        },
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally,) {
                            Box( contentAlignment = Alignment.Center){
                                Icon(painter = painterResource(id = R.drawable.bg_btn_place_cam_fab_main_scr), contentDescription = "", tint = reportUI.color2)
                                Icon(painter = painterResource(id = reportUI.icon),modifier = Modifier.size(30.dp), tint = reportUI.color1, contentDescription = "" )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = reportUI.title, color = Color.Black,fontSize = 10.sp)
                        }
                    }
                }
            }
            if (userType == UserTiers.ADMIN){
                Row(horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(horizontal = 20.dp)) {

                    items2.forEach { item ->
                        val reportUI = getReportUiByReportType(reportType = item, context = context)
                        Box(modifier = Modifier.pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = {onButtonClicked(item)}
                                )
                            },
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally,) {
                                Box( contentAlignment = Alignment.Center){
                                    Icon(painter = painterResource(id = R.drawable.bg_btn_place_cam_fab_main_scr), contentDescription = "", tint = reportUI.color2)
                                    Icon(painter = painterResource(id = reportUI.icon),modifier = Modifier.size(30.dp), tint = reportUI.color1, contentDescription = "" )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(text = reportUI.title, color = Color.Black,fontSize = 10.sp)
                            }
                        }
                    }

                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(text = stringResource(id = R.string.lbl_home_report_action_sheet_footer), modifier = Modifier.width(200.dp),color= Color(0XFF989898), textAlign = TextAlign.Center, fontSize = 12.sp)

            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}