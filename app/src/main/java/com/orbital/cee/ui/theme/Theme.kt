package com.orbital.cee.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable


private val DarkColorPalette = darkColors(
    background = d_ui_bg,
    primary = blurple,
    onPrimary = dark_purple,
    secondary = d_type_gray,
    error = red,
    onError = light_red,
    primaryVariant = green,
    secondaryVariant = light_green,
    surface = d_gray_fill,
)

private val LightColorPalette = lightColors(
    background = white,
    primary = blurple,
    onPrimary = light_purple,
    secondary = type_gray,
    onSecondary = gray_fill,
    error = red,
    onError = light_red,
    primaryVariant = green,
    secondaryVariant = light_green,
    surface = white,
)

@Composable
fun CEETheme(darkTheme: Boolean = false,langCode:()->String, content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography =if(langCode() == "en"){wTypography}else{bTypography} ,
        shapes = Shapes,
        content = content
    )
}
