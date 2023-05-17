package com.orbital.cee.view.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun ReliabilityProgressRatio(likeCount: Int, dislikeCount: Int) {
    val total = likeCount + dislikeCount
    val likePercentage = if (total > 0) likeCount.toFloat() / total.toFloat() else 0f

    val capsuleColor = when {
        likePercentage < 0.25 -> Color(0xFFEA4E34)
        likePercentage < 0.5 -> Color(0xFFF27D28)
        likePercentage < 0.75 -> Color(0xFF36B5FF)
        else -> Color(0xFF495CE8)
    }

    val reliabilityText = when {
        likePercentage < 0.25 -> "Low"
        likePercentage < 0.5 -> "Medium"
        likePercentage < 0.75 -> "High"
        else -> "Very High"
    }

    Column(verticalArrangement = Arrangement.Center) {
        Row {
            Text("Reliability:",fontSize = 16.sp, fontWeight = FontWeight.W500,color = Color(0xFF171729))
            Text(reliabilityText, color = capsuleColor,fontSize = 16.sp, fontWeight = FontWeight.W500)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .height(14.dp)
                    .clip(CircleShape)
                    .background(capsuleColor.copy(alpha = 0.5f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(likePercentage)
                        .height(14.dp)
                        .clip(CircleShape)
                        .background(capsuleColor)
                )
            }
            Spacer(modifier = Modifier.width(5.dp))
            Row(horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically,modifier = Modifier
                .wrapContentWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(painterResource(id = com.orbital.cee.R.drawable.ic_like), contentDescription = null)
                    Text("($likeCount)", color = Color(0xFF57D654),lineHeight = 16.sp,fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.width(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(painterResource(id = com.orbital.cee.R.drawable.ic_dislike), contentDescription = null)
                    Text("($dislikeCount)", color = Color(0xFFEA4E34),lineHeight = 16.sp,fontSize = 14.sp)
                }
            }
        }
    }
}
@Preview
@Composable
fun prev(){
    ReliabilityProgressRatio(20, 10)
}