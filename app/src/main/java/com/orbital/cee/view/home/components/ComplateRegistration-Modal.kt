package com.orbital.cee.view.home.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.util.Log
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.elevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.GeoPoint
import com.orbital.cee.R
import com.orbital.cee.model.ActionsButtonModel
import com.orbital.cee.utils.MetricsUtils.Companion.getAddress


import com.orbital.cee.view.home.HomeViewModel
import com.orbital.cee.view.home.Menu.componenets.switchButtonNormal
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

@Composable
fun ReportModalConfirmation(model: HomeViewModel, onPositiveClick: () -> Unit) {

    Box(modifier = Modifier
        .height(350.dp)
        .fillMaxWidth()
        .background(color = Color.Transparent), contentAlignment = Alignment.TopCenter){
        Box(modifier = Modifier.fillMaxSize(),contentAlignment = Alignment.BottomCenter){
            Card(
                Modifier
                    .height(300.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                backgroundColor = Color.White

            ) {
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.fillMaxHeight(0.05f))
                    Box(modifier = Modifier.fillMaxWidth(0.7f)){
                        Text(text = stringResource(id = R.string.lbl_home_alert_reportConfirmation_title), textAlign = TextAlign.Center, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.fillMaxHeight(0.2f))
                    Row(modifier = Modifier.fillMaxWidth(0.8f), horizontalArrangement = Arrangement.SpaceBetween) {
                        Button(
                            onClick = {model.showCustomDialogWithResult.value = false},
                            shape = RoundedCornerShape(8.dp),
                            elevation = elevation(
                                defaultElevation = 0.dp,
                                pressedElevation = 1.dp
                            ),
                            border = BorderStroke(width = 2.dp, color = Color(0XFFAAA9AB)),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                            modifier = Modifier
                                .fillMaxWidth(0.48f)
                                .height(50.dp)
                        ) {
                            Text(text = stringResource(id = R.string.btn_home_alert_reportConfirmation_no), fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                        Button(
                            onClick = onPositiveClick,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0XFF495CE8)),
                            elevation = elevation(
                                defaultElevation = 0.dp,
                                pressedElevation = 1.dp
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(text = stringResource(id = R.string.btn_home_alert_reportConfirmation_yes), fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
                Box(modifier = Modifier.fillMaxSize() , contentAlignment = Alignment.Center){
                    Box(modifier = Modifier.fillMaxWidth(0.7f)){
                        Text(text = "")
                    }
                }
                Box(modifier = Modifier.fillMaxSize() , contentAlignment = Alignment.BottomCenter){

                }

            }
        }
//        Spacer(modifier = Modifier.height(5.dp))
        Box(modifier = Modifier
            .height(100.dp)
            .width(100.dp)){
            Icon(painter = painterResource(id = R.drawable.ic_cee_two), tint = Color.Unspecified, contentDescription ="" )
        }

    }
}
@Composable
fun ReportPlaceError(onPositiveClick: () -> Unit) {
    val focusManager = LocalFocusManager.current
    Box(modifier = Modifier
        .height(300.dp)
        .fillMaxWidth()
        .pointerInput(Unit) {
            detectTapGestures(onTap = {
                focusManager.clearFocus()
            })
        }
        .background(color = Color.Transparent), contentAlignment = Alignment.TopCenter){
        Box(modifier = Modifier.fillMaxSize(),contentAlignment = Alignment.BottomCenter){
            Card(
                Modifier
                    .height(250.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                backgroundColor = Color.White

            ) {
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.fillMaxHeight(0.05f))
                    Text(modifier = Modifier.fillMaxWidth(0.6f),text = stringResource(id = R.string.lbl_error_report_place_dialog_title), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(modifier = Modifier.fillMaxHeight(0.05f))
                    Text(modifier = Modifier.fillMaxWidth(0.6f),text = stringResource(id = R.string.lbl_error_report_place_dialog_description),textAlign = TextAlign.Center, fontSize = 14.sp)
                    Spacer(modifier = Modifier.fillMaxHeight(0.05f))
                    Spacer(modifier = Modifier.fillMaxHeight(0.2f))
                    Row(modifier = Modifier.fillMaxWidth(0.9f), horizontalArrangement = Arrangement.SpaceBetween) {
                        Button(
                            onClick ={ onPositiveClick()},
                            shape = RoundedCornerShape(8.dp),
                            elevation =  ButtonDefaults.elevation(
                                defaultElevation = 0.dp,
                                pressedElevation = 0.dp,
                                disabledElevation = 0.dp,
                                hoveredElevation = 0.dp,
                                focusedElevation = 0.dp
                            ),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0XFF495CE8)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(45.dp)
                        ) {
                            Text(text = stringResource(id = R.string.btn_auth_alert_ok), modifier = Modifier.fillMaxWidth(0.6f), textAlign = TextAlign.Center, fontWeight = FontWeight.ExtraBold, color = Color.White, fontSize = 14.sp)
                        }
                    }
                }
                Box(modifier = Modifier.fillMaxSize() , contentAlignment = Alignment.Center){
                    Box(modifier = Modifier.fillMaxWidth(0.7f)){
                        Text(text = "")
                    }
                }
                Box(modifier = Modifier.fillMaxSize() , contentAlignment = Alignment.BottomCenter){

                }

            }
        }
//        Spacer(modifier = Modifier.height(5.dp))
        Box(modifier = Modifier
            .height(100.dp)
            .width(100.dp)){
            Icon(painter = painterResource(id = R.drawable.ic_cee_two), tint = Color.Unspecified, contentDescription ="" )
        }

    }
}



@Composable
fun RegisterModal(onPositiveClick: (username:String) -> Unit) {
    val fullName = remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    Box(modifier = Modifier
        .height(400.dp)
        .fillMaxWidth()
        .pointerInput(Unit) {
            detectTapGestures(onTap = {
                focusManager.clearFocus()
            })
        }
        .background(color = Color.Transparent), contentAlignment = Alignment.TopCenter){
        Box(modifier = Modifier.fillMaxSize(),contentAlignment = Alignment.BottomCenter){
            Card(
                Modifier
                    .height(350.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                backgroundColor = Color.White

            ) {
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.fillMaxHeight(0.05f))
                    Text(modifier = Modifier.fillMaxWidth(0.6f),text = stringResource(id = R.string.lbl_home_alert_username_title), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(modifier = Modifier.fillMaxHeight(0.05f))
                    Text(modifier = Modifier.fillMaxWidth(0.6f),text = stringResource(id = R.string.lbl_home_alert_username_description),textAlign = TextAlign.Center, fontSize = 14.sp)
                    Spacer(modifier = Modifier.fillMaxHeight(0.05f))
                    Box(modifier = Modifier.fillMaxWidth(0.9f)){
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp)
                                .border(
                                    BorderStroke(2.dp, Color(0xFFE4E4E4)),
                                    shape = RoundedCornerShape(10.dp)
                                ),
                            value = fullName.value,
                            placeholder = {
                                Text(text = stringResource(id = R.string.txtF_setting_name) , fontSize = 16.sp, color = Color(0XFFAAAAAA))
                            },
                            onValueChange = {fullName.value = it},
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                            shape = RoundedCornerShape(10.dp),
                        )
                    }
                    Spacer(modifier = Modifier.fillMaxHeight(0.2f))
                    Row(modifier = Modifier.fillMaxWidth(0.9f), horizontalArrangement = Arrangement.SpaceBetween) {
                        Button(
                            onClick ={ onPositiveClick(fullName.value)},
                            shape = RoundedCornerShape(8.dp),
                            elevation =  ButtonDefaults.elevation(
                                defaultElevation = 0.dp,
                                pressedElevation = 0.dp,
                                disabledElevation = 0.dp,
                                hoveredElevation = 0.dp,
                                focusedElevation = 0.dp
                            ),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0XFF495CE8)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(45.dp)
                        ) {
                            Text(text = stringResource(id = R.string.btn_home_alert_done), modifier = Modifier.fillMaxWidth(0.6f), textAlign = TextAlign.Center, fontWeight = FontWeight.ExtraBold, color = Color.White, fontSize = 14.sp)
                        }
                    }
                }
                Box(modifier = Modifier.fillMaxSize() , contentAlignment = Alignment.Center){
                    Box(modifier = Modifier.fillMaxWidth(0.7f)){
                        Text(text = "")
                    }
                }
                Box(modifier = Modifier.fillMaxSize() , contentAlignment = Alignment.BottomCenter){

                }

            }
        }
//        Spacer(modifier = Modifier.height(5.dp))
        Box(modifier = Modifier
            .height(100.dp)
            .width(100.dp)){
            Icon(painter = painterResource(id = R.drawable.ic_cee_two), tint = Color.Unspecified, contentDescription ="" )
        }

    }
}

@Composable
fun AddReportManuallyModal(onPositiveClick: (point:GeoPoint,type:Int,time:Timestamp,speedLimit:Int?,reportAddress : String,isWithNotification : Boolean) -> Unit,clickedPoint: GeoPoint) {
    val reportAddressName = remember { mutableStateOf("") }
    val lon = remember { mutableStateOf("${clickedPoint.longitude}") }
    val speedLimit = remember { mutableStateOf("0") }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    LaunchedEffect(Unit){
        reportAddressName.value =  getAddress(clickedPoint.latitude,clickedPoint.longitude, context)
    }

    var reportTypeSelected = remember {
        mutableStateOf(1)
    }
    val items = ArrayList<ActionsButtonModel>()
    items.add(
        ActionsButtonModel(
            id = 1,
            icon = R.drawable.ic_camera_fab,
            title = stringResource(id = R.string.btn_home_report_action_sheet_roadCam),
            color1 = Color(0XFF495CE8),
            color2 = Color(0x1A495CE8)
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
            id = 4,
            icon = R.drawable.ic_construction,
            title = stringResource(id = R.string.btn_home_report_action_sheet_construction),
            color1 = Color(0XFF1ED2AF),
            color2 = Color(0x191ED2AF)
        )
    )



    items.add(
        ActionsButtonModel(
            id = 5,
            icon = R.drawable.ic_static_camera,
            title = stringResource(id = R.string.btn_home_report_action_sheet_staticCam),
            color1 = Color.Unspecified,
            color2 = Color(0x1A495CE8)
        )
    )
    items.add(
        ActionsButtonModel(
            id = 6,
            icon = R.drawable.ic_point_to_point_camera,
            title = stringResource(id = R.string.btn_home_report_action_sheet_p2pCam),
            color1 = Color.Unspecified,
            color2 = Color(0x1A495CE8)
        )
    )

    val mContext = LocalContext.current
    val mCalendar = Calendar.getInstance()
    val mHour = mCalendar[Calendar.HOUR_OF_DAY]
    val mMinute = mCalendar[Calendar.MINUTE]
    val mYear = mCalendar.get(Calendar.YEAR)
    val mMonth = mCalendar.get(Calendar.MONTH)
    val mDay = mCalendar.get(Calendar.DAY_OF_MONTH)

    mCalendar.time = Date()


    val mTime = remember { mutableStateOf("$mHour:$mMinute") }
    val mDate = remember { mutableStateOf("${mYear}-${mMonth+1}-$mDay") }

    val isWithNotification = remember { mutableStateOf(false) }


    val mTimePickerDialog = TimePickerDialog(
        mContext,
        {_, mHour : Int, mMinute: Int ->
            mTime.value = "$mHour:$mMinute"
        }, mHour, mMinute, false
    )
    val mDatePickerDialog = DatePickerDialog(
        mContext,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            mDate.value = "$mYear-${mMonth+1}-$mDayOfMonth"
        }, mYear, mMonth, mDay
    )
    Box(
        Modifier
            .height(550.dp)
            .fillMaxWidth()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ),

    ) {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(modifier = Modifier.fillMaxWidth(0.6f),text = "Place Report Manually", textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.fillMaxHeight(0.05f))
            Row(modifier = Modifier.fillMaxWidth(0.9f), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically){
                Text(text = "Latitude: ${(clickedPoint.latitude * 10000.0).roundToInt() /10000.0}" , color = Color(0XFFAAAAAA),fontSize = 14.sp)
                Text(text = "Longitude: ${(clickedPoint.longitude * 10000.0).roundToInt() /10000.0}" , color = Color(0XFFAAAAAA), fontSize = 14.sp)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Notification", fontSize = 8.sp, modifier = Modifier
                        .horizontalScroll(rememberScrollState(0)))
                    Box(modifier = Modifier
                        .width(73.dp)
                        .height(47.dp)
                        .padding(8.dp), contentAlignment = Alignment.Center){
                        switchButtonNormal(MutableLiveData(isWithNotification.value)){
                            isWithNotification.value = it
                        }
                    }
                }

            }
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(horizontal = 2.dp)
                    .border(
                        BorderStroke(2.dp, Color(0xFFE4E4E4)),
                        shape = RoundedCornerShape(10.dp)
                    ),
                value = reportAddressName.value,
                placeholder = {
                    Text(text = "Address" , color = Color(0XFFAAAAAA), fontSize = 14.sp)
                },
                onValueChange = {reportAddressName.value = it},
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                shape = RoundedCornerShape(10.dp),
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(horizontal = 10.dp)) {

                items.forEach { item ->
                    Box(
                        modifier = Modifier.pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {reportTypeSelected.value = item.id}
                            )
                        },
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally,) {
                            Box(modifier  = Modifier
                                .size(45.dp)
                                .clip(shape = RoundedCornerShape(10.dp))
                                .border(
                                    width = if (reportTypeSelected.value == item.id) {
                                        1.5.dp
                                    } else {
                                        (-1).dp
                                    },
                                    color = Color(0XFF495CE8),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .background(color = item.color2), contentAlignment = Alignment.Center){
                                Icon(painter = painterResource(id = item.icon),modifier = Modifier.size(20.dp), tint = item.color1, contentDescription = item.title )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = item.title, color = Color.Black,fontSize = 8.sp)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.fillMaxHeight(0.05f))
            Row(modifier = Modifier.fillMaxWidth(0.9f)) {
                Button(
                    onClick ={ mDatePickerDialog.show()},
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0XFF495CE8)),
                    modifier = Modifier
                        .fillMaxWidth(0.30f)
                        .height(50.dp)
                        .padding(horizontal = 2.dp)
                ) {
                    Text(text = "Date", modifier = Modifier.fillMaxWidth(0.6f), textAlign = TextAlign.Center, fontWeight = FontWeight.ExtraBold, color = Color.White, fontSize = 14.sp)
                }
                Button(
                    onClick ={ mTimePickerDialog.show()},
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0XFF495CE8)),
                    modifier = Modifier
                        .fillMaxWidth(0.43f)
                        .height(50.dp)
                        .padding(horizontal = 2.dp)
                ) {
                    Text(text = "Time", modifier = Modifier.fillMaxWidth(0.6f), textAlign = TextAlign.Center, fontWeight = FontWeight.ExtraBold, color = Color.White, fontSize = 14.sp)
                }
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 2.dp)
                        .border(
                            BorderStroke(2.dp, Color(0xFFE4E4E4)),
                            shape = RoundedCornerShape(10.dp)
                        ),
                    value = speedLimit.value,
                    placeholder = {
                        Text(text = "Speed Limit" , color = Color(0XFFAAAAAA),fontSize = 14.sp)
                    },
                    onValueChange = {speedLimit.value = it},
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done, keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(10.dp),
                )
            }
            Spacer(modifier = Modifier.fillMaxHeight(0.2f))
            Row(modifier = Modifier.fillMaxWidth(0.9f), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(
                    onClick ={
                        onPositiveClick(
                            clickedPoint,reportTypeSelected.value,
                            stringToTimestamp(mDate.value+" "+mTime.value),
                            speedLimit.value.toInt(),
                            reportAddressName.value,
                            isWithNotification.value
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    elevation =  ButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp,
                        disabledElevation = 0.dp,
                        hoveredElevation = 0.dp,
                        focusedElevation = 0.dp
                    ),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0XFF495CE8)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)
                ) {
                    Text(text = "Place", modifier = Modifier.fillMaxWidth(0.6f), textAlign = TextAlign.Center, fontWeight = FontWeight.ExtraBold, color = Color.White, fontSize = 14.sp)
                }
            }
        }
        Box(modifier = Modifier.fillMaxSize() , contentAlignment = Alignment.Center){
            Box(modifier = Modifier.fillMaxWidth(0.7f)){
                Text(text = "")
            }
        }
        Box(modifier = Modifier.fillMaxSize() , contentAlignment = Alignment.BottomCenter){

        }

    }
}
@Composable
fun UpdateSpeedLimitModal(speedLimit : Int?,onPositiveClick: (speedLimit:Int) -> Unit) {
    val SpeedLimit = remember { mutableStateOf(speedLimit.toString()) }
    val focusManager = LocalFocusManager.current
    Box(modifier = Modifier
        .height(400.dp)
        .fillMaxWidth()
        .pointerInput(Unit) {
            detectTapGestures(onTap = {
                focusManager.clearFocus()
            })
        }
        .background(color = Color.Transparent), contentAlignment = Alignment.TopCenter){
        Box(modifier = Modifier.fillMaxSize(),contentAlignment = Alignment.BottomCenter){
            Card(
                Modifier
                    .height(350.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                backgroundColor = Color.White

            ) {
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.fillMaxHeight(0.05f))
                    Text(modifier = Modifier.fillMaxWidth(0.6f),text = stringResource(id = R.string.lbl_home_alert_username_title), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(modifier = Modifier.fillMaxHeight(0.05f))
                    Text(modifier = Modifier.fillMaxWidth(0.6f),text = stringResource(id = R.string.lbl_home_alert_username_description),textAlign = TextAlign.Center, fontSize = 14.sp)
                    Spacer(modifier = Modifier.fillMaxHeight(0.05f))
                    Box(modifier = Modifier.fillMaxWidth(0.9f)){
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp)
                                .border(
                                    BorderStroke(2.dp, Color(0xFFE4E4E4)),
                                    shape = RoundedCornerShape(10.dp)
                                ),
                            value = SpeedLimit.value,
                            placeholder = {
                                Text(text = stringResource(id = R.string.txtF_setting_name) , fontSize = 16.sp, color = Color(0XFFAAAAAA))
                            },
                            onValueChange = {SpeedLimit.value = it},
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done, keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(10.dp),
                        )
                    }
                    Spacer(modifier = Modifier.fillMaxHeight(0.2f))
                    Row(modifier = Modifier.fillMaxWidth(0.9f), horizontalArrangement = Arrangement.SpaceBetween) {
                        Button(
                            onClick ={ onPositiveClick(SpeedLimit.value.toInt())},
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0XFF495CE8)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(45.dp)
                        ) {
                            Text(text = "Update report", modifier = Modifier.fillMaxWidth(0.6f), textAlign = TextAlign.Center, fontWeight = FontWeight.ExtraBold, color = Color.White, fontSize = 14.sp)
                        }
                    }
                }
                Box(modifier = Modifier.fillMaxSize() , contentAlignment = Alignment.Center){
                    Box(modifier = Modifier.fillMaxWidth(0.7f)){
                        Text(text = "")
                    }
                }
                Box(modifier = Modifier.fillMaxSize() , contentAlignment = Alignment.BottomCenter){

                }

            }
        }
//        Spacer(modifier = Modifier.height(5.dp))
        Box(modifier = Modifier
            .height(100.dp)
            .width(100.dp)){
            Icon(painter = painterResource(id = R.drawable.ic_cee_two), tint = Color.Unspecified, contentDescription ="" )
        }

    }
}
fun stringToTimestamp(dateString: String): Timestamp {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
    val date = formatter.parse(dateString)
    return Timestamp(date)

}
//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview(){
//    AddReportManuallyModal(){e->
//
//    }
//}