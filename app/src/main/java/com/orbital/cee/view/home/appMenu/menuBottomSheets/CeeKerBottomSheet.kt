package com.orbital.cee.view.home.appMenu.menuBottomSheets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbital.cee.R
import com.orbital.cee.model.UserNew
import com.orbital.cee.ui.theme.black
import com.orbital.cee.ui.theme.blurple
import com.orbital.cee.ui.theme.green
import com.orbital.cee.ui.theme.light_green
import com.orbital.cee.ui.theme.type_gray
import com.orbital.cee.utils.MetricsUtils.Companion.calculatePointRemainToNextLevel
import com.orbital.cee.utils.MetricsUtils.Companion.calculatePointRemainToNextLevelPersint

@Composable
fun CeeKerBottomSheet(userInfo: UserNew,myReportsCount: Int, onClickClose:()->Unit){
    Box(modifier = Modifier
        .fillMaxWidth()
        .clip(shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp))
        .background(color = Color.White), contentAlignment = Alignment.Center){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 18.dp, bottom = 34.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Divider(
                color = Color(0XFFE4E4E4),
                thickness = 3.dp,
                modifier = Modifier.width(40.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier
                .fillMaxWidth()
                .height(85.dp)
                .background(color = light_green, shape = RoundedCornerShape(22.dp))
                .padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(painter = painterResource(id = R.drawable.ic_diamond), contentDescription = "", tint = green)
                Spacer(modifier = Modifier.width(10.dp))
                Column() {
                    Text(text = "Ceeker", fontSize = 22.sp, fontWeight = FontWeight.SemiBold, color = black)
                    Text(text = "${stringResource(id = R.string.lbl_menu_ceekerbottomshet_trust_level)} : ${userInfo.userLevel ?: 0}", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = type_gray)
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                Text(modifier = Modifier.defaultMinSize(minWidth = 140.dp), text = stringResource(id = R.string.lbl_menu_ceekerbottomshet_trust_point), fontSize = 14.sp, color = black)

                Text(text = stringResource(id = R.string.lbl_menu_ceekerbottomshet_number_of_reports), fontSize = 14.sp, color = black)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                Text(modifier = Modifier.defaultMinSize(minWidth = 140.dp), text = "${userInfo.userPoint ?: 0}", fontSize = 22.sp, fontWeight = FontWeight.SemiBold, color = green)

                Text(text = "$myReportsCount", fontSize = 22.sp, fontWeight = FontWeight.SemiBold, color = green)
            }
            Spacer(modifier = Modifier.height(22.dp))
            Row() {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .height(18.dp)
                    .clip(CircleShape)
                    .background(color = light_green)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(
                                calculatePointRemainToNextLevelPersint(userInfo.userPoint ?: 0)
                            )
                            .height(18.dp)
                            .clip(CircleShape)
                            .background(color = green)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(text = "+${calculatePointRemainToNextLevel(userInfo.userPoint ?: 0)} ${stringResource(id = R.string.lbl_menu_ceekerbottomshet_points_next_level)}", fontSize = 14.sp, color = Color(0x65000000))
            }
            Spacer(modifier = Modifier.height(22.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(text = stringResource(id = R.string.lbl_menu_ceekerbottomshet_as_you_help), fontSize = 14.sp, color = Color(
                    0xFF000000))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(text = "User Tier & Trust level ", style = androidx.compose.ui.text.TextStyle(textDecoration = TextDecoration.Underline),  fontWeight = FontWeight.Medium, fontSize = 16.sp, color = blurple)
            }
            Spacer(modifier = Modifier.height(30.dp))
            Button(modifier = Modifier
                .fillMaxWidth()
                .height(55.dp), onClick = onClickClose,shape = RoundedCornerShape(16.dp),colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF495CE8))) {
                Text(text = stringResource(id = R.string.btn_home_sound_sheet_close), color = Color.White, fontWeight = FontWeight.Bold)

            }
            Spacer(modifier = Modifier.height(40.dp))

        }
    }
    
}

@Preview
@Composable
fun CeeKerBottomSheetPreview(){
    CeeKerBottomSheet(
        UserNew(),5
    ){}
}