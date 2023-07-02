package com.orbital.cee.model

import com.mapbox.geojson.Point
import kotlinx.serialization.Serializable
import java.util.Date

sealed class Response<out T> {
    object Loading: Response<Nothing>()

    data class Success<out T>(
        val data: T?
    ): Response<T>()

    data class Error(
        val e: Exception?
    ): Response<Nothing>()
}



@Serializable
data class OverpassResponse(val elements: List<Element>)

@Serializable
data class Element(val tags: Tags)

@Serializable
data class Tags(val maxspeed: String)


@Serializable
data class GeometryAno(val type: String,val coordinates: List<Double>)