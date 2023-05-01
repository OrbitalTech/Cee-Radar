package com.orbital.cee.view.trip.SpeedoMeters

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.orbital.cee.R
import com.orbital.cee.view.home.components.ReportIconWithSpeedLimit

@Composable
fun SpeedOMeterOnBoard(timer:String,icon:Int,speedLimit:Int?,color: Color,text1:String,text2:String?){
    val conf = LocalConfiguration.current
    Row(modifier = Modifier

        .fillMaxWidth()
        .height(
            height = if (conf.screenWidthDp < 350) {
                105.dp
            } else {
                110.dp
            }
        ), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White), contentAlignment = Alignment.Center){
            Image(modifier = Modifier.fillMaxSize(),painter = painterResource(id = R.drawable.folder_shape), contentScale = ContentScale.FillWidth, contentDescription = "")
            Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.Top) {
                Column(Modifier.fillMaxSize(), horizontalAlignment = if (LocalConfiguration.current.layoutDirection == LayoutDirection.Rtl.ordinal){Alignment.Start}else{Alignment.End}) {
                    Row(modifier = Modifier
                        .fillMaxWidth(0.35f)
                        .padding(top = 6.dp, bottom = 4.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.Bottom) {
                        Text(text = timer, fontSize = 18.sp, fontWeight = FontWeight.W600)
                        Spacer(modifier = Modifier.width(5.dp))

                        Text(text = stringResource(id = R.string.lbl_speedometer_trip_duration_hour) , fontSize = 10.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                        Box(modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.25f), contentAlignment = Alignment.TopCenter){
                            ReportIconWithSpeedLimit(icon,speedLimit,color)

                        }
                        Box(modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(), contentAlignment = Alignment.TopCenter){
                            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center) {
                                Text(text = text1)
                                text2?.let {
                                    Text(text = it, fontSize =if(conf.screenWidthDp<350){18.sp}else{24.sp} , fontWeight = FontWeight.Bold)
                                }

                            }
                        }
                    }
                    
                }

            }
        }


    }

}

@Composable
@Preview
fun SpeedOMeterOnBoardingPreview(){
    SpeedOMeterOnBoard("",R.drawable.ic_camera_fab,30,Color.Red,"","")
}