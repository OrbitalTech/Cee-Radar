package com.orbital.cee.view.authentication.verifyOTP

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.orbital.cee.R
import com.orbital.cee.view.authentication.AuthenticationViewModel
import com.orbital.cee.view.authentication.component.DisplayResponseMessage
import com.orbital.cee.view.authentication.ShowErrorDialog
import com.orbital.cee.view.authentication.component.myOtpComposableOutlined
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun VerifyOTP(
    navController: NavController,
    viewModel: AuthenticationViewModel = hiltViewModel(),
    countryCode: String,
    phoneNumber : String
) {
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var otpVal = ""
    var errorMessage by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var resendCount by remember { mutableStateOf(1)}
    var resendSeconds by remember {mutableStateOf(60)}
    val focusManager = LocalFocusManager.current
    val bringIntoReqester = BringIntoViewRequester()
    val composition by rememberLottieComposition(
        LottieCompositionSpec
            .RawRes(R.raw.lottie_three_dot_loading)
    )
    val conf = LocalConfiguration.current
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true,
        speed = 1f,
        restartOnPlay = false
    )
    val isEnableButtonResend = remember {
        mutableStateOf(false)
    }
    suspend fun myTimer(){
        while (!isEnableButtonResend.value){
            delay(1000)
            resendSeconds -= 1
            if (resendSeconds <= 1){
                isEnableButtonResend.value = true
                resendCount += 1
                resendSeconds = 60 * resendCount
            }
        }
    }
    LaunchedEffect(Unit ){
        this.launch { myTimer()  }
    }

    Surface(color = Color(0xFF495CE8)) {
        Box{
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp).pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                focusManager.clearFocus()

                            }
                        )
                    },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DisplayResponseMessage(viewModel)
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height((conf.screenHeightDp * 0.60).dp)
                    .clip(shape = RoundedCornerShape(20.dp))
                    .background(color = Color.White)
                ){
                    Column(
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalArrangement = Arrangement.Start) {
                                Icon(painterResource(id = R.drawable.ic_arrow_left), modifier = Modifier
                                    .size(18.dp)
                                    .clickable { navController.popBackStack() }, contentDescription = "", tint = Color(0xFFA7A7A7))
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = stringResource(id = R.string.lbl_verif_code_sent) ,color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text ="${stringResource(id = R.string.lbl_verif_code_sent_message)} $countryCode $phoneNumber"  , textAlign = TextAlign.Center, fontSize = 12.sp,color = Color.Black, modifier = Modifier.width(240.dp))

                        Spacer(modifier = Modifier.height(25.dp))
//                        TextField(value = otpVal, onValueChange = {otpVal = it})
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                            LocalTextToolbar provides EmptyTextToolbar
                            myOtpComposableOutlined(
                                widthInDp =if(conf.screenWidthDp<350){40.dp}else{48.dp},
                                heightInDp = if(conf.screenWidthDp<350){40.dp}else{48.dp},
                                backgroundColor = Color.Transparent,
                                passwordToggle = false,
                                focusColor = Color(0xFF495CE8),
                                unfocusColor = Color(0XFFE4E4E4),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp)
                                    .onFocusEvent { evnt ->
                                        if (evnt.isFocused){
                                            coroutineScope.launch {
                                                bringIntoReqester.bringIntoView()
                                            }
                                        }
                                    },
                                cornerRadius = 8.dp,
                                otpComposableType = 6
                            )
                            {
                                otpVal = it
                            }
                        }

                        Spacer(modifier = Modifier.height(30.dp))
                        val a = viewModel.userCre.observeAsState()
                        val phone = viewModel.userPhone.observeAsState()
                        val cc = viewModel.userCCode.observeAsState()
                        Button(onClick = {
                            isLoading = true
                            if (otpVal.length==6){
                                coroutineScope.launch {
                                    viewModel.otpVerification(otpVal, a.value!!).collect{ responseDto ->
                                        if (responseDto.isSuccess){

//                                        viewModel.incrementLoginCount().collect{
//                                            if (it.isSuccess){
//
//                                            }else{
//                                                Log.d("ERROR-22",it.serverMessage)
//                                                Toast.makeText(context,"Unable, login right now.",Toast.LENGTH_LONG).show()
//                                            }
//                                        }
                                            viewModel.isAlreadyRegistered(phone.value.toString(),cc.value.toString()).collect{ it1 ->
                                                if (it1){
                                                    if(viewModel.isUserBanned()){
                                                        errorMessage = context.resources.getString(R.string.lbl_verifyOtp_currentUserBanned)
                                                        isLoading = false
                                                    }else{
                                                        viewModel.saveStatisticsFromFirestore()
                                                        navController.navigate("home")
//                                                        val navigate = Intent(context, HomeActivity::class.java)
//                                                        context.startActivity(navigate)
                                                        isLoading = false
                                                    }

                                                }else{
                                                    viewModel.register(phone.value.toString(),cc.value.toString()).collect{
                                                        if(it.isSuccess){
                                                            navController.navigate("home")
//                                                            val navigate = Intent(context, HomeActivity::class.java)
//                                                            context.startActivity(navigate)
                                                            isLoading = false
                                                        }else{
                                                            showErrorDialog = true
                                                            errorMessage = it.message
                                                            isLoading = false
                                                        }

                                                    }

                                                }
                                            }
                                        }else{
                                            showErrorDialog = true
                                            errorMessage = responseDto.message
                                            isLoading = false
                                            //viewModel.singOut()
                                            //navController.navigate(Screen.Authentication.route)
                                        }
                                    }
                                }
                            }else{
                                isLoading = false
                                showErrorDialog = true
                                errorMessage = context.resources.getString(R.string.lbl_verifyOtp_codeIsSix)
                            }
                        }, enabled = otpVal.length==6,
                            modifier = Modifier
                                .bringIntoViewRequester(bringIntoReqester)
                                .width(200.dp)
                                .height(45.dp), shape = RoundedCornerShape(10.dp)
                        ){
                            Text(text = stringResource(id = R.string.btn_verif_verify), color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(15.dp))
                        
                        Text(text = "${stringResource(id = R.string.lbl_verif_resend_code)} $resendSeconds", fontSize = 10.sp,color = Color.Gray)

                        Spacer(modifier = Modifier.height(20.dp))
                        if (isEnableButtonResend.value){
                            Row() {
                                Icon(painter = painterResource(id = R.drawable.ic_reload), tint = Color.Gray , contentDescription = "")
                                Spacer(modifier = Modifier.width(8.dp))
                                ClickableText(
                                    text = AnnotatedString(stringResource(id = R.string.lbl_verif_resend_code)) ,
                                    style = TextStyle(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    ),
                                    onClick = {
                                        isEnableButtonResend.value = false
                                        coroutineScope.launch {
                                            viewModel.sendOTP(countryCode,phoneNumber,context).collect{
                                                if(it.isSuccess){
                                                    Toast.makeText(context, it.message,Toast.LENGTH_LONG).show()
                                                    myTimer()
                                                }else{
                                                    showErrorDialog = true
                                                    errorMessage = it.message
                                                    isEnableButtonResend.value = true
                                                }
                                                Log.d("MSG-32",it.message)
                                            }
                                        }
                                    })
                            }
                            
                        }
                    }
                }
            }
            if (showErrorDialog) {
                ShowErrorDialog(
                    onOkayClick = {
                        showErrorDialog = false
                    },
                    onDismiss = {
                        showErrorDialog = true
                    },
                    title = stringResource(id = R.string.lbl_verifyOtp_anError),
                    message = errorMessage
                )
            }
            if(isLoading){
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color(0xB2000000)),
                    contentAlignment = Alignment.Center
                ) {
                    LottieAnimation(
                        composition,
                        progress,
                        modifier = Modifier.size(65.dp)
                    )
                }
            }
        }


    }

}

object EmptyTextToolbar: TextToolbar {
    override val status: TextToolbarStatus = TextToolbarStatus.Hidden

    override fun hide() {  }
    override fun showMenu(
        rect: androidx.compose.ui.geometry.Rect,
        onCopyRequested: (() -> Unit)?,
        onPasteRequested: (() -> Unit)?,
        onCutRequested: (() -> Unit)?,
        onSelectAllRequested: (() -> Unit)?
    ) {
        TODO("Not yet implemented")
    }
}