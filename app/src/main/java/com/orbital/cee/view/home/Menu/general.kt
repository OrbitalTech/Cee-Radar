package com.orbital.cee.view.home.Menu

import android.app.Activity
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.recreate
import com.orbital.cee.R
import com.orbital.cee.core.Constants
import com.orbital.cee.view.home.HomeViewModel
import com.orbital.cee.view.home.Menu.componenets.switchButton
import com.orbital.cee.view.home.Menu.componenets.switchButtonNormal

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun General(model:HomeViewModel,onClickBack:()->Unit){
    val rotate = if (LocalConfiguration.current.layoutDirection == LayoutDirection.Rtl.ordinal){180f}else{0f}
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
            Box(modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp), contentAlignment = Alignment.BottomStart){
                Box(modifier = Modifier.size(25.dp), contentAlignment = Alignment.Center){
                    Icon(modifier = Modifier
                        .clickable( indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = onClickBack)
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
                .padding(horizontal = 15.dp, vertical = 20.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {

                        }
                    )
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
           Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
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
               Text(text = "Enable Debug Mode.")
           }
//            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
//                Box(modifier = Modifier
//                    .width(73.dp)
//                    .height(47.dp)
//                    .padding(8.dp), contentAlignment = Alignment.Center){
//                    switchButtonNormal(isAlertRoadCameraEnabled)
//                }
//                Text(text = "Alert Me From Road Camera")
//            }

        }
    }
}