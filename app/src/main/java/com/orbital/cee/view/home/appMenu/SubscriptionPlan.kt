package com.orbital.cee.view.home.appMenu

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
//import com.flexcode.inapppurchasescompose.InAppPurchasesHelper
import com.orbital.cee.R
import com.orbital.cee.ui.theme.black
import com.orbital.cee.ui.theme.blurple
import com.orbital.cee.ui.theme.green
import com.orbital.cee.ui.theme.type_gray

@Composable
fun SubscriptionPlan(onClickClose:()->Unit,onSubscriptionError:(title:String,description:String)->Unit){
    val selectedPlan = remember {
        mutableStateOf(1)
    }

    val context = LocalContext.current

//    val billingPurchaseHelper = InAppPurchasesHelper(context as Activity,"test_product")
//    billingPurchaseHelper.setUpBillingPurchases()


    Column(modifier = Modifier
        .fillMaxSize()
        .background(color = MaterialTheme.colors.background)
        .padding(horizontal = 24.dp)
        .verticalScroll(rememberScrollState())) {
        Spacer(modifier = Modifier.height(35.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            Box(modifier = Modifier
                .size(50.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onClickClose
                ), contentAlignment = Alignment.Center){
                Icon(modifier = Modifier.size(18.dp),painter = painterResource(id = R.drawable.ic_close), tint = blurple, contentDescription = "")
            }
        }
        Spacer(modifier = Modifier.height(15.dp))
        Icon(painter = painterResource(id = R.drawable.img_subscription_trophy), tint = Color.Unspecified, contentDescription = "")
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Get access to all", fontSize = 24.sp, fontWeight = FontWeight.SemiBold, color = black)
        Text(text = "Pro Features", fontSize = 24.sp, fontWeight = FontWeight.SemiBold, color = blurple)
        Spacer(modifier = Modifier.height(15.dp))
        Row {
            Icon(painter = painterResource(id = R.drawable.ic_tick_circle), contentDescription = "", tint = green)
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = "Background Alert", fontSize = 16.sp, color = Color.Black)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row {
            Icon(painter = painterResource(id = R.drawable.ic_tick_circle), contentDescription = "", tint = green)
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = "Unlimited Trips", fontSize = 16.sp, color = Color.Black)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row {
            Icon(painter = painterResource(id = R.drawable.ic_tick_circle), contentDescription = "", tint = green)
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = "Pro Speedometer Style", fontSize = 16.sp, color = Color.Black)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row {
            Icon(painter = painterResource(id = R.drawable.ic_tick_circle), contentDescription = "", tint = green)
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = "No ADs", fontSize = 16.sp, color = Color.Black)
        }
        Spacer(modifier = Modifier.height(15.dp))
        Text(text = "Choose a Plan", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = black)
        Spacer(modifier = Modifier.height(22.dp))

        Row(modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .clickable(interactionSource = remember {
                MutableInteractionSource()
            }, indication = null, onClick = {
                selectedPlan.value = 1
            })
            .border(
                width = if (selectedPlan.value == 1){2.dp}else{ 1.dp},
                color = if (selectedPlan.value == 1){ blurple}else{Color(0xFFE4E4E4)} ,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 18.dp),horizontalArrangement = Arrangement.SpaceBetween) {
            Row() {
                Box(modifier = Modifier
                    .padding(top = 18.dp)
                    .size(20.dp)
                    .background(color = Color(0xFFF0F0F0), shape = CircleShape), contentAlignment = Alignment.Center){
                    if (selectedPlan.value == 1){
                        Box(modifier = Modifier
                            .size(12.dp)
                            .background(color = blurple, shape = CircleShape))
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    Text(text = "Weekly", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = black)
                    Text(text = "Pay Weekly, Cancel any time.", fontSize = 14.sp, color = type_gray)
                }
            }
            Text(modifier = Modifier.padding(top = 15.dp),text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                    baselineShift = BaselineShift.Superscript,
                    fontSize = 10.sp,
                    color = type_gray
                )
                ){
                    append("$ ")
                }
                withStyle(
                    SpanStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = black
                    )
                ){
                    append("0.99")
                }
                withStyle(
                    SpanStyle(
                        baselineShift = BaselineShift.None,
                        fontSize = 10.sp,
                        color = type_gray
                    )
                ){
                    append(" /W")
                }

            }, color = Color(0xFF919191),fontSize = 14.sp)

        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .clickable(interactionSource = remember {
                MutableInteractionSource()
            }, indication = null, onClick = {
                selectedPlan.value = 2
            })
            .border(
                width = if (selectedPlan.value == 2) {
                    2.dp
                } else {
                    1.dp
                },
                color = if (selectedPlan.value == 2) {
                    blurple
                } else {
                    Color(0xFFE4E4E4)
                },
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 18.dp),horizontalArrangement = Arrangement.SpaceBetween) {
            Row() {
                Box(modifier = Modifier
                    .padding(top = 18.dp)
                    .size(20.dp)
                    .background(color = Color(0xFFF0F0F0), shape = CircleShape), contentAlignment = Alignment.Center){
                    if (selectedPlan.value == 2){
                        Box(modifier = Modifier
                            .size(12.dp)
                            .background(color = blurple, shape = CircleShape))
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    Text(text = "Monthly", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = black)
                    Text(text = "Pay Monthly, Cancel any time.", fontSize = 14.sp, color = type_gray)
                    Text(text = "Save 10%", fontSize = 14.sp, color = type_gray, fontWeight = FontWeight.SemiBold)
                }
            }
            Text(modifier = Modifier.padding(top = 15.dp),text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        baselineShift = BaselineShift.Superscript,
                        fontSize = 10.sp,
                        color = type_gray
                    )
                ){
                    append("$ ")
                }
                withStyle(
                    SpanStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = black
                    )
                ){
                    append("3.50")
                }
                withStyle(
                    SpanStyle(
                        baselineShift = BaselineShift.None,
                        fontSize = 10.sp,
                        color = type_gray
                    )
                ){
                    append(" /M")
                }

            }, color = Color(0xFF919191),fontSize = 14.sp)

        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .clickable(interactionSource = remember {
                MutableInteractionSource()
            }, indication = null, onClick = {
                selectedPlan.value = 3
            })
            .border(
                width = if (selectedPlan.value == 3) {
                    2.dp
                } else {
                    1.dp
                },
                color = if (selectedPlan.value == 3) {
                    blurple
                } else {
                    Color(0xFFE4E4E4)
                },
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 18.dp),horizontalArrangement = Arrangement.SpaceBetween) {
            Row() {
                Box(modifier = Modifier
                    .padding(top = 18.dp)
                    .size(20.dp)
                    .background(color = Color(0xFFF0F0F0), shape = CircleShape), contentAlignment = Alignment.Center){
                    if (selectedPlan.value == 3){
                        Box(modifier = Modifier
                            .size(12.dp)
                            .background(color = blurple, shape = CircleShape))
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    Text(text = "Yearly", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = black)
                    Text(text = "Pay Yearly, Cancel any time. ", fontSize = 14.sp, color = type_gray)
                    Text(text = "Save 20%", fontSize = 14.sp, color = type_gray, fontWeight = FontWeight.SemiBold)
                }
            }
            Text(modifier = Modifier.padding(top = 15.dp),text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        baselineShift = BaselineShift.Superscript,
                        fontSize = 10.sp,
                        color = type_gray
                    )
                ){
                    append("$ ")
                }
                withStyle(
                    SpanStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = black
                    )
                ){
                    append("29.99")
                }
                withStyle(
                    SpanStyle(
                        baselineShift = BaselineShift.None,
                        fontSize = 10.sp,
                        color = type_gray
                    )
                ){
                    append(" /Y")
                }

            }, color = Color(0xFF919191),fontSize = 14.sp)

        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(modifier = Modifier
            .fillMaxWidth()
            .height(56.dp), onClick = {
            try {
//                billingPurchaseHelper.initializePurchase()
            }catch (e : Exception){
                onSubscriptionError("Purchase was not completed","There was a connection error، the purchase cannot be completed at this time, due to some issues beyond our control, please try again later.")
            }

        },shape = RoundedCornerShape(16.dp),colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF495CE8))) {
            Text(text = "Subscribe Now", color = Color.White, fontWeight = FontWeight.Bold)

        }
        Spacer(modifier = Modifier.height(65.dp))

    }
    
}
@Preview
@Composable
fun SubscriptionPlanPreview(){
    SubscriptionPlan({},{_,_->})

}