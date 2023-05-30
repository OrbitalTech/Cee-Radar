package com.orbital.cee.view.home.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.orbital.cee.R

@Composable
fun NewBottomBar(){
    Box(modifier = Modifier
        .height(175.dp)
        .fillMaxWidth()
        .padding(24.dp)
        .background(color = Color.Transparent)){
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier
                .size(88.dp)
                .background(color = Color.White, shape = CircleShape), contentAlignment = Alignment.Center){
                Speedometer()
            }
            Row(modifier = Modifier
                .width(119.dp)
                .height(68.dp)
                .background(color = Color.White, shape = RoundedCornerShape(34.dp))){
                Box(modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .fillMaxHeight()
                    .size(24.dp)
                    .clip(shape = RoundedCornerShape(topStart = 34.dp, bottomStart = 34.dp)), contentAlignment = Alignment.Center){
                    Icon(painter = painterResource(id = R.drawable.ic_plus), contentDescription = "",tint = Color(0xFF495CE8))
                }
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .size(24.dp)
                    .clip(shape = RoundedCornerShape(topEnd = 34.dp, bottomEnd = 34.dp)), contentAlignment = Alignment.Center){
                    Icon(painter = painterResource(id = R.drawable.ic_volume_slash), contentDescription = "", tint = Color(0xFF495CE8))
                }

            }

            
        }
    }
}

@Composable
fun Speedometer(){
    val progressAnimationValue by animateFloatAsState(
        targetValue = 0.3f,
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
    )
    Canvas(modifier = Modifier.fillMaxSize().padding(10.dp),) {
        drawArc(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFD6D6D6),
                    Color(0xFFD6D6D6),
                )
            ),
            startAngle = 134.5f,
            sweepAngle = 360 * 0.75f,
            useCenter = false,
            style = Stroke(
                5.dp.toPx(),
                cap = StrokeCap.Round,
            ),
        )
        drawArc(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF495CE8) ,
                    Color(0xFF495CE8) ,
                )
            ),
            startAngle = 135f,
            sweepAngle = 330 * progressAnimationValue,
            useCenter = false,
            style = Stroke(
                5.dp.toPx(),
                cap = StrokeCap.Round,
            ),
        )
    }
}
@Preview
@Composable
fun newPriv(){
    NewBottomBar()
}