package com.orbital.cee.view.home.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbital.cee.R

@Composable
fun LikeButton(isLiked:Boolean?,likeCount:Int?,likePresent:MutableState<Float>,onClick:()->Unit){

    val progressAnimationValue by animateFloatAsState(
        targetValue = likePresent.value,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
    )

    Box(modifier = Modifier
        .pointerInput(Unit) {
            detectTapGestures(onTap = { onClick.invoke() })
        }
        .height(60.dp)
        .background(color = Color.White)
        .fillMaxWidth(), contentAlignment = Alignment.Center){
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)
            .padding(start = 7.dp, end = 7.dp)
            .background(
                color = if (isLiked == false) {
                    Color(0xFFF7F7F7)
                } else {
                    Color(0xFFEEFBEE)
                },
            ), contentAlignment = Alignment.CenterStart){
            if (isLiked == true){
                Box(modifier = Modifier
                    .fillMaxWidth(progressAnimationValue)
                    .height(45.dp)
                    .background(
                        color = Color(0xFF57D654),
                    ))
            }
            if (isLiked == false){
                Box(modifier = Modifier
                    .fillMaxWidth(progressAnimationValue)
                    .height(45.dp)
                    .background(
                        color = Color(0xFFE4E4E4),
                    ))
            }
            Box(modifier = Modifier.fillMaxWidth()){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(painter = painterResource(id = R.drawable.ic_like), contentDescription ="", tint = when(isLiked) {true -> { Color(0xFF2C6B2A) }false -> {Color(0xFF848484)}else -> {Color(0xFF57D654)}})
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(id = R.string.btn_home_report_feedback_sheet_like_report) + if (likeCount != null){" (${likeCount})"}else{""}, fontSize = 14.sp, fontWeight = FontWeight.W600, color = when(isLiked) {true -> { Color(0xFF2C6B2A) }false -> {Color(0xFF848484)}else -> {Color(0xFF57D654)}})
                }
            }
        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(color = Color.Transparent, shape = RoundedCornerShape(18.dp))
            .border(width = 7.dp, shape = RoundedCornerShape(18.dp), color = Color.White))
    }
}
@Composable
fun DisLikeButton(isLiked:Boolean?,dislikeCount:Int?, disLikePresent:MutableState<Float>,onClick:()->Unit){

    val progressAnimationValue by animateFloatAsState(
        targetValue = disLikePresent.value,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
    )

    Box(modifier = Modifier
        .pointerInput(Unit) {
            detectTapGestures(onTap = { onClick.invoke() })
        }
        .height(60.dp)
        .background(color = Color.White)
        .fillMaxWidth(), contentAlignment = Alignment.Center){
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)
            .padding(start = 7.dp, end = 7.dp)
            .background(
                color = if (isLiked == false) {
                    Color(0xFFF7F7F7)
                } else {
                    Color(0xFFFDEDEB)
                },
            ), contentAlignment = Alignment.CenterStart){
            if (isLiked == true){
                Box(modifier = Modifier
                    .fillMaxWidth(progressAnimationValue)
                    .height(44.dp)
                    .background(
                        color = Color(0xFFEA4E34),
                    ))

            }
            if (isLiked == false){
                Box(modifier = Modifier
                    .fillMaxWidth(progressAnimationValue)
                    .height(45.dp)
                    .background(
                        color = Color(0xFFE4E4E4),
                    ))
            }
            Box(modifier = Modifier.fillMaxWidth()){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(painter = painterResource(id = R.drawable.ic_dislike), contentDescription ="", tint = when(isLiked) {true -> { Color(0xFF75271A) }false -> {Color(0xFF848484)}else -> {Color(0xFFEA4E34)}})
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(id = R.string.btn_home_report_feedback_sheet_dislike_report) + if (dislikeCount != null){" (${dislikeCount})"}else{""}, fontSize = 14.sp, fontWeight = FontWeight.W600, color = when(isLiked) {true -> { Color(0xFF75271A) }false -> {Color(0xFF848484)}else -> {Color(0xFFEA4E34)}})
                }
            }
        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(color = Color.Transparent, shape = RoundedCornerShape(18.dp))
            .border(width = 7.dp, shape = RoundedCornerShape(18.dp), color = Color.White))
    }
}
@Preview(showBackground = true)
@Composable
fun defaultPreviewe(){
    val aa = remember {
        mutableStateOf(0.3f)
    }
    DisLikeButton(false, disLikePresent = aa, dislikeCount = 55){

    }

}