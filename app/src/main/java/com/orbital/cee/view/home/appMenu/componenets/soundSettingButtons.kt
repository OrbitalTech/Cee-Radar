package com.orbital.cee.view.home.appMenu.componenets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.orbital.cee.R

@Composable
fun soundSettingButtons(currentStatus:MutableState<Int>,onSoundChange:(Status:Int)->Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(),Arrangement.SpaceBetween) {
        Column(Modifier.fillMaxWidth()) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .background(color = Color.White)){
                Box(modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .fillMaxHeight(), contentAlignment = Alignment.Center){
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(onClick = { onSoundChange(1) }) ,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .background(
                                    color = if (currentStatus.value == 1) Color(0XFF495CE8) else Color(
                                        0XFFE4E4E4
                                    ), shape = RoundedCornerShape(28.dp)
                                ), contentAlignment = Alignment.Center
                        ){
                            Icon(painter = painterResource(id = R.drawable.volume_high), modifier = Modifier.size(45.dp), tint =if (currentStatus.value == 1) Color.White else Color(0XFF989898) , contentDescription ="" )
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(text = stringResource(id = R.string.btn_home_sound_sheet_soundOn), color = Color(0XFF989898), fontWeight = FontWeight.Bold)
                    }
                }
                Divider(
                    color = Color(0XFFE4E4E4),
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                )
                Box(modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .fillMaxHeight(), contentAlignment = Alignment.Center){
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(onClick = { onSoundChange(2) }),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .background(
                                    color = if (currentStatus.value == 2) Color(0XFF495CE8) else Color(
                                        0XFFE4E4E4
                                    ), shape = RoundedCornerShape(28.dp)
                                ), contentAlignment = Alignment.Center
                        ){
                            Icon(painter = painterResource(id = R.drawable.ic_sound_vibrate), modifier = Modifier.size(45.dp), tint = if (currentStatus.value == 2) Color.White else Color(0XFF989898), contentDescription ="" )
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(text = stringResource(id = R.string.btn_home_sound_sheet_vibrationOnly), color = Color(0XFF989898), fontWeight = FontWeight.Bold)
                    }
                }
                Divider(
                    color = Color(0XFFE4E4E4),
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                )
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(), contentAlignment = Alignment.Center){
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(onClick = { onSoundChange(3) }),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .background(
                                    color = if (currentStatus.value == 3) Color(0XFF495CE8) else Color(
                                        0XFFE4E4E4
                                    ), shape = RoundedCornerShape(28.dp)
                                ), contentAlignment = Alignment.Center
                        ){
                            Icon(painter = painterResource(id = R.drawable.ic_volume_slash), modifier = Modifier.size(45.dp), tint = if (currentStatus.value == 3) Color.White else Color(0XFF989898), contentDescription ="" )
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(text = stringResource(id = R.string.btn_home_sound_sheet_soundOff), color = Color(0XFF989898), fontWeight = FontWeight.Bold)
                    }
                }
            }
            Divider(
                color = Color(0XFFE4E4E4),
                thickness = 1.dp
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun defaultPreview(){
    //sound()
}