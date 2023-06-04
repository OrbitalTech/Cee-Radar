package com.orbital.cee.view.home.Menu.componenets

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
import androidx.compose.foundation.layout.PaddingValues
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
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.orbital.cee.R
import com.orbital.cee.ui.theme.black
import com.orbital.cee.ui.theme.blurple
import com.orbital.cee.ui.theme.red
import com.orbital.cee.ui.theme.white

@OptIn(ExperimentalPagerApi::class)
@Composable
fun SpeedometerCarousel(){
    val pagerState = com.google.accompanist.pager.rememberPagerState(initialPage = 0)
    val selectedSpeedometerId = remember { mutableStateOf("1") }
    val scrollState = rememberScrollState()
    val sliderList = listOf(
        SpeedometerCardModel(
            id = "1",
            image = R.drawable.img_ceedometer,
            name = "Ceedometer",
            selected = true,
            locked = false,
            onClick = {}
        ),
        SpeedometerCardModel(
            id = "2",
            image = R.drawable.img_hitex_speedometer,
            name = "Hitex",
            selected = false,
            locked = true,
            onClick = {}
        ),
        SpeedometerCardModel(
            id = "3",
            image = R.drawable.img_hitex_speedometer,
            name = "Hitex",
            selected = false,
            locked = false,
            onClick = {}
        ),
    )
    Column(Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(scrollState)) {
            sliderList.forEach { speedo->
                Box(
                    modifier = Modifier
                        .width(164.dp)
                        .height(214.dp)
                        .padding(end = 10.dp)
                        .border(
                            width = 1.dp, color = if (speedo.selected) {
                                blurple
                            } else {
                                Color(0xFFF2F2F2)
                            }, shape = RoundedCornerShape(22.dp)
                        )
                        .clickable(
                            onClick = { speedo.onClick(speedo.id) },
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
                            if (speedo.locked){
                                Text(text = "Unlock", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = red)
                            }else if(speedo.selected){
                                Text(text = "Selected", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = blurple)
                            } else{
                                Text(text = "Unlocked", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = blurple)
                            }
                        }
                    }
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp), contentAlignment = Alignment.TopEnd){
                        Column() {
                            AnimatedVisibility(visible = speedo.selected, enter = fadeIn(), exit = fadeOut()) {
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
    val onClick: (id:String) -> Unit
)