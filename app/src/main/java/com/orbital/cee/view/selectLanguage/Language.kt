package com.orbital.cee.view.selectLanguage

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.orbital.cee.R
import com.orbital.cee.view.home.HomeViewModel
import com.orbital.cee.view.splash.component.RelaxCee
import java.util.*

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun Language(navController: NavController,model: HomeViewModel) {
    val configuration = LocalConfiguration.current
    val resources = LocalContext.current.resources


    val composition by rememberLottieComposition(
        LottieCompositionSpec
            .RawRes(R.raw.lottie_speedface_30to60km)
    )

    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true,
        speed = 1f,
        restartOnPlay = false
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colors.primary
            ),
        contentAlignment = Alignment.TopCenter){
        Column(modifier = Modifier.fillMaxSize(),horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {

//            LottieAnimation(
//                composition,
//                progress,
//                modifier = Modifier
//                    .size(150.dp)
//            )
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .background(color = MaterialTheme.colors.primary),
                contentAlignment = Alignment.Center){
                Icon(modifier = Modifier.fillMaxSize(),painter = painterResource(id = R.drawable.ic_cee_select_lang), contentDescription = "", tint = Color.Unspecified, )
            }
            Spacer(modifier = Modifier.height(height = 90.dp))
            Button(
                onClick = {
                    val locale = Locale("en")
                    configuration.setLocale(locale)
                    resources.updateConfiguration(configuration, resources.displayMetrics)
                    model.saveLanguageCode("en")
                    navController.navigate("onBoarding")
                          },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.width(250.dp).height(60.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)) {
                Text(text = "English",
                    color = Color.Black,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily(Font(R.font.work_sans_bold))
                    )
            }
            Spacer(modifier = Modifier.height(height = 10.dp))
//            Button(
//                onClick = {
//                    val locale = Locale("tr")
//                    configuration.setLocale(locale)
//                    resources.updateConfiguration(configuration, resources.displayMetrics)
////
//                    navController.navigate("onBoarding") },
//                shape = RoundedCornerShape(10.dp),
//                modifier = Modifier.width(250.dp).height(55.dp),
//                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)) {
//                Text(text = "Turkish",style = MaterialTheme.typography.button)
//            }
//            Spacer(modifier = Modifier.height(height = 10.dp))
            Button(
                onClick = {

                    val locale = Locale("ar")
                    configuration.setLocale(locale)
                    resources.updateConfiguration(configuration, resources.displayMetrics)
                    model.saveLanguageCode("ar")
                    navController.navigate("onBoarding")
                          },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.width(250.dp).height(60.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)) {
                    Text(text = "عربي",
                        color = Color.Black,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily(Font(R.font.bahij_the_sans_arabic_extra_bold))
                    )
            }
            Spacer(modifier = Modifier.height(height = 10.dp))
            Button(
                onClick = {
                    val locale = Locale("ku")
                    configuration.setLocale(locale)
                    resources.updateConfiguration(configuration, resources.displayMetrics)
                    model.saveLanguageCode("ku")
                    navController.navigate("onBoarding")
                          },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.width(250.dp).height(60.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)) {
                Text(text = "کوردی سۆرانی",
                    color = Color.Black,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily(Font(R.font.bahij_the_sans_arabic_extra_bold))
                )
            }
            Spacer(modifier = Modifier.height(height = 10.dp))
            Button(
                onClick = {
                    val locale = Locale("tr")
                    configuration.setLocale(locale)
                    resources.updateConfiguration(configuration, resources.displayMetrics)
                    model.saveLanguageCode("tr")
                    navController.navigate("onBoarding")
                          },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.width(250.dp).height(60.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)) {
                Text(text = "Turkish",
                    color = Color.Black,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily(Font(R.font.work_sans_bold))
                )
            }

        }
    }
}

//class DataStoreViewModelFactory(private val dataStorePreferenceRepository: DataStorePreferenceRepository):
//    ViewModelProvider.Factory {
//
//    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(LanguageViewModel::class.java)) {
//            return LanguageViewModel(dataStorePreferenceRepository) as T
//        }
//        throw IllegalStateException()
//    }
//}