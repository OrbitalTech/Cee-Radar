package com.orbital.cee.model

class ResponseDto (
    var isSuccess : Boolean,
    var message : String
    )
class ResponseWithData (
    var isSuccess : Boolean,
    var data : ArrayList<NewReport>?,
    var serverMessage : String?
)