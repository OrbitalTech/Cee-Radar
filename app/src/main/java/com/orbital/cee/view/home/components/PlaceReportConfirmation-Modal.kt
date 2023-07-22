package com.orbital.cee.view.home.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.MutableLiveData
import com.airbnb.lottie.compose.*
import com.orbital.cee.R
import com.orbital.cee.core.GeofenceBroadcastReceiver
import com.orbital.cee.core.MyLocationService.LSS.speed
import com.orbital.cee.model.UserTiers
import com.orbital.cee.view.home.HomeViewModel
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MyFab (userType: MutableLiveData<UserTiers>, isPointClicked:MutableState<Boolean>, onClickReport: ()-> Unit, onClickReportAddManually: ()-> Unit){
    val coroutineScope = rememberCoroutineScope()
    val scale1 = remember { Animatable(1f) }

    val composition by rememberLottieComposition(
        LottieCompositionSpec
            .RawRes(R.raw.lottie_three_dot_loading)
    )

    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true,
        speed = 1f,
        restartOnPlay = false
    )
    Column(
        modifier = Modifier.padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (userType.value == UserTiers.ADMIN && isPointClicked.value){
            Button(
                contentPadding = PaddingValues(0.dp),
                onClick = onClickReportAddManually,
                modifier = Modifier
                    .size(48.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_plus),
                    modifier = Modifier.size(35.dp),
                    contentDescription = "",
                    tint =Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
        androidx.compose.animation.AnimatedVisibility(
            visible =if(userType.value == UserTiers.ADMIN){true}else{speed.value > 10 } ,
            enter = slideInHorizontally(),
            exit = slideOutHorizontally()
        ) {
            if (GeofenceBroadcastReceiver.GBRS.GeoId.value == null){
                Box(modifier = Modifier
                    .size(64.dp)
                    .clickable (
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {
                            coroutineScope.launch {
                                scale1.animateTo(0.8f, animationSpec = spring())
                                scale1.animateTo(1f, animationSpec = spring())
                            }
                            onClickReport()
                        })
                    .scale(scale1.value),contentAlignment = Alignment.Center
                ) {
                    Icon(painter = painterResource(id = R.drawable.bg_btn_place_cam_fab_main_scr), contentDescription = "", tint = Color.Unspecified)
                    Icon(
                        painter = painterResource(id = R.drawable.ic_camera_fab,),
                        modifier = Modifier.size(30.dp),
                        tint = Color.White,
                        contentDescription = ""
                    )
                }
            }else{
                Box(contentAlignment = Alignment.Center
                ) {
                    Icon(painter = painterResource(id = R.drawable.bg_btn_place_cam_fab_main_scr), contentDescription = "", tint = Color.Unspecified)
                    LottieAnimation(
                        composition,
                        progress,
                        modifier = Modifier.size(35.dp)
                    )
                }
            }
        }
    }
}
//fun customShape() =  object : Shape {
//    override fun createOutline(
//        size: androidx.compose.ui.geometry.Size,
//        layoutDirection: LayoutDirection,
//        density: Density
//    ): Outline {
//        return Outline.Rectangle(androidx.compose.ui.geometry.Rect(302f,652f,64f , 64f ))
//    }
//}
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun showConfirmationDialog(
    onDismiss: () -> Unit,
    onNegativeClick: () -> Unit,
    onPositiveClick: () -> Unit,
    model: HomeViewModel
) {
    Dialog(onDismissRequest = onDismiss,properties = DialogProperties(
        usePlatformDefaultWidth = false
    ), content = {
        Surface(
            color = Color.Transparent,
            modifier = Modifier.fillMaxWidth(0.9f),
            content = {
                ReportModalConfirmation(model = model,onPositiveClick =  onPositiveClick)
            }
        )

    })
}
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun showRegisterDialog(
    onDismiss: () -> Unit,
    onPositiveClick: (userName : String) -> Unit,
) {
    Dialog(onDismissRequest = onDismiss,properties = DialogProperties(
        usePlatformDefaultWidth = false
    ), content = {
        Surface(
            color = Color.Transparent,
            modifier = Modifier.fillMaxWidth(0.9f),
            content = {
                RegisterModal(onPositiveClick =  onPositiveClick)
            }
        )

    })
}
@Composable
fun showPlaceErrorDialog(
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss,properties = DialogProperties(
        usePlatformDefaultWidth = false
    ), content = {
        Surface(
            color = Color.Transparent,
            modifier = Modifier.fillMaxWidth(0.9f),
            content = {
                ReportPlaceError(onPositiveClick =  onDismiss)
            }
        )

    })
}
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun InputValueModal(
    isString :Boolean = false,
    onDismiss: () -> Unit,
    speedLimit: String?,
    labelTwo: String?,
    labelOne: String,
    onPositiveClick: (speedLimit: String?) -> Unit,
) {
    Dialog(onDismissRequest = onDismiss,properties = DialogProperties(
        usePlatformDefaultWidth = false
    ), content = {
        Surface(
            color = Color.Transparent,
            modifier = Modifier.fillMaxWidth(0.9f),
            content = {
                UpdateSpeedLimitModal(
                    speedLimit = speedLimit,
                    onPositiveClick =  onPositiveClick,
                    labelOne = labelOne,
                    labelTwo = labelTwo,
                    isString = isString
                )
            }
        )

    })
}
//@OptIn(ExperimentalComposeUiApi::class)
//@Composable
//fun showAddReportManuallyDialog(
//    onDismiss: () -> Unit,
//    onPositiveClick: (point: GeoPoint, type:Int, time: Timestamp, speedLimit:Int?,address:String,isWithNoti:Boolean) -> Unit,
//    clickedPoint:GeoPoint
//) {
//    Dialog(onDismissRequest = onDismiss,properties = DialogProperties(
//        usePlatformDefaultWidth = false
//    ), content = {
//        Surface(
//            color = Color.Transparent,
//            modifier = Modifier.fillMaxWidth(0.9f),
//            content = {
//                AddReportManuallyModal(onPositiveClick =  onPositiveClick,clickedPoint)
//            }
//        )
//
//    })
//}