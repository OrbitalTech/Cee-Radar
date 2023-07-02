package com.orbital.cee.view.home.BottomSheets

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.orbital.cee.R
import com.orbital.cee.model.Response
import com.orbital.cee.ui.theme.black
import com.orbital.cee.ui.theme.blurple
import com.orbital.cee.ui.theme.light_gray
import com.orbital.cee.ui.theme.type_gray
import com.orbital.cee.ui.theme.white
import com.orbital.cee.view.authentication.AuthenticationViewModel
import com.orbital.cee.view.authentication.FbAuthActivity


@Composable
fun LoginRequired(
    viewModel: AuthenticationViewModel = hiltViewModel(),
    onClickLoginWithGoogle:()->Unit = {},
    onClickLoginWithPhone:()->Unit = {},
    onResult:(isSuccess:Boolean,message:String)->Unit,
    bottomNavBar:Int = 0
){
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height((350+bottomNavBar).dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            )
            .padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(modifier = Modifier.padding(horizontal = 22.dp), textAlign = TextAlign.Center,text = "Create account to access all the Features", fontSize = 22.sp, fontWeight = FontWeight.SemiBold, color = black)
        Spacer(modifier = Modifier.height(34.dp))
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(
                onClick = onClickLoginWithPhone,
                indication = null,
                interactionSource = remember { MutableInteractionSource() })
            .background(color = blurple, shape = RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center){
            Text(text = "Create an Account", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = white)
        }
        Spacer(modifier = Modifier.height(34.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Divider(modifier = Modifier.weight(1f),color = light_gray,thickness = 1.dp)
            Text(modifier = Modifier.padding(horizontal = 5.dp),text = "or continue with", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = type_gray)
            Divider(modifier = Modifier.weight(1f),color = light_gray,thickness = 1.dp)
        }
        Spacer(modifier = Modifier.height(25.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clickable(
                        onClick = {
                            viewModel.oneTapSignIn()
                            onClickLoginWithGoogle()
                        },
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() })
                    .border(width = 1.dp, color = light_gray, shape = CircleShape), contentAlignment = Alignment.Center
            ){
                Icon(painter = painterResource(id = R.drawable.ic_google_circle), tint = Color.Unspecified, contentDescription = "")
            }
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clickable(
                        onClick = {
                            val navigate = Intent(context, FbAuthActivity::class.java)
                            context.startActivity(navigate)
                        },
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() })
                    .border(width = 1.dp, color = light_gray, shape = CircleShape), contentAlignment = Alignment.Center
            ){
                Icon(painter = painterResource(id = R.drawable.ic_facebook_circle), tint = Color.Unspecified, contentDescription = "")

            }
        }
    }








    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val credentials = viewModel.oneTapClient.getSignInCredentialFromIntent(result.data)
                val googleIdToken = credentials.googleIdToken
                val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)
                viewModel.signInWithGoogle(googleCredentials)
                onResult( true,"success")
//                isLoading = false
            } catch (it: ApiException) {
                onResult( false,it.message.toString())
                Log.d("ERROR_MESSAGE_GOOGLE_LOGIN",it.message.toString())
                print(it)
            }
        }else{
            onResult( false,"error")
//            isLoading = false
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
                onResult( false,it.message.toString())
                Log.d("ERROR_MESSAGE_GOOGLE_LOGIN",it.message.toString())
//                errorMessage = it.message.toString()
//                showErrorDialog = true
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
//                    val navigate = Intent(context, HomeActivity::class.java)
//                    context.startActivity(navigate)
                    onResult( true,"success")
//                    (context as Activity).recreate()
                }
            }
        }
        is Response.Error -> signInWithGoogleResponse.e?.let {
            LaunchedEffect(Unit) {
                onResult( false,it.message.toString())
                Log.d("ERROR_MESSAGE_GOOGLE_LOGIN",it.message.toString())
//                errorMessage = it.message.toString()
//                showErrorDialog = true
                print(it)
            }
        }
    }

    when(val createUserResponse = viewModel.createUserResponse) {
        is Response.Loading -> {}
        is Response.Success -> createUserResponse.data?.let { isUserCreated ->
            if (isUserCreated) {
                LaunchedEffect(Unit) {
                    onResult( true,"success")
//                    val navigate = Intent(context, HomeActivity::class.java)
//                    (context as Activity).recreate()
                }
            }
        }
        is Response.Error -> createUserResponse.e?.let {
            LaunchedEffect(Unit) {
                onResult( false,it.message.toString())
                Log.d("ERROR_MESSAGE_GOOGLE_LOGIN",it.message.toString())
//                errorMessage = it.message.toString()
//                showErrorDialog = true
                print(it)
            }
        }
    }





}