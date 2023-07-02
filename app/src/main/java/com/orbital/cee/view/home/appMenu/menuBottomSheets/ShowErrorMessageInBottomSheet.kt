package com.orbital.cee.view.home.appMenu.menuBottomSheets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbital.cee.R
import com.orbital.cee.ui.theme.black

@Composable
fun ShowErrorMessageInBottomSheet(icon:Int = R.drawable.ic_danger,title:String,description:String,onCloseModal:()->Unit){
    Box(modifier = Modifier
        .fillMaxWidth()
        .clip(shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp))
        .background(color = Color.White), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 18.dp, bottom = 54.dp),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Divider(
                    color = Color(0XFFE4E4E4),
                    thickness = 3.dp,
                    modifier = Modifier.width(40.dp)
                )
                Spacer(modifier = Modifier.height(28.dp))
                Icon(modifier = Modifier.size(38.dp), tint = Color.Unspecified, painter = painterResource(id = icon), contentDescription = "")
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = title, fontSize = 22.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center,color = black)
                Spacer(modifier = Modifier.height(10.dp))
                Box(modifier = Modifier.fillMaxWidth()){
                    Text(textAlign = TextAlign.Center,text = description, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = black)
                }
                Spacer(modifier = Modifier.height(15.dp))
            }

            Button(modifier = Modifier
                .fillMaxWidth()
                .height(56.dp), onClick = onCloseModal,shape = RoundedCornerShape(16.dp),colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF495CE8))) {
                Text(text = "Okey", color = Color.White, fontWeight = FontWeight.Bold)

            }
            Spacer(modifier = Modifier.height(20.dp))
        }

    }
}
@Preview
@Composable
fun ShowErrorMessageInBottomSheetPreview(){
    ShowErrorMessageInBottomSheet(
        icon = R.drawable.ic_isolation_mode,
        title = "We are participants in HITEX",
        description = "Visit us at HITEX and enjoy the many gifts we have prepared for you."
    ){}
}