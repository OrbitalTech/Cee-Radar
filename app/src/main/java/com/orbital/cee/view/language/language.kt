package com.orbital.cee.view.language

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.LocaleList
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbital.cee.R
import com.orbital.cee.view.home.HomeActivity
import com.orbital.cee.view.home.HomeViewModel
import com.orbital.cee.view.home.Menu.componenets.radio
import java.util.*

@Composable
fun language(model:HomeViewModel,onClickBack:()->Unit) {
    val configuration = LocalConfiguration.current
    val resources = LocalContext.current.resources
    val context = LocalContext.current
    val rotate = if (LocalConfiguration.current.layoutDirection == LayoutDirection.Rtl.ordinal){180f}else{0f}
//    LaunchedEffect(Unit){
//        Toast.makeText(context,configuration.locale.language.toString(),Toast.LENGTH_LONG).show()
//    }
    var langCode = remember {
        mutableStateOf(configuration.locale.language)
    }
    Column(modifier = Modifier
        .fillMaxSize()
        .background(color = Color.White)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(85.dp)
                .clip(shape = RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp))
                .border(
                    width = 1.dp,
                    color = Color(0XFFE4E4E4),
                    shape = RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = stringResource(R.string.lbl_language_setting_appBar_title) , fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 20.sp)
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart){
                TextButton(onClick = onClickBack, modifier = Modifier.padding(end = 12.dp)) {
                    Icon(modifier = Modifier.size(22.dp).rotate(rotate), tint = Color.Black , painter = painterResource(id = R.drawable.ic_arrow_back), contentDescription ="" )
                }
            }
        }
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(bottom = 25.dp), Arrangement.SpaceBetween) {
            Column() {
                Row(
                    modifier = Modifier
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = {
                                langCode.value = "en"
                            })
                        }
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(65.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    radio(langCode.value == "en")
                    Spacer(modifier = Modifier.width(15.dp))
                    Text(text = stringResource(R.string.btn_english_lang), fontSize = 20.sp, fontWeight = if (langCode.value == "en") FontWeight.Bold else FontWeight.Normal)

                }
                Divider(
                    color = Color(0XFFE4E4E4),
                    thickness = 1.dp
                )
                Row(
                    modifier = Modifier
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = {
                                langCode.value = "ar"
                            })
                        }
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(65.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    radio(langCode.value == "ar")
                    Spacer(modifier = Modifier.width(15.dp))
                    Text(text = stringResource(R.string.btn_arabic_lang), fontSize = 20.sp, fontWeight = if (langCode.value == "ar") FontWeight.Bold else FontWeight.Normal)

                }

                Divider(
                    color = Color(0XFFE4E4E4),
                    thickness = 1.dp
                )
                Row(
                    modifier = Modifier
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = {
                                langCode.value = "ku"
                            })
                        }
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(65.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    radio(langCode.value == "ku")
                    Spacer(modifier = Modifier.width(15.dp))
                    Text(text =stringResource(R.string.btn_kurdish_so_lang), fontSize = 20.sp, fontWeight = if (langCode.value == "ku") FontWeight.Bold else FontWeight.Normal)

                }
                Divider(
                    color = Color(0XFFE4E4E4),
                    thickness = 1.dp
                )
                Row(
                    modifier = Modifier
                        .pointerInput(Unit){
                            detectTapGestures(onTap = {
                                langCode.value = "tr"
                            })
                        }
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(65.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    radio(langCode.value == "tr")
                    Spacer(modifier = Modifier.width(15.dp))
                    Text(text = "Turkish", fontSize = 20.sp, fontWeight = if (langCode.value == "tr") FontWeight.Bold else FontWeight.Normal)

                }
                Divider(
                    color = Color(0XFFE4E4E4),
                    thickness = 1.dp
                )
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 40.dp)
                .height(50.dp)) {
                Button(onClick = {
                    val locale = Locale(langCode.value)

                    val config = context.resources.configuration
                    config.apply {
                        config.setLayoutDirection(locale)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            config.setLocale(locale)
                            val localeList = LocaleList(locale)
                            LocaleList.setDefault(localeList)
                            config.setLocales(localeList)
                        } else {
                            config.setLocale(locale)
                        }
                    }

                    //context.createConfigurationContext(configuration)
                    if (langCode.value == "ku"){
                        config.setLayoutDirection(Locale("ar"))
                    }
                    resources.updateConfiguration(config, resources.displayMetrics)
                    model.saveLanguageCode(langCode.value)
                    context.startActivity(Intent(Intent.ACTION_VIEW, null, context, HomeActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    })
                    onClickBack.invoke()
                }, modifier = Modifier
                    .fillMaxSize(), shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.buttonColors(backgroundColor = Color(0XFF495CE8))) {
                    Text(text = stringResource(id = R.string.btn_home_alert_save), color = Color.White)
                }

            }
        }

    }

}