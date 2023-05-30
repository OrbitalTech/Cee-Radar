package com.orbital.cee.view.home.components

import android.content.Context
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.orbital.cee.R
import com.orbital.cee.model.NewReport
import com.orbital.cee.view.home.HomeViewModel
import com.orbital.cee.view.trip.advancedShadow
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun SheetNearestIncident(list: MutableIterator<NewReport>,currentLocation : Location,onClickIncidents: (geoPoint:GeoPoint) -> Unit) {
    val context = LocalContext.current
    var repoLi : ArrayList<NewReport> = arrayListOf()
    var  iterator : MutableIterator<NewReport>? = null
    while(list.hasNext()){
        repoLi.add(list.next())
    }
    repoLi.sortBy {
        val thisIncLocation = Location("")
        thisIncLocation.latitude = it.geoLocation?.get(0)!! as Double
        thisIncLocation.longitude = it.geoLocation?.get(1)!! as Double
        currentLocation.distanceTo(thisIncLocation)
    }
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.Top) {

        repoLi.forEach{report->
            if(report.reportByUID != null){
                val thisIncLocation = Location("")
                thisIncLocation.latitude = report.geoLocation?.get(0)!! as Double
                thisIncLocation.longitude = report.geoLocation?.get(1)!! as Double
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(66.dp)
                        .background(color = Color(0xFFF7F7F7), shape = RoundedCornerShape(10.dp))
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        .clickable {
                            onClickIncidents(
                                GeoPoint(
                                    report.geoLocation?.get(0)!! as Double,
                                    report.geoLocation?.get(1)!! as Double
                                )
                            )
                        },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(color = Color.White, shape = RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ){
                        Icon(modifier = Modifier.size(25.dp),painter = painterResource(
                            id = when(report.reportType){
                                1->R.drawable.ic_camera_fab
                                2->R.drawable.ic_car_crash
                                3->R.drawable.ic_police
                                4->R.drawable.ic_construction
                                5->R.drawable.ic_static_camera
                                6->R.drawable.ic_point_to_point_camera
                                7-> R.drawable.ic_trafic_light
                                10-> R.drawable.ic_static_camera
                                11-> R.drawable.ic_point_to_point_camera
                                405-> R.drawable.ic_static_camera
                                else -> {R.drawable.ic_camera_fab}
                            }),contentDescription = "", tint = Color(0xFF495CE8))
                    }
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 20.dp)) {

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(text =incidentDistance(currentLocation.distanceTo(thisIncLocation)) , color = Color(0xFFAAAAAA))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(modifier = Modifier.size(15.dp),painter = painterResource(id = R.drawable.ic_clock), tint = Color(0xFF848484), contentDescription = "")
                                Spacer(modifier = Modifier.width(1.dp))
                                Text(maxLines = 1, overflow = TextOverflow.Ellipsis,text = incidentTime(report.reportTimeStamp!!,context)
                                    ,color = Color(0xFFAAAAAA), textAlign = TextAlign.Center)

                            }

                        }
                        report.reportAddress?.let { Text(modifier = Modifier.fillMaxWidth(0.8f),text = it, fontWeight = FontWeight.W500, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
//                list.remove()
            }
        }
