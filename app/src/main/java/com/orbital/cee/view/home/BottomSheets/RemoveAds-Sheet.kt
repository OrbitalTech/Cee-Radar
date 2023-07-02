package com.orbital.cee.view.home.BottomSheets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.orbital.cee.R

@Composable
fun RemoveAds(
    isWatched: MutableState<Boolean>,
    onClickClose:()->Unit,
    onClickWatchVideo:()->Unit
){

    val composition by rememberLottieComposition(
        LottieCompositionSpec
            .RawRes(resId = if(isWatched.value){R.raw.lottie_cee_adfree}else{R.raw.lottie_cee_remove_ads})
    )

    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true,
        speed = 1f,
        restartOnPlay = false
    )

    Column(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(
            fraction = if (!isWatched.value) {
                0.58f
            } else {
                0.45f
            }
        )
        .background(
            color = Color.White,
            shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
        ), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.fillMaxWidth()){
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, end = 15.dp, top = 15.dp), contentAlignment = Alignment.Center) {
                Text(text =if (isWatched.value){ stringResource(id = R.string.lbl_home_adFree_sheet_freeOfAds_title)}else{
                    stringResource(id = R.string.lbl_home_adFree_sheet_removeAds_title)}, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            }
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, end = 15.dp, top = 15.dp), contentAlignment = Alignment.CenterEnd) {
                IconButton(onClick = onClickClose) {
                    Icon(modifier = Modifier.size(20.dp),painter = painterResource(id = R.drawable.ic_close), tint = Color(
                        0xFFB9B9B9
                    ), contentDescription = "")
                }
            }
        }
        Box(modifier = Modifier
            .fillMaxWidth(0.8f)
            .fillMaxHeight(
                fraction = if (isWatched.value) {
                    0.35f
                } else {
                    0.45f
                }
            ), contentAlignment = Alignment.Center){

            LottieAnimation(
                composition,
                progress,
                modifier = Modifier.fillMaxSize()
            )

//            Icon(modifier = Modifier
//                .fillMaxWidth()
//                .fillMaxHeight(), painter = painterResource(id = if(isWatched.value){R.drawable.vector_cee_watched_rewarded}else{ R.drawable.img_cee_ads_remove}), tint = Color.Unspecified, contentDescription = "")
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = if(isWatched.value){ stringResource(id = R.string.lbl_home_adFree_sheet_freeOfAds_description)}else{ stringResource(id = R.string.lbl_home_adFree_sheet_removeAds_description)}, color = Color(0xFF727272),fontSize = 16.sp, fontWeight = FontWeight.W500,fontFamily  = FontFamily(Font(R.font.work_sans_medium)), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(0.8f))
        Spacer(modifier = Modifier.height(20.dp))
        if (isWatched.value){
            Button(onClick = onClickClose, modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(55.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF495CE8))) {
                Text(text = stringResource(id = R.string.btn_home_alert_done), fontFamily  = FontFamily(Font(R.font.work_sans_medium)), fontWeight = FontWeight.ExtraBold, color = Color.White)
            }
        }else{
            Button(onClick = onClickWatchVideo, modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(55.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF495CE8))) {
                Text(text = stringResource(id = R.string.btn_home_adFree_sheet_watch_ad), fontFamily  = FontFamily(Font(R.font.work_sans_medium)), fontWeight = FontWeight.ExtraBold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(5.dp))
        if (!isWatched.value){
            TextButton(onClick = { onClickClose.invoke() }) {
                Text(text = stringResource(id = R.string.btn_home_adFree_sheet_dont_remove_ad),color = Color(0xFF727272))
            }

            Spacer(modifier = Modifier.height(20.dp))
        }


    }

}
