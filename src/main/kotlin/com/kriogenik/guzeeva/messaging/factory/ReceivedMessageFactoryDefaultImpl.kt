package com.kriogenik.guzeeva.messaging.factory

import com.kriogenik.guzeeva.messaging.model.ReceivedMessage
import org.json.JSONObject
import org.springframework.stereotype.Component
import com.petersamokhin.bots.sdk.objects.Message as VkMessage

@Component
class ReceivedMessageFactoryDefaultImpl: ReceivedMessageFactory<VkMessage> {

    override fun createReceivedMessage(message: VkMessage): ReceivedMessage {
        return ReceivedMessage(message.text, message.getMessageButtonPayload())
    }

    private fun VkMessage.getMessageButtonPayload() = with(this.attachmentsOfReceivedMessage){
        println(this.toString())
        when(this.has("payload")){
            true -> {
                this.getString("payload")
                /*JSONObject(this["payload"].toString())
                val button = JSONObject(payload.toString())
                button["button"].toString()*/
            }
            false -> ""
        }
    }

}
