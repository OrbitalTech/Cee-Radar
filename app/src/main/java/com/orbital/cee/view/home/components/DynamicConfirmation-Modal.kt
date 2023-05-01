package com.orbital.cee.view.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.orbital.cee.R


@Composable
fun CustomModal(
    onPositiveClick: () -> Unit,
    title : String,
    onNegativeClick : () -> Unit,
    buttonPositiveText : String,
    buttonNegativeText : String,
) {
    Box(modifier = Modifier
        .height(350.dp)
        .fillMaxWidth()
        .background(color = Color.Transparent), contentAlignment = Alignment.TopCenter){
        Box(modifier = Modifier.fillMaxSize(),contentAlignment = Alignment.BottomCenter){
            Card(
                Modifier
                    .height(300.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                backgroundColor = Color.White
            ) {
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.fillMaxHeight(0.05f))
                    Box(modifier = Modifier.fillMaxWidth(0.7f), contentAlignment = Alignment.Center){
                        Text(text = title, textAlign = TextAlign.Center, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.fillMaxHeight(0.2f))
                    Row(modifier = Modifier.fillMaxWidth(0.8f), horizontalArrangement = Arrangement.SpaceBetween) {
                        Button(
                            onClick = onNegativeClick,
                            shape = RoundedCornerShape(8.dp),
                            elevation =  ButtonDefaults.elevation(
                                defaultElevation = 0.dp,
                                pressedElevation = 0.dp,
                                disabledElevation = 0.dp,
                                hoveredElevation = 0.dp,
                                focusedElevation = 0.dp
                            ),
                            border = BorderStroke(width = 2.dp, color = Color(0XFFAAA9AB)),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                            modifier = Modifier
                                .fillMaxWidth(0.48f)
                                .height(45.dp)
                        ) {
                            Text(text = buttonNegativeText, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                        Button(
                            onClick = onPositiveClick,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0XFF495CE8)),
                            elevation =  ButtonDefaults.elevation(
                                defaultElevation = 0.dp,
                                pressedElevation = 0.dp,
                                disabledElevation = 0.dp,
                                hoveredElevation = 0.dp,
                                focusedElevation = 0.dp
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(45.dp)
                        ) {
                            Text(text = buttonPositiveText, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
                Box(modifier = Modifier.fillMaxSize() , contentAlignment = Alignment.Center){
                    Box(modifier = Modifier.fillMaxWidth(0.7f)){
                        Text(text = "")
                    }
                }
                Box(modifier = Modifier.fillMaxSize() , contentAlignment = Alignment.BottomCenter){}
            }
        }
        Box(modifier = Modifier
            .height(100.dp)
            .width(100.dp)){
            Icon(painter = painterResource(id = R.drawable.ic_cee_select_lang), tint = Color.Unspecified, contentDescription ="" )
        }
    }
}
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun showCustomDialog(
    onNegativeClick: () -> Unit,
    onPositiveClick: () -> Unit,
    title : String,
    buttonPositiveText : String,
    buttonNegativeText : String,
) {
    Dialog(onDismissRequest = {},properties = DialogProperties(
        usePlatformDefaultWidth = false
    ), content = {
        Surface(
            color = Color.Transparent,
            modifier = Modifier.fillMaxWidth(0.9f),
            content = {
                CustomModal(
                    onNegativeClick = onNegativeClick,
                    onPositiveClick = onPositiveClick,
                    title = title,
                    buttonNegativeText = buttonNegativeText,
                    buttonPositiveText = buttonPositiveText
                )
            }
        )

    })
}