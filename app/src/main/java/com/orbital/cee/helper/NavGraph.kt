package com.orbital.cee.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.orbital.cee.helper.Screen
import com.orbital.cee.view.authentication.Authentication
import com.orbital.cee.view.authentication.SignUp
import com.orbital.cee.view.authentication.verifyOTP.VerifyOTP
import com.orbital.cee.view.home.HomeViewModel
import com.orbital.cee.view.onBoarding.OnBoarding
import com.orbital.cee.view.selectLanguage.Language
import com.orbital.cee.view.splash.Splash

@RequiresApi(Build.VERSION_CODES.S)
@ExperimentalAnimationApi
@Composable
fun NavGraph(
    model: HomeViewModel ,
    //authModel: AuthenticationViewModel
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ){
        composable(
            route = Screen.Splash.route
        ){
            Splash(navController = navController,model)
        }
        composable(
            route = Screen.OnBoarding.route
        ){
            OnBoarding(navController = navController,model)
        }
        composable(
            route = Screen.Language.route
        ){
            Language(navController = navController,model)
        }
        composable(
            route = Screen.Authentication.route
        ){
            Authentication(navController = navController)
        }
        composable(
            route = Screen.SignUp.route
        ){
            SignUp(navController = navController)
        }
//        composable(
//            route = Screen.VerifyOTP.route
//        ){
//            VerifyOTP(navController = navController,authModel)
//        }
        composable(
            route = Screen.VerifyOTP.route + "/{cCode}/{phoneNumber}",
//            arguments = listOf(
//                navArgument("cCode"){
//                type = NavType.StringType
//            },
//                navArgument("phoneNumber"){
//                type = NavType.StringType
//            }
//        )
        ){
            var vOtp = it.arguments?.getString("cCode").toString()
            var phoneNumber = it.arguments?.getString("phoneNumber").toString()
            VerifyOTP(navController = navController,countryCode = vOtp, phoneNumber = phoneNumber)
        }
        composable(
            route = Screen.Speed.route
        ){
            //Speed(navController)
        }
    }
}