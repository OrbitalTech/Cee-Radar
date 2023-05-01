package com.orbital.cee.view.trip

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbital.cee.R
import com.orbital.cee.core.Constants
import com.orbital.cee.helper.DBHandler
import com.orbital.cee.helper.TripModel
import com.orbital.cee.model.Trip
import com.orbital.cee.view.home.HomeViewModel
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@Composable
fun TripHistory( onClickBack:()->Unit, tripList : ArrayList<Trip?>?, onDeleteTrip:(trip:Trip)->Unit) {
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.DOWN
    var isShowTripDetail by remember { mutableStateOf(false) }
    var _trip by remember { mutableStateOf(Trip()) }
//    var context = LocalContext.current
//    lateinit var courseList: List<TripModel>
//    courseList = ArrayList<TripModel>()
//    val dbHandler: DBHandler = DBHandler(context)
//    courseList = dbHandler.readTrips()!!
    
    val rotate = if (LocalConfiguration.current.layoutDirection == LayoutDirection.Rtl.ordinal){180f}else{0f}
    val li = tripList?.asReversed()
    Box(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectDragGestures(onDrag = { point, offset ->
                if (offset.x > Constants.OFFSET_X && point.position.x < Constants.POINT_X) {
                    onClickBack.invoke()
                }
            })
        }) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .border(
                        width = 1.dp,
                        color = Color(0XFFE4E4E4),
                    )
                    .padding(bottom = 8.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Text(text = stringResource(id = R.string.lbl_history_appBar_title), fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 20.sp)
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomStart){
                    IconButton(onClick = onClickBack, modifier = Modifier.padding(end = 12.dp)) {
                        Icon(modifier = Modifier
                            .size(22.dp)
                            .rotate(rotate), tint = Color(0xFF848484) , painter = painterResource(id = R.drawable.ic_arrow_back), contentDescription ="" )
                    }
                }
            }
            Column(modifier = Modifier
                .padding(12.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
            ) {
                if (li != null) {
                    if(li.size<=1){
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp), contentAlignment = Alignment.Center){
                            Text(text = "Your trip list is empty !!")
                        }
                    }else{
                        li.forEach{ trip->
                            if (trip != null) {
                                if (trip.startTime != null){
                                    Row (modifier = Modifier
                                        .fillMaxSize()
                                        .height(65.dp)
                                        .pointerInput(Unit) {
                                            detectTapGestures(onTap = {
                                                _trip = trip
                                                isShowTripDetail = true
                                            })
                                            _trip = Trip()
                                        }
                                        .background(
                                            color = Color(0XFFF7F7F7),
                                            shape = RoundedCornerShape(10.dp)
                                        ), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically
                                    ){
                                        Box(modifier = Modifier.size(60.dp),contentAlignment = Alignment.Center) {
                                            Icon(modifier = Modifier.size(32.dp), tint = Color.Unspecified , painter = painterResource(id = R.drawable.ic_trip), contentDescription ="" )
                                        }
                                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)
                                        Column(modifier = Modifier
                                            .height(60.dp)
                                            .padding(vertical = 10.dp)
                                            .fillMaxWidth(0.8f), verticalArrangement = Arrangement.SpaceBetween) {
                                            Text(text = "${stringResource(R.string.lbl_trip_history_detail_appBar_title)} ${trip.startTime?.let { sdf.format(it) }}", maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Bold)
                                            Text(text = "${df.format(trip.distance)} km | ${df.format(trip.speedAverage)} km/h", fontSize = 12.sp, color = Color(0XFF848484))
                                        }
                                        Box(modifier = Modifier.size(60.dp), contentAlignment = Alignment.Center) {
                                            Icon(modifier = Modifier
                                                .size(22.dp)
                                                .rotate(rotate), tint = Color(0XFF848484) , painter = painterResource(id = R.drawable.ic_arrow), contentDescription ="" )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
        AnimatedVisibility(visible = isShowTripDetail, enter = fadeIn(), exit = fadeOut(), modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)) {
            TripDetail(trip = _trip,onClickBack = {isShowTripDetail = false},onClickDelete = {
                onDeleteTrip(_trip).let {
                    isShowTripDetail = false
                }

            })
        }

    }
}