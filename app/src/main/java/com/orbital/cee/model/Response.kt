package com.orbital.cee.model

import kotlinx.serialization.Serializable

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