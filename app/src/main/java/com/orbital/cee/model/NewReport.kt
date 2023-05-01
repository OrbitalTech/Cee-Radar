package com.orbital.cee.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class NewReport(
    var isActive: Boolean? = false,
    var geoLocation: List<*>? = null,
    var reportByUID: String? = null,
    var reportId: String? = null,
    var reportTimeStamp: Timestamp? = null,
    var reportType : Int = 0,
    var reportAddress : String? = null,
    var reportSpeedLimit: Int? = 0,
    var reportDirection: Any? = null,
)