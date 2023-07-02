package com.orbital.cee.model

import com.mapbox.geojson.Point
import java.util.*
import kotlin.collections.ArrayList

data class Trip(
    var speedAverage: Int = 0,
    var maxSpeed: Int = 0,
    var distance : Float = 0f,
    var listOfLatLon : ArrayList<Point> = arrayListOf(),
    var startTime : Date? = null,
    var duration : Int? = null,
    var endTime : Date? = null,
    var isFavorite : Boolean = false,
    var alertCount : Int = 0
)