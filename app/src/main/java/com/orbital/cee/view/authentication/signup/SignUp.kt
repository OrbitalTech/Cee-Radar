package com.orbital.cee.view.authentication

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.orbital.cee.R
import com.orbital.cee.view.authentication.component.DisplayResponseMessage
import com.orbital.cee.view.home.HomeActivity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Composable
fun SignUp (navController: NavController,
            viewModel: AuthenticationViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val fullName = remember { mutableStateOf("") }
    Scaffold(backgroundColor = MaterialTheme.colors.primary,) {_->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DisplayResponseMessage(viewModel)
            Spacer(modifier = Modifier.height(80.dp))
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(470.dp)
                .clip(shape = RoundedCornerShape(20.dp))
                .background(color = Color.White,)
            ){
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.Start) {
                        Icon(painter = painterResource(id =R.drawable.ic_arrow_back),
                            modifier = Modifier.size(18.dp).clickable {navController.popBackStack()},
                            contentDescription = "",
                            tint = Color(0xFFA7A7A7)
                        )
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(text = "Username" ,color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "To continue  please enter your name", textAlign = TextAlign.Center, fontSize = 12.sp,color = Color.Black, modifier = Modifier.width(220.dp))
                    Spacer(modifier = Modifier.height(25.dp))
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .border(
                                BorderStroke(2.dp, Color(0xFFE4E4E4)),
                                shape = RoundedCornerShape(10.dp)
                            ),
                        value = fullName.value,
                        placeholder = {
                            Text(text = "Full Name")
                        },
                        onValueChange = {fullName.value = it},
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        shape = RoundedCornerShape(10.dp),
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(text = "By verifying you’re agreeing on out term & conditions.", fontSize = 8.sp,color = Color.Gray)
                    Spacer(modifier = Modifier.height(30.dp))
                    if (isLoading){
                        CircularProgressIndicator()
                    }else{
                        Button(onClick = {
                            isLoading = true
                            coroutineScope.launch {
                                viewModel.register("","").collect{
                                    if (it.isSuccess){
                                        val navigate = Intent(context, HomeActivity::class.java)
                                        context.startActivity(navigate)
                                    }else{
                                        Toast.makeText(context, it.serverMessage, Toast.LENGTH_LONG).show()
                                    }
                                }
                                //updateScreen(viewModel,navController,context)
                                isLoading = false
                            }
                        }, modifier = Modifier
                            .width(200.dp)
                            .height(50.dp), shape = RoundedCornerShape(10.dp)) {
                            Text(text = "Continue", color = Color.White)
                        }
                    }

                }
            }
        }
    }
}
