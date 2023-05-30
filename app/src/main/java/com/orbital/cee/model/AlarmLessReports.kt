package com.orbital.cee.model

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable

@Serializable
data class AlarmLessReports (
    val mutedReports: List<String>  = listOf()
)