//        while(repoLi){
//            val incidents = list.next()
//
//
//        }
//        repoLi.forEachIndexed{ _, incidents ->
//            val thisIncLocation = Location("")
//            thisIncLocation.latitude = incidents.geoLocation?.get(0)!! as Double
//            thisIncLocation.longitude = incidents.geoLocation?.get(1)!! as Double
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(66.dp)
//                    .background(color = Color(0xFFF7F7F7), shape = RoundedCornerShape(10.dp))
//                    .padding(horizontal = 20.dp, vertical = 10.dp)
//                    .clickable {
//                        onClickIncidents(
//                            GeoPoint(
//                                incidents.geoLocation?.get(0)!! as Double,
//                                incidents.geoLocation?.get(1)!! as Double
//                            )
//                        )
//                    },
//                horizontalArrangement = Arrangement.Center,
//                verticalAlignment = Alignment.CenterVertically
//
//            ) {
//                Box(
//                    modifier = Modifier
//                        .size(40.dp)
//                        .background(color = Color.White, shape = RoundedCornerShape(12.dp)),
//                    contentAlignment = Alignment.Center
//                ){
//                    Icon(modifier = Modifier.size(25.dp),painter = painterResource(
//                        id = when(incidents.reportType){
//                            1->R.drawable.ic_camera_fab
//                            2->R.drawable.ic_car_crash
//                            3->R.drawable.ic_police
//                            4->R.drawable.ic_construction
//                            5->R.drawable.ic_static_camera
//                            6->R.drawable.ic_point_to_point_camera
//                            7-> R.drawable.ic_trafic_light
//                            10-> R.drawable.ic_static_camera
//                            11-> R.drawable.ic_point_to_point_camera
//                            405-> R.drawable.ic_static_camera
//                            else -> {R.drawable.ic_camera_fab}
//                        }),contentDescription = "", tint = Color(0xFF495CE8))
//                }
//                Column(modifier = Modifier
//                    .fillMaxSize()
//                    .padding(start = 20.dp)) {
//
//                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
//                        Text(text =incidentDistance(model.lastLocation.value.distanceTo(thisIncLocation)) , color = Color(0xFFAAAAAA))
//                        Row(verticalAlignment = Alignment.CenterVertically) {
//                            Icon(modifier = Modifier.size(15.dp),painter = painterResource(id = R.drawable.ic_clock), tint = Color(0xFF848484), contentDescription = "")
//                            Spacer(modifier = Modifier.width(1.dp))
//                            Text(maxLines = 1, overflow = TextOverflow.Ellipsis,text = incidentTime(incidents.reportTimeStamp!!,context)
//                                ,color = Color(0xFFAAAAAA), textAlign = TextAlign.Center)
//
//                        }
//
//                    }
//                    incidents.reportAddress?.let { Text(modifier = Modifier.fillMaxWidth(0.8f),text = it, fontWeight = FontWeight.W500, maxLines = 1, overflow = TextOverflow.Ellipsis) }
//                }
//            }
//            Spacer(modifier = Modifier.height(10.dp))
//        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}
@RequiresApi(Build.VERSION_CODES.S)
@ExperimentalPagerApi
@Composable
fun TabLayout(list: MutableIterator<NewReport>,currentLocation : Location,model: HomeViewModel,onClickIncidents: (geoPoint:GeoPoint) -> Unit) {
    val pagerState = rememberPagerState(pageCount = 2)
    val configuration = LocalConfiguration.current

    Box(modifier = Modifier
        .fillMaxWidth()
        .heightIn(
            max = (configuration.screenHeightDp * 0.9).dp,
            min = (configuration.screenHeightDp * 0.3).dp
        )
        .background(
            color = Color.White,
            shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)
        )
        .wrapContentWidth(unbounded = false)
        //.wrapContentHeight(unbounded = true)
        .padding(20.dp, 20.dp, 20.dp, 20.dp)
    ){
        Column(
            modifier = Modifier.background(Color.White)
        ) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .height(20.dp), horizontalArrangement = Arrangement.Center) {
                Divider(
                    color = Color(0XFFE4E4E4),
                    thickness = 3.dp,
                    modifier = Modifier.width(40.dp)
                )
            }
//            SheetNearestIncident(list,onClickIncidents = onClickIncidents,currentLocation = currentLocation)
            Tabs(pagerState = pagerState,userType = model.userType.value)
            Spacer(modifier = Modifier.height(10.dp))
            TabsContent(pagerState = pagerState, model = model,onClickIncidents = onClickIncidents,currentLocation = currentLocation, list = list)
        }
    }
}


//Row(modifier = Modifier.fillMaxWidth().height(30.dp), horizontalArrangement = Arrangement.Center) {
//    Divider(
//        color = Color(0XFFE4E4E4),
//        thickness = 3.dp,
//        modifier = Modifier.width(40.dp)
//    )
//}
//Row(modifier = Modifier.fillMaxWidth()) {
//    Text(text = "Nearest Incidents", fontWeight = FontWeight.Bold, fontSize = 20.sp)
//}
//Spacer(modifier = Modifier.height(10.dp))

