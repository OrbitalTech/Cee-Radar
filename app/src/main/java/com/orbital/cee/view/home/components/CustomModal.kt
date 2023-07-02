package com.orbital.cee.view.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ButtonElevation
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.orbital.cee.R

@Composable
fun DynamicModalView(
    icon:Int,
    title:String,
    description:String? = null,
    modalHeight :Int = 350,

    positiveButtonText:String,
    negativeButtonText: String? = null,

    positiveButtonBgColor: Color = Color(0XFF495CE8),
    negativeButtonBgColor: Color = Color.White,

    positiveButtonTextColor: Color = Color.White,
    negativeButtonTextColor: Color = Color.Black,

    positiveButtonAction:() -> Unit,
    negativeButtonAction:() -> Unit? = {},

    positiveButtonModifier: Modifier = Modifier,
    negativeButtonModifier: Modifier = Modifier
    ){
    val scroll = rememberScrollState(0)
    Box(modifier = Modifier
        .height(modalHeight.dp)
        .fillMaxWidth()
        .background(color = Color.Transparent), contentAlignment = Alignment.TopCenter){
        Box(modifier = Modifier.fillMaxSize(),contentAlignment = Alignment.BottomCenter){
            Box(
                Modifier
                    .height((modalHeight - 54).dp)
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(20.dp)),
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(top = 90.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
                    Column(Modifier.fillMaxWidth(0.75f),horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(modifier = Modifier.height(height = if (description == null) { 10.dp } else { 0.dp }))
                        Text(text = title, textAlign = TextAlign.Center, fontWeight = FontWeight.W700, fontSize = 20.sp)
                        description?.let {
                            Text(modifier = Modifier
                                .padding(top = 15.dp, bottom = 10.dp)
                                .verticalScroll(scroll),
                                text = description,
//                                lineHeight = 16.2.sp,
                                color = Color(0xFF848484),
                                fontWeight = FontWeight.W500,
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp
                            )
                        }
                     }
                    if (negativeButtonText != null){
                        Row(modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(bottom = 42.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Button(
                                onClick ={ positiveButtonAction()},
                                shape = RoundedCornerShape(8.dp),
                                elevation = ButtonDefaults.elevation(0.dp) ,
                                colors = ButtonDefaults.buttonColors(backgroundColor =positiveButtonBgColor  ),
                                modifier = positiveButtonModifier.height(46.dp)
                            ) {
                                Text(text = positiveButtonText, textAlign = TextAlign.Center, fontWeight = FontWeight.ExtraBold, color = positiveButtonTextColor, fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Button(
                                onClick ={ negativeButtonAction()},
                                shape = RoundedCornerShape(8.dp),
                                elevation = ButtonDefaults.elevation(0.dp) ,
                                colors = ButtonDefaults.buttonColors(backgroundColor = negativeButtonBgColor),
                                modifier =negativeButtonModifier.height(46.dp)
                            ) {
                                Text(text = negativeButtonText, textAlign = TextAlign.Center, fontWeight = FontWeight.ExtraBold, color =negativeButtonTextColor , fontSize = 14.sp)
                            }
                        }
                    }else{
                        Box(modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(bottom = 42.dp), contentAlignment = Alignment.Center){
                            Button(
                                onClick ={ positiveButtonAction()},
                                shape = RoundedCornerShape(8.dp),
                                elevation = ButtonDefaults.elevation(0.dp) ,
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0XFF495CE8)),
                                modifier = positiveButtonModifier.height(46.dp)

                            ) {
                                Text(text = positiveButtonText, modifier = Modifier.fillMaxWidth(0.6f), textAlign = TextAlign.Center, fontWeight = FontWeight.ExtraBold, color = Color.White, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
        Box(modifier = Modifier
            .height(108.dp)
            .width(108.dp)){
            Icon(painter = painterResource(id = icon), tint = Color.Unspecified, contentDescription ="" )
        }
    }
}

@Composable
fun DynamicModal(
    icon:Int,
    title:String,
    description:String? = null,
    modalHeight :Int = 350,

    positiveButtonText:String,
    negativeButtonText: String? = null,

    positiveButtonBgColor: Color = Color(0XFF495CE8),
    negativeButtonBgColor: Color = Color.White,

    positiveButtonTextColor: Color = Color.White,
    negativeButtonTextColor: Color = Color.Black,

    positiveButtonAction:() -> Unit,
    negativeButtonAction:() -> Unit,

    positiveButtonModifier: Modifier = Modifier,
    negativeButtonModifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = {
        if (negativeButtonText == null) {
            positiveButtonAction()
        } else {
            negativeButtonAction()
        }
    },properties = DialogProperties(
        usePlatformDefaultWidth = false
    ), content = {
        Surface(
            color = Color.Transparent,
            modifier = Modifier.fillMaxWidth(0.9f),
            content = {
                DynamicModalView(
                    icon = icon,
                    title = title,
                    description = description,
                    modalHeight = modalHeight,
                    positiveButtonAction = positiveButtonAction,
                    negativeButtonAction = negativeButtonAction,
                    positiveButtonText = positiveButtonText,
                    negativeButtonText = negativeButtonText,
                    positiveButtonModifier =positiveButtonModifier ,
                    negativeButtonModifier = negativeButtonModifier,
                    positiveButtonBgColor = positiveButtonBgColor,
                    positiveButtonTextColor = positiveButtonTextColor,
                    negativeButtonTextColor = negativeButtonTextColor,
                    negativeButtonBgColor = negativeButtonBgColor
                )
            }
        )

    })
}

@Preview
@Composable
private fun BorderProgressBar() {
    DynamicModalView(
        title = "You Can't Report yet!",
        description = "Drive while Cee is opened for 15km to unlock the ability to add report",
        icon = R.drawable.ic_cee_two,
        positiveButtonAction = {},
        negativeButtonAction = {},
        positiveButtonText = "Done",
        negativeButtonText = "None",
        positiveButtonModifier =Modifier.fillMaxWidth(0.49f) ,
        negativeButtonModifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = Color(0xFFAAA9AB), shape = RoundedCornerShape(8.dp))
    )
}