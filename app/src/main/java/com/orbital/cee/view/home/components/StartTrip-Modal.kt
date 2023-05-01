package com.orbital.cee.view.home.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.airbnb.lottie.compose.*
import com.orbital.cee.R

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun startTripView (onClickStart: ()-> Unit, onClickContinue: ()-> Unit){
    val focusManager = LocalFocusManager.current
    Box(modifier = Modifier
        .height(350.dp)
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
                    .height(300.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                backgroundColor = Color.White
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(top = 80.dp, bottom = 20.dp), verticalArrangement = Arrangement.SpaceBetween, horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(modifier = Modifier.fillMaxWidth(0.7f),text = stringResource(id = R.string.lbl_home_alert_trip_start_title), fontWeight = FontWeight.Bold,textAlign = TextAlign.Center, fontSize = 16.sp)


                    Column() {
                        Row(modifier = Modifier.fillMaxWidth(0.7f), horizontalArrangement = Arrangement.SpaceBetween) {
                            Button(
                                onClick =onClickContinue,
                                elevation = ButtonDefaults.elevation(
                                    defaultElevation = 0.dp,
                                    pressedElevation = 2.dp,
                                    disabledElevation = 0.dp
                                ),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0XFF495CE8)),


                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(45.dp)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.lbl_home_alert_trip_start_continue_trip),
                                    modifier = Modifier.fillMaxWidth(1f),
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.fillMaxHeight(0.05f))
                        Row(modifier = Modifier.fillMaxWidth(0.7f), horizontalArrangement = Arrangement.SpaceBetween) {
                            Button(
                                onClick =onClickStart,
                                shape = RoundedCornerShape(8.dp),
                                elevation = ButtonDefaults.elevation(
                                    defaultElevation = 0.dp,
                                    pressedElevation = 2.dp,
                                    disabledElevation = 0.dp
                                ),
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White, contentColor = Color(0XFF495CE8)),
                                border = BorderStroke(1.dp, color = Color(0XFF495CE8)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(45.dp)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.lbl_home_alert_trip_start_start_new_trip),
                                    modifier = Modifier.fillMaxWidth(1f),
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0XFF495CE8),
                                    fontSize = 14.sp,
                                    letterSpacing = 0.5.sp
                                )
                            }
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
            Icon(painter = painterResource(id = R.drawable.ic_cee_select_lang), tint = Color.Unspecified, contentDescription ="" )
        }
    }
}
@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun startTripDialog(
    onClickStart: ()-> Unit, onClickContinue: ()-> Unit,onDismiss:()->Unit
) {
    Dialog(onDismissRequest = onDismiss,properties = DialogProperties(
        usePlatformDefaultWidth = false
    ), content = {
        Surface(
            color = Color.Transparent,
            modifier = Modifier.fillMaxWidth(0.9f),
            content = {
                startTripView(onClickStart = onClickStart,onClickContinue = onClickContinue)
            }
        )
    })
}

@RequiresApi(Build.VERSION_CODES.S)
@Preview()
@Composable
fun modalPreview(){
    startTripView(onClickContinue = {}, onClickStart = {})
}