package com.orbital.cee.view.home.appMenu.componenets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import com.orbital.cee.ui.theme.light_purple
import com.orbital.cee.ui.theme.red

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PromotionCarousel(){
    val pagerState = com.google.accompanist.pager.rememberPagerState(initialPage = 0)
    val sliderList = listOf(
        PromotionCardModel(
            image = R.drawable.ic_isolation_mode,
            title1 = "Cee you at",
            title2 = "HITEX 2023",
            title1Color = black,
            title2Color = red,
            description = "Let's meet at HITEX 2023, bonus get your gift a Speedometers & Cursor theme.",
            bgColor = Color(0x19E3454B),
            onClick = null
        ),
        PromotionCardModel(
            image = R.drawable.ic_award,
            title1 = "Supercharge Your",
            title2 = "Experience",
            title1Color = black,
            title2Color = blurple,
            description = "Unlock Background Alert, No ADs, Unlimited Trips, and more.",
            bgColor = light_purple,
            onClick = { }
        ),
    )
    Column(modifier = Modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState,
            count = sliderList.size,
            contentPadding = PaddingValues(end = 20.dp),
            modifier = Modifier.height(120.dp)
        ) {page->
           Box(
               modifier = Modifier
                   .fillMaxSize()
                   .padding(end = 10.dp)
                   .clickable(onClick = sliderList[page].onClick ?: {}, interactionSource = remember{ MutableInteractionSource() }, indication = null)
                   .background(color = sliderList[page].bgColor, shape = RoundedCornerShape(14.dp))
           ){
               Row(modifier = Modifier
                   .fillMaxSize()
                   .padding(horizontal = 16.dp, vertical = 20.dp)) {
                   Box(modifier = Modifier.size(50.dp), contentAlignment = Alignment.Center){
                       Icon(painter = painterResource(id = sliderList[page].image), contentDescription = "", tint = Color.Unspecified)
                   }
                   Spacer(modifier = Modifier.width(10.dp))
                   Column {
                       Row {
                           Text(text = sliderList[page].title1, color = sliderList[page].title1Color ?: black, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                           sliderList[page].title2?.let {
                               Spacer(modifier = Modifier.width(5.dp))
                               Text(text = it, color = sliderList[page].title2Color ?: blurple, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                           }
                       }
                       Text(
                           text = sliderList[page].description,
                           fontSize = 14.sp,
                           color = Color(0x66000000)
                       )
                   }
                   sliderList[page].onClick?.let {
                       Box(modifier = Modifier
                           .width(50.dp)
                           .fillMaxHeight(), contentAlignment = Alignment.Center){
                           Icon(painter = painterResource(id = R.drawable.ic_arrow_right), contentDescription = "", tint = blurple)
                       }
                   }
               }
           }
        }
    }
}

data class PromotionCardModel(
    val image:Int,
    val title1: String,
    val title2:String?,
    val title1Color: Color?,
    val title2Color:Color?,
    val description: String,
    val bgColor: Color,
    val onClick: (() -> Unit)?
)