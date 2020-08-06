package com.kriogenik.guzeeva.actions

import com.kriogenik.guzeeva.messaging.model.ReceivedMessage
import com.kriogenik.guzeeva.messaging.model.ResponseMessage
import java.util.*

interface Action<T> {

    abstract val actionName: String

    fun perform(message: ReceivedMessage): Optional<ResponseMessage>

}
