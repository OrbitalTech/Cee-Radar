package com.orbital.cee.view.home.components

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbital.cee.R
import com.orbital.cee.model.ActionsButtonModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UpdateCeeMap(onCloseClick: () -> Unit , onButtonClicked:(type:Int) -> Unit,userType:Int = 0) {
   // val coroutineScope = rememberCoroutineScope()

    val items = ArrayList<ActionsButtonModel>()
    val items2 = ArrayList<ActionsButtonModel>()
    items.add(
        ActionsButtonModel(
            id = 4,
            icon = R.drawable.ic_construction,
            title = stringResource(id = R.string.btn_home_report_action_sheet_construction),
            color1 = Color(0XFF1ED2AF),
            color2 = Color(0x191ED2AF)
        )
    )
    items.add(
        ActionsButtonModel(
            id = 3,
            icon = R.drawable.ic_police,
            title = stringResource(id = R.string.btn_home_report_action_sheet_police),
            color1 = Color(0XFF36B5FF),
            color2 = Color(0x1A36B5FF)
        )
    )
    items.add(
        ActionsButtonModel(
            id = 2,
            icon = R.drawable.ic_car_crash,
            title = stringResource(id = R.string.btn_home_report_action_sheet_carCrash),
            color1 = Color(0XFFF27D28),
            color2 = Color(0x1AF27D28)
        )
    )
    items.add(
        ActionsButtonModel(
            id = 1,
            icon = R.drawable.ic_camera_fab,
            title = stringResource(id = R.string.btn_home_report_action_sheet_roadCam),
            color1 = Color(0XFF495CE8),
            color2 = Color(0x1A495CE8)
        )
    )
        items2.add(
        ActionsButtonModel(
            id = 5,
            icon = R.drawable.ic_static_camera,
            title = stringResource(id = R.string.btn_home_report_action_sheet_staticCam),
            color1 = Color.Unspecified,
            color2 = Color(0x1A495CE8)
        )
    )
    items2.add(
        ActionsButtonModel(
            id = 6,
            icon = R.drawable.ic_point_to_point_camera,
            title = stringResource(id = R.string.btn_home_report_action_sheet_p2pCam),
            color1 = Color.Unspecified,
            color2 = Color(0x1A495CE8)
        )
       )
    items2.add(
        ActionsButtonModel(
            id = 405,
            icon = R.drawable.ic_static_camera,
            title = stringResource(id = R.string.btn_home_report_action_sheet_staticCam) + "405",
            color1 = Color(0xff848484),
            color2 = Color(0x66848484)
        )
    )


    Box(modifier = Modifier
        .fillMaxWidth()
        .height(
            if (userType == 2) {
                400.dp
            } else {
                300.dp
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
                    Box(
                        modifier = Modifier.pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {onButtonClicked(item.id)}
                            )
                        },
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally,) {
                            Box(modifier  = Modifier
                                .size(65.dp)
                                .clip(shape = RoundedCornerShape(20.dp))
                                .background(color = item.color2), contentAlignment = Alignment.Center){
                                Icon(painter = painterResource(id = item.icon),modifier = Modifier.size(30.dp), tint = item.color1, contentDescription = item.title )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = item.title, color = Color.Black,fontSize = 10.sp)
                        }
                    }
                }
            }
            if (userType == 2){
                Row(horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                    .fillMaxWidth(items2.size * 0.26f)
                    .height(100.dp)
                    .padding(horizontal = 20.dp)) {

                    items2.forEach { item ->
                        Box(
                            modifier = Modifier.pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = {
                                        onButtonClicked(item.id)
                                    }
                                )
                            },
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally,) {
                                Box(modifier  = Modifier
                                    .size(65.dp)
                                    .clip(shape = RoundedCornerShape(20.dp))
                                    .background(color = item.color2), contentAlignment = Alignment.Center){
                                    Icon(painter = painterResource(id = item.icon),modifier = Modifier.size(30.dp), tint = item.color1, contentDescription = item.title )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(text = item.title, color = Color.Black,fontSize = 10.sp)
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