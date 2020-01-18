package com.kriogenik.guzeeva.messaging.factory

import com.kriogenik.guzeeva.messaging.model.ReceivedMessage

interface ReceivedMessageFactory<VK_MESSAGE> {

    fun createReceivedMessage(message: VK_MESSAGE): ReceivedMessage

}
