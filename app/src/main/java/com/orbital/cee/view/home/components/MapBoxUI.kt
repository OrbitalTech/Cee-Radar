package com.orbital.cee.view.home.components

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.mapbox.geojson.Point
import com.mapbox.maps.*
import kotlinx.coroutines.launch


@Composable
fun Map(lat: Double, lng: Double, zoom: Double) {
    MapboxMap(lat = lat, lng = lng, zoom = zoom)
}


@Composable
fun MapboxMap(lat: Double, lng: Double, zoom: Double) {
    val map = rememberMapboxViewWithLifecycle()

    MapboxMapContainer(map = map, lat = lat, lng = lng, zoom = zoom)
}


@Composable()
fun MapboxMapContainer(map: MapView, lat: Double, lng: Double, zoom: Double) {
    val (isMapInitialized, setMapInitialized) = remember(map) { mutableStateOf(false) }

    LaunchedEffect(map, isMapInitialized) {
        if (!isMapInitialized) {
            val mbxMap = map.getMapboxMap()

            mbxMap.loadStyleUri(Style.OUTDOORS) {
                mbxMap.centerTo(lat = lat, lng = lng, zoom = zoom)

                setMapInitialized(true)
            }
        }
    }

    val coroutineScope = rememberCoroutineScope()

    AndroidView(factory = { map }) {
        coroutineScope.launch {
            val mbxMap = it.getMapboxMap()

            mbxMap.centerTo(lat = lat, lng = lng, zoom = zoom)
        }
    }
}


@Composable
private fun rememberMapboxViewWithLifecycle(): MapView {
    val context = LocalContext.current

    val opt = MapInitOptions(context, plugins = emptyList())
    val map = remember { MapView(context, opt) }

    val observer = rememberMapboxViewLifecycleObserver(map)
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(lifecycle) {
        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    return map
}

@Composable
private fun rememberMapboxViewLifecycleObserver(map: MapView): LifecycleEventObserver {
    return remember(map) {
        LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> map.onStart()
                Lifecycle.Event.ON_STOP -> map.onStop()
                Lifecycle.Event.ON_DESTROY -> map.onDestroy()
                else -> Unit // nop
            }
        }
    }
}


fun MapboxMap.centerTo(lat: Double, lng: Double, zoom: Double) {
    val point = Point.fromLngLat(lng, lat)

    val camera = CameraOptions.Builder()
        .center(point)
        .zoom(zoom)
        .build()

    setCamera(camera)
}