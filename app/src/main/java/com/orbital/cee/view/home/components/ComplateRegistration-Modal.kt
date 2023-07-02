package com.orbital.cee.view.home.components

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.elevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Timestamp
import com.orbital.cee.R


import com.orbital.cee.view.home.HomeViewModel
import java.text.SimpleDateFormat

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
fun UpdateSpeedLimitModal(
    isString :Boolean,
    speedLimit : String?,
    onPositiveClick: (speedLimit:String) -> Unit,
    labelOne:String,
    labelTwo: String? = null
    ) {
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
                    Text(modifier = Modifier.fillMaxWidth(0.6f),text = labelOne, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    labelTwo?.let {
                        Spacer(modifier = Modifier.fillMaxHeight(0.05f))
                        Text(modifier = Modifier.fillMaxWidth(0.6f),text = it,textAlign = TextAlign.Center, fontSize = 14.sp)
                    }
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
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done, keyboardType = if(!isString){KeyboardType.Number}else{KeyboardType.Text}),
                            shape = RoundedCornerShape(10.dp),
                        )
                    }
                    Spacer(modifier = Modifier.fillMaxHeight(0.2f))
                    Row(modifier = Modifier.fillMaxWidth(0.9f), horizontalArrangement = Arrangement.SpaceBetween) {
                        Button(
                            onClick ={ onPositiveClick(SpeedLimit.value)},
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0XFF495CE8)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(45.dp)
                        ) {
                            Text(text = "Complete", modifier = Modifier.fillMaxWidth(0.6f), textAlign = TextAlign.Center, fontWeight = FontWeight.ExtraBold, color = Color.White, fontSize = 14.sp)
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