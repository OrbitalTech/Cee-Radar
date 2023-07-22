package com.orbital.cee.view.home.appMenu

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbital.cee.R
import com.orbital.cee.core.Constants
import com.orbital.cee.model.HelpQuestionModel


@Composable
fun help (onClickBack:()-> Unit) {

    val context = LocalContext.current
    val questions = ArrayList<HelpQuestionModel>()
    questions.add(HelpQuestionModel(context.getString(R.string.q_and_a_help_q1),context.getString(R.string.q_and_a_help_a1), remember {mutableStateOf(false) }))
    questions.add(HelpQuestionModel(context.getString(R.string.q_and_a_help_q2),context.getString(R.string.q_and_a_help_a2), remember {mutableStateOf(false) }))
    questions.add(HelpQuestionModel(context.getString(R.string.q_and_a_help_q3),context.getString(R.string.q_and_a_help_a3), remember {mutableStateOf(false) }))
    questions.add(HelpQuestionModel(context.getString(R.string.q_and_a_help_q4),context.getString(R.string.q_and_a_help_a4), remember {mutableStateOf(false) }))
    questions.add(HelpQuestionModel(context.getString(R.string.q_and_a_help_q5),context.getString(R.string.q_and_a_help_a5), remember {mutableStateOf(false) }))
    questions.add(HelpQuestionModel(context.getString(R.string.q_and_a_help_q6),context.getString(R.string.q_and_a_help_a6), remember {mutableStateOf(false) }))
    questions.add(HelpQuestionModel(context.getString(R.string.q_and_a_help_q7),context.getString(R.string.q_and_a_help_a7), remember {mutableStateOf(false) }))
    questions.add(HelpQuestionModel(context.getString(R.string.q_and_a_help_q8),context.getString(R.string.q_and_a_help_a8), remember {mutableStateOf(false) }))
    questions.add(HelpQuestionModel(context.getString(R.string.q_and_a_help_q9),context.getString(R.string.q_and_a_help_a9), remember {mutableStateOf(false) }))
    questions.add(HelpQuestionModel(context.getString(R.string.q_and_a_help_q10),context.getString(R.string.q_and_a_help_a10), remember {mutableStateOf(false) }))
    questions.add(HelpQuestionModel(context.getString(R.string.q_and_a_help_q11),context.getString(R.string.q_and_a_help_a11), remember {mutableStateOf(false) }))
    questions.add(HelpQuestionModel(context.getString(R.string.q_and_a_help_q12),context.getString(R.string.q_and_a_help_a12), remember {mutableStateOf(false) }))
    questions.add(HelpQuestionModel(context.getString(R.string.q_and_a_help_q13),context.getString(R.string.q_and_a_help_a13), remember {mutableStateOf(false) }))
    questions.add(HelpQuestionModel(context.getString(R.string.q_and_a_help_q14),context.getString(R.string.q_and_a_help_a14), remember {mutableStateOf(false) }))
    questions.add(HelpQuestionModel(context.getString(R.string.q_and_a_help_q15),context.getString(R.string.q_and_a_help_a15), remember {mutableStateOf(false) }))
    questions.add(HelpQuestionModel(context.getString(R.string.q_and_a_help_q16),context.getString(R.string.q_and_a_help_a16), remember {mutableStateOf(false) }))
    questions.add(HelpQuestionModel(context.getString(R.string.q_and_a_help_q17),context.getString(R.string.q_and_a_help_a17), remember {mutableStateOf(false) }))
    questions.add(HelpQuestionModel(context.getString(R.string.q_and_a_help_q18),context.getString(R.string.q_and_a_help_a18), remember {mutableStateOf(false) }))
    questions.add(HelpQuestionModel(context.getString(R.string.q_and_a_help_q19),context.getString(R.string.q_and_a_help_a19), remember {mutableStateOf(false) }))
    questions.add(HelpQuestionModel(context.getString(R.string.q_and_a_help_q20),context.getString(R.string.q_and_a_help_a20), remember {mutableStateOf(false) }))


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
                    Icon(modifier = Modifier.size(20.dp).rotate(rotate), tint = Color.Gray , painter = painterResource(id = R.drawable.ic_arrow_left), contentDescription ="" )
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
                    Text(modifier = Modifier.fillMaxWidth(0.9f), text = question.title, fontWeight = FontWeight.Bold)
                    IconButton(onClick = {question.isExpanded.value = !question.isExpanded.value}){
                        Crossfade(targetState = question.isExpanded.value) { isChecked ->
                            if (isChecked) {
                                Icon(modifier = Modifier.size(18.dp), tint = Color(0xff707070),painter = painterResource(id = R.drawable.ic_arrow_down), contentDescription = "")
                            } else {
                                Icon(modifier = Modifier.size(18.dp).rotate(rotate), tint = Color(0xff707070),painter = painterResource(id =R.drawable.ic_arrow_right), contentDescription = "")
                            }
                        }
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