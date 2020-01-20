package com.kriogenik.guzeeva.messaging.model

import com.kriogenik.guzeeva.model.Person
import java.util.*

data class ReceivedMessage(val userVkId: Int,
                           val message:  String,
                           val payload: Optional<Payload>){}
