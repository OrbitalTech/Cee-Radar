package com.orbital.cee.view.home.components

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.airbnb.lottie.compose.*
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.orbital.cee.R
import com.orbital.cee.core.GeofenceBroadcastReceiver
import com.orbital.cee.view.home.HomeViewModel
import com.orbital.cee.view.trip.advancedShadow


@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun fab (model: HomeViewModel, onClickIndicator: ()-> Unit,onClickReport: ()-> Unit,onClickReportAddManually: ()-> Unit){

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
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (model.userType.value == 2){
            Button(
                contentPadding = PaddingValues(0.dp),
                onClick = onClickReportAddManually,
                modifier = Modifier
                    .size(40.dp)
                    .advancedShadow(color = Color(0xFF495CE8), alpha = 0.06f,cornersRadius = 21.dp,
                        shadowBlurRadius = 8.dp, offsetX = 0.dp, offsetY = 5.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    modifier = Modifier.size(25.dp),
                    contentDescription = "",
                    tint =Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
        }
        Button(
            contentPadding = PaddingValues(0.dp),
            onClick = onClickIndicator,
            modifier = Modifier
                .size(40.dp)
                .advancedShadow(color = Color(0xFF495CE8), alpha = 0.06f,cornersRadius = 21.dp,
                    shadowBlurRadius = 8.dp, offsetX = 0.dp, offsetY = 5.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_user_puck),
                modifier = Modifier.size(25.dp),
                contentDescription = "",
                tint =if (model.isCameraMove.value) Color(0xFF495CE8) else Color.Unspecified
            )
        }
        Spacer(modifier = Modifier.height(18.dp))

        if (GeofenceBroadcastReceiver.GBRS.GeoId.value == null){
            Button(
                contentPadding = PaddingValues(0.dp),
                onClick = onClickReport,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF495CE8 )),
                modifier = Modifier
                    .size(60.dp)
                    .advancedShadow(color = Color(0xFF495CE8), alpha = 0.06f, cornersRadius = 23.dp, shadowBlurRadius = 8.dp, offsetX = 0.dp, offsetY = 5.dp),
                shape = RoundedCornerShape(23.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_camera_fab,),
                    modifier = Modifier.size(30.dp),
                    tint = Color.White,
                    contentDescription = ""
                )
            }
        }else{
            Button(
                contentPadding = PaddingValues(0.dp),
                onClick = {},
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF495CE8 )),
                modifier = Modifier
                    .size(60.dp)
//                    .shadow(
//                        elevation = 5.dp,
//                        shape = RoundedCornerShape(23.dp),
//                        clip = true
//                    )
                ,
                shape = RoundedCornerShape(23.dp)
            ) {
                LottieAnimation(
                    composition,
                    progress,
                    modifier = Modifier.size(35.dp)
                )
            }


        }

    }
}

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
fun showEditReportDialog(
    onDismiss: () -> Unit,
    speedLimit: Int?,
    onPositiveClick: (speedLimit: Int?) -> Unit,
) {
    Dialog(onDismissRequest = onDismiss,properties = DialogProperties(
        usePlatformDefaultWidth = false
    ), content = {
        Surface(
            color = Color.Transparent,
            modifier = Modifier.fillMaxWidth(0.9f),
            content = {
                UpdateSpeedLimitModal(speedLimit = speedLimit,onPositiveClick =  onPositiveClick)
            }
        )

    })
}
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun showAddReportManuallyDialog(
    onDismiss: () -> Unit,
    onPositiveClick: (point: GeoPoint, type:Int, time: Timestamp, speedLimit:Int?) -> Unit,
    clickedPoint:GeoPoint
) {
    Dialog(onDismissRequest = onDismiss,properties = DialogProperties(
        usePlatformDefaultWidth = false
    ), content = {
        Surface(
            color = Color.Transparent,
            modifier = Modifier.fillMaxWidth(0.9f),
            content = {
                AddReportManuallyModal(onPositiveClick =  onPositiveClick,clickedPoint)
            }
        )

    })
}