package com.orbital.cee.view.trip

import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Polyline
import com.mapbox.geojson.*
import com.mapbox.geojson.BoundingBox.fromLngLats
import com.mapbox.geojson.LineString.fromLngLats
import com.mapbox.geojson.MultiLineString.fromLngLats
import com.mapbox.geojson.MultiPoint.fromLngLats
import com.mapbox.maps.*
//import com.mapbox.maps.extension.style.layers.addLayer
//import com.mapbox.maps.extension.style.layers.generated.lineLayer
//import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
//import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
//import com.mapbox.maps.extension.style.sources.addSource
//import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
//import com.mapbox.maps.plugin.animation.easeTo
//import com.mapbox.maps.plugin.animation.flyTo
//import com.mapbox.maps.plugin.annotation.AnnotationConfig
//import com.mapbox.maps.plugin.annotation.annotations
//import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
//import com.mapbox.maps.plugin.compass.compass
//import com.mapbox.maps.plugin.gestures.gestures
//import com.mapbox.maps.plugin.logo.logo
//import com.mapbox.maps.plugin.scalebar.scalebar
//import com.mapbox.maps.viewannotation.viewAnnotationOptions
import com.orbital.cee.R
import com.orbital.cee.core.Constants
import com.orbital.cee.core.Permissions
import com.orbital.cee.model.Trip
import com.orbital.cee.utils.MetricsUtils
import com.orbital.cee.utils.MetricsUtils.Companion.getAddress
import com.orbital.cee.view.home.components.CustomModal
import com.orbital.cee.view.home.components.showCustomDialog
import com.orbital.cee.view.home.components.showRegisterDialog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TripDetail(trip:Trip,onClickBack:()->Unit,onClickDelete:()->Unit){
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)
    val sdff = SimpleDateFormat("hh:mm", Locale.US)
    val sdfff = SimpleDateFormat("EEEE", Locale.US)
    val df = DecimalFormat("#.##")
    val context = LocalContext.current
    var isShowTripDeleteConfirmationDialog = remember { mutableStateOf(false) }
    val rotate = if (LocalConfiguration.current.layoutDirection == LayoutDirection.Rtl.ordinal){180f}else{0f}
    df.roundingMode = RoundingMode.DOWN

//    var mapView by remember { mutableStateOf<MapView?>(null) }

