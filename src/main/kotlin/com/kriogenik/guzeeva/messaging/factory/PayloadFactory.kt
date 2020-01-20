package com.kriogenik.guzeeva.messaging.factory

import com.kriogenik.guzeeva.messaging.model.Payload
import com.petersamokhin.bots.sdk.objects.Message
import java.util.*

interface PayloadFactory{

    fun getPayload(message: Message): Optional<Payload>

}
