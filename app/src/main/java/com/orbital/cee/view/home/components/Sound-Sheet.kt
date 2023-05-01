package com.orbital.cee.view.home.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbital.cee.R
import com.orbital.cee.view.home.HomeActivity
import com.orbital.cee.view.home.HomeViewModel
import com.orbital.cee.view.home.Menu.componenets.soundSettingButtons

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun SoundBottomModal (model: HomeViewModel, onClose : ()-> Unit ){
    var tempStatus = remember{ mutableStateOf(0) }
    val soundSta = model.soundStatus.observeAsState()
    tempStatus.value = soundSta.value!!
    Column(modifier = Modifier
        .fillMaxWidth()
        .height(390.dp)
        .background(
            color = Color.White,
            shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)
        ), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(vertical = 20.dp), horizontalArrangement = Arrangement.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Divider(
                    color = Color(0XFFE4E4E4),
                    thickness = 3.dp,
                    modifier = Modifier.width(40.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = stringResource(id = R.string.lbl_home_sound_sheet_title), fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        }
        Divider(color = Color(0XFFE4E4E4),thickness = 1.dp)
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)) {
            soundSettingButtons (currentStatus = tempStatus){
                model.updateSoundStatus(it)
                HomeActivity.Singlt.set(it)
            }
        }
        Button(modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(55.dp), onClick = onClose,shape = RoundedCornerShape(10.dp),colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF495CE8))) {
            Text(text = stringResource(id = R.string.btn_home_sound_sheet_close), color = Color.White, fontWeight = FontWeight.Bold)

        }
    }


}