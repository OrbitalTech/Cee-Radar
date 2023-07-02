package com.orbital.cee.view.home.appMenu.componenets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbital.cee.R
import com.orbital.cee.ui.theme.black
import com.orbital.cee.ui.theme.blurple
import com.orbital.cee.ui.theme.red
import com.orbital.cee.ui.theme.white

@Composable
fun SpeedometerCarousel(speedometerID :String,onClickUnlockSpeedometer:()->Unit,
                        ownSpeedometers : List<String?>?,
                        onClickUnlockHitexSpeedometer:()->Unit,
                        onSelectedSpeedometer:(id:String)->Unit
){
   val selectedSpeedometerId = remember { mutableStateOf(speedometerID) }
    val scrollState = rememberScrollState()
//    val speedometerList : ArrayList<SpeedometerCardModel> = arrayListOf()
    val sliderList = listOf(
        SpeedometerCardModel(
            id = "Original",
            image = R.drawable.img_ceedometer,
            name = "Ceedometer",
            selected = true,
            locked = false,
        ),
        SpeedometerCardModel(
            id = "HITEX",
            image = R.drawable.img_hitex_speedometer,
            name = "Hitex",
            selected = false,
            locked = true,
        ),
        SpeedometerCardModel(
            id = "Analog",
            image = R.drawable.img_speedometer_classic,
            name = "Classic",
            selected = false,
            locked = true,
        ),
        SpeedometerCardModel(
            id = "Pro",
            image = R.drawable.img_speedometer_pro,
            name = "Ceedometer Pro",
            selected = false,
            locked = true,
        ),
        SpeedometerCardModel(
            id = "ProPlus",
            image = R.drawable.img_speedometer_one,
            name = "Pro Plus",
            selected = false,
            locked = true,
        ),
        SpeedometerCardModel(
            id = "ProMax",
            image = R.drawable.img_speedometer_two,
            name = "Pro Max",
            selected = false,
            locked = true,
        ),
    )
    fun isOwn(id: String) : String?{
        return ownSpeedometers?.find { report ->
            report == id
        }
    }
    if (isOwn(speedometerID) == null){
        selectedSpeedometerId.value = "Original"
        onSelectedSpeedometer("Original")
    }

//    val selectedSpeedometerId = remember { mutableStateOf(isOwn(speedometerID) ?: "Original") }
//    LaunchedEffect(Unit){
//        sliderList.forEach {speedo->
//            if (isOwn(speedo.id) != null){
//                speedometerList.add(
//                    SpeedometerCardModel(
//                        id = speedo.id,
//                        image = speedo.image,
//                        name = speedo.name,
//                        selected = speedo.selected,
//                        locked = false
//                ))
//            }else{
//                speedometerList.add(speedo)
//            }
//        }
//    }



    Column(Modifier.fillMaxWidth()) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)) {
            sliderList.forEach { speedo->
                Box(
                    modifier = Modifier
                        .width(164.dp)
                        .height(214.dp)
                        .padding(end = 10.dp)
                        .border(
                            width = 1.dp, color = if (selectedSpeedometerId.value == speedo.id) {
                                blurple
                            } else {
                                Color(0xFFF2F2F2)
                            }, shape = RoundedCornerShape(22.dp)
                        )
                        .clickable(
                            onClick = {
                                if (isOwn(speedo.id) == null) {
                                    if (speedo.id == "HITEX"){
                                        onClickUnlockHitexSpeedometer()
                                    }else{
                                        onClickUnlockSpeedometer()
                                    }

                                } else {
                                    onSelectedSpeedometer(speedo.id)
                                    selectedSpeedometerId.value = speedo.id
                                }
                            },
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        )
                        .background(color = white, shape = RoundedCornerShape(22.dp))
                ){
                    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween, horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(modifier = Modifier
                            .size(132.dp)
                            .padding(start = 16.dp, end = 16.dp, top = 19.dp), contentAlignment = Alignment.Center){
                            Image(modifier = Modifier.fillMaxSize(),painter = painterResource(id = speedo.image), contentDescription = "" )
                        }
                        Column(modifier = Modifier
                            .padding(bottom = 14.dp, start = 14.dp)
                            .fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                            Text(text = speedo.name, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = black)
                            Spacer(modifier = Modifier.height(5.dp))
                            if (isOwn(speedo.id) == null){
                                if (speedo.id == "HITEX"){
                                    Text(text = "Try it free", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = red)
                                }else{
                                    Text(text = "Unlock", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = red)
                                }
                            }else if(selectedSpeedometerId.value == speedo.id){
                                Text(text = "Selected", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = blurple)
                            } else{
                                Text(text = "Unlocked", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = blurple)
                            }
                        }
                    }
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp), contentAlignment = Alignment.TopEnd){
                        Column() {
                            AnimatedVisibility(visible = selectedSpeedometerId.value == speedo.id, enter = fadeIn(), exit = fadeOut()) {
                                Icon(painter = painterResource(id = R.drawable.ic_tick_circle), contentDescription = "", tint = blurple)
                            }
                        }
                    }
                }
            }
        }
    }
}

data class SpeedometerCardModel(
    val id:String,
    val image:Int,
    val name: String,
    val selected:Boolean,
    val locked : Boolean,
)