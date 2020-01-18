package com.kriogenik.guzeeva.messaging.model

data class ReceivedMessage(val userVkId: Int,
                           val message:  String,
                           val payload:  String)
