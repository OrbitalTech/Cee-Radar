package com.orbital.cee.model

import androidx.compose.runtime.MutableState

class HelpQuestionModel (
    var title : String,
    var discreption : String,
    var isExpanded : MutableState<Boolean>
)