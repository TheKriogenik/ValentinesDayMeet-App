package com.kriogenik.guzeeva.handlers.received

import com.kriogenik.guzeeva.handlers.Handler
import com.kriogenik.guzeeva.messaging.model.ReceivedMessage
import com.kriogenik.guzeeva.messaging.model.ResponseMessage
import com.kriogenik.guzeeva.model.PersonRole
import java.util.*

interface PersonReceivedMessageHandler{

    abstract val personRole: PersonRole.Role

    fun handle(target: ReceivedMessage): Optional<ResponseMessage>

}
