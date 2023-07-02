package com.orbital.cee.model

import android.location.Location

data class SingleCustomReport(
    var reportByUID : String = "",
    var isSuccess : Boolean,
    var reportLocation : Location? = null,
    var isReportOwner : Boolean = false,
    var reportTime : String? = null,
    var reportAddress : String? = null,
    var reportType: Int = 0,
    var alertedCount: Int = 0,
    var reportSpeedLimit: Int? = 0,

    var reportId: String? = null,
    var isActive: Int = 1,
//    var reportOwnerType: Int? = 0,

    var isLiked: Boolean? = null,
    var feedbackLikeCount : Int = 0,
    var feedbackDisLikeCount : Int = 0
)
data class UserNameAndID(
    var userId : String = "",
    var username :String =  "",
)