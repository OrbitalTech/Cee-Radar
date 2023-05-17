package com.orbital.cee.view.home.Menu.componenets

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import com.orbital.cee.view.trip.advancedShadow
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun switchButton(isActive:MutableState<Boolean>,isUserAdmin:Boolean,onClick :()->Unit){
    var horizontalBias by remember { mutableStateOf(1f) }
    val alignment by animateHorizontalAlignmentAsState(horizontalBias)
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    Column(modifier = Modifier
        .width(120.dp)
        .height(45.dp)
        .advancedShadow(
            color = Color.LightGray,
            alpha = 0.8f,
            cornersRadius = 12.dp,
            shadowBlurRadius = 12.dp,
            offsetX = 0.dp,
            offsetY = 3.dp
        )
        .pointerInput(Unit) {
            detectTapGestures(onTap = {
                horizontalBias *= -1
                isActive.value = horizontalBias == 1f
                if (isUserAdmin) {
                    onClick.invoke()
                } else {
                    coroutineScope.launch {
                        delay(500)
                        horizontalBias = 1f
                        isActive.value = false
                        Toast
                            .makeText(
                                context,
                                "sorry, dark mode currently unavailable.",
                                Toast.LENGTH_LONG
                            )
                            .show()
                    }
                }


            })
        }
        .background(color = Color(0xFFECEEFD), shape = RoundedCornerShape(15.dp)), verticalArrangement = Arrangement.Center, horizontalAlignment = alignment){
        Box(
            modifier = Modifier
                .size(31.dp)
                .padding(horizontal = 4.dp, vertical = 3.dp)
                .background(color = Color(0xFF495CE8), shape = RoundedCornerShape(100.dp))
            , contentAlignment = Alignment.Center){
            Crossfade(targetState = isActive.value) { isChecked ->
                if (isChecked) {
                    Icon(modifier = Modifier.size(17.dp),painter = painterResource(id = com.orbital.cee.R.drawable.ic_moon), contentDescription ="", tint = Color.Unspecified )
                } else {
                    Icon(modifier = Modifier.size(17.dp),painter = painterResource(id = com.orbital.cee.R.drawable.ic_sun), contentDescription ="", tint = Color.White )
                }
            }

        }
    }

}
@Composable
fun switchButtonNormal(isActive:MutableLiveData<Boolean>,isOn:(isOn : Boolean)->Unit){
    var horizontalBias by remember { mutableStateOf(if (isActive.value == true){1f}else{-1f}) }
    val alignment by animateHorizontalAlignmentAsState(horizontalBias)
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var color1 = remember {
        mutableStateOf(Color(0xFF495CE8))
    }
    var color2 = remember {
        mutableStateOf(Color(0xFFECEEFD))
    }
    LaunchedEffect(Unit){
        if (isActive.value == true) {
            color1.value = Color(0xFF495CE8)
            color2.value = Color(0xFFECEEFD)
        } else {
            color1.value = Color(0xFFECEEFD)
            color2.value = Color(0xFF495CE8)
        }
    }

    Column(modifier = Modifier
        .width(120.dp)
        .height(45.dp)
        .advancedShadow(
            color = Color.LightGray,
            alpha = 0.8f,
            cornersRadius = 12.dp,
            shadowBlurRadius = 12.dp,
            offsetX = 0.dp,
            offsetY = 3.dp
        )
        .pointerInput(Unit) {
            detectTapGestures(onTap = {
                horizontalBias *= -1
                isActive.value = horizontalBias == 1f
                isOn(horizontalBias == 1f)
                if (isActive.value == true) {
                    color1.value = Color(0xFF495CE8)
                    color2.value = Color(0xFFECEEFD)
                } else {
                    color1.value = Color(0xFFECEEFD)
                    color2.value = Color(0xFF495CE8)
                }
//                coroutineScope.launch {
//                    delay(700)
//                    horizontalBias = -1f
//                    isActive.value = true
//                    color1.value = Color(0xFF495CE8)
//                    color2.value = Color(0xFFECEEFD)
//                    //Toast.makeText(context,"sorry, dark mode currently unavailable.",Toast.LENGTH_LONG).show()
//                }

            })
        }
        .background(color = color1.value, shape = RoundedCornerShape(15.dp)), verticalArrangement = Arrangement.Center, horizontalAlignment = alignment){
        Box(
            modifier = Modifier
                .size(33.dp)
                .padding(horizontal = 4.dp, vertical = 3.dp)
                .background(color = color2.value, shape = RoundedCornerShape(100.dp))
            , contentAlignment = Alignment.Center){
//            Crossfade(targetState = isActive.value) { isChecked ->
//                if (isChecked == true) {
//                    Icon(modifier = Modifier.size(17.dp),painter = painterResource(id = com.orbital.cee.R.drawable.ic_moon), contentDescription ="", tint = Color.Unspecified )
//                } else {
//                    Icon(modifier = Modifier.size(17.dp),painter = painterResource(id = com.orbital.cee.R.drawable.ic_sun), contentDescription ="", tint = Color.White )
//                }
//            }

        }
    }

}
@Composable
private fun animateHorizontalAlignmentAsState(
    targetBiasValue: Float
): State<BiasAlignment.Horizontal> {
    val bias by animateFloatAsState(targetBiasValue)
    return remember { derivedStateOf { BiasAlignment.Horizontal(bias) } }
}
@Preview(showBackground = true)
@Composable
fun defaultPreviewe(){
    val ab = remember {
        MutableLiveData(false)
    }
    switchButtonNormal(isActive = ab){

    }

}