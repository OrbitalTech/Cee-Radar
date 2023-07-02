package com.orbital.cee.view.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbital.cee.R

@Composable
fun ReportIconWithSpeedLimit(icon:Int,speedLimit:Int?,color: Color){
    Box(modifier = Modifier.size(60.dp)){
        Box(modifier = Modifier
            .padding(top = 5.dp), contentAlignment = Alignment.Center){
            Icon(painter = painterResource(id = R.drawable.bg_btn_place_cam_fab_main_scr), contentDescription = "", tint = color)
            Icon(modifier = Modifier.size(28.dp),painter = painterResource(id = icon), tint = Color.White, contentDescription = "")
        }
        speedLimit?.let {
            if (it > 9){
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Box(modifier = Modifier
                        .size(23.dp)
                        .background(color = Color.White, shape = CircleShape)
                        .border(color = Color(0xFFEA4E34), width = 1.5.dp, shape = CircleShape), contentAlignment = Alignment.Center){
                        Text(text = "$speedLimit", color = Color(0xFFEA4E34), fontSize = 10.sp, fontWeight = FontWeight.W700)
                    }
                }
            }
        }
    }
}
@Composable
@Preview
fun ReportIconWithSpeedLimitPreview(){
    val icon = remember { mutableStateOf(R.drawable.cee) }
    icon.value = R.drawable.ic_camera_fab
    ReportIconWithSpeedLimit(icon.value,110, Color(0xFFEA4E34))
}