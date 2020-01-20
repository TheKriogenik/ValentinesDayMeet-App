package com.kriogenik.guzeeva.messaging.factory

import com.kriogenik.guzeeva.handlers.PersonActionFactory
import com.kriogenik.guzeeva.messaging.model.Payload
import com.petersamokhin.bots.sdk.objects.Message
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class PayloadFactoryDefaultImpl: PayloadFactory {

    @Autowired
    private lateinit var actionFactory: PersonActionFactory

    override fun getPayload(message: Message): Optional<Payload> {
        return message.getMessageButtonPayload()
    }

    private fun Message.getMessageButtonPayload() = with(this.attachmentsOfReceivedMessage){
        println(this.toString())
        when(this.has("payload")){
            true -> {
                this.getString("payload")
                val payload = JSONObject(this["payload"].toString())
                val button = JSONObject(payload.toString())
                button["button"].toString().split(':').let{lst ->
                    actionFactory.getPersonAction(lst.first()).map{action ->
                        Payload(action, lst.drop(1))
                    }
                }
            }
            false -> Optional.empty()
        }
    }

}