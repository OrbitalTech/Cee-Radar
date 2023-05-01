package com.orbital.cee.view.authentication.component

import android.app.Activity
import android.content.*
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.nativeKeyCode
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status


private var string = ""

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun myOtpComposableOutlined(
    widthInDp : Dp,
    heightInDp : Dp,
    backgroundColor : Color,
    passwordToggle : Boolean,
    focusColor : Color,
    unfocusColor: Color,
    cornerRadius : Dp,
    modifier: Modifier,
    automaticCapture : Boolean? = false,
    otpComposableType : Int = 4,
    onvaluechange: (String) -> Unit
)
{
    var otp1 by remember { mutableStateOf("") }
    var otp2 by remember { mutableStateOf("") }
    var otp3 by remember { mutableStateOf("") }
    var otp4 by remember { mutableStateOf("") }
    var otp5 by remember { mutableStateOf("") }
    var otp6 by remember { mutableStateOf("") }
    val conf = LocalConfiguration.current
    if (automaticCapture == true){
        SmsRetrieverUserConsentBroadcast(smsCodeLength = otpComposableType){ message, code ->
            otp1 = code[0].toString()
            otp2 = code[1].toString()
            otp3 = code[2].toString()
            otp4 = code[3].toString()

            if (otpComposableType != 4){
                try {
                    otp5 = code[4].toString()
                    otp6 = code[5].toString()
                    string = otp1+otp2+otp3+otp4+otp5+otp6
                    onvaluechange(string)
                }catch (e : Exception){
                    e.toString()
                }
            }else{
                string = otp1+otp2+otp3+otp4
                onvaluechange(string)
            }
        }
    }

    val (item1, item2, item3, item4, item5, item6) = remember { FocusRequester.createRefs() }
    val focusManager = LocalFocusManager.current

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        OutlinedTextField(
            modifier = Modifier
                .size(widthInDp, heightInDp)
                .focusRequester(item1)
                .onKeyEvent {
                    if (it.key.nativeKeyCode == 67) {
                        otp1 = ""
                        item1.freeFocus()
                        onvaluechange("")
                        true
                    } else {
                        false
                    }
                },
            value = otp1,
            maxLines = 1,
            singleLine = true,
            shape = RoundedCornerShape(cornerRadius),
            visualTransformation = if (passwordToggle) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Next
            ),
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, color = Color.Black, fontWeight = FontWeight.Bold, fontSize =if(conf.screenWidthDp<350){8.sp}else{14.sp}),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = focusColor,
                unfocusedBorderColor =if (otp1.length == 1) {focusColor}else{unfocusColor}
            ),
            onValueChange = {
                Log.d("DebugLengthOTP",it.length.toString())
                if (it.length > 1){
                    otp1 = it[0].toString()
                    item1.freeFocus()
                    if (it.length>2)
                        otp2 = it[1].toString()
                    item2.freeFocus()
                    if (it.length>=3)
                        otp3 = it[2].toString()
                    item3.freeFocus()
                    if (it.length>=4)
                        otp4 = it[3].toString()
                    item4.freeFocus()
                    if (it.length>=5)
                        otp5 = it[4].toString()
                    item5.freeFocus()
                    if (it.length>=6)
                        otp6 = it[5].toString()
                    item6.freeFocus()

                    onvaluechange(it)
                }else{
                    if (it.length == 1) {
                        otp1 = it[0].toString()
                        item1.freeFocus()
                        item2.requestFocus()
                    }
                }

            },
        )
        OutlinedTextField(
            modifier = Modifier
                .size(widthInDp, heightInDp)
                .focusRequester(item2)
                .background(color = backgroundColor)
                .onKeyEvent {
                    if (it.key.nativeKeyCode == 67) {
                        otp2 = ""
                        item1.requestFocus()
                        item2.freeFocus()
                        onvaluechange("")
                        true
                    } else {
                        // let other handlers receive this event
                        false
                    }
                },
            value = otp2,
            maxLines = 1,
            singleLine = true,
            shape = RoundedCornerShape(cornerRadius),
            visualTransformation = if (passwordToggle) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Next
            ),
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, color = Color.Black, fontWeight = FontWeight.Bold, fontSize =if(conf.screenWidthDp<350){8.sp}else{14.sp}),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = focusColor,
                unfocusedBorderColor =if (otp2.length == 1) {focusColor}else{unfocusColor}
            ),
            onValueChange = {
                if (it.length > 1){
                    otp1 = it[0].toString()
                    item1.freeFocus()
                    otp2 = it[1].toString()
                    item2.freeFocus()
                    if (it.length>=3)
                        otp3 = it[2].toString()
                    item3.freeFocus()
                    if (it.length>=4)
                        otp4 = it[3].toString()
                    item4.freeFocus()
                    if (it.length>=5)
                        otp5 = it[4].toString()
                    item5.freeFocus()
                    if (it.length>=6)
                        otp6 = it[5].toString()
                    item6.freeFocus()

                    onvaluechange(it)
                }else{if (it.length == 1) {
                    otp2 = it
                    item2.freeFocus()
                    item3.requestFocus()
                }}

            },
        )
        OutlinedTextField(
            modifier = Modifier
                .size(widthInDp, heightInDp)
                .focusRequester(item3)
                .background(backgroundColor)
                .onKeyEvent {
                    if (it.key.nativeKeyCode == 67) {
                        otp3 = ""
                        item2.requestFocus()
                        item3.freeFocus()
                        onvaluechange("")
                        true
                    } else {
                        // let other handlers receive this event
                        false
                    }
                },
            value = otp3,
            maxLines = 1,
            singleLine = true,
            shape = RoundedCornerShape(cornerRadius),
            visualTransformation = if (passwordToggle) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Next
            ),
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, color = Color.Black, fontWeight = FontWeight.Bold, fontSize =if(conf.screenWidthDp<350){8.sp}else{14.sp}),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = focusColor,
                unfocusedBorderColor =if (otp3.length == 1) {focusColor}else{unfocusColor}
            ),
            onValueChange = {
                if (it.length > 1){
                    otp1 = it[0].toString()
                    item1.freeFocus()
                    otp2 = it[1].toString()
                    item2.freeFocus()
                    if (it.length>=3)
                        otp3 = it[2].toString()
                    item3.freeFocus()
                    if (it.length>=4)
                        otp4 = it[3].toString()
                    item4.freeFocus()
                    if (it.length>=5)
                        otp5 = it[4].toString()
                    item5.freeFocus()
                    if (it.length>=6)
                        otp6 = it[5].toString()
                    item6.freeFocus()

                    onvaluechange(it)
                }else{if (it.length == 1) {
                    otp3 = it
                    item3.freeFocus()
                    item4.requestFocus()
                }}

            },
        )
        OutlinedTextField(
            modifier = Modifier.size(widthInDp, heightInDp)
                .focusRequester(item4)
                .background(backgroundColor)
                .onKeyEvent {
                    if (it.key.nativeKeyCode == 67) {
                        otp4 = ""
                        item3.requestFocus()
                        item4.freeFocus()
                        onvaluechange("")
                        true
                    } else {
                        // let other handlers receive this event
                        false
                    }
                },
            value = otp4,
            maxLines = 1,
            singleLine = true,
            shape = RoundedCornerShape(cornerRadius),
            visualTransformation = if (passwordToggle) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Go
            ),
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, color = Color.Black, fontWeight = FontWeight.Bold, fontSize =if(conf.screenWidthDp<350){8.sp}else{14.sp}),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = focusColor,
                unfocusedBorderColor =if (otp4.length == 1) {focusColor}else{unfocusColor}
            ),
            onValueChange = {
                if (it.length > 1){
                    otp1 = it[0].toString()
                    item1.freeFocus()
                    otp2 = it[1].toString()
                    item2.freeFocus()
                    if (it.length>=3)
                        otp3 = it[2].toString()
                    item3.freeFocus()
                    if (it.length>=4)
                        otp4 = it[3].toString()
                    item4.freeFocus()
                    if (it.length>=5)
                        otp5 = it[4].toString()
                    item5.freeFocus()
                    if (it.length>=6)
                        otp6 = it[5].toString()
                    item6.freeFocus()

                    onvaluechange(it)
                }else{if (it.length == 1) {
                    otp4 = it
                    item5.requestFocus()
                } else {
                    otp4 = ""
                }}

            },
        )

        if (otpComposableType != 4) {
            OutlinedTextField(
                modifier = Modifier
                    .size(widthInDp, heightInDp)
                    .focusRequester(item5)
                    .background(backgroundColor)
                    .onKeyEvent {
                        if (it.key.nativeKeyCode == 67) {
                            otp5 = ""
                            item4.requestFocus()
                            item5.freeFocus()
                            onvaluechange("")
                            true
                        } else {
                            // let other handlers receive this event
                            false
                        }
                    },
                value = otp5,
                maxLines = 1,
                singleLine = true,
                shape = RoundedCornerShape(cornerRadius),
                visualTransformation = if (passwordToggle) PasswordVisualTransformation() else VisualTransformation.None,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Next
                ),
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, color = Color.Black, fontWeight = FontWeight.Bold, fontSize =if(conf.screenWidthDp<350){8.sp}else{14.sp}),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = focusColor,
                    unfocusedBorderColor =if (otp5.length == 1) {focusColor}else{unfocusColor}
                ),
                onValueChange = {
                    if (it.length > 1){
                        otp1 = it[0].toString()
                        item1.freeFocus()
                        otp2 = it[1].toString()
                        item2.freeFocus()
                        if (it.length>=3)
                            otp3 = it[2].toString()
                        item3.freeFocus()
                        if (it.length>=4)
                            otp4 = it[3].toString()
                        item4.freeFocus()
                        if (it.length>=5)
                            otp5 = it[4].toString()
                        item5.freeFocus()
                        if (it.length>=6)
                            otp6 = it[5].toString()
                        item6.freeFocus()

                        onvaluechange(it)
                    }else{if (it.length == 1) {
                        otp5 = it
                        item6.requestFocus()
                    } else {
                        otp5 = ""
                    }}

                },
            )
            OutlinedTextField(
                modifier = Modifier.size(widthInDp, heightInDp)
                    .focusRequester(item6)
                    .background(backgroundColor)
                    .onKeyEvent {
                        if (it.key.nativeKeyCode == 67) {
                            otp6 = ""
                            item5.requestFocus()
                            item6.freeFocus()
                            onvaluechange("")
                            true
                        } else {
                            // let other handlers receive this event
                            false
                        }
                    },
                value = otp6,
                maxLines = 1,
                singleLine = true,
                shape = RoundedCornerShape(cornerRadius),
                visualTransformation = if (passwordToggle) PasswordVisualTransformation() else VisualTransformation.None,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Go
                ),
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, color = Color.Black, fontWeight = FontWeight.Bold, fontSize =if(conf.screenWidthDp<350){8.sp}else{14.sp}),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = focusColor,
                    unfocusedBorderColor =if (otp6.length == 1) {focusColor}else{unfocusColor}
                ),
                onValueChange = {
                    if (it.length > 1){
                        otp1 = it[0].toString()
                        item1.freeFocus()
                        otp2 = it[1].toString()
                        item2.freeFocus()
                        if (it.length>=3)
                            otp3 = it[2].toString()
                        item3.freeFocus()
                        if (it.length>=4)
                            otp4 = it[3].toString()
                        item4.freeFocus()
                        if (it.length>=5)
                            otp5 = it[4].toString()
                        item5.freeFocus()
                        if (it.length>=6)
                            otp6 = it[5].toString()
                        item6.freeFocus()

                        onvaluechange(it)
                    }else{if (it.length == 1) {
                        otp6 = it
                        string = otp1 + otp2 + otp3 + otp4 + otp5 + otp6
                        onvaluechange(string)
                        focusManager.clearFocus()
                    } else {
                        otp6 = ""
                    }}

                },
            )
        }
    }
}
@Composable
internal fun SmsRetrieverUserConsentBroadcast(
    smsCodeLength: Int,
    onSmsReceived: (message: String, code: String) -> Unit,
) {
    val context = LocalContext.current

    var shouldRegisterReceiver by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        Log.d("LogTag", "Initializing Sms Retriever client")
        com.google.android.gms.auth.api.phone.SmsRetriever.getClient(context)
            .startSmsUserConsent(null)
            .addOnSuccessListener {
                Log.d("LogTag", "SmsRetriever started successfully")
                shouldRegisterReceiver = true
            }
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK && it.data != null) {
            val message: String? = it.data!!.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
            message?.let {
                Log.d("LogTag","Sms received: $message")
                val verificationCode = getVerificationCodeFromSms(message, smsCodeLength)
                Log.d("LogTag","Verification code parsed: $verificationCode")

                onSmsReceived(message, verificationCode)
            }
            shouldRegisterReceiver = false
        } else {
            Log.d("LogTag","Consent denied. User can type OTP manually.")
        }
    }

    if (shouldRegisterReceiver) {
        SystemBroadcastReceiver(systemAction = com.google.android.gms.auth.api.phone.SmsRetriever.SMS_RETRIEVED_ACTION,
        ) { intent ->
            if (intent != null && com.google.android.gms.auth.api.phone.SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
                val extras = intent.extras

                val smsRetrieverStatus = extras?.get(com.google.android.gms.auth.api.phone.SmsRetriever.EXTRA_STATUS) as Status
                when (smsRetrieverStatus.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        val consentIntent =
                            extras.getParcelable<Intent>(com.google.android.gms.auth.api.phone.SmsRetriever.EXTRA_CONSENT_INTENT)
                        try {
                            launcher.launch(consentIntent)
                        } catch (e: ActivityNotFoundException) {
                            Log.e("LogTag", "Activity Not found for SMS consent API")
                        }
                    }

                    CommonStatusCodes.TIMEOUT -> Log.d(
                        "LogTag",
                        "Timeout in sms verification receiver"
                    )
                }
            }
        }
    }
}

@Composable
internal fun SystemBroadcastReceiver(
    systemAction: String,
    onSystemEvent: (intent: Intent?) -> Unit
) {
    val context = LocalContext.current
    val currentOnSystemEvent by rememberUpdatedState(onSystemEvent)
    DisposableEffect(context, systemAction) {
        val intentFilter = IntentFilter(systemAction)
        val broadcast = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                currentOnSystemEvent(intent)
            }
        }
        context.registerReceiver(broadcast, intentFilter)

        onDispose {
            context.unregisterReceiver(broadcast)
        }
    }
}

internal fun getVerificationCodeFromSms(sms: String, smsCodeLength: Int): String =
    sms.filter {
        it.isDigit() }
        .substring(0 until smsCodeLength)