//    DisposableEffect(Unit) {
//        val newMapView =
//        mapView = newMapView
//        onDispose {
//            mapView?.onDestroy()
//        }
//    }

    // Render the MapView in the Composable


    Column(modifier = Modifier
        .fillMaxSize()
        .background(color = MaterialTheme.colors.background)
        .pointerInput(Unit) {
            detectDragGestures(onDrag = { point, offset ->
                if (offset.x > Constants.OFFSET_X && point.position.x < Constants.POINT_X) {
                    onClickBack.invoke()
                }
            })
        }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(85.dp)
                .border(
                    width = 1.dp,
                    color = Color(0XFFE4E4E4),
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "${stringResource(R.string.lbl_trip_history_detail_appBar_title)} ${trip.startTime?.let { sdf.format(it) }}", maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Bold)
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart){
                IconButton(onClick = {
                    onClickBack.invoke()
                    //mapView.onDestroy()
                                     }, modifier = Modifier.padding(end = 12.dp)) {
                    Icon(modifier = Modifier
                        .size(22.dp)
                        .rotate(rotate), tint = Color.Black , painter = painterResource(id = R.drawable.ic_arrow_back), contentDescription ="" )
                }
            }
        }
        Spacer(modifier = Modifier.height(15.dp))
        Column(modifier = Modifier
            .background(color = MaterialTheme.colors.background)
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            //.verticalScroll(rememberScrollState())
        ) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0XFFF7F7F7),
                    shape = RoundedCornerShape(10.dp)
                )
                .border(width = 2.dp, color = Color(0XFFE4E4E4), shape = RoundedCornerShape(10.dp))){
                Column(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                        .border(
                            width = 2.dp,
                            color = Color(0XFFE4E4E4),
                            shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                        )) {
//                        AndroidView(modifier = Modifier
//                            .height(240.dp)
//                            .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)),factory = {
//                            MapView(context).apply {
//                                getMapboxMap().loadStyleUri("mapbox://styles/orbital-cee/cl5wwmzcw000814qoaa1zkqrw") {sty ->
//                                    trip.listOfLatLon.let { points ->
//                                        val polygon = Polygon.fromLngLats(listOf(points))
//                                        val cameraPosition = getMapboxMap().cameraForGeometry(polygon,padding =  EdgeInsets(220.0, 320.0, 220.0, 320.0))
//                                        val viewAnnotationManager = this.viewAnnotationManager
//                                        viewAnnotationManager.addViewAnnotation(
//                                            R.layout.camera_report_annotation,
//                                            viewAnnotationOptions {
//                                                geometry(points[0])
//                                                allowOverlap(false)
//                                                anchor(ViewAnnotationAnchor.BOTTOM)
//                                                visible(true)
//                                            }
//                                        )
//                                        viewAnnotationManager.addViewAnnotation(
//                                            R.layout.police_report_annotation,
//                                            viewAnnotationOptions {
//                                                geometry(points[trip.listOfLatLon.size - 1])
//                                                allowOverlap(false)
//                                                anchor(ViewAnnotationAnchor.CENTER)
//                                                visible(true)
//                                            }
//                                        )
//                                        getMapboxMap().setCamera(cameraPosition)
//                                    }
//                                    val lineString = trip.listOfLatLon.let { LineString.fromLngLats(it) }
//                                    val feature = Feature.fromGeometry(lineString)
//                                    val data = geoJsonSource("line") {
//                                        featureCollection(FeatureCollection.fromFeature(feature))
//                                    }
//                                    sty.addSource(data)
//                                    sty.addLayer(lineLayer("linelayer", "line") {
//                                        lineCap(LineCap.ROUND)
//                                        lineJoin(LineJoin.ROUND)
//                                        lineWidth(4.0)
//                                        lineColor("#495CE8")
//                                    })
//                                }
//                                scalebar.enabled = false
//                                compass.enabled = false
//                                gestures.scrollEnabled = false
//                                gestures.pitchEnabled = false
//                            }
//                        })

                    }
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)){
                            Row(modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp, vertical = 15.dp)) {
                                Column(modifier = Modifier
                                    .fillMaxWidth(0.15f)
                                    .fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "${trip.startTime?.let { sdff.format(it) }}")
                                    Text(text = "${trip.endTime?.let { sdff.format(it) }}")
                                }
                                Column(
                                    Modifier
                                        .fillMaxWidth(0.10f)
                                        .fillMaxHeight(),
                                    verticalArrangement = Arrangement.SpaceBetween, horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(painter = painterResource(id = R.drawable.ic_circle), contentDescription = "", tint = Color.Unspecified,modifier = Modifier.fillMaxHeight(0.3f))
                                    Divider(modifier = Modifier
                                        .fillMaxHeight(0.75f)
                                        .width(1.5.dp),color = Color(0XFF495CE8))
                                    Icon(painter = painterResource(id = R.drawable.ic_triangle), contentDescription = "", tint = Color.Unspecified,modifier = Modifier.fillMaxHeight())

                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(
                                    Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(),
                                    verticalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text(text = getAddress(trip.listOfLatLon[0].latitude(),trip.listOfLatLon[0].longitude(),context),maxLines=2, modifier = Modifier
                                        .fillMaxWidth(0.9f)
                                        .height(35.dp))
                                    Text(text = getAddress(trip.listOfLatLon[trip.listOfLatLon.size - 1].latitude(),trip.listOfLatLon[trip.listOfLatLon.size - 1].longitude(),context),maxLines=2, modifier = Modifier
                                        .fillMaxWidth(0.9f)
                                        .height(35.dp))
                                }
                            }
                    }
                }
            }
            Spacer(modifier = Modifier.height(15.dp))
            Text(text = stringResource(R.string.lbl_trip_history_detail_title), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0XFFF7F7F7),
                    shape = RoundedCornerShape(10.dp)
                )
                .border(width = 2.dp, color = Color(0XFFE4E4E4), shape = RoundedCornerShape(10.dp))){
                Column(modifier = Modifier.padding(15.dp)) {
                    Text(text = "${stringResource(R.string.lbl_trip_history_detail_appBar_title)} ${trip.startTime?.let { sdf.format(it) }}", fontSize = 18.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.ExtraBold)
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text =  "${trip.startTime?.let { sdfff.format(it)+", "+sdf.format(it) }}", color = Color(0XFF848484), maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.W400)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        Column(modifier = Modifier.fillMaxWidth(0.32f)) {
                            Text(text = stringResource(R.string.lbl_speedometer_trip_avg_speed),fontWeight = FontWeight.W500, fontFamily = FontFamily(Font(R.font.work_sans_medium)))
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "${trip.speedAverage}", fontWeight = FontWeight.ExtraBold,fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(text = "KM/h")
                            }
                        }
                        Column(modifier = Modifier.fillMaxWidth(0.5f)) {
                            Text(text = stringResource(R.string.lbl_speedometer_trip_max_speed),fontWeight = FontWeight.W500, fontFamily = FontFamily(Font(R.font.work_sans_medium)))
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "${trip.maxSpeed}", fontWeight = FontWeight.ExtraBold,fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(text = "KM/h")
                            }
                        }
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(text =stringResource(R.string.lbl_trip_history_detail_alert_count),fontWeight = FontWeight.W500, fontFamily = FontFamily(Font(R.font.work_sans_medium)))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "${trip.alertCount}", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                        }

                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                        Column(modifier = Modifier.fillMaxWidth(0.32f)) {
                            Text(text = stringResource(R.string.lbl_speedometer_trip_duration), fontWeight = FontWeight.W500, fontFamily = FontFamily(Font(R.font.work_sans_medium)))
                            Spacer(modifier = Modifier.height(8.dp))
                                Text(text = MetricsUtils.getDuration(trip.startTime, trip.endTime), fontWeight = FontWeight.ExtraBold,fontSize = 20.sp)
                        }
                        Column(modifier = Modifier.fillMaxWidth(0.42f)) {
                            Text(text = stringResource(R.string.lbl_speedometer_trip_distance), fontWeight = FontWeight.W500, fontFamily = FontFamily(Font(R.font.work_sans_medium)))
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = df.format(trip.distance), fontWeight = FontWeight.ExtraBold,fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(text = "KM")
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                TextButton(onClick ={isShowTripDeleteConfirmationDialog.value = true}) {
                    Text(text =  stringResource(R.string.lbl_trip_history_detail_alert_title), color = Color(0XFFEA4E34), fontSize = 16.sp )
                }
            }
            Spacer(modifier = Modifier.height(50.dp))

        }
        if (isShowTripDeleteConfirmationDialog.value) {
            showCustomDialog(
                onNegativeClick = { isShowTripDeleteConfirmationDialog.value = false },
                onPositiveClick = {
                    GlobalScope.launch {
                        isShowTripDeleteConfirmationDialog.value = false
                        delay(1000)
                        onClickDelete.invoke()
                    }
                },
                buttonNegativeText =stringResource(R.string.btn_setting_alert_delete_account_no),
                buttonPositiveText =stringResource(R.string.btn_setting_alert_delete_account_yes),
                title = stringResource(R.string.lbl_trip_history_detail_alert_description)
            )
        }

    }
}
@RequiresApi(Build.VERSION_CODES.S)
private fun zoomCamera(){

}