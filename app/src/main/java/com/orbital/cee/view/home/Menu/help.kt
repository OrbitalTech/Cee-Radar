package com.orbital.cee.view.home.Menu

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbital.cee.R
import com.orbital.cee.core.Constants
import com.orbital.cee.model.HelpQuestionModel
import com.orbital.cee.model.OnBoardingModel


@Composable
fun help (onClickBack:()-> Unit) {

    val questions = ArrayList<HelpQuestionModel>()
    questions.add(HelpQuestionModel("How to place a camera?","Scroll down from the top to open the notification bar, then slide the notification you don’t want to the left and tap the Settings icon. Select DISABLE NOTIFICATIONS. In this step, you can also select MORE SETTINGS to enter the Manage Notifications interface and customize your notifications for specific applications.",remember {mutableStateOf(false) }))
    questions.add(HelpQuestionModel("How to report an accident?","",remember {mutableStateOf(false) }))
    questions.add(HelpQuestionModel("How to change my phone number?","",remember {mutableStateOf(false) }))
    questions.add(HelpQuestionModel("Where can i find report list?","",remember {mutableStateOf(false) }))
    questions.add(HelpQuestionModel("How can i share cee with my friend?","",remember {mutableStateOf(false) }))
    questions.add(HelpQuestionModel("How many hours does a report last?","",remember {mutableStateOf(false) }))
    questions.add(HelpQuestionModel("How can i mute notifications?","", remember {mutableStateOf(false) }))

    val rotate = if (LocalConfiguration.current.layoutDirection == LayoutDirection.Rtl.ordinal){180f}else{0f}
    Column(modifier = Modifier.fillMaxSize().pointerInput(Unit) {
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
            Text(text = "Help Center", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 20.sp)
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart){
                IconButton(onClick = onClickBack, modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)) {
                    Icon(modifier = Modifier.size(20.dp).rotate(rotate), tint = Color.Gray , painter = painterResource(id = R.drawable.ic_arrow_back), contentDescription ="" )
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(color = Color.White)
                .padding(horizontal = 12.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {

                        }
                    )
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            questions.forEach{question ->

            Column(modifier = Modifier
                .fillMaxSize().pointerInput(Unit){detectTapGestures(onTap = {question.isExpanded.value = !question.isExpanded.value})}) {
                Divider(thickness = 1.dp, color = Color(0xFFD9D9D9))
                Row(modifier = Modifier
                    .fillMaxSize(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = question.title, fontWeight = FontWeight.Bold)
                    IconButton(onClick = {question.isExpanded.value = !question.isExpanded.value}){
                        Icon(modifier = Modifier.size(18.dp).rotate(degrees =if (question.isExpanded.value) 0f else rotate), tint = Color(0xff707070),painter = painterResource(id = if (question.isExpanded.value){R.drawable.ic_arrow_dropdown}else{R.drawable.ic_arrow}), contentDescription = "")
                    }
                }
                if (question.isExpanded.value){
                    Row(modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 15.dp)) {
                        Text(text = question.discreption)
                    }
                }
            }
            }
            Divider(thickness = 1.dp, color = Color(0xFFD9D9D9))
        }
    }
}