package com.orbital.cee.view.home.appMenu.componenets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun radio (isChecked : Boolean,errorColor:Color = Color(0XFF707070)){
    Box(
        modifier = Modifier
            .size(22.dp)
            .background(
                color = Color(0XFFFFFFFF),
                shape = RoundedCornerShape(100.dp)
            )
            .border(
                border = BorderStroke(
                    width = 2.5.dp,
                    color = if(isChecked) Color(0XFF495CE8)  else  errorColor
                ),
                shape = RoundedCornerShape(100.dp)
            ),
        contentAlignment = Alignment.Center
    ){
        if(isChecked){
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        color = Color(0XFF495CE8),
                        shape = RoundedCornerShape(100.dp)
                    )
            )
        }

    }
}