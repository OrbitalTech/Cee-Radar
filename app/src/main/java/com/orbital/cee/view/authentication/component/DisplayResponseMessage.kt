package com.orbital.cee.view.authentication.component

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.orbital.cee.view.authentication.AuthenticationViewModel

@Composable
fun DisplayResponseMessage(vm : AuthenticationViewModel) {
    val notificationStatus = vm.responseMessage.value
    val notificationMessage = notificationStatus?.getContentOrNull()
    if(notificationMessage != null){
        Toast.makeText(LocalContext.current,notificationMessage,Toast.LENGTH_LONG).show()
    }

}