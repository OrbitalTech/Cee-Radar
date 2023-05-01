package com.orbital.cee.view.LocationNotAvailable

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.orbital.cee.R
import com.orbital.cee.core.Permissions
import com.orbital.cee.view.home.HomeViewModel
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun LocationNotAvailable(model: HomeViewModel) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isTrue = remember {
        mutableStateOf(true)
    }
    LaunchedEffect(Unit){
        while(isTrue.value){
            delay(1500)
            if (Permissions.hasLocationPermission(context) && model.checkDeviceLocationSettings(context)){
                isTrue.value = false
                model.isLocationNotAvailable.value = false
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF495CE8)),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(height = (configuration.screenHeightDp / 6.5).dp))
            Box(
                modifier = Modifier
                    .height(height = 200.dp)
                    .width(width = 180.dp)
                    .background(color = Color(0xFF495CE8)),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(id = R.drawable.ic_cee_select_lang),
                    contentDescription = "",
                    tint = Color.Unspecified,
                )
            }
            //Spacer(modifier = Modifier.)
            Box(modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(10.dp), contentAlignment = Alignment.Center){
                Text(text = stringResource(id = R.string.lbl_location_error_alert), textAlign = TextAlign.Center, color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.fillMaxHeight(0.7f))
            Button(
                onClick = {
                    context.startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(60.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White,
                    contentColor = Color.Black
                )
            ) {
                Text(text = "Enable Location")
            }
        }
    }
}