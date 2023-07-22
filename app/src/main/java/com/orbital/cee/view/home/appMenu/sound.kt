package com.orbital.cee.view.sound

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbital.cee.R
import com.orbital.cee.core.Constants
import com.orbital.cee.view.home.HomeActivity
import com.orbital.cee.view.home.HomeViewModel
import com.orbital.cee.view.home.appMenu.componenets.soundSettingButtons

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun sound(model: HomeViewModel, onClickBack:()->Unit) {
    var status =remember{mutableStateOf(0)}
    var tempStatus =remember{mutableStateOf(0)}
    val rotate = if (LocalConfiguration.current.layoutDirection == LayoutDirection.Rtl.ordinal){180f}else{0f}
    val soundSta = model.soundStatus.observeAsState()
    tempStatus.value = soundSta.value!!
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(bottom = 60.dp)
        .background(color = Color.White)
        .pointerInput(Unit) {
            detectDragGestures(onDrag = { point, offset ->
                if (offset.x > Constants.OFFSET_X && point.position.x < Constants.POINT_X) {
                    onClickBack.invoke()
                }
            })
        }, verticalArrangement = Arrangement.SpaceBetween, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier
            .fillMaxHeight(0.5f)
            .fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(85.dp)
                    .clip(shape = RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp))
                    .border(
                        width = 1.dp,
                        color = Color(0XFFE4E4E4),
                        shape = RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(id = R.string.lbl_setting_general_sound_setting), fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 20.sp)
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart){
                    TextButton(onClick = onClickBack, modifier = Modifier.padding(end = 12.dp)) {
                        Icon(modifier = Modifier
                            .size(22.dp)
                            .rotate(rotate), tint = Color.Black , painter = painterResource(id = R.drawable.ic_arrow_left), contentDescription ="" )
                    }
                }
            }

            Box(modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)) {
                soundSettingButtons(currentStatus = tempStatus){
                    tempStatus.value = it
                    status.value = it
                }
            }

        }
        Button(modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(55.dp), onClick = {
            onClickBack.invoke()
            model.updateSoundStatus(status.value)
            HomeActivity.Singlt.set(status.value)
        },shape = RoundedCornerShape(10.dp),colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF495CE8))) {
            Text(text = stringResource(id = R.string.btn_home_alert_save), color = Color.White, fontWeight = FontWeight.Bold)

        }


    }

}