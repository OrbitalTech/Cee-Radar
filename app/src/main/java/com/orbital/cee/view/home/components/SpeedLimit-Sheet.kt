package com.orbital.cee.view.home.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.orbital.cee.R
import com.orbital.cee.model.OnBoardingModel
import com.orbital.cee.view.trip.advancedShadow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class SpeedLimits(
    val id: Int,
    val value: Int,
    val desc: String
)
@Composable
fun speedLimit(onSelectedSpeed:(speed:Int)->Unit,onDismiss:()->Unit){

    val items = ArrayList<SpeedLimits>()
    items.add(SpeedLimits(id = 0, value = 30,""))
    items.add(SpeedLimits(id = 1, value = 40,""))
    items.add(SpeedLimits(id = 2, value = 50,""))
    items.add(SpeedLimits(id = 3, value = 60,""))
    items.add(SpeedLimits(id = 4, value = 80,""))
    items.add(SpeedLimits(id = 5, value = 100,""))
    items.add(SpeedLimits(id = 6, value = 110,""))

    val conf = LocalConfiguration.current
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val selectedSpeed = remember {
        mutableStateOf(-1)
    }
    LaunchedEffect(Unit){
//        delay(2000)
        scope.launch { scrollState.animateScrollTo(1550,tween(durationMillis = 2000, easing = FastOutSlowInEasing)) }
    }

    Column(modifier = Modifier
//        .fillMaxWidth()
//        .fillMaxHeight(fraction = 0.58f)
        .fillMaxHeight(0.48f)
//        .height(380.dp)
//        .width(500.dp)
        .fillMaxWidth()
        .background(
            color = Color.White,
            shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
        ), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.2f), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(id = R.string.lbl_speedlimit_sheet_title), textAlign = TextAlign.Center, fontSize = 18.sp, modifier = Modifier.fillMaxWidth(0.6f))
        }
        Row(modifier = Modifier
            .horizontalScroll(scrollState)
            .fillMaxWidth()
            .fillMaxHeight(0.45f)) {
            items.forEach {SL->
                Box(modifier = Modifier
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            selectedSpeed.value = SL.id
                        })
                    }
                    .width((conf.screenWidthDp / 4).toFloat().dp)
                    .fillMaxHeight()
                    .background(color = Color.White, shape = RoundedCornerShape(20.dp))
                    .border(
                        width = 1.dp,
                        color = if (SL.id == selectedSpeed.value) {
                            Color(0XFF495CE8)
                        } else {
                            Color.LightGray
                        },
                        shape = RoundedCornerShape(20.dp)
                    )
                    , contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(bottom = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier
                            .fillMaxHeight(0.9f)
                            .padding(vertical = 10.dp), verticalArrangement = Arrangement.SpaceBetween, horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = stringResource(id = R.string.lbl_speedlimit_sheet_card_title), fontWeight = FontWeight.W700,textAlign = TextAlign.Center, fontSize = 16.sp, modifier = Modifier.fillMaxWidth(0.8f))

                            Text(text = "${SL.value}",textAlign = TextAlign.Center, color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 24.sp, modifier = Modifier.fillMaxWidth(0.8f))

                        }
                        Text(text = "Km/h",textAlign = TextAlign.Center, fontSize = 12.sp, modifier = Modifier.fillMaxWidth(0.8f))
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
        
        Row(modifier = Modifier.fillMaxWidth(0.9f)) {
            Button(
                onClick ={
                            if (selectedSpeed.value >-1){
                                onSelectedSpeed(items[selectedSpeed.value].value)
                                onDismiss.invoke()
                            }
                         },
                enabled = selectedSpeed.value >-1,

                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0XFF495CE8), disabledBackgroundColor = Color.LightGray, disabledContentColor = Color.DarkGray),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = stringResource(id = R.string.btn_speedlimit_sheet_add_button), modifier = Modifier.fillMaxWidth(0.6f), textAlign = TextAlign.Center, fontWeight = FontWeight.ExtraBold, color = Color.White, fontSize = 14.sp)
            }
            
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            TextButton(
                onClick =onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.btn_speedlimit_sheet_dismiss_button), textAlign = TextAlign.Center, fontWeight = FontWeight.ExtraBold, color = Color.LightGray, fontSize = 14.sp)
            }

        }

    }
}
@Preview(showBackground = true)
@Composable
fun SpeedLimitPreview(){
    speedLimit(onDismiss = {}, onSelectedSpeed = {})
}