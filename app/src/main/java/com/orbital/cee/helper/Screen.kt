package com.orbital.cee.helper

sealed class Screen(val route: String){
    object Splash: Screen(route = "splash")
    object OnBoarding: Screen(route = "onBoarding")
    object Language: Screen(route = "language")

    object Authentication: Screen(route = "authentication")
    object SignUp: Screen(route = "signup")
    object VerifyOTP: Screen(route = "verify")
    object Home: Screen(route = "home")
    object Speed: Screen(route = "speed")
    object Menu: Screen(route = "menu")
    object Setting: Screen(route = "setting")
    object Sound: Screen(route = "sound")
    object LanguageIn: Screen(route = "languageIn")

    object LocationNotAvailable : Screen(route = "locationDisabled")
}
