package com.orbital.cee.model

import com.google.firebase.Timestamp

class ResponseDto (
    var isSuccess : Boolean,
    var serverMessage : String
    )
class ResponseWithData (
    var isSuccess : Boolean,
    var data : ArrayList<NewReport>?,
    var serverMessage : String?
)