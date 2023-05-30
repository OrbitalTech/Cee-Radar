package com.orbital.cee.view.home.Menu

import android.app.Activity
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.recreate
import com.orbital.cee.R
import com.orbital.cee.core.Constants
import com.orbital.cee.data.repository.AppSetting
import com.orbital.cee.view.home.HomeViewModel
import com.orbital.cee.view.home.Menu.componenets.switchButton
import com.orbital.cee.view.home.Menu.componenets.switchButtonNormal
import com.orbital.cee.view.home.components.ReportIconWithSpeedLimit
import com.orbital.cee.view.home.components.SunriseSlider
import com.orbital.cee.view.home.components.sunriseSliderColorsDefaultt
import com.orbital.cee.view.home.components.valuesListt
import java.time.format.TextStyle

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun General(model:HomeViewModel,onClickBack:()->Unit){
    val rotate = if (LocalConfiguration.current.layoutDirection == LayoutDirection.Rtl.ordinal){180f}else{0f}
    val context = LocalContext.current
    val activity = context as Activity

    val isSleepModeDisabled = remember {
        mutableStateOf(false)
    }
    val _isSleepModeDisabled = remember {
        mutableStateOf(false)
    }
    val _isChangeSetting = remember {
        mutableStateOf(false)
    }
    val sleepTimeout = remember { mutableStateOf(0f) }

    model.appSetting.value?.let {
        isSleepModeDisabled.value = it.preventScreenSleep
        _isSleepModeDisabled.value = it.preventScreenSleep
        sleepTimeout.value = it.screenSleepTimeOutInSecond.toFloat()
    }

    val enabledValue by remember { mutableStateOf(false) }
    val values by remember { mutableStateOf(valuesListt()) }
    val steps by remember { mutableStateOf(0) }


    val valueRange: ClosedFloatingPointRange<Float>? by remember { mutableStateOf(null) }
    val onValueChangeFinished by remember { mutableStateOf({

    }) }
    val tutorialEnabled by remember { mutableStateOf(false) }
    val interactionSource = remember {
        MutableInteractionSource()
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(color = Color.White)
        .pointerInput(Unit) {
            detectDragGestures(onDrag = { point, offset ->
                if (offset.x > Constants.OFFSET_X && point.position.x < Constants.POINT_X) {
                    onClickBack.invoke()
                }
            })
        }){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                    })
                }
                .height(105.dp)
                .background(color = Color.White)
                .clip(shape = RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp))
                .padding(vertical = 25.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Text(text = "General Setting", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 20.sp)
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp), contentAlignment = Alignment.BottomStart){
                Box(modifier = Modifier.size(25.dp), contentAlignment = Alignment.Center){
                    Icon(modifier = Modifier
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = onClickBack
                        )
                        .size(20.dp)
                        .rotate(rotate), tint = Color.Gray , painter = painterResource(id = R.drawable.ic_arrow_back), contentDescription ="" )
                }
            }

        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(color = Color.White)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {

                        }
                    )
                },
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.padding(horizontal = 15.dp, vertical = 20.dp),horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Enable debug mode.")
                    Box(modifier = Modifier
                        .width(73.dp)
                        .height(47.dp)
                        .padding(8.dp), contentAlignment = Alignment.Center){
                        switchButtonNormal(model.isDebugMode){
                            Log.d("DEBUG_DEBUG_MODE","Deb: "+it)
                            model.changeDebugMode(it)
                            model.getReportsAndAddGeofences()
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Enable prevent sleep.")
                    Box(modifier = Modifier
                        .width(73.dp)
                        .height(47.dp)
                        .padding(8.dp), contentAlignment = Alignment.Center){

                        switchButtonNormal(isSleepModeDisabled){
                            Log.d("DEBUG_DEBUG_MODE","Deb: "+it)
                            model.changeScreenStatus(isEnable = it)
                            _isSleepModeDisabled.value = it
                            _isChangeSetting.value = true
                        }
                    }
                }
                AnimatedVisibility(visible = _isSleepModeDisabled.value, enter = fadeIn(), exit = fadeOut()) {
                    Column() {
                        Spacer(modifier = Modifier.height(20.dp))
                        SunriseSlider(
                            value = sleepTimeout.value,
                            onValueChange = { float: Float ->
                                sleepTimeout.value = float
                            },
                            valueRangeParam = valueRange,
                            values = values,
                            steps = steps,
                            interactionSource = interactionSource,
                            enabled = enabledValue,
                            tutorialEnabled = tutorialEnabled,
                            onValueChangeFinished = {
                                model.changeScreenStatus(time = sleepTimeout.value.toInt())
                                model.retrieveIsPreventScreenSleep()
                                _isChangeSetting.value = true
                            },
                            colors = sunriseSliderColorsDefaultt(),
                            isRtl = false
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "1h", fontSize = 10.sp, fontWeight = FontWeight.W500, color =if(sleepTimeout.value.toInt() == 200){Color(0XFF495CE8)}else{Color(0xFF848484)} )
                            Text(text = "3h", fontSize = 10.sp, fontWeight = FontWeight.W500, color = if(sleepTimeout.value.toInt() == 300){Color(0XFF495CE8)}else{Color(0xFF848484)})
                            Text(text = "4h", fontSize = 10.sp, fontWeight = FontWeight.W500, color = if(sleepTimeout.value.toInt() == 400){Color(0XFF495CE8)}else{Color(0xFF848484)})
                            Text(text = "5h", fontSize = 10.sp, fontWeight = FontWeight.W500, color = if(sleepTimeout.value.toInt() == 500){Color(0XFF495CE8)}else{Color(0xFF848484)})
                            Text(text = "6h", fontSize = 10.sp, fontWeight = FontWeight.W500, color = if(sleepTimeout.value.toInt() == 600){Color(0XFF495CE8)}else{Color(0xFF848484)})
                            Text(text = "7h", fontSize = 10.sp, fontWeight = FontWeight.W500, color = if(sleepTimeout.value.toInt() == 699){Color(0XFF495CE8)}else{Color(0xFF848484)})
                            Text(text = "8h", fontSize = 10.sp, fontWeight = FontWeight.W500, color = if(sleepTimeout.value.toInt() == 800){Color(0XFF495CE8)}else{Color(0xFF848484)})
                        }
                    }

                }
            }

            AnimatedVisibility(visible = _isChangeSetting.value, enter = slideInHorizontally(), exit = slideOutHorizontally()) {
                Box(modifier = Modifier
                    .wrapContentSize()
                    .padding(horizontal = 10.dp)
                    .pointerInput(Unit) {
                        detectDragGestures(onDrag = { _, offset ->
                            if (offset.x < -30) {
                                _isChangeSetting.value = false
                            }
                        })
                    }
                    .background(color = Color.Transparent), contentAlignment = Alignment.Center) {

                    Column() {
                        Row(modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .defaultMinSize(minHeight = 64.dp)
//                            .height(64.dp)
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .border(
                                color = Color(0xFFFECD5C),
                                width = 1.5.dp,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                            
                            Icon(modifier = Modifier.size(35.dp),painter = painterResource(id = R.drawable.ic_message_with_background), contentDescription = "", tint = Color.Unspecified)
                            Spacer(modifier = Modifier.width(15.dp))

                            val restart = " restart"
                            val annotatedString = buildAnnotatedString {
                                append("This change cannot take effect immediately. It will work on the next opening of app, or")
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold), ) {
                                    pushStringAnnotation(tag = restart, annotation = restart)
                                    append(restart)
                                }
                            }
                            ClickableText(text = annotatedString, modifier = Modifier.fillMaxWidth(0.9f), style = androidx.compose.ui.text.TextStyle(fontSize = 14.sp,fontWeight = FontWeight.W400), onClick = { offset ->
                                annotatedString.getStringAnnotations(offset, offset)
                                    .firstOrNull()?.let { span ->
                                        Log.d("ABCD_EFG",span.item)
                                        activity.recreate()
                                    }
                            })
                            Icon(modifier = Modifier
                                .clickable(onClick = { _isChangeSetting.value = false })
                                .padding(5.dp),
                                painter = painterResource(id = R.drawable.ic_close), contentDescription ="", tint = Color(0xFFFECD5C) )

                        }
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }

            }

        }
    }
}