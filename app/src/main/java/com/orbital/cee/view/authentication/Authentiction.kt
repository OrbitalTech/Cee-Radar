package com.orbital.cee.view.authentication

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider.getCredential
import com.orbital.cee.R
import com.orbital.cee.core.countryList
import com.orbital.cee.model.Country
import com.orbital.cee.model.Response
import com.orbital.cee.utils.Utils.getCountryCode
import com.orbital.cee.view.authentication.component.CountryPickerBottomSheet
import com.orbital.cee.view.home.HomeActivity
import kotlinx.coroutines.launch

@Composable
fun Authentication(
    navController: NavController,
    viewModel: AuthenticationViewModel = hiltViewModel(),
//    model : HomeViewModel = viewModel(),
) {
    val phoneNumber = remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isShowCountryPicker by remember { mutableStateOf(false) }
    val isLogin by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var selectedCountry by remember { mutableStateOf<Country?>(Country("AE","United Arab Emirates","+971")) }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val countries = remember { countryList(context) }

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
    val composition1 by rememberLottieComposition(
        LottieCompositionSpec
            .RawRes(R.raw.lottie_signup_cee)
    )


    val progress1 by animateLottieCompositionAsState(
        composition1,
        iterations = LottieConstants.IterateForever,
        isPlaying = true,
        speed = 1f,
        restartOnPlay = false
    )

    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        viewModel.singOut()
        val a =  getCountryCode(context).uppercase()
        countries.forEach {
            if (it.code == a){
                Log.d("COUNTRYCODE",it.dialCode)
                selectedCountry = it
            }
        }
    }
