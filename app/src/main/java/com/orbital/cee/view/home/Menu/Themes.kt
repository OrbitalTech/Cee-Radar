package com.orbital.cee.view.home.Menu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbital.cee.R
import com.orbital.cee.ui.theme.black
import com.orbital.cee.view.home.Menu.componenets.CursorCarousel
import com.orbital.cee.view.home.Menu.componenets.PromotionCarousel
import com.orbital.cee.view.home.Menu.componenets.SpeedometerCarousel

@Composable
fun themes(onClickBack:()->Unit){
    val rotate = if (LocalConfiguration.current.layoutDirection == LayoutDirection.Rtl.ordinal){180f}else{0f}
    Column(modifier = Modifier
        .fillMaxSize()
        .background(color = MaterialTheme.colors.background)
        .padding(start = 24.dp)) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(85.dp)
            .clickable(
                onClick = onClickBack,
                indication = null,
                interactionSource = remember { MutableInteractionSource() })
            .padding(bottom = 8.dp), verticalAlignment = Alignment.Bottom) {
            Icon(modifier = Modifier
                .rotate(rotate)
                .padding(bottom = 5.dp), tint = Color(0xFF848484) , painter = painterResource(id = R.drawable.ic_arrow_left), contentDescription ="" )
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = "Themes", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = black)
        }
        Spacer(modifier = Modifier.height(15.dp))

        PromotionCarousel()
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Speedometers", color = black, fontSize = 20.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(12.dp))
        SpeedometerCarousel()
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Cursor", color = black, fontSize = 20.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(12.dp))
        CursorCarousel()



    }
}

