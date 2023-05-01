package com.orbital.cee.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.type.DateTime

class User(
    var countryCode: String? = null,
    var g: String? = null,
    var geoLocation: List<*>? = null,
    var isUserBanned: Boolean? = null,
    var joiningTimeStamp: Timestamp? = null,
    var lastSeen: Timestamp? = null,
    var phoneNumber: String? = null,
    var pushId: String? = null,
    var provider: String? = null,
    var status: String? = null,
    var subscriptionPlan: String? = "Gold-Annual",
    var userAvatar: String? = null,
    var userCoin: Int? = null,
    var userContribution: Int? = null,
    var userEmail: String? = null ,
    var userGender: String? = null,
    var userId: String? = null,
    var userType: Int? = null,
    var userLoginCount: Int? = null,
    var username: String? = null

)

class UserNew(
    var phoneNumber: String? = null,
    var provider: String? = null,
    var userAvatar: String? = null,
    var userEmail: String? = null ,
    var userGender: String? = null,
    var userType: Int? = null,
    var username: String? = null

)