//    val langCode = model.langCode.observeAsState()
//    val configuration = LocalConfiguration.current
//    LaunchedEffect(Unit ){
//        var locale = langCode.value?.let { Locale(it) }
//        configuration.setLocale(locale)
//        if (langCode.value == "ku"){
//            configuration.setLayoutDirection(Locale("ar"))
//        }
//        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
//    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF495CE8))
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        focusManager.clearFocus()
                    }
                )
            },
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxSize(),contentAlignment = Alignment.BottomCenter){
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.90f)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    ),
            ){
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.height(60.dp))
                    Box(
                        modifier = Modifier
                            .background(color = Color.White),
                        contentAlignment = Alignment.Center){
                        LottieAnimation(
                            composition1,
                            progress1,
                            modifier = Modifier.size(150.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(25.dp))
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Row(modifier = Modifier.padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedButton(
                                modifier = Modifier
                                    .weight(0.3f).height(54.dp),
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, color = Color(0xFFE4E4E4)),
                                onClick = { isShowCountryPicker = !isShowCountryPicker },
                            ) {
                                Row(modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Row{
                                        Text(text = selectedCountry!!.dialCode, color = Color.Black)
                                    }
                                    Icon(
                                        painterResource(id = R.drawable.ic_arrow_down),
                                        contentDescription = "ic_down",
                                        modifier = Modifier.size(17.dp),
                                        tint = Color(0xFFE4E4E4)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            OutlinedTextField(
                                modifier =
                                Modifier
                                    .weight(weight = 0.7f, fill = true)
                                    .height(54.dp)
                                    .border(
                                        BorderStroke(1.dp, Color(0xFFE4E4E4)),
                                        shape = RoundedCornerShape(10.dp)
                                    ),
                                value = phoneNumber.value,
                                placeholder = {
                                    Text(text = stringResource(id = R.string.txtF_phone_number_hint),color = Color.Gray, fontSize = 14.sp)
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                                onValueChange = {
                                    if (it.length <= 11){ phoneNumber.value = it}else{
                                        focusManager.clearFocus()
                                    }
                                   },
                                shape = RoundedCornerShape(10.dp),
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(15.dp))
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            isLoading = true
                            if (phoneNumber.value.length < 10){
                                showErrorDialog = true
                                errorMessage =context.resources.getString(R.string.lbl_auth_invalidPhoneNumber)
                                isLoading = false

                            }else{
                                coroutineScope.launch {
                                    viewModel.sendOTP(selectedCountry!!.dialCode,phoneNumber.value, context).collect{
                                        if (it.isSuccess){
                                            navController.navigate("verifyOtp/${selectedCountry!!.dialCode}/${phoneNumber.value}")
                                            isLoading = false
                                        }else{
                                            errorMessage = it.message
                                            showErrorDialog = true
                                            isLoading = false
                                        }
                                    }
                                }
                            }

                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                            .padding(horizontal = 20.dp)
                    ) {
                        Text(text =if(isLogin) stringResource(id = R.string.lbl_auth_login) else stringResource(id = R.string.btn_auth_continue_with_phone), color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                    Row(modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                        ,verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceAround) {
                        Divider(thickness = 1.dp, modifier = Modifier
                            .fillMaxWidth(0.45f)
                            .padding(5.dp), color = Color(0xFFE4E4E4))
                        Row(modifier = Modifier.width(40.dp), horizontalArrangement = Arrangement.Center){
                            Text(text = stringResource(id = R.string.lbl_auth_or), color = Color(0xFFE4E4E4))
                        }
                        Divider(thickness = 1.dp, modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),color = Color(0xFFE4E4E4))

//                    Divider(thickness = 1.dp, modifier = Modifier
//                        .fillMaxWidth(0.4f)
//                        .padding(5.dp), color = Color(0xFFE4E4E4))
//                    Text(text = "or", color = Color(0xFFE4E4E4))
//                    Divider(thickness = 1.dp, modifier = Modifier
//                        .fillMaxWidth(0.4f)
//                        .padding(5.dp),color = Color(0xFFE4E4E4))

                    }
                    Spacer(modifier = Modifier.height(30.dp))
                    OutlinedButton(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.oneTapSignIn()
                            isLoading = true
                                  },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFFE4E4E4)),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(68.dp)
                            .padding(vertical = 5.dp, horizontal = 20.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
                            Icon(
                                painterResource(id = R.drawable.ic_google_color),
                                contentDescription = "google",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(25.dp)
                            )
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Text(stringResource(id = R.string.btn_auth_continue_with_google),color = Color.Black)
                            }
                        }
                    }


                OutlinedButton(
                    onClick = {
                        focusManager.clearFocus()
                        val navigate = Intent(context, FbAuthActivity::class.java)
                        context.startActivity(navigate)
                    },
                    border = BorderStroke(1.dp, Color(0xFFE4E4E4)),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(68.dp)
                        .padding(vertical = 5.dp, horizontal = 20.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
                        Icon(
                            painterResource(id = R.drawable.ic_facebook),
                            contentDescription = "facebook",
                            tint = Color(0xFF495CE8),
                            modifier = Modifier.size(28.dp)
                        )
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text(stringResource(id = R.string.btn_auth_continue_with_facebook), color = Color.Black)

                        }
                    }
                }
//                Spacer(modifier = Modifier.height(15.dp))
//                Row() {
//                    Text(text =if(isLogin) "Don’t have an account? " else "Already have an account? ", fontWeight = FontWeight.Bold)
//
//                    ClickableText(
//                        text = AnnotatedString(text = if(isLogin) "Sign Up"  else "Log In") ,
//                        style = TextStyle(
//                            fontWeight = FontWeight.Bold,
//                            color = Color(0xFF495CE8),
//                            fontSize = 16.sp
//                        ),
//                        onClick = {
//                            isLogin = !isLogin
//                        })
//                }

                }
                CountryPickerBottomSheet(
                    title = {
                        Text(
                            text = stringResource(id = R.string.lbl_auth_selectCountry),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    },
                    onItemSelected = {
                        selectedCountry = it
                        isShowCountryPicker = false
                    },
                    show = isShowCountryPicker,
                    onDismissRequest = {
                        isShowCountryPicker = false
                    }
                ){}
            }
            if(isLoading){
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color(0xB2000000))
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = {

                            })
                        },
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

        if (showErrorDialog) {
            ShowErrorDialog(
                onOkayClick = {
                    showErrorDialog = false
                    isLoading = false
                },
                onDismiss = {
                    showErrorDialog = true
                },
                title = stringResource(id = R.string.lbl_auth_alert_errorOTP),
                message = errorMessage
            )
        }
    }




    val launcher = rememberLauncherForActivityResult(StartIntentSenderForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            try {
                val credentials = viewModel.oneTapClient.getSignInCredentialFromIntent(result.data)
                val googleIdToken = credentials.googleIdToken
                val googleCredentials = getCredential(googleIdToken, null)
                viewModel.signInWithGoogle(googleCredentials)
                isLoading = false
            } catch (it: ApiException) {
                print(it)
            }
        }else{
            isLoading = false
        }
    }

    fun launch(signInResult: BeginSignInResult) {
        val intent = IntentSenderRequest.Builder(signInResult.pendingIntent.intentSender).build()
        launcher.launch(intent)
    }

    when(val oneTapSignInResponse = viewModel.oneTapSignInResponse) {
        is Response.Loading -> {}
        is Response.Success -> oneTapSignInResponse.data?.let {
            LaunchedEffect(it) {
                launch(it)
            }
        }
        is Response.Error -> oneTapSignInResponse.e?.let {
            LaunchedEffect(Unit) {
                errorMessage = it.message.toString()
                showErrorDialog = true
                print(it)
            }
        }
    }

    when(val signInWithGoogleResponse = viewModel.signInWithGoogleResponse) {
        is Response.Loading -> {}
        is Response.Success -> signInWithGoogleResponse.data?.let { isNewUser ->
            if (isNewUser) {
                LaunchedEffect(isNewUser) {
                    viewModel.createUser()
                }
            } else {
                LaunchedEffect(Unit) {
                    val navigate = Intent(context, HomeActivity::class.java)
                    context.startActivity(navigate)
                }
            }
        }
        is Response.Error -> signInWithGoogleResponse.e?.let {
            LaunchedEffect(Unit) {
                errorMessage = it.message.toString()
                showErrorDialog = true
                print(it)
            }
        }
    }

    when(val createUserResponse = viewModel.createUserResponse) {
        is Response.Loading -> {}
        is Response.Success -> createUserResponse.data?.let { isUserCreated ->
            if (isUserCreated) {
                LaunchedEffect(Unit) {
                    val navigate = Intent(context, HomeActivity::class.java)
                    context.startActivity(navigate)
                }
            }
        }
        is Response.Error -> createUserResponse.e?.let {
            LaunchedEffect(Unit) {
                errorMessage = it.message.toString()
                showErrorDialog = true
                print(it)
            }
        }
    }
    }
