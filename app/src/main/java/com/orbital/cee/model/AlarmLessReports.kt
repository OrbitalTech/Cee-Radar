package com.orbital.cee.model

import kotlinx.serialization.Serializable

@Serializable
data class AlarmLessReports (
    val mutedReports: List<String>  = listOf()
)