//
@RequiresApi(Build.VERSION_CODES.S)
@ExperimentalPagerApi
@Composable
fun Tabs(pagerState: PagerState,userType : Int?) {
    val list =if(userType == 2) {
        listOf(
            "Nearest Incidents" to Icons.Default.Home,
            "My Reports" to Icons.Default.ShoppingCart,
        )
    }else{
        listOf(
            "Nearest Incidents" to Icons.Default.Home,
//            "My Reports" to Icons.Default.ShoppingCart,
        )
    }
    val scope = rememberCoroutineScope()
    TabRow(
        modifier = Modifier.padding(horizontal = 10.dp),
        selectedTabIndex = pagerState.currentPage,
        backgroundColor = Color.White,
        contentColor = Color(0xFF495CE8),
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                height = 2.dp,
                color = Color(0xFF495CE8)
            )
        }
    ) {
        list.forEachIndexed { index, _ ->
            Tab(
                text = {
                    Text(
                        list[index].first,
                        color = if (pagerState.currentPage == index) Color.Black else Color(0XFFAAAAAA)
                    )
                },
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    }
}
@RequiresApi(Build.VERSION_CODES.S)
@ExperimentalPagerApi
@Composable
fun TabsContent(pagerState: PagerState,model:HomeViewModel, list: MutableIterator<NewReport>,currentLocation : Location,onClickIncidents: (geoPoint:GeoPoint) -> Unit) {
    HorizontalPager(state = pagerState, dragEnabled = model.userType.value == 2) {
            page ->
        when (page) {
            0 -> SheetNearestIncident(list,onClickIncidents = onClickIncidents,currentLocation = currentLocation)
            1 -> TabContentScreen(model,data = "Your report list is empty !!",onClickIncidents)
        }
    }

}
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun TabContentScreen(model: HomeViewModel,data: String,onClickIncidents: (geoPoint: GeoPoint) -> Unit) {
    LaunchedEffect(Unit){
        model.getMyReports()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        model.myReports.forEach {
            Box(modifier = Modifier
                .fillMaxWidth()
                .background(color = Color(0xFFFAFAFA), shape = RoundedCornerShape(12.dp))){
                Column(modifier = Modifier
                    .fillMaxSize()
                    , verticalArrangement = Arrangement.SpaceBetween) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Reported Camera", fontWeight = FontWeight.ExtraBold)
                            it.reportTime?.let {time->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(modifier = Modifier.size(15.dp),painter = painterResource(id = R.drawable.ic_clock), tint = Color(0xFF848484), contentDescription = "")
                                    Spacer(modifier = Modifier.width(1.dp))
                                    Text(maxLines = 1, overflow = TextOverflow.Ellipsis,text = time
                                        ,color = Color(0xFFAAAAAA), textAlign = TextAlign.Center)

                                }
                            }

                        }
                        Spacer(modifier = Modifier.height(15.dp))
                        Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(Modifier.fillMaxWidth(0.5f), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(32.dp),contentAlignment = Alignment.Center
                                ) {
                                    Icon(painter = painterResource(id = R.drawable.bg_btn_place_cam_fab_main_scr), contentDescription = "", tint = Color(0xFFECEEFD))
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_like,),
                                        modifier = Modifier.size(15.dp),
                                        tint = Color(0xFF495CE8),
                                        contentDescription = ""
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column() {
                                    Text(text = "Like", fontSize = 12.sp,fontWeight =FontWeight.Medium,color = Color(0xFFAAAAAA))
                                    Text(text = "${it.feedbackLikeCount}", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF171729))
                                }
                            }
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(32.dp),contentAlignment = Alignment.Center
                                ) {
                                    Icon(painter = painterResource(id = R.drawable.bg_btn_place_cam_fab_main_scr), contentDescription = "", tint = Color(0xFFECEEFD))
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_dislike,),
                                        modifier = Modifier.size(15.dp),
                                        tint = Color(0xFF495CE8),
                                        contentDescription = ""
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column() {
                                    Text(text = "Dislike", fontSize = 12.sp,fontWeight =FontWeight.Medium,color = Color(0xFFAAAAAA))
                                    Text(text = "${it.feedbackDisLikeCount}", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF171729))
                                }
                            }

                        }
                        Spacer(modifier = Modifier.height(15.dp))
                        Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(Modifier.fillMaxWidth(0.5f), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(32.dp),contentAlignment = Alignment.Center
                                ) {
                                    Icon(painter = painterResource(id = R.drawable.bg_btn_place_cam_fab_main_scr), contentDescription = "", tint = Color(0xFFECEEFD))
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_alerted,),
                                        modifier = Modifier.size(15.dp),
                                        tint = Color(0xFF495CE8),
                                        contentDescription = ""
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column() {
                                    Text(text = "Alerted", fontSize = 12.sp,fontWeight =FontWeight.Medium,color = Color(0xFFAAAAAA))
                                    Text(text = "${it.alertedCount}", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF171729))
                                }
                            }
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(32.dp),contentAlignment = Alignment.Center
                                ) {
                                    Icon(painter = painterResource(id = R.drawable.bg_btn_place_cam_fab_main_scr), contentDescription = "", tint = Color(0xFFECEEFD))
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_bold_location,),
                                        modifier = Modifier.size(15.dp),
                                        tint = Color(0xFF495CE8),
                                        contentDescription = ""
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column() {
                                    Text(text = "Location", fontSize = 12.sp, fontWeight = FontWeight.Medium,color = Color(0xFFAAAAAA))
                                    Text(modifier = Modifier.horizontalScroll(rememberScrollState(0)),text = "${it.reportAddress}", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF171729))
                                }
                            }

                        }
                    }
                    if (it.isActive == 1){
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)) {
                            Box(modifier = Modifier
                                .fillMaxWidth(0.49f)
                                .fillMaxHeight()
                                .background(
                                    color = Color(0xFFECEEFD),
                                    shape = RoundedCornerShape(bottomStart = 12.dp)
                                )
                                .clickable(onClick = {
                                    onClickIncidents(
                                        GeoPoint(
                                            it.reportLocation?.latitude ?: 0.0,
                                            it.reportLocation?.longitude ?: 0.0
                                        )
                                    )
                                }), contentAlignment = Alignment.Center){
                                Text(text = "View on Map", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF495CE8))
                            }
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .background(
                                    color = Color(0xFFFDEDEB),
                                    shape = RoundedCornerShape(bottomEnd = 12.dp)
                                )
                                .clickable(onClick = {
//                                    it.reportId?.let { rId ->
//                                        model.deleteReport(rId)
//                                    }
                                })
                                ,contentAlignment = Alignment.Center){
                                Text(text = "Delete Report", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFFEA4E34))
                            }
                        }
                    }


                }
            }
            Spacer(modifier = Modifier.height(16.dp))

        }
        Spacer(modifier = Modifier.height(25.dp))
