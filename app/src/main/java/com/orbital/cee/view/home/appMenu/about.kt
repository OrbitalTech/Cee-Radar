package com.orbital.cee.view.home.appMenu

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbital.cee.R
import com.orbital.cee.core.Constants

@Composable
fun About(onClickBack:()->Unit,){
    val rotate = if (LocalConfiguration.current.layoutDirection == LayoutDirection.Rtl.ordinal){180f}else{0f}
    Column(modifier = Modifier
        .fillMaxSize()
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
                .height(85.dp)
                .background(color = Color.White)
                .clip(shape = RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp))
                .padding(vertical = 25.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = stringResource(id = R.string.lbl_setting_general_about), fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 20.sp)
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart){
                IconButton(onClick = onClickBack, modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)) {
                    Icon(modifier = Modifier
                        .size(20.dp)
                        .rotate(rotate), tint = Color.Gray , painter = painterResource(id = R.drawable.ic_arrow_left), contentDescription ="" )
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
            Icon(painter = painterResource(id = R.drawable.ic_cee_select_lang), contentDescription = "", tint = Color.Unspecified)
            Spacer(modifier = Modifier.height(15.dp))
            Text(text = stringResource(id = R.string.lbl_about_cee), modifier = Modifier.fillMaxWidth())

        }
    }
}