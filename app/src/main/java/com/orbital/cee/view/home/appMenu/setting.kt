package com.orbital.cee.view.home.appMenu

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.airbnb.lottie.compose.*
import com.orbital.cee.R
import com.orbital.cee.core.Constants
import com.orbital.cee.view.MainActivity
import com.orbital.cee.view.home.HomeViewModel
import com.orbital.cee.view.home.appMenu.componenets.radio
import com.orbital.cee.view.home.components.showCustomDialog
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun Setting( model: HomeViewModel,onClickDone: ()-> Unit) {
    val fullName = remember { mutableStateOf("${model.userInfo.value.username}") }
    val phone = remember { mutableStateOf("${model.userInfo.value.phoneNumber}") }
    val email = remember { mutableStateOf("${model.userInfo.value.userEmail}") }
    val gender = remember { mutableStateOf("${model.userInfo.value.userGender}") }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isChange by remember { mutableStateOf(false) }
    val openDialog = remember{mutableStateOf(false)}
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()){ uri : Uri? ->
        imageUri = uri
    }
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
    Column(modifier = Modifier.fillMaxSize().pointerInput(Unit) {
        detectDragGestures(onDrag = { point, offset ->
            if (offset.x > Constants.OFFSET_X && point.position.x < Constants.POINT_X) {
                onClickDone.invoke()
            }
        })
    }){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {})
                }
                .height(80.dp)
                .background(color = Color.White)
                .clip(shape = RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp))
                .border(
                    width = 1.dp,
                    color = Color(0XFFE4E4E4),
                    shape = RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp)
                ).padding(bottom = 8.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Text(text = stringResource(R.string.lbl_setting_appBar_title), fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 20.sp)
            Box(modifier = Modifier.fillMaxSize().padding(horizontal = 15.dp).padding(bottom = 8.dp), contentAlignment = Alignment.BottomEnd){
                if(isChange){
                    ClickableText(
                        text = AnnotatedString("SAVE") ,
                        style = TextStyle(color =Color(0XFF495CE8)),
                        onClick = {
                            coroutineScope.launch{
                                if (fullName.value.length >= 3){
                                    if (phone.value.isNotBlank()){
                                        model.updateUserInfo(fullName.value,phone.value,email.value,gender.value).collect{
                                            isChange = false
                                            model.loadUserInfoFromFirebase()
                                            Toast.makeText(context,it.message,Toast.LENGTH_LONG).show()
                                        }
//                                    if (email.value.length >= 10 && email.value.contains("@")){
//
//                                    }
                                    }
                                }
                            }
                    })

//                    TextButton(onClick = {
//                        coroutineScope.launch{
//                            if (fullName.value.length >= 3){
//                                if (phone.value.isNotBlank()){
//                                    model.updateUserInfo(fullName.value,phone.value,email.value,gender.value).collect{
//                                        isChange = false
//                                        model.loadUserInfoFromFirebase()
//                                        Toast.makeText(context,it.serverMessage,Toast.LENGTH_LONG).show()
//                                    }
////                                    if (email.value.length >= 10 && email.value.contains("@")){
////
////                                    }
//                                }
//                            }
//                        }
//
//
//
//                    }, modifier = Modifier.padding(end = 12.dp)) {
//                        Text(text ="SAVE", fontWeight = FontWeight.Bold, color = Color(0XFF495CE8))
//                    }
                }else{
                    ClickableText(
                        text = AnnotatedString("DONE") ,
                        style = TextStyle(color =Color(0XFF495CE8)),
                        onClick = {
                            onClickDone.invoke()
                        })
//                    TextButton(onClick = onClickDone, modifier = Modifier.padding(end = 12.dp)) {
//                        Text(text ="DONE", fontWeight = FontWeight.Bold, color = Color(0XFF495CE8))
//                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(color = Color.White)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            focusManager.clearFocus()
                        }
                    )
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            //Spacer(modifier = Modifier.height(85.dp))

            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)) {
                Text(text = stringResource(R.string.lbl_setting_title_profile), fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 20.sp)

            }
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally){
                Card(
                    Modifier
                        .size(110.dp)
                        .padding(5.dp),
                    shape = RoundedCornerShape(35.dp),
                    backgroundColor = Color(0XFF495CE8)

                ){
                    imageUri?.let {
                        coroutineScope.launch {
                            model.uploadPhotos(it).collect{response ->
                                if (response.isSuccess){
                                    Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
                                }else{
                                    Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                        if (Build.VERSION.SDK_INT < 28){
                            bitmap.value = MediaStore.Images.Media.getBitmap(context.contentResolver,it)
                        }else{
                            val source = ImageDecoder.createSource(context.contentResolver,it)
                            bitmap.value = ImageDecoder.decodeBitmap(source)
                        }
                    }
                    SubcomposeAsyncImage(
                        modifier = Modifier.fillMaxSize(),
                        model = "${model.userInfo.value.userAvatar}",
                        loading = {
                            Box(modifier = Modifier
                                .fillMaxSize()
                                .background(color = Color.DarkGray), contentAlignment = Alignment.Center){
                                LottieAnimation(
                                    composition,
                                    progress,
                                    modifier = Modifier.size(45.dp)
                                )
                            }

                        },
                        contentScale = ContentScale.Crop,
                        contentDescription = null
                    )

                    bitmap.value?.let { btm ->
                        Image(bitmap = btm.asImageBitmap() , contentDescription ="",
                            modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)

                    }
                }
                TextButton(onClick = { launcher.launch("image/*") }) {
                    Text(text = stringResource(R.string.lbl_change_picture), color = Color(0XFF495CE8))
                }

            }
            Spacer(modifier = Modifier.height(30.dp))
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp), horizontalAlignment = Alignment.Start) {
                Text(text = stringResource(R.string.txtF_setting_name), fontWeight = FontWeight.Bold, color = Color(0XFFABAAAC))
                Spacer(modifier = Modifier.height(5.dp))


                OutlinedTextField(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .background(color = Color(0XFFF7F7F7), shape = RoundedCornerShape(10.dp))
                        .border(
                            BorderStroke(2.dp, Color(0xFFE4E4E4)),
                            shape = RoundedCornerShape(10.dp)
                        ),
                    value = fullName.value,
                    placeholder = {
                        Text(text = stringResource(R.string.txtF_setting_name),color = Color.Gray, fontSize = 14.sp)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                    }),
                    onValueChange = {
                        fullName.value = it
                        isChange = it != model.userInfo.value.username
                                    },
                    shape = RoundedCornerShape(10.dp),
                )
                Spacer(modifier = Modifier.height(15.dp))
                Text(text = stringResource(R.string.txtF_setting_number), fontWeight = FontWeight.Bold, color = Color(0XFFABAAAC))
                Spacer(modifier = Modifier.height(5.dp))


                OutlinedTextField(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .background(color = Color(0XFFF7F7F7), shape = RoundedCornerShape(10.dp))
                        .border(
                            BorderStroke(2.dp, Color(0xFFE4E4E4)),
                            shape = RoundedCornerShape(10.dp)
                        ),
                    value = phone.value,
                    placeholder = {
                        Text(text = stringResource(R.string.txtF_setting_number),color = Color.Gray, fontSize = 14.sp)
                    },
                    enabled = model.userInfo.value.provider != "Phone",
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                    }),
                    onValueChange = {
                        phone.value = it
                        isChange = it != model.userInfo.value.phoneNumber
                    },
                    shape = RoundedCornerShape(10.dp),
                )

                Spacer(modifier = Modifier.height(15.dp))
                Text(text = stringResource(R.string.txtF_setting_email), fontWeight = FontWeight.Bold, color = Color(0XFFABAAAC))
                Spacer(modifier = Modifier.height(5.dp))


                OutlinedTextField(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .background(color = Color(0XFFF7F7F7), shape = RoundedCornerShape(10.dp))
                        .border(
                            BorderStroke(2.dp, Color(0xFFE4E4E4)),
                            shape = RoundedCornerShape(10.dp)
                        ),
                    value = email.value,
                    enabled = model.userInfo.value.provider == "Phone",
                    placeholder = {
                        Text(text = stringResource(R.string.txtF_setting_email),color = Color.Gray, fontSize = 14.sp)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                    }),
                    onValueChange = {email.value = it
                        isChange = it != model.userInfo.value.userEmail
                    },
                    shape = RoundedCornerShape(10.dp),
                )

                
                Spacer(modifier = Modifier.height(15.dp))
                Text(text = stringResource(R.string.lbl_setting_genderPicker_title), fontWeight = FontWeight.Bold, color = Color(0XFFABAAAC))
                Spacer(modifier = Modifier.height(5.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(modifier = Modifier
                        .fillMaxWidth(0.48f)
                        .background(color = Color(0XFFF7F7F7), shape = RoundedCornerShape(10.dp))
                        .height(55.dp)
                        .clickable {
                            gender.value = "M"
                            isChange = gender.value != model.userInfo.value.userGender
                        }
                        .border(
                            BorderStroke(2.dp, Color(0xFFE4E4E4)),
                            shape = RoundedCornerShape(10.dp)
                        ), contentAlignment = Alignment.CenterStart){
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp), verticalAlignment = Alignment.CenterVertically) {
                            radio(gender.value == "M")
                            Text(
                                text = stringResource(R.string.btn_setting_genderPicker_male),
                                modifier = Modifier.padding(horizontal = 15.dp),
                                color = if (isSystemInDarkTheme()) Color(0xFF969EBD) else Color.Gray,
                                fontSize = 14.sp
                            )
                        }

                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color(0XFFF7F7F7), shape = RoundedCornerShape(10.dp))
                        .height(55.dp)
                        .clickable {
                            gender.value = "F"
                            isChange = gender.value != model.userInfo.value.userGender
                        }
                        .border(
                            BorderStroke(2.dp, Color(0xFFE4E4E4)),
                            shape = RoundedCornerShape(10.dp)
                        ), contentAlignment = Alignment.CenterStart){
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp), verticalAlignment = Alignment.CenterVertically) {
                            radio(gender.value == "F")
                            Text(
                                text = stringResource(R.string.btn_setting_genderPicker_female),
                                modifier = Modifier.padding(horizontal = 15.dp),
                                color = if (isSystemInDarkTheme()) Color(0xFF969EBD) else Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }

                }
            }
            Spacer(modifier = Modifier.height(40.dp))
            Divider(color = Color(0XFFE4E4E4), thickness = 1.dp)

            Spacer(modifier = Modifier.height(30.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                TextButton(onClick = { openDialog.value = true }) {
                    Text(text = stringResource(R.string.btn_setting_alert_delete_account), color =Color(0xFFEA4E34))
                }
            }
            Spacer(modifier = Modifier.height(60.dp))
        }
        if (openDialog.value){
            showCustomDialog(
                onNegativeClick = {
                    openDialog.value = false
                },
                onPositiveClick = {
                    model.deleteUser().let {
                        val navigate = Intent(context, MainActivity::class.java)
                        context.startActivity(navigate)
                    }
                },
                title = stringResource(R.string.lbl_setting_alert_delete_account_description),
                buttonPositiveText = stringResource(R.string.btn_home_alert_reportConfirmation_yes),
                buttonNegativeText = stringResource(R.string.btn_home_alert_reportConfirmation_no)
            )
        }
    }

}

//@Preview(showBackground = true)
//@Composable
//fun defaultPreview(){
//    //setting()
//
//}