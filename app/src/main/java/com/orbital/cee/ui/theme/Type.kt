package com.orbital.cee.ui.theme

import android.app.Application
import androidx.compose.material.Typography
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.orbital.cee.R
import dagger.hilt.android.qualifiers.ApplicationContext

val Bahij = FontFamily(
    Font(R.font.bahij_light),
    Font(R.font.bahij_the_sans_arabic_black,FontWeight.Black),
    Font(R.font.bahij_the_sans_arabic_bold,FontWeight.Bold),
    Font(R.font.bahij_the_sans_arabic_semi_bold,FontWeight.SemiBold),
)
val Work = FontFamily(
    Font(R.font.work_regular),
    Font(R.font.work_bold,FontWeight.Bold),
    Font(R.font.work_sans_semi_bold,FontWeight.SemiBold),
    Font(R.font.work_sans_medium,FontWeight.Medium),
)
val bTypography = Typography(
    body1 = TextStyle(
        fontFamily =Bahij,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    ),
    body2 = TextStyle(
        fontFamily =Bahij,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
    ),
    h1 = TextStyle(
        fontFamily = Bahij,
        fontWeight = FontWeight.Black,
        fontSize = 28.sp
    ),
    h2 = TextStyle(
        fontFamily = Bahij,
        fontWeight = FontWeight.Black,
        fontSize = 20.sp
    ),
    h3 = TextStyle(
        fontFamily =Bahij,
        fontWeight = FontWeight.Black,
        fontSize = 18.sp
    ),
    //tabs1
    h4 = TextStyle(
        fontFamily = Bahij,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    ),
    //body3
    h5 = TextStyle(
        fontFamily = Bahij,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp
    ),
    //body4
    h6 = TextStyle(
        fontFamily = Bahij,
        fontWeight = FontWeight.Bold,
        fontSize = 8.sp
    ),
    subtitle1 = TextStyle(
        fontFamily = Bahij,
        fontWeight = FontWeight.Medium,
        fontSize = 6.sp
    ),
    button = TextStyle(
        fontFamily = Bahij,
        fontWeight = FontWeight.Black,
        fontSize = 14.sp
    ),
)
val wTypography = Typography(
    body1 = TextStyle(
        fontFamily = Work,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    body2 = TextStyle(
        fontFamily =Work,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    ),
    h1 = TextStyle(
        fontFamily = Work,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp
    ),
    h2 = TextStyle(
        fontFamily = Work,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    ),
    h3 = TextStyle(
        fontFamily =Work,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    ),
    //tabs1
    h4 = TextStyle(
        fontFamily = Work,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    ),
    //body3
    h5 = TextStyle(
        fontFamily = Work,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp
    ),
    //body4
    h6 = TextStyle(
        fontFamily = Work,
        fontWeight = FontWeight.Medium,
        fontSize = 8.sp
    ),
    subtitle1 = TextStyle(
        fontFamily = Work,
        fontWeight = FontWeight.Medium,
        fontSize = 6.sp
    ),
    button = TextStyle(
        fontFamily = Work,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    ),
)