//        Text(
//            text = data,
//            style = MaterialTheme.typography.h6,
//            color = Color(0xFF808080),
//            fontWeight = FontWeight.Bold,
//            textAlign = TextAlign.Center
//        )
    }
}



fun incidentTime(stamp : Timestamp,ctx : Context) : String{
    val mins = (Timestamp.now().seconds - stamp.seconds)/60

    return if(mins > 60){
        if (mins/1440.0 > 1.0){
            if (mins/43200.0 >1.0){
                "${mins/43200} ${ctx.getString(R.string.lbl_nearestIncedednts_monthAgo)}"
            }else{
                "${mins/1440} ${ctx.getString(R.string.lbl_nearestIncedents_dayAgo)}"
            }
        }else{
            "${mins/60} ${ctx.getString(R.string.lbl_nearestIncedednts_hourAgo)}"
        }
    }else{
        if(mins<1){
            ctx.getString(R.string.lbl_nearestIncedednts_hourAgo)
        }else{
            "$mins ${ctx.getString(R.string.lbl_nearestIncedednts_minuteAgo)}"
        }
    }
}

fun incidentDistance(distance : Float) : String{
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.DOWN
    if (distance > 1000){
        return "${df.format((distance/1000))} km"
    }else{
        return "${df.format(distance)} m"
    }
}
