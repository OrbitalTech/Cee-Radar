package com.orbital.cee.view.authentication

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.FragmentActivity
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.Timestamp
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.orbital.cee.R
import com.orbital.cee.core.Constants
import com.orbital.cee.ui.theme.CEETheme
import com.orbital.cee.view.MainActivity
import com.orbital.cee.view.home.HomeActivity
import org.json.JSONObject
import java.util.HashMap

val LocalFacebookCallbackManager =
    staticCompositionLocalOf<CallbackManager> { error("No CallbackManager provided") }

class FbAuthActivity : FragmentActivity() {
    private var callbackManager = CallbackManager.Factory.create()
    var langCode = mutableStateOf("en")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.fullyInitialize()
        AppEventsLogger.activateApp(application)
        setContent {
            CEETheme(langCode=langCode.value) {
                CompositionLocalProvider(
                    LocalFacebookCallbackManager provides callbackManager
                ) {
                    LoginScreen()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}

@Composable
fun LoginScreen() {
    val callbackManager = LocalFacebookCallbackManager.current
    val mAuth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    Box(modifier = Modifier.fillMaxSize().background(color = Color(0xFF495CE8))) {
        if (showErrorDialog) {
            ShowErrorDialog(
                onOkayClick = {
                    showErrorDialog = false
                    val navigate = Intent(context, MainActivity::class.java)
                    context.startActivity(navigate)
                },
                onDismiss = {
                    showErrorDialog = true
                },
                title = "Authentication canceled.",
                message = errorMessage
            )
        }

    }
    DisposableEffect(Unit) {
        LoginManager.getInstance().registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                @RequiresApi(Build.VERSION_CODES.S)
                override fun onSuccess(loginResult: LoginResult) {
                    var username =""
                    var email = ""
                    var gender = ""
                    var picture: JSONObject? = null
                    var purl = ""
                    val graphRequest = GraphRequest.newMeRequest(loginResult.accessToken){obj,response ->
                        try {
                            if (obj?.has("id") == true){
                                username = obj.getString("name")
//                                email = obj.getString("email")
                                //gender = if(obj.getString("gender") == "male"){"M"}else{"F"}
                                picture = obj.getJSONObject("picture")
                                picture = picture!!.getJSONObject("data")
                                purl = picture!!.getString("url")
                                purl= purl.replace("\'","")


                                val authCre = FacebookAuthProvider.getCredential(loginResult.accessToken.token)
                                mAuth.signInWithCredential(authCre).addOnCompleteListener{task->
                                    if (task.isSuccessful){
                                        val fbUser = mAuth.currentUser
                                        if (fbUser != null){
                                            if (fbUser.photoUrl != null){
                                                val id = fbUser.uid
                                                val document = db.collection(Constants.DB_REF_USER).document(id)
                                                val user : HashMap<String, Any> = HashMap<String, Any>()
                                                user["countryCode"] = ""
                                                user["isUserBanned"] = false
                                                user["joiningTimeStamp"] = Timestamp.now()
                                                user["lastSeen"] = Timestamp.now()
                                                user["phoneNumber"] = ""
                                                user["userCoin"] = 0
                                                user["pushId"] = ""
                                                user["status"] = "online"
                                                user["subscriptionPlan"] = "Free"
                                                user["userAvatar"] = purl
                                                user["userLoginCount"] = 1
                                                user["userContribution"] = 0
                                                user["userId"] = id
                                                user["registrationMethod"] = "Facebook"
                                                user["userEmail"] = email
                                                user["userGender"] = gender
                                                user["username"] = username

                                                document.set(user).addOnSuccessListener {
                                                    val navigate = Intent(context, HomeActivity::class.java)
                                                    context.startActivity(navigate)
                                                }
                                            }
                                        }else{
                                            Toast.makeText(context,"Error: user is empty.",Toast.LENGTH_LONG).show()
                                        }
                                    }else{
                                        Toast.makeText(context,"Error: ${task.exception?.message}",Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        }catch (e:Exception){
                            Toast.makeText(context,"Error: ${e.message}",Toast.LENGTH_LONG).show()
                        }
                    }
                    val param = Bundle()
                    param.putString("fields","name,id,gender,picture.type(large)")
                    graphRequest.parameters = param
                    graphRequest.executeAsync()
                }


                override fun onCancel() {
                    println("onCancel")
                    showErrorDialog = true
                    errorMessage = "Error: Authentication canceled."
                }

                override fun onError(error: FacebookException) {
                    println("onError $error")
                    showErrorDialog = true
                    errorMessage = error.message.toString()
                    Toast.makeText(context,"Error: ${error.message}",Toast.LENGTH_LONG).show()
                    Log.d("onSuccess","Error: ${error.message}")
                }
            }
        )
        onDispose {
            LoginManager.getInstance().unregisterCallback(callbackManager)
        }

    }
    LaunchedEffect(Unit){
        context.findActivity()?.let {
            LoginManager.getInstance()
                .logInWithReadPermissions(it, listOf("public_profile"))
        }
    }
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}