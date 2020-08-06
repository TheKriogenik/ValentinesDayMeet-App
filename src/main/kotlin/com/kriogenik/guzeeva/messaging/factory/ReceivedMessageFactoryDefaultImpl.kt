package com.kriogenik.guzeeva.messaging.factory

import com.kriogenik.guzeeva.handlers.PersonActionFactory
import com.kriogenik.guzeeva.messaging.model.ReceivedMessage
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import com.petersamokhin.bots.sdk.objects.Message as VkMessage

@Component
class ReceivedMessageFactoryDefaultImpl: ReceivedMessageFactory<VkMessage> {

    @Autowired
    private lateinit var actionFactory: PersonActionFactory

    @Autowired
    private lateinit var payloadFactory: PayloadFactory

    override fun createReceivedMessage(message: VkMessage): ReceivedMessage {
        return ReceivedMessage(message.authorId(), message.text, payloadFactory.getPayload(message))
    }

}