@Composable
fun ShowErrorDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onOkayClick: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss,properties = DialogProperties(
        usePlatformDefaultWidth = false
    ), content = {
        Surface(
            color = Color.Transparent,
            modifier = Modifier.fillMaxWidth(0.9f),
            content = {
                ErrorModal(onOkayClick =  onOkayClick,title = title,message = message)
            }
        )

    })
}
@Composable
fun ErrorModal(onOkayClick: () -> Unit,title: String,message: String) {
    val scroll = rememberScrollState(0)
    val focusManager = LocalFocusManager.current
    Box(modifier = Modifier
        .height(345.dp)
        .fillMaxWidth()
        .pointerInput(Unit) {
            detectTapGestures(onTap = {
                focusManager.clearFocus()
            })
        }
        .background(color = Color.Transparent), contentAlignment = Alignment.TopCenter){
        Box(modifier = Modifier.fillMaxSize(),contentAlignment = Alignment.BottomCenter){
            Card(
                Modifier
                    .height(300.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                backgroundColor = Color.White

            ) {
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween, horizontalAlignment = Alignment.CenterHorizontally) {
                    Column(verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(modifier = Modifier.height(60.dp))
                        Box(modifier = Modifier
                            .fillMaxWidth(0.9f), contentAlignment = Alignment.Center){
                            Text(text = title, textAlign = TextAlign.Center, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                        Box(modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(110.dp),contentAlignment = Alignment.Center){
                            Text(modifier = Modifier.verticalScroll(scroll),text = message,textAlign = TextAlign.Center, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xff848484))
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                    }

                    Row(modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .padding(bottom = 10.dp)
                        .height(50.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Button(
                            onClick =onOkayClick,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0XFF495CE8)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(45.dp)
                        ) {
                            Text(text = stringResource(id = R.string.btn_auth_alert_ok), fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
                Box(modifier = Modifier.fillMaxSize() , contentAlignment = Alignment.Center){
                    Box(modifier = Modifier.fillMaxWidth(0.7f)){
                        Text(text = "")
                    }
                }
                Box(modifier = Modifier.fillMaxSize() , contentAlignment = Alignment.BottomCenter){

                }

            }
        }
        Box(modifier = Modifier
            .height(100.dp)
            .width(100.dp)){
            Icon(painter = painterResource(id = R.drawable.ic_cee_ungry), modifier = Modifier.size(155.dp), tint = Color.Unspecified, contentDescription ="" )
        }

    }
}