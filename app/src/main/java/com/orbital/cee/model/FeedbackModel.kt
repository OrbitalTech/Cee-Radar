package com.orbital.cee.model

import android.location.Location

data class FeedbackModel(
    var isLiked: Boolean? = null,
    var feedbackLikeCount : Int = 0,
    var feedbackDisLikeCount : Int = 0
)