package com.orbital.cee.view.home.appMenu

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.orbital.cee.R
import com.orbital.cee.model.UserNameAndID
import com.orbital.cee.model.UserNew
import com.orbital.cee.ui.theme.black
import com.orbital.cee.ui.theme.type_gray


@OptIn(ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun SearchForUser(
    onClickUser: (userId: String) -> Unit,
    onClickBack: () -> Unit,
    onClickSearch: (phoneOrEmail: String) -> Unit,
    userss: MutableList<UserNameAndID>,
    usersFound:MutableState<Boolean>
){
    val rotate = if (LocalConfiguration.current.layoutDirection == LayoutDirection.Rtl.ordinal){180f}else{0f}
    val userPhoneOrEmail = remember {
        mutableStateOf("")
    }
    Column(modifier = Modifier
        .background(color = Color.White)
        .fillMaxSize()
        .padding(horizontal = 24.dp)) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(85.dp)
            .clickable(
                onClick = onClickBack,
                indication = null,
                interactionSource = remember { MutableInteractionSource() })
            .padding(bottom = 8.dp), verticalAlignment = Alignment.Bottom) {
            Icon(modifier = Modifier
                .rotate(rotate)
                .padding(bottom = 5.dp), tint = Color(0xFF848484) , painter = painterResource(id = R.drawable.ic_arrow_left), contentDescription ="" )
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = "SearchForUser", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = black)
        }
        Spacer(modifier = Modifier.height(15.dp))

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            BasicTextField(
                value = userPhoneOrEmail.value,
                onValueChange = {userPhoneOrEmail.value = it},
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth(0.7f)
                ,
                decorationBox = {innerTextField ->
                    TextFieldDefaults.TextFieldDecorationBox(
                        value = userPhoneOrEmail.value,
                        innerTextField = innerTextField,
                        enabled = true,
                        singleLine = true,
                        visualTransformation = VisualTransformation.None,
                        interactionSource = remember { MutableInteractionSource() },
                        contentPadding = PaddingValues(0.dp),
                    )
                    Text(text = "phoneNumber or Email")
                }
            )
            Spacer(modifier = Modifier.width(10.dp))
            Button(onClick = {
                onClickSearch(userPhoneOrEmail.value)
            }) {
                Text(text = "Search")
            }
        }
        Spacer(modifier = Modifier.height(15.dp))
        if (usersFound.value){
            userss.forEach{
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .background(color = type_gray, shape = RoundedCornerShape(16.dp))
                    .padding(10.dp)
                    .height(40.dp)
                    .clickable(onClick = {
                        //navigate to userDetail
                        onClickUser(it.userId)
                    }),
                    horizontalArrangement = Arrangement.Center) {
                    Text(text = it.username)
                }
                Spacer(modifier = Modifier.height(5.dp))

            }

        }

    }
}
@Composable
fun UserDetail(onClickBack: () -> Unit, userDetail: MutableState<UserNew>){
    val rotate = if (LocalConfiguration.current.layoutDirection == LayoutDirection.Rtl.ordinal){180f}else{0f}
    val composition by rememberLottieComposition(
        LottieCompositionSpec
            .RawRes(R.raw.lottie_three_dot_loading)
    )
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true,
        speed = 1f,
        restartOnPlay = false
    )
    Column(modifier = Modifier
        .background(color = Color.White)
        .fillMaxSize()
        .padding(horizontal = 24.dp)) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(85.dp)
            .clickable(
                onClick = onClickBack,
                indication = null,
                interactionSource = remember { MutableInteractionSource() })
            .padding(bottom = 8.dp), verticalAlignment = Alignment.Bottom) {
            Icon(modifier = Modifier
                .rotate(rotate)
                .padding(bottom = 5.dp), tint = Color(0xFF848484) , painter = painterResource(id = R.drawable.ic_arrow_left), contentDescription ="" )
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = "SearchForUser", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = black)
        }
        Spacer(modifier = Modifier.height(15.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Box(modifier = Modifier
                .size(150.dp), contentAlignment = Alignment.Center){
                Card(
                    Modifier.size(150.dp),
                    shape = RoundedCornerShape(19.dp),
                    ){
                    SubcomposeAsyncImage(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = Color(0xFFEEEEEE)),
                        model = "${userDetail.value.userAvatar}",
                        loading = {
                            Box(modifier = Modifier
                                .fillMaxSize()
                                .background(color = Color.DarkGray), contentAlignment = Alignment.Center){
                                LottieAnimation(
                                    composition,
                                    progress,
                                    modifier = Modifier.size(100.dp)
                                )
                            }
                        },
                        contentScale = ContentScale.Crop,
                        contentDescription = null
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(15.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(text = "username: ")
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = "${userDetail.value.username}")
        }
        Spacer(modifier = Modifier.height(15.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Row() {
                Text(text = "userPoint: ")
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "${userDetail.value.userPoint}")
            }
            Row() {
                Text(text = "userType: ")
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "${userDetail.value.userType}")
            }
        }
        Spacer(modifier = Modifier.height(15.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Row() {
                Text(text = "userLevel: ")
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "${userDetail.value.userLevel}")
            }
            Row() {
                Text(text = "isBanned: ")
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "false")
            }
        }
        Spacer(modifier = Modifier.height(15.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Row() {
                Text(text = "speedometers: ")
                Spacer(modifier = Modifier.width(10.dp))
                Column() {
                    userDetail.value.ceedometers?.forEach {
                        it?.let {
                            Text(text = it)
                        }

                    }
                }
            }
            Row() {
                Text(text = "cursors: ")
                Spacer(modifier = Modifier.width(10.dp))
                Column() {
                    userDetail.value.cursor?.forEach {
                        it?.let {
                            Text(text = it)
                        }

                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(15.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(onClick = { /*TODO*/ }) {
                    Text(text = "Add HITEX SPEEDOMETER + Cursor")
                }

        }
        Spacer(modifier = Modifier.height(15.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Button(onClick = { /*TODO*/ }) {
                Text(text = "Update UserType to 3")
            }

        }
        Spacer(modifier = Modifier.height(15.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Button(onClick = { /*TODO*/ }) {
                Text(text = "Band")
            }

        }
    }
}