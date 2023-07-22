package com.orbital.cee.view.home.BottomSheets

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.orbital.cee.R
import com.orbital.cee.model.ActionsButtonModel
import com.orbital.cee.ui.theme.light_gray
import com.orbital.cee.ui.theme.light_yellow
import com.orbital.cee.ui.theme.type_gray
import com.orbital.cee.ui.theme.yellow
import com.orbital.cee.utils.MetricsUtils
import com.orbital.cee.view.home.appMenu.componenets.switchButtonNormal
import com.orbital.cee.view.home.components.stringToTimestamp
import java.util.Calendar
import java.util.Date
import kotlin.math.roundToInt

@Composable
fun AddReportManuallyModal(onPositiveClick: (point: GeoPoint, type:Int, time: Timestamp, speedLimit:Int?, reportAddress : String, isWithNotification : Boolean,title:String,description:String) -> Unit, clickedPoint: GeoPoint) {
    val reportAddressName = remember { mutableStateOf("") }
    val notifyTitle = remember { mutableStateOf("") }
    val notifyDescription = remember { mutableStateOf("") }
    val lon = remember { mutableStateOf("${clickedPoint.longitude}") }
    val speedLimit = remember { mutableStateOf("0") }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    LaunchedEffect(Unit){
        reportAddressName.value =  MetricsUtils.getAddress(clickedPoint.latitude,clickedPoint.longitude, context)
    }
    val reportTypeSelected = remember {
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
            icon = R.drawable.ic_new_police,
            title = stringResource(id = R.string.btn_home_report_action_sheet_police),
            color1 = Color(0XFF36B5FF),
            color2 = Color(0x1A36B5FF)
        )
    )
    items.add(
        ActionsButtonModel(
            id = 2,
            icon = R.drawable.ic_carcrash,
            title = stringResource(id = R.string.btn_home_report_action_sheet_carCrash),
            color1 = Color(0XFFF27D28),
            color2 = Color(0x1AF27D28)
        )
    )
    items.add(
        ActionsButtonModel(
            id = 4,
            icon = R.drawable.ic_new_construction,
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
    items.add(
        ActionsButtonModel(
            id = 405,
            icon = R.drawable.ic_static_camera,
            title = stringResource(id = R.string.btn_home_report_action_sheet_staticCam) +" 405",
            color1 = type_gray,
            color2 = light_gray
        )
    )
    items.add(
        ActionsButtonModel(
            id = 406,
            icon = R.drawable.ic_point_to_point_camera,
            title = stringResource(id = R.string.btn_home_report_action_sheet_p2pCam) +" 405",
            color1 = type_gray,
            color2 = light_gray
        )
    )
    items.add(
        ActionsButtonModel(
            id = 8,
            icon = R.drawable.ic_danger,
            title = "Hazard",
            color1 = yellow,
            color2 = light_yellow
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
            .height(700.dp)
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
            if(isWithNotification.value){
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(horizontal = 2.dp)
                        .border(
                            BorderStroke(2.dp, Color(0xFFE4E4E4)),
                            shape = RoundedCornerShape(10.dp)
                        ),
                    value = notifyTitle.value,
                    placeholder = {
                        Text(text = "hey : \$ be careful." , color = Color(0XFFAAAAAA), fontSize = 14.sp)
                    },
                    onValueChange = {notifyTitle.value = it},
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    shape = RoundedCornerShape(10.dp),
                )
                Spacer(modifier = Modifier.height(5.dp))
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(horizontal = 2.dp)
                        .border(
                            BorderStroke(2.dp, Color(0xFFE4E4E4)),
                            shape = RoundedCornerShape(10.dp)
                        ),
                    value = notifyDescription.value,
                    placeholder = {
                        Text(text = "may be near you." , color = Color(0XFFAAAAAA), fontSize = 14.sp)
                    },
                    onValueChange = {notifyDescription.value = it},
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    shape = RoundedCornerShape(10.dp),
                )
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
                .horizontalScroll(state = rememberScrollState())
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
                            isWithNotification.value,
                            notifyTitle.value,
                            notifyDescription.value
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
                        .height(56.dp)
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