package com.orbital.cee.view.home.appMenu.componenets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbital.cee.R
import com.orbital.cee.ui.theme.black
import com.orbital.cee.ui.theme.blurple
import com.orbital.cee.ui.theme.red
import com.orbital.cee.ui.theme.white

@Composable
fun CursorCarousel(
    cursorId :String,
    ownCursors : List<String?>?,
    onClickUnlockHitexCursor:()->Unit,
    onSelectedCursor:(id:String)->Unit
){
    val selectedCursorId = remember { mutableStateOf(cursorId) }
    val scrollState = rememberScrollState()
    val sliderList = listOf(
        CursorCardModel(
            id = "Original",
            image = R.drawable.ic_default_cursor,
            name = "Cee Basic",
            selected = true,
            locked = false,
            onClick = {}
        ),
        CursorCardModel(
            id = "HITEX",
            image = R.drawable.ic_hitex_cursor,
            name = "Cee Hitex",
            selected = false,
            locked = true,
            onClick = {}
        ),
    )
    fun isOwn(id: String) : String?{
        return ownCursors?.find { report ->
            report == id
        }
    }
    if (isOwn(cursorId) == null){
        selectedCursorId.value = "Original"
        onSelectedCursor("Original")
    }

    Column(Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(scrollState)) {
            sliderList.forEach { speedo->
                Box(
                    modifier = Modifier
                        .width(164.dp)
                        .height(171.dp)
                        .padding(end = 10.dp)
                        .border(
                            width = 1.dp, color = if (selectedCursorId.value == speedo.id) {
                                blurple
                            } else {
                                Color(0xFFF2F2F2)
                            }, shape = RoundedCornerShape(22.dp)
                        )
                        .clickable(
                            onClick = {
                                if (isOwn(speedo.id) == null) {
                                    if (speedo.id == "HITEX"){
                                        onClickUnlockHitexCursor()
                                    }
                                } else {
                                    onSelectedCursor(speedo.id)
                                    selectedCursorId.value = speedo.id
                                } },
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        )
                        .background(color = white, shape = RoundedCornerShape(22.dp))
                ){
                    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                        Box(modifier = Modifier
                            .size(80.dp)
                            .padding(start = 16.dp, end = 16.dp, top = 19.dp), contentAlignment = Alignment.Center){
                            Icon(modifier = Modifier.fillMaxSize(),painter = painterResource(id = speedo.image), contentDescription = "", tint = Color.Unspecified )
                        }
                        Column(modifier = Modifier
                            .padding(bottom = 14.dp, start = 14.dp)
                            .fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                            Text(text = speedo.name, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = black)
                            Spacer(modifier = Modifier.height(5.dp))
                            if (isOwn(speedo.id) == null){
                                if (speedo.id == "HITEX"){
                                    Text(text = "Try it free", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = red)
                                }else{
                                    Text(text = "Unlock", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = red)
                                }
                            }else if(selectedCursorId.value == speedo.id){
                                Text(text = "Selected", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = blurple)
                            } else{
                                Text(text = "Unlocked", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = blurple)
                            }
                        }
                    }
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp), contentAlignment = Alignment.TopEnd){
                        Column() {
                            AnimatedVisibility(visible = selectedCursorId.value == speedo.id, enter = fadeIn(), exit = fadeOut()) {
                                Icon(painter = painterResource(id = R.drawable.ic_tick_circle), contentDescription = "", tint = blurple)
                            }
                        }
                    }
                }
            }
        }
    }
}

data class CursorCardModel(
    val id:String,
    val image:Int,
    val name: String,
    val selected:Boolean,
    val locked : Boolean,
    val onClick: (id:String) -> Unit
)